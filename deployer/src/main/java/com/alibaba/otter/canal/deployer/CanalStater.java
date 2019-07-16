package com.alibaba.otter.canal.deployer;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.otter.canal.common.MQProperties;
import com.alibaba.otter.canal.common.utils.JsonUtils;
import com.alibaba.otter.canal.instance.manager.CanalConfigClient;
import com.alibaba.otter.canal.instance.manager.model.CanalCoreParameter;
import com.alibaba.otter.canal.kafka.CanalKafkaProducer;
import com.alibaba.otter.canal.rocketmq.CanalRocketMQProducer;
import com.alibaba.otter.canal.server.CanalMQStarter;
import com.alibaba.otter.canal.spi.CanalMQProducer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Canal server 启动类
 *
 * @author rewerma 2018-12-30 下午05:12:16
 * @version 1.0.1
 */
public class CanalStater {

    private static final Logger logger          = LoggerFactory.getLogger(CanalStater.class);

    private CanalController     controller      = null;
    private CanalMQProducer     canalMQProducer = null;
    private Thread              shutdownThread  = null;
    private CanalMQStarter      canalMQStarter  = null;

    /**
     * 启动方法
     *
     * @param properties canal.properties 配置
     * @throws Throwable
     */
    synchronized void start(CanalConfigClient canalConfigClient) throws Throwable {
        CanalCoreParameter parameter = canalConfigClient.getCoreConfig();
        String serverMode = parameter.getServerMode();
        if (serverMode.equalsIgnoreCase("kafka")) {
            canalMQProducer = new CanalKafkaProducer();
        } else if (serverMode.equalsIgnoreCase("rocketmq")) {
            canalMQProducer = new CanalRocketMQProducer();
        }

        if (canalMQProducer != null) {
            // disable netty
            System.setProperty(CanalConstants.CANAL_WITHOUT_NETTY, "true");
            // 设置为raw避免ByteString->Entry的二次解析
            System.setProperty("canal.instance.memory.rawEntry", "false");

            /*boolean autoScan = parameter.getAutoScan();
            if ("true".equals(autoScan)) {
                String rootDir = parameter.getConfDir();
                if (StringUtils.isEmpty(rootDir)) {
                    rootDir = "../conf";
                }
                File rootdir = new File(rootDir);
                if (rootdir.exists()) {
                    File[] instanceDirs = rootdir.listFiles(new FileFilter() {

                        @Override
                        public boolean accept(File pathname) {
                            String filename = pathname.getName();
                            return pathname.isDirectory() && !"spring".equalsIgnoreCase(filename);
                        }
                    });
                    if (instanceDirs != null && instanceDirs.length > 0) {
                        List<String> instances = Lists.transform(Arrays.asList(instanceDirs),
                            new Function<File, String>() {

                                @Override
                                public String apply(File instanceDir) {
                                    return instanceDir.getName();
                                }
                            });
                        System.setProperty(CanalConstants.CANAL_DESTINATIONS, Joiner.on(",").join(instances));
                    }
                }
            } else {
                String destinations = parameter.getDestinations();
                System.setProperty(CanalConstants.CANAL_DESTINATIONS, destinations);
            }*/
        }

        logger.info("## start the canal server.");
        controller = new CanalController(parameter, canalConfigClient);
        controller.start();
        logger.info("## the canal server is running now ......");
        shutdownThread = new Thread() {

            public void run() {
                try {
                    logger.info("## stop the canal server");
                    controller.stop();
                    CanalLauncher.running = false;
                } catch (Throwable e) {
                    logger.warn("##something goes wrong when stopping canal Server:", e);
                } finally {
                    logger.info("## canal server is down.");
                }
            }

        };
        Runtime.getRuntime().addShutdownHook(shutdownThread);

        if (canalMQProducer != null) {
            canalMQStarter = new CanalMQStarter(canalMQProducer);
            MQProperties mqProperties = buildMQProperties(parameter);
            canalMQStarter.start(mqProperties);
            controller.setCanalMQStarter(canalMQStarter);
        }
    }

