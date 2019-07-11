package com.alibaba.otter.canal.instance.manager;

import com.alibaba.otter.canal.common.CanalException;
import com.alibaba.otter.canal.common.alarm.CanalAlarmHandler;
import com.alibaba.otter.canal.common.alarm.LogAlarmHandler;
import com.alibaba.otter.canal.common.utils.JsonUtils;
import com.alibaba.otter.canal.common.zookeeper.ZkClientx;
import com.alibaba.otter.canal.filter.aviater.AviaterRegexFilter;
import com.alibaba.otter.canal.instance.core.AbstractCanalInstance;
import com.alibaba.otter.canal.instance.core.CanalMQConfig;
import com.alibaba.otter.canal.instance.manager.model.CanalCoreParameter;
import com.alibaba.otter.canal.instance.manager.model.CanalInstanceParameter;
import com.alibaba.otter.canal.meta.FileMixedMetaManager;
import com.alibaba.otter.canal.meta.MemoryMetaManager;
import com.alibaba.otter.canal.meta.PeriodMixedMetaManager;
import com.alibaba.otter.canal.meta.ZooKeeperMetaManager;
import com.alibaba.otter.canal.parse.CanalEventParser;
import com.alibaba.otter.canal.parse.ha.CanalHAController;
import com.alibaba.otter.canal.parse.ha.HeartBeatHAController;
import com.alibaba.otter.canal.parse.inbound.AbstractEventParser;
import com.alibaba.otter.canal.parse.inbound.mysql.MysqlEventParser;
import com.alibaba.otter.canal.parse.inbound.mysql.rds.RdsBinlogEventParserProxy;
import com.alibaba.otter.canal.parse.inbound.mysql.tsdb.DefaultTableMetaTSDBFactory;
import com.alibaba.otter.canal.parse.inbound.mysql.tsdb.TableMetaTSDB;
import com.alibaba.otter.canal.parse.inbound.mysql.tsdb.TableMetaTSDBBuilder;
import com.alibaba.otter.canal.parse.index.*;
import com.alibaba.otter.canal.parse.support.AuthenticationInfo;
import com.alibaba.otter.canal.protocol.position.EntryPosition;
import com.alibaba.otter.canal.sink.entry.EntryEventSink;
import com.alibaba.otter.canal.sink.entry.group.GroupEventSink;
import com.alibaba.otter.canal.store.AbstractCanalStoreScavenge;
import com.alibaba.otter.canal.store.memory.MemoryEventStoreWithBuffer;
import com.alibaba.otter.canal.store.model.BatchMode;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 单个canal实例，比如一个destination会独立一个实例
 * 
 * @author jianghang 2012-7-11 下午09:26:51
 * @version 1.0.0
 */
public class CanalInstanceWithManager extends AbstractCanalInstance {

    private static final Logger logger = LoggerFactory.getLogger(CanalInstanceWithManager.class);
    protected CanalCoreParameter        coreParameter;
    protected CanalInstanceParameter    instanceParameter;                                                      // 对应参数
    private ZkClientx zkClientx;

    public CanalInstanceWithManager(CanalCoreParameter coreParameter, CanalInstanceParameter instanceParameter, ZkClientx zkClientx){
        this.zkClientx = zkClientx;
        this.coreParameter = coreParameter;
        this.instanceParameter = instanceParameter;
        this.canalId = coreParameter.getId();
        this.destination = instanceParameter.getDestination();

        logger.info("init CanalInstance for {}-{} with parameters:{},{}", canalId, destination, coreParameter, instanceParameter);

        CanalMQConfig config = new CanalMQConfig();
        config.setTopic(instanceParameter.getMqTopic());
        config.setPartition(instanceParameter.getMqPartition());
        config.setPartitionHash(instanceParameter.getMqPartitionHash());
        config.setPartitionsNum(instanceParameter.getMqPartitionsNum());
        this.mqConfig = config;

        // 初始化报警机制
        initAlarmHandler();
        // 初始化metaManager
        initMetaManager();
        // 初始化eventStore
        initEventStore();
        // 初始化eventSink
        initEventSink();
        // 初始化eventParser;
        initEventParser();

        // 基础工具，需要提前start，会有先订阅再根据filter条件启动parse的需求
        if (!alarmHandler.isStart()) {
            alarmHandler.start();
        }

        if (!metaManager.isStart()) {
            metaManager.start();
        }
        logger.info("init successful....");
    }

