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

    private Map<String, CanalInstanceParameter> canalConfig = new HashMap<>();

    private CanalCoreParameter canalCoreParameter;

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

    public void loadCoreConfig(String config) {
        logger.info("local canal core config:{}", config);
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
        if(CollectionUtils.isEmpty(map)) {
            return;
        }
        CanalCoreParameter parameter = CanalFieldConvert.convert(CanalCoreParameter.class, map);
        if(parameter != null) {
            canalCoreParameter = parameter;
        }
    }

    public void loadInstanceConfig(String config) {
        logger.info("local canal instance config:{}", config);
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
        canalConfig.clear();
        if(CollectionUtils.isEmpty(map)) {
            return;
        }
        Iterator<Map.Entry<String, Map<String, String>>> iter = map.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String, Map<String, String>> entry = iter.next();
            String destination = entry.getKey();
            Map<String, String> parameter = entry.getValue();
            if(StringUtils.isNotBlank(destination) && parameter != null) {
                CanalInstanceParameter instanceParameter = CanalFieldConvert.convert(CanalInstanceParameter.class, parameter);
                canalConfig.put(destination, instanceParameter);
            } else {
                //canal有问题
                logger.warn("illegal canal config:{},{}", destination, parameter);
            }
        }
    }

    public static void main(String[] args) {
        String str = "{\"canal.id\":\"1\",\"canal.ip\":\"\",\"canal.port\":\"11111\",\"canal.metrics.pull.port\":\"11113\",\"canal.zkServers\":\"10.100.12.68:2181\",\"canal.zookeeper.flush.period\":\"1000\",\"canal.withoutNetty\":\"false\",\"canal.serverMode\":\"tcp\",\"canal.file.data.dir\":\"\",\"canal.file.flush.period\":\"1000\",\"canal.aliyun.accesskey\":\"\",\"canal.aliyun.secretkey\":\"\",\"canal.destinations\":\"\",\"canal.conf.dir\":\"./deployer/src/main/resources\",\"canal.auto.scan\":\"true\",\"canal.auto.scan.interval\":\"5\",\"canal.instance.global.mode\":\"manager\",\"canal.instance.global.lazy\":\"false\",\"canal.mq.servers\":\"127.0.0.1:6667\",\"canal.mq.retries\":\"0\",\"canal.mq.batchSize\":\"16384\",\"canal.mq.maxRequestSize\":\"1048576\",\"canal.mq.lingerMs\":\"1\",\"canal.mq.bufferMemory\":\"33554432\",\"canal.mq.canalBatchSize\":\"50\",\"canal.mq.canalGetTimeout\":\"100\",\"canal.mq.flatMessage\":\"true\",\"canal.mq.compressionType\":\"none\",\"canal.mq.acks\":\"all\"}";

        Map<String, String> map = JSON.parseObject(str, new TypeReference<Map<String, String>>(){});
        CanalCoreParameter parameter = CanalFieldConvert.convert(CanalCoreParameter.class, map);
        System.out.println(JSON.toJSONString(parameter));

    }
}
