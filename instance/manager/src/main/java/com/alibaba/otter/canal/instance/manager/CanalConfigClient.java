package com.alibaba.otter.canal.instance.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.otter.canal.instance.manager.model.CanalCoreParameter;
import com.alibaba.otter.canal.instance.manager.model.CanalFieldConvert;
import com.alibaba.otter.canal.instance.manager.model.CanalInstanceParameter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 对应canal的配置
 * 
 * @author jianghang 2012-7-4 下午03:09:17
 * @version 1.0.0
 */
public class CanalConfigClient {
    private static final Logger logger   = LoggerFactory.getLogger(CanalConfigClient.class);

    private Map<String, String> localCoreParameter;

    private Map<String, CanalInstanceParameter> canalConfig = new HashMap<>();

    private CanalCoreParameter canalCoreParameter;

    public CanalConfigClient() {
        this.localCoreParameter = new HashMap<>();
    }

    public CanalConfigClient(Map<String, String> localCoreParameter) {
        this.localCoreParameter = localCoreParameter;
    }

    /**
     * 根据对应的destinantion查询Canal信息
     */
    public CanalInstanceParameter findCanal(String destination) {
        if(canalConfig.containsKey(destination)) {
            return canalConfig.get(destination);
        }
        return null;
    }

    public CanalCoreParameter getCoreConfig() {
        return canalCoreParameter;
    }

    public Map<String, CanalInstanceParameter> getInstanceConfig() {
        return canalConfig;
    }

    public synchronized void loadCoreConfig(String config) {
        logger.warn("local canal core config:{}", config);
        if(StringUtils.isBlank(config)) {
            return;
        }
        Map<String, String> map = null;
        try {
            map = JSON.parseObject(config, new TypeReference<Map<String, String>>(){});
        } catch (Exception e) {
            logger.error("parse config info exception", e);
            return;
        }
        if(map == null) {
            map = localCoreParameter;
        } else {
            map.putAll(localCoreParameter);
        }
        if(CollectionUtils.isEmpty(map)) {
            return;
        }
        CanalCoreParameter parameter = CanalFieldConvert.convert(CanalCoreParameter.class, map);
        if(parameter != null) {
            canalCoreParameter = parameter;
        }
    }

    public synchronized void loadInstanceConfig(String config) {
        logger.warn("local canal instance config:{}", config);
        if(StringUtils.isBlank(config)) {
            return;
        }
        Map<String, Map<String, String>> map = null;
        try {
            map = JSON.parseObject(config, new TypeReference<Map<String, Map<String, String>>>(){});
        } catch (Exception e) {
            //转换异常
            logger.error("json parse exception", e);
            return;
        }
        if(CollectionUtils.isEmpty(map)) {
            canalConfig = new HashMap<>();
            return;
        }
        Map<String, CanalInstanceParameter> refreshMap = new HashMap<>();

        Iterator<Map.Entry<String, Map<String, String>>> iter = map.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String, Map<String, String>> entry = iter.next();
            String destination = entry.getKey();
            Map<String, String> parameter = entry.getValue();
            if(StringUtils.isNotBlank(destination) && parameter != null) {
                CanalInstanceParameter instanceParameter = CanalFieldConvert.convert(CanalInstanceParameter.class, parameter);
                if(StringUtils.isBlank(instanceParameter.getDestination())) {
                    instanceParameter.setDestination(destination);
                }
                refreshMap.put(destination, instanceParameter);
            } else {
                //canal实例配置有问题
                logger.warn("illegal canal config:{},{}", destination, parameter);
            }
        }
        canalConfig = refreshMap;
    }
}
