package com.alibaba.otter.canal.instance.manager.model;

import java.io.Serializable;

/**
 * NOTE:
 *
 * @author lizhiyang
 * @Date 2019-07-09 17:08
 */
public class CanalInstanceParameter implements Serializable {
    @CanalField("canal.instance.name")
	private String destination;
    @CanalField("canal.instance.sourcingType")
    private String sourcingType = "mysql";
    @CanalField("canal.instance.mysql.slaveId")
	private Long mysqlSlaveId;
    @CanalField("canal.instance.gtidon")
	private Boolean gtidon = false;
    @CanalField("canal.instance.rds.accesskey")
	private String rdsAccesskey;
    @CanalField("canal.instance.rds.secretkey")
	private String rdsSecretkey;
    @CanalField("canal.instance.rds.instanceId")
	private String rdsInstanceId;
    @CanalField("canal.instance.master.address")
	private String masterAddress;
    @CanalField("canal.instance.master.position")
	private String masterPosition;
    @CanalField("canal.instance.standby.address")
	private String standbyAddress;
    @CanalField("canal.instance.standby.position")
	private String standbyPosition;
    @CanalField("canal.instance.dbUsername")
	private String dbUsername;
    @CanalField("canal.instance.dbPassword")
	private String dbPassword;
    @CanalField("canal.instance.connectionCharset")
	private String connectionCharset = "UTF-8";
    @CanalField("canal.instance.defaultDatabaseName")
	private String defaultDatabaseName;
    @CanalField("canal.instance.filter.regex")
	private String filterRegex = ".*\\..*";
    @CanalField("canal.instance.filter.black.regex")
	private String filterBlackRegex;
    @CanalField("canal.mq.topic")
	private String mqTopic;
    @CanalField("canal.mq.partition")
	private Integer mqPartition;
    @CanalField("canal.mq.partitionsNum")
	private Integer mqPartitionsNum;
    @CanalField("canal.mq.partitionHash")
	private String mqPartitionHash;
    @CanalField(("canal.instance.storage.mode"))
    private String storageMode = "memory";
    @CanalField("canal.instance.memory.buffer.size")
	private Integer memoryBufferSize = 16384;
    @CanalField("canal.instance.memory.buffer.memunit")
	private Integer memoryBufferMemunit = 1024;
    @CanalField("canal.instance.memory.batch.mode")
	private String memoryBatchMode = "MEMSIZE";
    @CanalField("canal.instance.memory.rawEntry")
	private Boolean memoryRawEntry = true;
    @CanalField("canal.instance.storage.scavenge.mode")
    private String storageScavengeMode = "ack";
    @CanalField("canal.instance.storage.scavenge.schedule")
    private String storageScangengeSchedule;
    @CanalField("canal.instance.detecting.enable")
	private Boolean detectionEnable = false;
    @CanalField("canal.instance.detecting.sql")
	private String detectingSql = "select 1";
    @CanalField("canal.instance.detecting.interval.time")
	private Integer detectingInvervalTime = 3;
    @CanalField("canal.instance.ha.mode")
    private String haMode = "heartbeat";
    @CanalField("canal.instance.detecting.retry.threshold")
	private Integer detectingRetryThreshold = 3;
    @CanalField("canal.instance.detecting.heartbeatHaEnable")
	private Boolean detectingHeartbeatHaEnable = false;
    @CanalField("canal.instance.transaction.size")
	private Integer transactionSize = 1024;
    @CanalField("canal.instance.fallbackIntervalInSeconds")
	private Integer fallbackIntervalInSeconds = 60;
    @CanalField("canal.instance.network.receiveBufferSize")
	private Integer networkReceiveBufferSize = 16384;
    @CanalField("canal.instance.network.sendBufferSize")
	private Integer networkSendBufferSize = 16384;
    @CanalField("canal.instance.network.soTimeout")
	private Integer networkSoTimeout = 30;
    @CanalField("canal.instance.filter.druid.ddl")
	private Boolean filterDruidDdl = true;
    @CanalField("canal.instance.filter.query.dcl")
	private Boolean filterQueryDcl = false;
    @CanalField("canal.instance.filter.query.dml")
	private Boolean filterQueryDml = false;
    @CanalField("canal.instance.filter.query.ddl")
	private Boolean filterQueryddl = false;
    @CanalField("canal.instance.filter.table.error")
	private Boolean filterTableError = false;
    @CanalField("canal.instance.filter.rows")
	private Boolean filterRows = false;
    @CanalField("canal.instance.filter.transaction.entry")
	private Boolean filterTransactionEntry = false;
    @CanalField("canal.instance.filter.delete.entry")
    private Boolean filterDeleteEntry = false;
    @CanalField("canal.instance.binlog.format")
	private String binlogFormat;
    @CanalField("canal.instance.binlog.image")
	private String binlogImage;
    @CanalField("canal.instance.get.ddl.isolation")
	private Boolean ddlIsolation = false;
    @CanalField("canal.instance.parser.parallel")
	private Boolean parserParallel = true;
    @CanalField("canal.instance.parser.parallelThreadSize")
	private Integer parserParallelThreadSize = 16;
    @CanalField("canal.instance.parser.parallelBufferSize")
	private Integer parserParallelBufferSize = 256;
    @CanalField("canal.instance.tsdb.enable")
	private Boolean tsdbEnable = false;
    @CanalField("canal.instance.tsdb.url")
	private String tsdbUrl;
    @CanalField("canal.instance.tsdb.dbUsername")
	private String tsdbDbUsername;
    @CanalField("canal.instance.tsdb.dbPassword")
	private String tsdbDbPassword;
    @CanalField("canal.instance.tsdb.snapshot.interval")
	private Integer tsdbSnapshotInterval;
    @CanalField("canal.instance.tsdb.snapshot.expire")
	private Integer tsdbSnapshotExpire;
    @CanalField("canal.instance.meta.mode")
    private String metaMode = "mixed";
    @CanalField("canal.instance.meta.file.dataDir")
    private String metaFileDataDir;
    @CanalField("canal.instance.meta.file.flushPeriod")
    private Long metaFileFlushPeriod;
    @CanalField("canal.instance.position.index.mode")
    private String positionIndexMode = "failback";

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSourcingType() {
        return sourcingType;
    }

