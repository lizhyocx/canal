package com.alibaba.otter.canal.deployer.monitor;

import com.alibaba.otter.canal.common.AbstractCanalLifeCycle;
import com.alibaba.otter.canal.common.CanalLifeCycle;
import com.alibaba.otter.canal.common.utils.NamedThreadFactory;
import com.alibaba.otter.canal.instance.manager.CanalConfigClient;
import com.alibaba.otter.canal.instance.manager.model.CanalInstanceParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jianghang 2013-2-18 下午03:19:06
 * @version 1.0.1
 */
public class ManagerInstanceConfigMonitor extends AbstractCanalLifeCycle implements InstanceConfigMonitor, CanalLifeCycle {
    private static final Logger logger               = LoggerFactory.getLogger(ManagerInstanceConfigMonitor.class);


    private InstanceAction defaultAction;

    private long                             scanIntervalInSecond = 5;

    private ScheduledExecutorService executor             =  new ScheduledThreadPoolExecutor(1,
            new NamedThreadFactory("canal-instance-manager-scan"));

    private Map<String, InstanceAction> actions = new HashMap<>();

    private Map<String, CanalInstanceParameter> lastCanalMap = new HashMap<>();
    private CanalConfigClient canalConfigClient;

    @Override
    public void start() {
        super.start();

        executor.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                try {
                    scan();

                } catch (Throwable e) {
                    logger.error("scan failed", e);
                }
            }

        }, 0, scanIntervalInSecond, TimeUnit.SECONDS);
    }

    @Override
    public void register(String destination, InstanceAction action) {
        if (action != null) {
            actions.put(destination, action);
        } else {
            actions.put(destination, defaultAction);
        }
    }

    @Override
    public void unregister(String destination) {
        actions.remove(destination);
    }

    public void judgeChange(Map<String, CanalInstanceParameter> canalMap) {
        Iterator<Map.Entry<String, CanalInstanceParameter>> iter = canalMap.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String, CanalInstanceParameter> entry = iter.next();
            String destination = entry.getKey();
            CanalInstanceParameter canal = entry.getValue();

            if(actions.containsKey(destination)) {
                CanalInstanceParameter oldCanal = lastCanalMap.get(destination);
                if(!canal.equals(oldCanal)) {
                    actions.get(destination).reload(destination);
                }
            } else {
                actions.put(destination, defaultAction);
                defaultAction.start(destination);
            }
        }
        lastCanalMap = canalMap;

        Set<String> destinations = actions.keySet();
        for(String destination : destinations) {
            if(!canalMap.containsKey(destination)) {
                InstanceAction action = actions.remove(destination);
                action.stop(destination);
            }
        }
    }

    private void scan() {
        Map<String, CanalInstanceParameter> canalMap = canalConfigClient.getInstanceConfig();
        judgeChange(canalMap);
    }

    public void setDefaultAction(InstanceAction defaultAction) {
        this.defaultAction = defaultAction;
    }

    public void setCanalConfigClient(CanalConfigClient canalConfigClient) {
        this.canalConfigClient = canalConfigClient;
    }
}
