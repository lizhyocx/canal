package com.alibaba.otter.canal.instance.manager.diamond;

import com.alibaba.otter.canal.instance.manager.CanalConfigClient;
import com.taobao.diamond.client.DiamondConfigure;
import com.taobao.diamond.client.DiamondSubscriber;
import com.taobao.diamond.client.impl.DefaultSubscriberListener;
import com.taobao.diamond.client.impl.DiamondClientFactory;
import com.taobao.diamond.manager.ManagerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.Executor;

/**
 * NOTE:
 *
 * @author lizhiyang
 * @Date 2019-07-08 11:17
 */
public class DiamondPropFetcher {
    private static final Logger logger   = LoggerFactory.getLogger(DiamondPropFetcher.class);

    private DiamondSubscriber diamondSubscriber = DiamondClientFactory.getSingletonDiamondSubscriber();
    private DiamondConfigure configure;

    private CanalConfigClient canalConfigClient;

    private final String GROUP_ID = "onebcc-canal";
    private final String DATA_ID = "com.yunzong.canal.instance.config.DiamondPropFetcher";

    public DiamondPropFetcher(CanalConfigClient canalConfigClient) {

        this.canalConfigClient = canalConfigClient;
        configure = new DiamondConfigure();
        configure.setDomainNameList(Arrays.asList("ds.diamond.yunzong"));
        configure.setPort(10510);
        configure.setConfigServerAddress("ds.diamond.yunzong");
        configure.setConfigServerPort(10510);
        diamondSubscriber.setDiamondConfigure(configure);
        diamondSubscriber.addDataId(DATA_ID, GROUP_ID);
        diamondSubscriber.start();
        logger.info("diamondPropFetcher start.");
    }

    public void start() {
        loadCoreConfig();
        loadInstanceConfig();
    }

    private void loadCoreConfig() {
        //主动拉取一次
        canalConfigClient.loadCoreConfig("{\"canal.id\":\"1\",\"canal.ip\":\"\",\"canal.port\":\"11111\",\"canal.metrics.pull.port\":\"11113\",\"canal.zkServers\":\"10.100.12.68:2181\",\"canal.zookeeper.flush.period\":\"1000\",\"canal.withoutNetty\":\"false\",\"canal.serverMode\":\"tcp\",\"canal.file.data.dir\":\"\",\"canal.file.flush.period\":\"1000\",\"canal.aliyun.accesskey\":\"\",\"canal.aliyun.secretkey\":\"\",\"canal.destinations\":\"\",\"canal.conf.dir\":\"./deployer/src/main/resources\",\"canal.auto.scan\":\"true\",\"canal.auto.scan.interval\":\"5\",\"canal.instance.global.mode\":\"manager\",\"canal.instance.global.lazy\":\"false\",\"canal.mq.servers\":\"127.0.0.1:6667\",\"canal.mq.retries\":\"0\",\"canal.mq.batchSize\":\"16384\",\"canal.mq.maxRequestSize\":\"1048576\",\"canal.mq.lingerMs\":\"1\",\"canal.mq.bufferMemory\":\"33554432\",\"canal.mq.canalBatchSize\":\"50\",\"canal.mq.canalGetTimeout\":\"100\",\"canal.mq.flatMessage\":\"true\",\"canal.mq.compressionType\":\"none\",\"canal.mq.acks\":\"all\"}");
    }

    private void loadInstanceConfig() {

        ((DefaultSubscriberListener)diamondSubscriber.getSubscriberListener()).addManagerListener(DATA_ID, GROUP_ID, new ManagerListener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                logger.info(DATA_ID + " receive config:{}", configInfo);
                canalConfigClient.loadInstanceConfig(configInfo);
            }
        });
    }

}