    @Override
    public void start() {
        // 初始化metaManager
        logger.info("start CannalInstance for {}-{} with parameters:{},{}", canalId, destination, coreParameter, instanceParameter);
        super.start();
    }

    protected void initAlarmHandler() {
        logger.info("init alarmHandler begin...");
        alarmHandler = new LogAlarmHandler();
        logger.info("init alarmHandler end! \n\t load CanalAlarmHandler:{} ", alarmHandler.getClass().getName());
    }

    protected void initMetaManager() {
        logger.info("init metaManager begin...");
        String mode = instanceParameter.getMetaMode();
        if ("memory".equals(mode)) {
            metaManager = new MemoryMetaManager();
        } else if ("zookeeper".equals(mode)) {
            metaManager = new ZooKeeperMetaManager();
            ((ZooKeeperMetaManager) metaManager).setZkClientx(getZkclientx());
        } else if ("mixed".equals(mode)) {
            // metaManager = new MixedMetaManager();
            metaManager = new PeriodMixedMetaManager();// 换用优化过的mixed, at
                                                       // 2012-09-11
            // 设置内嵌的zk metaManager
            ZooKeeperMetaManager zooKeeperMetaManager = new ZooKeeperMetaManager();
            zooKeeperMetaManager.setZkClientx(getZkclientx());
            ((PeriodMixedMetaManager) metaManager).setZooKeeperMetaManager(zooKeeperMetaManager);
        } else if ("file".equals(mode)) {
            FileMixedMetaManager fileMixedMetaManager = new FileMixedMetaManager();
            fileMixedMetaManager.setDataDir(instanceParameter.getMetaFileDataDir());
            fileMixedMetaManager.setPeriod(instanceParameter.getMetaFileFlushPeriod());
            metaManager = fileMixedMetaManager;
        } else {
            throw new CanalException("unsupport MetaMode for " + mode);
        }

        logger.info("init metaManager end! \n\t load CanalMetaManager:{} ", metaManager.getClass().getName());
    }

    protected void initEventStore() {
        logger.info("init eventStore begin...");
        String mode = instanceParameter.getStorageMode();
        if ("memory".equals(mode)) {
            MemoryEventStoreWithBuffer memoryEventStore = new MemoryEventStoreWithBuffer();
            memoryEventStore.setBufferSize(instanceParameter.getMemoryBufferSize());
            memoryEventStore.setBufferMemUnit(instanceParameter.getMemoryBufferMemunit());
            memoryEventStore.setBatchMode(BatchMode.valueOf(instanceParameter.getMemoryBatchMode()));
            memoryEventStore.setDdlIsolation(instanceParameter.getDdlIsolation());
            memoryEventStore.setRaw(instanceParameter.getMemoryRawEntry());
            eventStore = memoryEventStore;
        } else {
            throw new CanalException("unsupport MetaMode for " + mode);
        }

        if (eventStore instanceof AbstractCanalStoreScavenge) {
            String scanvengeMode = instanceParameter.getStorageScavengeMode();
            AbstractCanalStoreScavenge eventScavengeStore = (AbstractCanalStoreScavenge) eventStore;
            eventScavengeStore.setDestination(destination);
            eventScavengeStore.setCanalMetaManager(metaManager);
            eventScavengeStore.setOnAck("ack".equalsIgnoreCase(scanvengeMode));
            eventScavengeStore.setOnFull("full".equalsIgnoreCase(scanvengeMode));
            eventScavengeStore.setOnSchedule("schedule".equalsIgnoreCase(scanvengeMode));
            if ("schedule".equals(scanvengeMode)) {
                eventScavengeStore.setScavengeSchedule(instanceParameter.getStorageScangengeSchedule());
            }
        }
        logger.info("init eventStore end! \n\t load CanalEventStore:{}", eventStore.getClass().getName());
    }

