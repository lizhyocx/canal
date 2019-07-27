package com.alibaba.otter.canal.instance.manager.diamond;

import com.alibaba.otter.canal.instance.manager.CanalConfigClient;
import com.taobao.diamond.client.DiamondConfigure;
import com.taobao.diamond.client.DiamondSubscriber;
import com.taobao.diamond.client.impl.DefaultSubscriberListener;
import com.taobao.diamond.client.impl.DiamondClientFactory;
import com.taobao.diamond.manager.ManagerListener;
import org.apache.commons.lang.StringUtils;
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

    private final String GROUP_ID = "canalx";
    private final String CORE_DATA_ID = "com.otter.canal.instance.config.DiamondPropFetcher.Core";
    private final String INSTANCE_DATA_ID = "com.otter.canal.instance.config.DiamondPropFetcher.Instance";

    public DiamondPropFetcher(DiamondConfig diamondConfig, CanalConfigClient canalConfigClient) {

        this.canalConfigClient = canalConfigClient;
        configure = new DiamondConfigure();
        configure.setAppName(diamondConfig.getAppName());
        String host = diamondConfig.getDiamondHost();
        if(StringUtils.isNotBlank(host)) {
            String[] arrHost = host.split(",");
            configure.setDomainNameList(Arrays.asList(arrHost));
            configure.setConfigServerAddress(arrHost[0]);
        }
        configure.setPort(diamondConfig.getDiamondPort());
        configure.setConfigServerPort(diamondConfig.getDiamondPort());

        diamondSubscriber.setDiamondConfigure(configure);
        diamondSubscriber.start();
        logger.info("diamondPropFetcher start.");
    }

    public void start() {
        loadCoreConfig();
        loadInstanceConfig();
    }

    private void loadCoreConfig() {
        String coreConfig = diamondSubscriber.getConfigureInfomation(CORE_DATA_ID, GROUP_ID, 15*1000);
        //主动拉取一次
        canalConfigClient.loadCoreConfig(coreConfig);
    }

    private void loadInstanceConfig() {
        diamondSubscriber.addDataId(INSTANCE_DATA_ID, GROUP_ID);
        ((DefaultSubscriberListener)diamondSubscriber.getSubscriberListener()).addManagerListener(INSTANCE_DATA_ID, GROUP_ID, new ManagerListener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                logger.info("instance receive config:{}", configInfo);
                canalConfigClient.loadInstanceConfig(configInfo);
            }
        });
    }

}