    public void setSourcingType(String sourcingType) {
        this.sourcingType = sourcingType;
    }

    public Long getMysqlSlaveId() {
        return mysqlSlaveId;
    }

    public void setMysqlSlaveId(Long mysqlSlaveId) {
        this.mysqlSlaveId = mysqlSlaveId;
    }

    public Boolean getGtidon() {
        return gtidon;
    }

    public void setGtidon(Boolean gtidon) {
        this.gtidon = gtidon;
    }

    public String getRdsAccesskey() {
        return rdsAccesskey;
    }

    public void setRdsAccesskey(String rdsAccesskey) {
        this.rdsAccesskey = rdsAccesskey;
    }

    public String getRdsSecretkey() {
        return rdsSecretkey;
    }

    public void setRdsSecretkey(String rdsSecretkey) {
        this.rdsSecretkey = rdsSecretkey;
    }

    public String getRdsInstanceId() {
        return rdsInstanceId;
    }

    public void setRdsInstanceId(String rdsInstanceId) {
        this.rdsInstanceId = rdsInstanceId;
    }

    public String getMasterAddress() {
        return masterAddress;
    }

    public void setMasterAddress(String masterAddress) {
        this.masterAddress = masterAddress;
    }

    public String getMasterPosition() {
        return masterPosition;
    }

    public void setMasterPosition(String masterPosition) {
        this.masterPosition = masterPosition;
    }

    public String getStandbyAddress() {
        return standbyAddress;
    }

    public void setStandbyAddress(String standbyAddress) {
        this.standbyAddress = standbyAddress;
    }

    public String getStandbyPosition() {
        return standbyPosition;
    }