    protected void initEventSink() {
        logger.info("init eventSink begin...");

        int groupSize = getGroupSize();
        if (groupSize <= 1) {
            eventSink = new EntryEventSink();
        } else {
            eventSink = new GroupEventSink(groupSize);
        }

        if (eventSink instanceof EntryEventSink) {
            ((EntryEventSink) eventSink).setFilterTransactionEntry(instanceParameter.getFilterTransactionEntry());
            ((EntryEventSink) eventSink).setEventStore(getEventStore());
        }
        // if (StringUtils.isNotEmpty(filter)) {
        // AviaterRegexFilter aviaterFilter = new AviaterRegexFilter(filter);
        // ((AbstractCanalEventSink) eventSink).setFilter(aviaterFilter);
        // }
        logger.info("init eventSink end! \n\t load CanalEventSink:{}", eventSink.getClass().getName());
    }

    protected void initEventParser() {
        logger.info("init eventParser begin...");
        String type = instanceParameter.getSourcingType();

//        List<List<DataSourcing>> groupDbAddresses = parameters.getGroupDbAddresses();
        /*if (!CollectionUtils.isEmpty(groupDbAddresses)) {
            int size = groupDbAddresses.get(0).size();// 取第一个分组的数量，主备分组的数量必须一致
            List<CanalEventParser> eventParsers = new ArrayList<CanalEventParser>();
            for (int i = 0; i < size; i++) {
                List<InetSocketAddress> dbAddress = new ArrayList<InetSocketAddress>();
                SourcingType lastType = null;
                for (List<DataSourcing> groupDbAddress : groupDbAddresses) {
                    if (lastType != null && !lastType.equals(groupDbAddress.get(i).getType())) {
                        throw new CanalException(String.format("master/slave Sourcing type is unmatch. %s vs %s",
                            lastType,
                            groupDbAddress.get(i).getType()));
                    }

                    lastType = groupDbAddress.get(i).getType();
                    dbAddress.add(groupDbAddress.get(i).getDbAddress());
                }

                // 初始化其中的一个分组parser
                eventParsers.add(doInitEventParser(lastType, dbAddress));
            }

            if (eventParsers.size() > 1) { // 如果存在分组，构造分组的parser
                GroupEventParser groupEventParser = new GroupEventParser();
                groupEventParser.setEventParsers(eventParsers);
                this.eventParser = groupEventParser;
            } else {
                this.eventParser = eventParsers.get(0);
            }
        } else {
            // 创建一个空数据库地址的parser，可能使用了tddl指定地址，启动的时候才会从tddl获取地址
            this.eventParser = doInitEventParser(type, new ArrayList<InetSocketAddress>());
        }*/
        List<InetSocketAddress> addressList = new ArrayList<>();
        String masterAddress = instanceParameter.getMasterAddress();
        if(StringUtils.isNotBlank(masterAddress)) {
            try {
                int index = masterAddress.indexOf(":");
                String host = masterAddress.substring(0, index);
                Integer port = Integer.parseInt(masterAddress.substring(index+1));
                InetSocketAddress master = InetSocketAddress.createUnresolved(host, port);
                addressList.add(master);
            } catch (Exception e) {
                logger.error("illegal master address:{},{}", masterAddress, e);
            }

        }
        String slaveAddress = instanceParameter.getStandbyAddress();
        if(StringUtils.isNotBlank(slaveAddress)) {
            try {
                int index = slaveAddress.indexOf(":");
                String host = slaveAddress.substring(0, index);
                Integer port = Integer.parseInt(slaveAddress.substring(index+1));
                InetSocketAddress slave = InetSocketAddress.createUnresolved(host, port);
                addressList.add(slave);
            } catch (Exception e) {
                logger.error("illegal standby address:{},{}", masterAddress, e);
            }
        }
        this.eventParser = doInitEventParser(type, addressList);

        logger.info("init eventParser end! \n\t load CanalEventParser:{}", eventParser.getClass().getName());
    }

