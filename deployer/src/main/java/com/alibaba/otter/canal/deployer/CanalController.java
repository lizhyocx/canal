package com.alibaba.otter.canal.deployer;

import com.alibaba.otter.canal.common.CanalException;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.common.zookeeper.ZkClientx;
import com.alibaba.otter.canal.common.zookeeper.ZookeeperPathUtils;
import com.alibaba.otter.canal.common.zookeeper.running.ServerRunningData;
import com.alibaba.otter.canal.common.zookeeper.running.ServerRunningListener;
import com.alibaba.otter.canal.common.zookeeper.running.ServerRunningMonitor;
import com.alibaba.otter.canal.common.zookeeper.running.ServerRunningMonitors;
import com.alibaba.otter.canal.deployer.InstanceConfig.InstanceMode;
import com.alibaba.otter.canal.deployer.monitor.InstanceAction;
import com.alibaba.otter.canal.deployer.monitor.InstanceConfigMonitor;
import com.alibaba.otter.canal.deployer.monitor.ManagerInstanceConfigMonitor;
import com.alibaba.otter.canal.deployer.monitor.SpringInstanceConfigMonitor;
import com.alibaba.otter.canal.instance.core.CanalInstance;
import com.alibaba.otter.canal.instance.core.CanalInstanceGenerator;
import com.alibaba.otter.canal.instance.manager.CanalConfigClient;
import com.alibaba.otter.canal.instance.manager.ManagerCanalInstanceGenerator;
import com.alibaba.otter.canal.instance.manager.model.CanalCoreParameter;
import com.alibaba.otter.canal.instance.spring.SpringCanalInstanceGenerator;
import com.alibaba.otter.canal.parse.CanalEventParser;
import com.alibaba.otter.canal.server.CanalMQStarter;
import com.alibaba.otter.canal.server.embedded.CanalServerWithEmbedded;
import com.alibaba.otter.canal.server.exception.CanalServerException;
import com.alibaba.otter.canal.server.netty.CanalServerWithNetty;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.common.collect.MigrateMap;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;
import java.util.Properties;

/**
 * canal调度控制器
 *
 * @author jianghang 2012-11-8 下午12:03:11
 * @version 1.0.0
 */
public class CanalController {

    private static final Logger                      logger   = LoggerFactory.getLogger(CanalController.class);
    private Long                                     cid;
    private String                                   ip;
    private int                                      port;
    // 默认使用spring的方式载入
    private Map<String, InstanceConfig>              instanceConfigs;
    private InstanceConfig                           globalInstanceConfig;
    private CanalConfigClient                        canalConfigClient;
    // 监听instance config的变化
    private boolean                                  autoScan = true;
    private InstanceAction                           defaultAction;
    private Map<InstanceMode, InstanceConfigMonitor> instanceConfigMonitors;
    private CanalServerWithEmbedded                  embededCanalServer;
    private CanalServerWithNetty                     canalServer;

    private CanalInstanceGenerator                   instanceGenerator;
    private ZkClientx                                zkclientx;

    private CanalMQStarter                           canalMQStarter;

    public CanalController(){
        this(new CanalCoreParameter(), new CanalConfigClient());
    }

