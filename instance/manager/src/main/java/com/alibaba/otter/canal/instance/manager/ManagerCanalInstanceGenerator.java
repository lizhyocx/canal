package com.alibaba.otter.canal.instance.manager;

import com.alibaba.otter.canal.common.zookeeper.ZkClientx;
import com.alibaba.otter.canal.instance.core.CanalInstance;
import com.alibaba.otter.canal.instance.core.CanalInstanceGenerator;
import com.alibaba.otter.canal.instance.manager.model.CanalCoreParameter;
import com.alibaba.otter.canal.instance.manager.model.CanalInstanceParameter;

/**
 * 基于manager生成对应的{@linkplain CanalInstance}
 * 
 * @author jianghang 2012-7-12 下午05:37:09
 * @version 1.0.0
 */
public class ManagerCanalInstanceGenerator implements CanalInstanceGenerator {

    private CanalConfigClient canalConfigClient;

    @Override
    public CanalInstance generate(String destination) {
        CanalInstanceParameter instanceParameter = canalConfigClient.findCanal(destination);
        CanalCoreParameter coreParameter = canalConfigClient.getCoreConfig();
        ZkClientx zkClientx = ZkClientx.getZkClient(canalConfigClient.getCoreConfig().getZkServers());
        return new CanalInstanceWithManager(coreParameter, instanceParameter, zkClientx);
    }

    // ================ setter / getter ================

    public void setCanalConfigClient(CanalConfigClient canalConfigClient) {
        this.canalConfigClient = canalConfigClient;
    }

}
