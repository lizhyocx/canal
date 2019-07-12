package com.alibaba.otter.canal.deployer;

import com.alibaba.otter.canal.common.MQProperties;
import com.alibaba.otter.canal.instance.manager.CanalConfigClient;
import com.alibaba.otter.canal.instance.manager.diamond.DiamondConfig;
import com.alibaba.otter.canal.instance.manager.diamond.DiamondPropFetcher;
import com.alibaba.otter.canal.instance.manager.model.CanalCoreParameter;
import com.alibaba.otter.canal.instance.manager.model.CanalFieldConvert;
import com.alibaba.otter.canal.kafka.CanalKafkaProducer;
import com.alibaba.otter.canal.rocketmq.CanalRocketMQProducer;
import com.alibaba.otter.canal.server.CanalMQStarter;
import com.alibaba.otter.canal.spi.CanalMQProducer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * canal独立版本启动的入口类
 *
 * @author jianghang 2012-11-6 下午05:20:49
 * @version 1.0.0
 */
public class CanalLauncher {

    private static final String APP_PROPERTY_PATH = "app.properties";
    private static final Logger logger               = LoggerFactory.getLogger(CanalLauncher.class);

    public static void main(String[] args) throws Throwable {
        try {
            logger.info("## set default uncaught exception handler");
            setGlobalUncaughtExceptionHandler();

            logger.info("## load canal configurations");

            Properties properties = new Properties();
            properties.load(CanalLauncher.class.getClassLoader().getResourceAsStream(APP_PROPERTY_PATH));
            Map<String, String> appMap = new HashMap<>((Map)properties);

            if(!CollectionUtils.isEmpty(appMap)) {
                for(Map.Entry<String, String> entry : appMap.entrySet()) {
                    String value = entry.getValue();
                    if(value != null && value.startsWith("${") && value.endsWith("}")) {
                        logger.warn("app.properties--> [{}={}] variable not illegal,use default value", entry.getKey(), value);
                        entry.setValue(null);
                    }
                }
            }

            DiamondConfig diamondConfig = CanalFieldConvert.convert(DiamondConfig.class, appMap);
            CanalConfigClient canalConfigClient = new CanalConfigClient();
            DiamondPropFetcher diamondPropFetcher = new DiamondPropFetcher(diamondConfig, canalConfigClient);
            diamondPropFetcher.start();

            CanalCoreParameter parameter = canalConfigClient.getCoreConfig();

            CanalMQProducer canalMQProducer = null;
            String serverMode = parameter.getServerMode(); //CanalController.getProperty(properties, CanalConstants.CANAL_SERVER_MODE);
            if (serverMode.equalsIgnoreCase("kafka")) {
                canalMQProducer = new CanalKafkaProducer();
            } else if (serverMode.equalsIgnoreCase("rocketmq")) {
                canalMQProducer = new CanalRocketMQProducer();
            }

            if (canalMQProducer != null) {
                // disable netty
                System.setProperty(CanalConstants.CANAL_WITHOUT_NETTY, "true");
            }

            logger.info("## start the canal server.");
            final CanalController controller = new CanalController(parameter, canalConfigClient);
            controller.start();
            logger.info("## the canal server is running now ......");
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    try {
                        logger.info("## stop the canal server");
                        controller.stop();
                    } catch (Throwable e) {
                        logger.warn("##something goes wrong when stopping canal Server:", e);
                    } finally {
                        logger.info("## canal server is down.");
                    }
                }

            });

            if (canalMQProducer != null) {
                CanalMQStarter canalMQStarter = new CanalMQStarter(canalMQProducer);
                MQProperties mqProperties = buildMQPosition(parameter);
                canalMQStarter.start(mqProperties);
                controller.setCanalMQStarter(canalMQStarter);
            }
        } catch (Throwable e) {
            logger.error("## Something goes wrong when starting up the canal Server:", e);
            System.exit(0);
        }
    }

    private static MQProperties buildMQPosition(CanalCoreParameter parameter) {
        MQProperties mqProperties = new MQProperties();
        String servers = parameter.getMqServers(); //CanalController.getProperty(properties, CanalConstants.CANAL_MQ_SERVERS);
        if (!StringUtils.isEmpty(servers)) {
            mqProperties.setServers(servers);
        }
        String retires = parameter.getMqRetries(); //CanalController.getProperty(properties, CanalConstants.CANAL_MQ_RETRIES);
        if (!StringUtils.isEmpty(retires)) {
            mqProperties.setRetries(Integer.valueOf(retires));
        }
        String batchSize = parameter.getMqBatchSize(); //CanalController.getProperty(properties, CanalConstants.CANAL_MQ_BATCHSIZE);
        if (!StringUtils.isEmpty(batchSize)) {
            mqProperties.setBatchSize(Integer.valueOf(batchSize));
        }
        String lingerMs = parameter.getMqLingerMs(); //CanalController.getProperty(properties, CanalConstants.CANAL_MQ_LINGERMS);
        if (!StringUtils.isEmpty(lingerMs)) {
            mqProperties.setLingerMs(Integer.valueOf(lingerMs));
        }
        String maxRequestSize = parameter.getMqMaxRequestSize(); //CanalController.getProperty(properties, CanalConstants.CANAL_MQ_MAXREQUESTSIZE);
        if (!StringUtils.isEmpty(maxRequestSize)) {
            mqProperties.setMaxRequestSize(Integer.valueOf(maxRequestSize));
        }
        String bufferMemory = parameter.getMqBufferMemory(); //CanalController.getProperty(properties, CanalConstants.CANAL_MQ_BUFFERMEMORY);
        if (!StringUtils.isEmpty(bufferMemory)) {
            mqProperties.setBufferMemory(Long.valueOf(bufferMemory));
        }
        String canalBatchSize = parameter.getMqCanalBatchSize(); //CanalController.getProperty(properties, CanalConstants.CANAL_MQ_CANALBATCHSIZE);
        if (!StringUtils.isEmpty(canalBatchSize)) {
            mqProperties.setCanalBatchSize(Integer.valueOf(canalBatchSize));
        }
        String canalGetTimeout = parameter.getMqCanalGetTimeout(); //CanalController.getProperty(properties, CanalConstants.CANAL_MQ_CANALGETTIMEOUT);
        if (!StringUtils.isEmpty(canalGetTimeout)) {
            mqProperties.setCanalGetTimeout(Long.valueOf(canalGetTimeout));
        }
        String flatMessage = parameter.getMqFlatMessage(); //CanalController.getProperty(properties, CanalConstants.CANAL_MQ_FLATMESSAGE);
        if (!StringUtils.isEmpty(flatMessage)) {
            mqProperties.setFlatMessage(Boolean.valueOf(flatMessage));
        }
        String compressionType = parameter.getMqCompressionType(); //CanalController.getProperty(properties, CanalConstants.CANAL_MQ_COMPRESSION_TYPE);
        if (!StringUtils.isEmpty(compressionType)) {
            mqProperties.setCompressionType(compressionType);
        }
        String acks = parameter.getMqAcks(); //CanalController.getProperty(properties, CanalConstants.CANAL_MQ_ACKS);
        if (!StringUtils.isEmpty(acks)) {
            mqProperties.setAcks(acks);
        }

        String aliyunAccessKey = parameter.getAliyunAccessKey(); //CanalController.getProperty(properties, CanalConstants.CANAL_ALIYUN_ACCESSKEY);
        if (!StringUtils.isEmpty(aliyunAccessKey)) {
            mqProperties.setAliyunAccessKey(aliyunAccessKey);
        }
        String aliyunSecretKey = parameter.getAliuyunSecretKey(); //CanalController.getProperty(properties, CanalConstants.CANAL_ALIYUN_SECRETKEY);
        if (!StringUtils.isEmpty(aliyunSecretKey)) {
            mqProperties.setAliyunSecretKey(aliyunSecretKey);
        }
        return mqProperties;
    }

    private static void setGlobalUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("UnCaughtException", e);
            }
        });
    }
}