    public CanalController(final CanalCoreParameter parameter, final CanalConfigClient canalConfigClient){

        this.canalConfigClient = canalConfigClient;
        // 初始化全局参数设置
        globalInstanceConfig = initGlobalConfig(parameter);
        instanceConfigs = new MapMaker().makeMap();
        // 初始化instance config
        initInstanceConfig(parameter);

        // init socketChannel
        /*String socketChannel = getProperty(properties, CanalConstants.CANAL_SOCKETCHANNEL);
        if (StringUtils.isNotEmpty(socketChannel)) {
            System.setProperty(CanalConstants.CANAL_SOCKETCHANNEL, socketChannel);
        }*/

        /*// 兼容1.1.0版本的ak/sk参数名
        String accesskey = getProperty(properties, "canal.instance.rds.accesskey");
        String secretkey = getProperty(properties, "canal.instance.rds.secretkey");
        if (StringUtils.isNotEmpty(accesskey)) {
            System.setProperty(CanalConstants.CANAL_ALIYUN_ACCESSKEY, accesskey);
        }
        if (StringUtils.isNotEmpty(secretkey)) {
            System.setProperty(CanalConstants.CANAL_ALIYUN_SECRETKEY, secretkey);
        }*/

        // 准备canal server
        //cid = Long.valueOf(getProperty(properties, CanalConstants.CANAL_ID));
        cid = parameter.getId();
        ip = parameter.getIp(); //getProperty(properties, CanalConstants.CANAL_IP);
        port = parameter.getPort(); //Integer.valueOf(getProperty(properties, CanalConstants.CANAL_PORT));
        embededCanalServer = CanalServerWithEmbedded.instance();
        embededCanalServer.setCanalInstanceGenerator(instanceGenerator);// 设置自定义的instanceGenerator
        try {
            int metricsPort = parameter.getMetricsPullPort(); //Integer.valueOf(getProperty(properties, CanalConstants.CANAL_METRICS_PULL_PORT));
            embededCanalServer.setMetricsPort(metricsPort);
        } catch (NumberFormatException e) {
            logger.info("No valid metrics server port found, use default 11112.");
            embededCanalServer.setMetricsPort(11112);
        }

        Boolean canalWithoutNetty = parameter.getWithoutNetty(); //getProperty(properties, CanalConstants.CANAL_WITHOUT_NETTY);
        if (Boolean.FALSE.equals(canalWithoutNetty)) {
            canalServer = CanalServerWithNetty.instance();
            canalServer.setIp(ip);
            canalServer.setPort(port);
        }

        // 处理下ip为空，默认使用hostIp暴露到zk中
        if (StringUtils.isEmpty(ip)) {
            ip = AddressUtils.getHostIp();
        }
        final String zkServers = parameter.getZkServers(); //getProperty(properties, CanalConstants.CANAL_ZKSERVERS);
        if (StringUtils.isNotEmpty(zkServers)) {
            zkclientx = ZkClientx.getZkClient(zkServers);
            // 初始化系统目录
            zkclientx.createPersistent(ZookeeperPathUtils.DESTINATION_ROOT_NODE, true);
            zkclientx.createPersistent(ZookeeperPathUtils.CANAL_CLUSTER_ROOT_NODE, true);
        }

        final ServerRunningData serverData = new ServerRunningData(cid, ip + ":" + port);
        ServerRunningMonitors.setServerData(serverData);
        ServerRunningMonitors
            .setRunningMonitors(MigrateMap.makeComputingMap(new Function<String, ServerRunningMonitor>() {

            @Override
            public ServerRunningMonitor apply(final String destination) {
                ServerRunningMonitor runningMonitor = new ServerRunningMonitor(serverData);
                runningMonitor.setDestination(destination);
                runningMonitor.setListener(new ServerRunningListener() {

                    @Override
                    public void processActiveEnter() {
                        try {
                            MDC.put(CanalConstants.MDC_DESTINATION, String.valueOf(destination));
                            embededCanalServer.start(destination);
                        } finally {
                            MDC.remove(CanalConstants.MDC_DESTINATION);
                        }
                    }

                    @Override
                    public void processActiveExit() {
                        try {
                            MDC.put(CanalConstants.MDC_DESTINATION, String.valueOf(destination));
                            embededCanalServer.stop(destination);
                        } finally {
                            MDC.remove(CanalConstants.MDC_DESTINATION);
                        }
                    }

                    @Override
                    public void processStart() {
                        try {
                            if (zkclientx != null) {
                                final String path = ZookeeperPathUtils.getDestinationClusterNode(destination, ip + ":"
                                                                                                              + port);
                                initCid(path);
                                zkclientx.subscribeStateChanges(new IZkStateListener() {

                                    @Override
                                    public void handleStateChanged(KeeperState state) throws Exception {

                                        }

                                    @Override
                                    public void handleNewSession() throws Exception {
                                        initCid(path);
                                    }

                                        @Override
                                        public void handleSessionEstablishmentError(Throwable error) throws Exception {
                                            logger.error("failed to connect to zookeeper", error);
                                        }
                                    });
                                }
                            } finally {
                                MDC.remove(CanalConstants.MDC_DESTINATION);
                            }
                        }


                    @Override
                    public void processStop() {
                        try {
                            MDC.put(CanalConstants.MDC_DESTINATION, String.valueOf(destination));
                            if (zkclientx != null) {
                                final String path = ZookeeperPathUtils.getDestinationClusterNode(destination, ip + ":"
                                        + port);
                                releaseCid(path);
                            }
                        } finally {
                            MDC.remove(CanalConstants.MDC_DESTINATION);
                        }
                    }

                    });
                    if (zkclientx != null) {
                        runningMonitor.setZkClient(zkclientx);
                    }
                    // 触发创建一下cid节点
                    runningMonitor.init();
                    return runningMonitor;
                }
            }));

        // 初始化monitor机制
        autoScan = parameter.getAutoScan(); //BooleanUtils.toBoolean(getProperty(properties, CanalConstants.CANAL_AUTO_SCAN));
        if (autoScan) {
            defaultAction = new InstanceAction() {

                @Override
                public void start(String destination) {
                    InstanceConfig config = instanceConfigs.get(destination);
                    if (config == null) {
                        // 重新读取一下instance config
                        config = parseInstanceConfig(parameter, destination);
                        instanceConfigs.put(destination, config);
                    }

                    if (!embededCanalServer.isStart(destination)) {
                        // HA机制启动
                        ServerRunningMonitor runningMonitor = ServerRunningMonitors.getRunningMonitor(destination);
                        if (!config.getLazy() && !runningMonitor.isStart()) {
                            runningMonitor.start();
                            if (canalMQStarter != null) {
                                canalMQStarter.startDestination(destination);
                            }
                        }
                    }
                }

                @Override
                public void stop(String destination) {
                    // 此处的stop，代表强制退出，非HA机制，所以需要退出HA的monitor和配置信息
                    InstanceConfig config = instanceConfigs.remove(destination);
                    if (config != null) {
                        if (canalMQStarter != null) {
                            canalMQStarter.stopDestination(destination);
                        }
                        embededCanalServer.stop(destination);
                        ServerRunningMonitor runningMonitor = ServerRunningMonitors.getRunningMonitor(destination);
                        if (runningMonitor.isStart()) {
                            runningMonitor.stop();
                        }
                    }
                }

                @Override
                public void reload(String destination) {
                    // 目前任何配置变化，直接重启，简单处理
                    stop(destination);
                    start(destination);
                }
            };

            instanceConfigMonitors = MigrateMap.makeComputingMap(new Function<InstanceMode, InstanceConfigMonitor>() {

                @Override
                public InstanceConfigMonitor apply(InstanceMode mode) {
                    int scanInterval = parameter.getAutoScanInterval(); //Integer.valueOf(getProperty(properties, CanalConstants.CANAL_AUTO_SCAN_INTERVAL));

                    if (mode.isSpring()) {
                        SpringInstanceConfigMonitor monitor = new SpringInstanceConfigMonitor();
                        monitor.setScanIntervalInSecond(scanInterval);
                        monitor.setDefaultAction(defaultAction);
                        // 设置conf目录，默认是user.dir + conf目录组成
                        String rootDir = parameter.getConfDir(); //getProperty(properties, CanalConstants.CANAL_CONF_DIR);
                        if (StringUtils.isEmpty(rootDir)) {
                            rootDir = "../conf";
                        }

                        if (StringUtils.equals("otter-canal", System.getProperty("appName"))) {
                            monitor.setRootConf(rootDir);
                        } else {
                            // eclipse debug模式
                            monitor.setRootConf("src/main/resources/");
                        }
                        return monitor;
                    } else if (mode.isManager()) {
                        ManagerInstanceConfigMonitor monitor =  new ManagerInstanceConfigMonitor();
                        monitor.setCanalConfigClient(canalConfigClient);
                        monitor.setDefaultAction(defaultAction);
                      return monitor;
                    } else {
                        throw new UnsupportedOperationException("unknow mode :" + mode + " for monitor");
                    }
                }
            });
        }
    }