    private CanalEventParser doInitEventParser(String type, List<InetSocketAddress> dbAddresses) {
        CanalEventParser eventParser;
        if ("mysql".equals(type)) {
            MysqlEventParser mysqlEventParser = null;
            if (StringUtils.isNotEmpty(instanceParameter.getRdsAccesskey())
                && StringUtils.isNotEmpty(instanceParameter.getRdsSecretkey())
                && StringUtils.isNotEmpty(instanceParameter.getRdsInstanceId())) {

                mysqlEventParser = new RdsBinlogEventParserProxy();
                ((RdsBinlogEventParserProxy) mysqlEventParser).setAccesskey(instanceParameter.getRdsAccesskey());
                ((RdsBinlogEventParserProxy) mysqlEventParser).setSecretkey(instanceParameter.getRdsSecretkey());
                ((RdsBinlogEventParserProxy) mysqlEventParser).setInstanceId(instanceParameter.getRdsInstanceId());
            } else {
                mysqlEventParser = new MysqlEventParser();
            }
            mysqlEventParser.setDestination(destination);
            // 编码参数
            mysqlEventParser.setConnectionCharset(Charset.forName(instanceParameter.getConnectionCharset()));
            //mysqlEventParser.setConnectionCharsetNumber(instanceParameter.getConnectionCharsetNumber());
            // 网络相关参数
            mysqlEventParser.setDefaultConnectionTimeoutInSeconds(instanceParameter.getNetworkSoTimeout());
            mysqlEventParser.setSendBufferSize(instanceParameter.getNetworkSendBufferSize());
            mysqlEventParser.setReceiveBufferSize(instanceParameter.getNetworkReceiveBufferSize());

            mysqlEventParser.setParallel(instanceParameter.getParserParallel());
            mysqlEventParser.setParallelBufferSize(instanceParameter.getParserParallelBufferSize());
            mysqlEventParser.setParallelThreadSize(instanceParameter.getParserParallelThreadSize());
            // 心跳检查参数
            mysqlEventParser.setDetectingEnable(instanceParameter.getDetectionEnable());
            mysqlEventParser.setDetectingSQL(instanceParameter.getDetectingSql());
            mysqlEventParser.setDetectingIntervalInSeconds(instanceParameter.getDetectingInvervalTime());
            // 数据库信息参数
            mysqlEventParser.setSlaveId(instanceParameter.getMysqlSlaveId());
            if (!CollectionUtils.isEmpty(dbAddresses)) {
                mysqlEventParser.setMasterInfo(new AuthenticationInfo(dbAddresses.get(0),
                    instanceParameter.getDbUsername(),
                    instanceParameter.getDbPassword(),
                    instanceParameter.getDefaultDatabaseName()));
                if (dbAddresses.size() > 1) {
                    mysqlEventParser.setStandbyInfo(new AuthenticationInfo(dbAddresses.get(1),
                        instanceParameter.getDbUsername(),
                        instanceParameter.getDbPassword(),
                        instanceParameter.getDefaultDatabaseName()));
                }
            }

            if (StringUtils.isNotBlank(instanceParameter.getMasterPosition())) {
                try {
                    EntryPosition masterPosition = JsonUtils.unmarshalFromString(instanceParameter.getMasterPosition(),
                            EntryPosition.class);
                    // binlog位置参数
                    mysqlEventParser.setMasterPosition(masterPosition);

                    if (StringUtils.isNotBlank(instanceParameter.getStandbyPosition())) {
                        EntryPosition standbyPosition = JsonUtils.unmarshalFromString(instanceParameter.getStandbyPosition(),
                                EntryPosition.class);
                        mysqlEventParser.setStandbyPosition(standbyPosition);
                    }
                } catch (Exception e) {
                    logger.error("set position exception",e );
                }
            }
            mysqlEventParser.setFallbackIntervalInSeconds(instanceParameter.getFallbackIntervalInSeconds());
            mysqlEventParser.setProfilingEnabled(false);
            mysqlEventParser.setFilterTableError(instanceParameter.getFilterTableError());
            mysqlEventParser.setIsGTIDMode(BooleanUtils.toBoolean(instanceParameter.getGtidon()));
            mysqlEventParser.setUseDruidDdlFilter(instanceParameter.getFilterDruidDdl());
            mysqlEventParser.setFilterQueryDcl(instanceParameter.getFilterQueryDcl());
            mysqlEventParser.setFilterQueryDdl(instanceParameter.getFilterQueryddl());
            mysqlEventParser.setFilterQueryDml(instanceParameter.getFilterQueryDml());
            mysqlEventParser.setFilterRows(instanceParameter.getFilterRows());
            mysqlEventParser.setSupportBinlogFormats(instanceParameter.getBinlogFormat());
            mysqlEventParser.setSupportBinlogImages(instanceParameter.getBinlogImage());
            // tsdb
            if (instanceParameter.getTsdbSnapshotInterval() != null) {
                mysqlEventParser.setTsdbSnapshotInterval(instanceParameter.getTsdbSnapshotInterval());
            }
            if (instanceParameter.getTsdbSnapshotExpire() != null) {
                mysqlEventParser.setTsdbSnapshotExpire(instanceParameter.getTsdbSnapshotExpire());
            }
            boolean tsdbEnable = BooleanUtils.toBoolean(instanceParameter.getTsdbEnable());
            if (tsdbEnable) {
                mysqlEventParser.setTableMetaTSDBFactory(new DefaultTableMetaTSDBFactory() {

                    @Override
                    public void destory(String destination) {
                        TableMetaTSDBBuilder.destory(destination);
                    }

                    @Override
                    public TableMetaTSDB build(String destination, String springXml) {
                        try {
                            System.setProperty("canal.instance.tsdb.url", instanceParameter.getTsdbUrl());
                            System.setProperty("canal.instance.tsdb.dbUsername", instanceParameter.getTsdbDbUsername());
                            System.setProperty("canal.instance.tsdb.dbPassword", instanceParameter.getTsdbDbPassword());

                            return TableMetaTSDBBuilder.build(destination, "classpath:spring/tsdb/mysql-tsdb.xml");
                        } finally {
                            System.setProperty("canal.instance.tsdb.url", "");
                            System.setProperty("canal.instance.tsdb.dbUsername", "");
                            System.setProperty("canal.instance.tsdb.dbPassword", "");
                        }
                    }
                });
                mysqlEventParser.setEnableTsdb(tsdbEnable);
            }
            eventParser = mysqlEventParser;
        } else if ("binlog".equals(type)) {
            /*LocalBinlogEventParser localBinlogEventParser = new LocalBinlogEventParser();
            localBinlogEventParser.setDestination(destination);
            localBinlogEventParser.setBufferSize(instanceParameter.getNetworkReceiveBufferSize());
            localBinlogEventParser.setConnectionCharset(Charset.forName(instanceParameter.getConnectionCharset()));
            localBinlogEventParser.setConnectionCharsetNumber(parameters.getConnectionCharsetNumber());
            localBinlogEventParser.setDirectory(parameters.getLocalBinlogDirectory());
            localBinlogEventParser.setProfilingEnabled(false);
            localBinlogEventParser.setDetectingEnable(parameters.getDetectingEnable());
            localBinlogEventParser.setDetectingIntervalInSeconds(parameters.getDetectingIntervalInSeconds());
            localBinlogEventParser.setFilterTableError(parameters.getFilterTableError());
            // 数据库信息，反查表结构时需要
            if (!CollectionUtils.isEmpty(dbAddresses)) {
                localBinlogEventParser.setMasterInfo(new AuthenticationInfo(dbAddresses.get(0),
                    parameters.getDbUsername(),
                    parameters.getDbPassword(),
                    parameters.getDefaultDatabaseName()));
            }

            eventParser = localBinlogEventParser;*/
            throw new CanalException("unsupport SourcingType for " + type);
        } else if ("oracle".equals(type)) {
            throw new CanalException("unsupport SourcingType for " + type);
        } else {
            throw new CanalException("unsupport SourcingType for " + type);
        }

        // add transaction support at 2012-12-06
        if (eventParser instanceof AbstractEventParser) {
            AbstractEventParser abstractEventParser = (AbstractEventParser) eventParser;
            abstractEventParser.setTransactionSize(instanceParameter.getTransactionSize());
            abstractEventParser.setLogPositionManager(initLogPositionManager());
            abstractEventParser.setAlarmHandler(getAlarmHandler());
            abstractEventParser.setEventSink(getEventSink());

            if (StringUtils.isNotEmpty(instanceParameter.getFilterRegex())) {
                AviaterRegexFilter aviaterFilter = new AviaterRegexFilter(instanceParameter.getFilterRegex());
                abstractEventParser.setEventFilter(aviaterFilter);
            }

            // 设置黑名单
            if (StringUtils.isNotEmpty(instanceParameter.getFilterBlackRegex())) {
                AviaterRegexFilter aviaterFilter = new AviaterRegexFilter(instanceParameter.getFilterBlackRegex());
                abstractEventParser.setEventBlackFilter(aviaterFilter);
            }
        }
        if (eventParser instanceof MysqlEventParser) {
            MysqlEventParser mysqlEventParser = (MysqlEventParser) eventParser;

            // 初始化haController，绑定与eventParser的关系，haController会控制eventParser
            CanalHAController haController = initHaController();
            mysqlEventParser.setHaController(haController);
        }
        return eventParser;
    }