    /**
     * 销毁方法，远程配置变更时调用
     *
     * @throws Throwable
     */
    synchronized void destroy() throws Throwable {
        if (controller != null) {
            controller.stop();
            controller = null;
        }
        if (shutdownThread != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownThread);
            shutdownThread = null;
        }
        if (canalMQProducer != null && canalMQStarter != null) {
            canalMQStarter.destroy();
            canalMQStarter = null;
            canalMQProducer = null;
        }
    }

    /**
     * 构造MQ对应的配置
     *
     * @param properties canal.properties 配置
     * @return
     */
    private static MQProperties buildMQProperties(CanalCoreParameter parameter) {
        MQProperties mqProperties = new MQProperties();
        String servers = parameter.getMqServers();
        if (!StringUtils.isEmpty(servers)) {
            mqProperties.setServers(servers);
        }
        String retires = parameter.getMqRetries();
        if (!StringUtils.isEmpty(retires)) {
            mqProperties.setRetries(Integer.valueOf(retires));
        }
        String batchSize = parameter.getMqBatchSize();
        if (!StringUtils.isEmpty(batchSize)) {
            mqProperties.setBatchSize(Integer.valueOf(batchSize));
        }
        String lingerMs = parameter.getMqLingerMs();
        if (!StringUtils.isEmpty(lingerMs)) {
            mqProperties.setLingerMs(Integer.valueOf(lingerMs));
        }
        String maxRequestSize = parameter.getMqMaxRequestSize();
        if (!StringUtils.isEmpty(maxRequestSize)) {
            mqProperties.setMaxRequestSize(Integer.valueOf(maxRequestSize));
        }
        String bufferMemory = parameter.getMqBufferMemory();
        if (!StringUtils.isEmpty(bufferMemory)) {
            mqProperties.setBufferMemory(Long.valueOf(bufferMemory));
        }
        String canalBatchSize = parameter.getMqCanalBatchSize();
        if (!StringUtils.isEmpty(canalBatchSize)) {
            mqProperties.setCanalBatchSize(Integer.valueOf(canalBatchSize));
        }
        String canalGetTimeout = parameter.getMqCanalGetTimeout();
        if (!StringUtils.isEmpty(canalGetTimeout)) {
            mqProperties.setCanalGetTimeout(Long.valueOf(canalGetTimeout));
        }
        String flatMessage = parameter.getMqFlatMessage();
        if (!StringUtils.isEmpty(flatMessage)) {
            mqProperties.setFlatMessage(Boolean.valueOf(flatMessage));
        }
        String compressionType = parameter.getMqCompressionType();
        if (!StringUtils.isEmpty(compressionType)) {
            mqProperties.setCompressionType(compressionType);
        }
        String acks = parameter.getMqAcks();
        if (!StringUtils.isEmpty(acks)) {
            mqProperties.setAcks(acks);
        }
        String aliyunAccessKey = parameter.getAliyunAccessKey();
        if (!StringUtils.isEmpty(aliyunAccessKey)) {
            mqProperties.setAliyunAccessKey(aliyunAccessKey);
        }
        String aliyunSecretKey = parameter.getAliyunSecretKey();
        if (!StringUtils.isEmpty(aliyunSecretKey)) {
            mqProperties.setAliyunSecretKey(aliyunSecretKey);
        }
        String transaction = parameter.getMqTransaction();
        if (!StringUtils.isEmpty(transaction)) {
            mqProperties.setTransaction(Boolean.valueOf(transaction));
        }

        String producerGroup = parameter.getMqProducerGroup();
        if (!StringUtils.isEmpty(producerGroup)) {
            mqProperties.setProducerGroup(producerGroup);
        }

        String properties = parameter.getMqProperties();
        if(!StringUtils.isEmpty(properties)) {
            Map<String, String> map = JsonUtils.unmarshalFromString(properties, new TypeReference<Map<String, String>>(){});
            mqProperties.getProperties().putAll(map);
        }

        return mqProperties;
    }
}