    private InstanceConfig initGlobalConfig(CanalCoreParameter parameter) {
        InstanceConfig globalConfig = new InstanceConfig();
        String modeStr = parameter.getInstanceGlobalMode(); //getProperty(properties, CanalConstants.getInstanceModeKey(CanalConstants.GLOBAL_NAME));
        if (StringUtils.isNotEmpty(modeStr)) {
            globalConfig.setMode(InstanceMode.valueOf(StringUtils.upperCase(modeStr)));
        }

        Boolean lazy = parameter.getInstanceGlobalLazy(); //getProperty(properties, CanalConstants.getInstancLazyKey(CanalConstants.GLOBAL_NAME));
        globalConfig.setLazy(lazy);

        /*String managerAddress = getProperty(properties,
            CanalConstants.getInstanceManagerAddressKey(CanalConstants.GLOBAL_NAME));
        if (StringUtils.isNotEmpty(managerAddress)) {
            globalConfig.setManagerAddress(managerAddress);
        }*/

        String springXml = parameter.getInstanceGlobalSpringXml(); //getProperty(properties, CanalConstants.getInstancSpringXmlKey(CanalConstants.GLOBAL_NAME));
        if (StringUtils.isNotEmpty(springXml)) {
            globalConfig.setSpringXml(springXml);
        }

        instanceGenerator = new CanalInstanceGenerator() {

            @Override
            public CanalInstance generate(String destination) {
                InstanceConfig config = instanceConfigs.get(destination);
                if (config == null) {
                    throw new CanalServerException("can't find destination:{}");
                }

                if (config.getMode().isManager()) {
                    ManagerCanalInstanceGenerator instanceGenerator = new ManagerCanalInstanceGenerator();
                    instanceGenerator.setCanalConfigClient(canalConfigClient);
                    return instanceGenerator.generate(destination);
                } else if (config.getMode().isSpring()) {
                    SpringCanalInstanceGenerator instanceGenerator = new SpringCanalInstanceGenerator();
                    synchronized (CanalEventParser.class) {
                        try {
                            // 设置当前正在加载的通道，加载spring查找文件时会用到该变量
                            System.setProperty(CanalConstants.CANAL_DESTINATION_PROPERTY, destination);
                            instanceGenerator.setBeanFactory(getBeanFactory(config.getSpringXml()));
                            return instanceGenerator.generate(destination);
                        } catch (Throwable e) {
                            logger.error("generator instance failed.", e);
                            throw new CanalException(e);
                        } finally {
                            System.setProperty(CanalConstants.CANAL_DESTINATION_PROPERTY, "");
                        }
                    }
                } else {
                    throw new UnsupportedOperationException("unknow mode :" + config.getMode());
                }

            }

        };

        return globalConfig;
    }