    public void setStandbyPosition(String standbyPosition) {
        this.standbyPosition = standbyPosition;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getConnectionCharset() {
        return connectionCharset;
    }

    public void setConnectionCharset(String connectionCharset) {
        this.connectionCharset = connectionCharset;
    }

    public String getDefaultDatabaseName() {
        return defaultDatabaseName;
    }

    public void setDefaultDatabaseName(String defaultDatabaseName) {
        this.defaultDatabaseName = defaultDatabaseName;
    }

    public String getFilterRegex() {
        return filterRegex;
    }

    public void setFilterRegex(String filterRegex) {
        this.filterRegex = filterRegex;
    }

    public String getFilterBlackRegex() {
        return filterBlackRegex;
    }

    public void setFilterBlackRegex(String filterBlackRegex) {
        this.filterBlackRegex = filterBlackRegex;
    }

    public String getMqTopic() {
        return mqTopic;
    }

    public void setMqTopic(String mqTopic) {
        this.mqTopic = mqTopic;
    }

    public Integer getMqPartition() {
        return mqPartition;
    }

    public void setMqPartition(Integer mqPartition) {
        this.mqPartition = mqPartition;
    }

    public Integer getMqPartitionsNum() {
        return mqPartitionsNum;
    }

    public void setMqPartitionsNum(Integer mqPartitionsNum) {
        this.mqPartitionsNum = mqPartitionsNum;
    }

    public String getMqPartitionHash() {
        return mqPartitionHash;
    }

    public void setMqPartitionHash(String mqPartitionHash) {
        this.mqPartitionHash = mqPartitionHash;
    }

    public String getStorageMode() {
        return storageMode;
    }

    public void setStorageMode(String storageMode) {
        this.storageMode = storageMode;
    }

    public Integer getMemoryBufferSize() {
        return memoryBufferSize;
    }

    public void setMemoryBufferSize(Integer memoryBufferSize) {
        this.memoryBufferSize = memoryBufferSize;
    }

    public Integer getMemoryBufferMemunit() {
        return memoryBufferMemunit;
    }

    public void setMemoryBufferMemunit(Integer memoryBufferMemunit) {
        this.memoryBufferMemunit = memoryBufferMemunit;
    }

    public String getMemoryBatchMode() {
        return memoryBatchMode;
    }

    public void setMemoryBatchMode(String memoryBatchMode) {
        this.memoryBatchMode = memoryBatchMode;
    }

    public Boolean getMemoryRawEntry() {
        return memoryRawEntry;
    }

    public void setMemoryRawEntry(Boolean memoryRawEntry) {
        this.memoryRawEntry = memoryRawEntry;
    }

    public String getStorageScavengeMode() {
        return storageScavengeMode;
    }

    public void setStorageScavengeMode(String storageScavengeMode) {
        this.storageScavengeMode = storageScavengeMode;
    }

    public String getStorageScangengeSchedule() {
        return storageScangengeSchedule;
    }

    public void setStorageScangengeSchedule(String storageScangengeSchedule) {
        this.storageScangengeSchedule = storageScangengeSchedule;
    }

    public Boolean getDetectionEnable() {
        return detectionEnable;
    }

    public void setDetectionEnable(Boolean detectionEnable) {
        this.detectionEnable = detectionEnable;
    }

    public String getDetectingSql() {
        return detectingSql;
    }

    public void setDetectingSql(String detectingSql) {
        this.detectingSql = detectingSql;
    }

    public Integer getDetectingInvervalTime() {
        return detectingInvervalTime;
    }

    public void setDetectingInvervalTime(Integer detectingInvervalTime) {
        this.detectingInvervalTime = detectingInvervalTime;
    }

    public String getHaMode() {
        return haMode;
    }

    public void setHaMode(String haMode) {
        this.haMode = haMode;
    }

    public Integer getDetectingRetryThreshold() {
        return detectingRetryThreshold;
    }

    public void setDetectingRetryThreshold(Integer detectingRetryThreshold) {
        this.detectingRetryThreshold = detectingRetryThreshold;
    }

    public Boolean getDetectingHeartbeatHaEnable() {
        return detectingHeartbeatHaEnable;
    }

    public void setDetectingHeartbeatHaEnable(Boolean detectingHeartbeatHaEnable) {
        this.detectingHeartbeatHaEnable = detectingHeartbeatHaEnable;
    }

    public Integer getTransactionSize() {
        return transactionSize;
    }

    public void setTransactionSize(Integer transactionSize) {
        this.transactionSize = transactionSize;
    }

    public Integer getFallbackIntervalInSeconds() {
        return fallbackIntervalInSeconds;
    }

    public void setFallbackIntervalInSeconds(Integer fallbackIntervalInSeconds) {
        this.fallbackIntervalInSeconds = fallbackIntervalInSeconds;
    }

    public Integer getNetworkReceiveBufferSize() {
        return networkReceiveBufferSize;
    }

    public void setNetworkReceiveBufferSize(Integer networkReceiveBufferSize) {
        this.networkReceiveBufferSize = networkReceiveBufferSize;
    }

    public Integer getNetworkSendBufferSize() {
        return networkSendBufferSize;
    }

    public void setNetworkSendBufferSize(Integer networkSendBufferSize) {
        this.networkSendBufferSize = networkSendBufferSize;
    }

    public Integer getNetworkSoTimeout() {
        return networkSoTimeout;
    }

    public void setNetworkSoTimeout(Integer networkSoTimeout) {
        this.networkSoTimeout = networkSoTimeout;
    }

    public Boolean getFilterDruidDdl() {
        return filterDruidDdl;
    }

    public void setFilterDruidDdl(Boolean filterDruidDdl) {
        this.filterDruidDdl = filterDruidDdl;
    }

    public Boolean getFilterQueryDcl() {
        return filterQueryDcl;
    }

    public void setFilterQueryDcl(Boolean filterQueryDcl) {
        this.filterQueryDcl = filterQueryDcl;
    }

    public Boolean getFilterQueryDml() {
        return filterQueryDml;
    }

    public void setFilterQueryDml(Boolean filterQueryDml) {
        this.filterQueryDml = filterQueryDml;
    }

    public Boolean getFilterQueryddl() {
        return filterQueryddl;
    }

    public void setFilterQueryddl(Boolean filterQueryddl) {
        this.filterQueryddl = filterQueryddl;
    }

    public Boolean getFilterTableError() {
        return filterTableError;
    }

    public void setFilterTableError(Boolean filterTableError) {
        this.filterTableError = filterTableError;
    }

    public Boolean getFilterRows() {
        return filterRows;
    }

    public void setFilterRows(Boolean filterRows) {
        this.filterRows = filterRows;
    }

    public Boolean getFilterTransactionEntry() {
        return filterTransactionEntry;
    }

    public void setFilterTransactionEntry(Boolean filterTransactionEntry) {
        this.filterTransactionEntry = filterTransactionEntry;
    }

    public Boolean getFilterDeleteEntry() {
        return filterDeleteEntry;
    }

    public void setFilterDeleteEntry(Boolean filterDeleteEntry) {
        this.filterDeleteEntry = filterDeleteEntry;
    }

    public String getBinlogFormat() {
        return binlogFormat;
    }

    public void setBinlogFormat(String binlogFormat) {
        this.binlogFormat = binlogFormat;
    }

    public String getBinlogImage() {
        return binlogImage;
    }

    public void setBinlogImage(String binlogImage) {
        this.binlogImage = binlogImage;
    }

    public Boolean getDdlIsolation() {
        return ddlIsolation;
    }

    public void setDdlIsolation(Boolean ddlIsolation) {
        this.ddlIsolation = ddlIsolation;
    }

    public Boolean getParserParallel() {
        return parserParallel;
    }

    public void setParserParallel(Boolean parserParallel) {
        this.parserParallel = parserParallel;
    }

    public Integer getParserParallelThreadSize() {
        return parserParallelThreadSize;
    }

    public void setParserParallelThreadSize(Integer parserParallelThreadSize) {
        this.parserParallelThreadSize = parserParallelThreadSize;
    }

    public Integer getParserParallelBufferSize() {
        return parserParallelBufferSize;
    }

    public void setParserParallelBufferSize(Integer parserParallelBufferSize) {
        this.parserParallelBufferSize = parserParallelBufferSize;
    }

    public Boolean getTsdbEnable() {
        return tsdbEnable;
    }

    public void setTsdbEnable(Boolean tsdbEnable) {
        this.tsdbEnable = tsdbEnable;
    }

    public String getTsdbUrl() {
        return tsdbUrl;
    }

    public void setTsdbUrl(String tsdbUrl) {
        this.tsdbUrl = tsdbUrl;
    }

    public String getTsdbDbUsername() {
        return tsdbDbUsername;
    }

    public void setTsdbDbUsername(String tsdbDbUsername) {
        this.tsdbDbUsername = tsdbDbUsername;
    }

    public String getTsdbDbPassword() {
        return tsdbDbPassword;
    }

    public void setTsdbDbPassword(String tsdbDbPassword) {
        this.tsdbDbPassword = tsdbDbPassword;
    }

    public Integer getTsdbSnapshotInterval() {
        return tsdbSnapshotInterval;
    }

    public void setTsdbSnapshotInterval(Integer tsdbSnapshotInterval) {
        this.tsdbSnapshotInterval = tsdbSnapshotInterval;
    }

    public Integer getTsdbSnapshotExpire() {
        return tsdbSnapshotExpire;
    }

    public void setTsdbSnapshotExpire(Integer tsdbSnapshotExpire) {
        this.tsdbSnapshotExpire = tsdbSnapshotExpire;
    }

    public String getMetaMode() {
        return metaMode;
    }

    public void setMetaMode(String metaMode) {
        this.metaMode = metaMode;
    }

    public String getMetaFileDataDir() {
        return metaFileDataDir;
    }

    public void setMetaFileDataDir(String metaFileDataDir) {
        this.metaFileDataDir = metaFileDataDir;
    }

    public Long getMetaFileFlushPeriod() {
        return metaFileFlushPeriod;
    }

    public void setMetaFileFlushPeriod(Long metaFileFlushPeriod) {
        this.metaFileFlushPeriod = metaFileFlushPeriod;
    }

    public String getPositionIndexMode() {
        return positionIndexMode;
    }

    public void setPositionIndexMode(String positionIndexMode) {
        this.positionIndexMode = positionIndexMode;
    }
}
