package com.alibaba.otter.canal.deployer;

import com.alibaba.otter.canal.instance.manager.CanalConfigClient;
import com.alibaba.otter.canal.instance.manager.diamond.DiamondConfig;
import com.alibaba.otter.canal.instance.manager.diamond.DiamondPropFetcher;
import com.alibaba.otter.canal.instance.manager.model.CanalFieldConvert;
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
    private static final Logger    logger               = LoggerFactory.getLogger(CanalLauncher.class);
    public static volatile boolean running              = false;

    public static void main(String[] args) {
        try {
            running = true;
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


            /*remoteConfigLoader = RemoteConfigLoaderFactory.getRemoteConfigLoader(properties);
            if (remoteConfigLoader != null) {
                // 加载远程canal.properties
                Properties remoteConfig = remoteConfigLoader.loadRemoteConfig();
                // 加载remote instance配置
                remoteConfigLoader.loadRemoteInstanceConfigs();
                if (remoteConfig != null) {
                    properties = remoteConfig;
                } else {
                    remoteConfigLoader = null;
                }
            }*/

            final CanalStater canalStater = new CanalStater();
            canalStater.start(canalConfigClient);

            /*if (remoteConfigLoader != null) {
                remoteConfigLoader.startMonitor(new RemoteCanalConfigMonitor() {

                    @Override
                    public void onChange(Properties properties) {
                        try {
                            // 远程配置canal.properties修改重新加载整个应用
                            canalStater.destroy();
                            canalStater.start(properties);
                        } catch (Throwable throwable) {
                            logger.error(throwable.getMessage(), throwable);
                        }
                    }
                });
            }*/

            while (running) {
                Thread.sleep(1000);
            }

            /*if (remoteConfigLoader != null) {
                remoteConfigLoader.destroy();
            }*/
        } catch (Throwable e) {
            logger.error("## Something goes wrong when starting up the canal Server:", e);
        }
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