    private BeanFactory getBeanFactory(String springXml) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(springXml);
        return applicationContext;
    }

    private void initInstanceConfig(CanalCoreParameter parameter) {
        String destinationStr = parameter.getDestinations(); //getProperty(properties, CanalConstants.CANAL_DESTINATIONS);
        if(StringUtils.isBlank(destinationStr)) {
            return;
        }
        String[] destinations = StringUtils.split(destinationStr, CanalConstants.CANAL_DESTINATION_SPLIT);

        for (String destination : destinations) {
            InstanceConfig config = parseInstanceConfig(parameter, destination);
            InstanceConfig oldConfig = instanceConfigs.put(destination, config);

            if (oldConfig != null) {
                logger
                        .warn("destination:{} old config:{} has replace by new config:{}", destination, oldConfig, config);
            }
        }
    }

    private InstanceConfig parseInstanceConfig(CanalCoreParameter parameter, String destination) {
        InstanceConfig config = new InstanceConfig(globalInstanceConfig);
        String modeStr = parameter.getInstanceGlobalMode(); //getProperty(properties, CanalConstants.getInstanceModeKey(destination));
        if (!StringUtils.isEmpty(modeStr)) {
            config.setMode(InstanceMode.valueOf(StringUtils.upperCase(modeStr)));
        }

        Boolean lazy = parameter.getInstanceGlobalLazy(); //getProperty(properties, CanalConstants.getInstancLazyKey(destination));
        config.setLazy(lazy);

        if (config.getMode().isManager()) {
            /*String managerAddress = getProperty(properties, CanalConstants.getInstanceManagerAddressKey(destination));
            if (StringUtils.isNotEmpty(managerAddress)) {
                config.setManagerAddress(managerAddress);
            }*/
        } else if (config.getMode().isSpring()) {
            String springXml = parameter.getInstanceGlobalSpringXml(); //getProperty(properties, CanalConstants.getInstancSpringXmlKey(destination));
            if (StringUtils.isNotEmpty(springXml)) {
                config.setSpringXml(springXml);
            }
        }

        return config;
    }

    public static String getProperty(Properties properties, String key) {
        key = StringUtils.trim(key);
        String value = System.getProperty(key);

        if (value == null) {
            value = System.getenv(key);
        }

        if (value == null) {
            value = properties.getProperty(key);
        }

        return StringUtils.trim(value);
    }

    public void start() throws Throwable {
        logger.info("## start the canal server[{}:{}]", ip, port);
        // 创建整个canal的工作节点
        final String path = ZookeeperPathUtils.getCanalClusterNode(ip + ":" + port);
        initCid(path);
        if (zkclientx != null) {
            this.zkclientx.subscribeStateChanges(new IZkStateListener() {

                @Override
                public void handleStateChanged(KeeperState state) throws Exception {

                }

                @Override
                public void handleNewSession() throws Exception {
                    initCid(path);
                }

                @Override
                public void handleSessionEstablishmentError(Throwable error) throws Exception {
                    logger.error("failed to connect to zookeeper", error);
                }
            });
        }
        // 优先启动embeded服务
        embededCanalServer.start();
        // 尝试启动一下非lazy状态的通道
        for (Map.Entry<String, InstanceConfig> entry : instanceConfigs.entrySet()) {
            final String destination = entry.getKey();
            InstanceConfig config = entry.getValue();
            // 创建destination的工作节点
            if (!embededCanalServer.isStart(destination)) {
                // HA机制启动
                ServerRunningMonitor runningMonitor = ServerRunningMonitors.getRunningMonitor(destination);
                if (!config.getLazy() && !runningMonitor.isStart()) {
                    runningMonitor.start();
                }
            }

            if (autoScan) {
                instanceConfigMonitors.get(config.getMode()).register(destination, defaultAction);
            }
        }

        if (autoScan) {
            instanceConfigMonitors.get(globalInstanceConfig.getMode()).start();
            for (InstanceConfigMonitor monitor : instanceConfigMonitors.values()) {
                if (!monitor.isStart()) {
                    monitor.start();
                }
            }
        }

        // 启动网络接口
        if (canalServer != null) {
            canalServer.start();
        }
    }

    public void stop() throws Throwable {

        if (canalServer != null) {
            canalServer.stop();
        }

        if (autoScan) {
            for (InstanceConfigMonitor monitor : instanceConfigMonitors.values()) {
                if (monitor.isStart()) {
                    monitor.stop();
                }
            }
        }

        for (ServerRunningMonitor runningMonitor : ServerRunningMonitors.getRunningMonitors().values()) {
            if (runningMonitor.isStart()) {
                runningMonitor.stop();
            }
        }

        // 释放canal的工作节点
        releaseCid(ZookeeperPathUtils.getCanalClusterNode(ip + ":" + port));
        logger.info("## stop the canal server[{}:{}]", ip, port);

        if (zkclientx != null) {
            zkclientx.close();
        }

        //关闭时清理缓存
        if (instanceConfigs != null) {
            instanceConfigs.clear();
        }
        if (instanceConfigMonitors != null) {
            instanceConfigMonitors.clear();
        }

        ZkClientx.clearClients();
    }

    private void initCid(String path) {
        // logger.info("## init the canalId = {}", cid);
        // 初始化系统目录
        if (zkclientx != null) {
            try {
                zkclientx.createEphemeral(path);
            } catch (ZkNoNodeException e) {
                // 如果父目录不存在，则创建
                String parentDir = path.substring(0, path.lastIndexOf('/'));
                zkclientx.createPersistent(parentDir, true);
                zkclientx.createEphemeral(path);
            } catch (ZkNodeExistsException e) {
                // ignore
                // 因为第一次启动时创建了cid,但在stop/start的时可能会关闭和新建,允许出现NodeExists问题s
            }

        }
    }

    private void releaseCid(String path) {
        // logger.info("## release the canalId = {}", cid);
        // 初始化系统目录
        if (zkclientx != null) {
            zkclientx.delete(path);
        }
    }

    public CanalMQStarter getCanalMQStarter() {
        return canalMQStarter;
    }

    public void setCanalMQStarter(CanalMQStarter canalMQStarter) {
        this.canalMQStarter = canalMQStarter;
    }
}