    protected CanalHAController initHaController() {
        logger.info("init haController begin...");
        String haMode = instanceParameter.getHaMode();
        CanalHAController haController = null;
        if ("heartbeat".equals(haMode)) {
            haController = new HeartBeatHAController();
            ((HeartBeatHAController) haController).setDetectingRetryTimes(instanceParameter.getDetectingRetryThreshold());
            ((HeartBeatHAController) haController).setSwitchEnable(instanceParameter.getDetectingHeartbeatHaEnable());
        } else {
            throw new CanalException("unsupport HAMode for " + haMode);
        }
        logger.info("init haController end! \n\t load CanalHAController:{}", haController.getClass().getName());

        return haController;
    }

    protected CanalLogPositionManager initLogPositionManager() {
        logger.info("init logPositionPersistManager begin...");
        String indexMode = instanceParameter.getPositionIndexMode();
        CanalLogPositionManager logPositionManager;
        if ("memory".equals(indexMode)) {
            logPositionManager = new MemoryLogPositionManager();
        } else if ("zookeeper".equals(indexMode)) {
            logPositionManager = new ZooKeeperLogPositionManager(getZkclientx());
        } else if ("mixed".equals(indexMode)) {
            MemoryLogPositionManager memoryLogPositionManager = new MemoryLogPositionManager();
            ZooKeeperLogPositionManager zooKeeperLogPositionManager = new ZooKeeperLogPositionManager(getZkclientx());
            logPositionManager = new PeriodMixedLogPositionManager(memoryLogPositionManager,
                zooKeeperLogPositionManager,
                1000L);
        } else if ("meta".equals(indexMode)) {
            logPositionManager = new MetaLogPositionManager(metaManager);
        } else if ("failback".equals(indexMode)) {
            MemoryLogPositionManager primary = new MemoryLogPositionManager();
            MetaLogPositionManager secondary = new MetaLogPositionManager(metaManager);

            logPositionManager = new FailbackLogPositionManager(primary, secondary);
        } else {
            throw new CanalException("unsupport indexMode for " + indexMode);
        }

        logger.info("init logPositionManager end! \n\t load CanalLogPositionManager:{}", logPositionManager.getClass()
            .getName());

        return logPositionManager;
    }

    @Override
    protected void startEventParserInternal(CanalEventParser eventParser, boolean isGroup) {
        if (eventParser instanceof AbstractEventParser) {
            AbstractEventParser abstractEventParser = (AbstractEventParser) eventParser;
            abstractEventParser.setAlarmHandler(getAlarmHandler());
        }

        super.startEventParserInternal(eventParser, isGroup);
    }

    private int getGroupSize() {
        /*List<List<DataSourcing>> groupDbAddresses = parameters.getGroupDbAddresses();
        if (!CollectionUtils.isEmpty(groupDbAddresses)) {
            return groupDbAddresses.get(0).size();
        } else {
            // 可能是基于tddl的启动
            return 1;
        }*/
        return 1;
    }

    private synchronized ZkClientx getZkclientx() {
        return zkClientx;
    }

    public void setAlarmHandler(CanalAlarmHandler alarmHandler) {
        this.alarmHandler = alarmHandler;
    }

}
