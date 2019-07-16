package com.alibaba.otter.canal.instance.manager.diamond;

import com.alibaba.otter.canal.instance.manager.model.CanalField;

import java.io.Serializable;

/**
 * NOTE: Diamond相关配置
 *
 * @author lizhiyang
 * @Date 2019-07-11 17:53
 */
public class DiamondConfig implements Serializable {

    @CanalField("application.name")
    private String appName = "canalx";
    @CanalField("diamond.server.host")
    private String diamondHost = "diamond.yunzong";
    @CanalField("diamond.server.port")
    private Integer diamondPort = 10510;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDiamondHost() {
        return diamondHost;
    }

    public void setDiamondHost(String diamondHost) {
        this.diamondHost = diamondHost;
    }

    public Integer getDiamondPort() {
        return diamondPort;
    }

    public void setDiamondPort(Integer diamondPort) {
        this.diamondPort = diamondPort;
    }
}
