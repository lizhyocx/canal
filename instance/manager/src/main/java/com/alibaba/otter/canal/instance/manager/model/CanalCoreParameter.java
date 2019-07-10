package com.alibaba.otter.canal.instance.manager.model;

import java.io.Serializable;

/**
 * NOTE: canal核心配置文件
 *
 * @author lizhiyang
 * @Date 2019-07-09 14:23
 */
public class CanalCoreParameter implements Serializable {

    @CanalField("canal.id")
    private Long id;
    @CanalField("canal.ip")
    private String ip;
    @CanalField("canal.port")
    private Integer port;
    @CanalField("canal.metrics.pull.port")
    private Integer metricsPullPort;
    @CanalField("canal.zkServers")
    private String zkServers;
    @CanalField("canal.zookeeper.flush.period")
    private String zkFlushPeriod;
    @CanalField("canal.withoutNetty")
    private String withoutNetty;
    @CanalField("canal.serverMode")
    private String serverMode;
    @CanalField("canal.file.data.dir")
    private String fileDataDir;
    @CanalField("canal.file.flush.period")
    private String fileFlushPeriod;
    @CanalField("canal.aliyun.accesskey")
    private String aliyunAccessKey;
    @CanalField("canal.aliyun.secretkey")
    private String aliuyunSecretKey;
    @CanalField("canal.destinations")
    private String destinations;
    @CanalField("canal.conf.dir")
    private String confDir;
    @CanalField("canal.auto.scan")
    private Boolean autoScan;
    @CanalField("canal.auto.scan.interval")
    private Integer autoScanInterval;
    @CanalField("canal.instance.global.mode")
    private String instanceGlobalMode;
    @CanalField("canal.instance.global.lazy")
    private String instanceGlobalLazy;
    @CanalField("canal.instance.global.spring.xml")
    private String instanceGlobalSpringXml;
    @CanalField("canal.mq.servers")
    private String mqServers;
    @CanalField("canal.mq.retries")
    private String mqRetries;
    @CanalField("canal.mq.batchSize")
    private String mqBatchSize;
    @CanalField("canal.mq.maxRequestSize")
    private String mqMaxRequestSize;
    @CanalField("canal.mq.lingerMs")
    private String mqLingerMs;
    @CanalField("canal.mq.bufferSize")
    private String mqBufferMemory;
    @CanalField("canal.mq.canalBatchSize")
    private String mqCanalBatchSize;
    @CanalField("canal.mq.canalGetTimeout")
    private String mqCanalGetTimeout;
    @CanalField("canal.mq.flatMessage")
    private String mqFlatMessage;
    @CanalField("canal.mq.compressionType")
    private String mqCompressionType;
    @CanalField("canal.mq.acks")
    private String mqAcks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getMetricsPullPort() {
        return metricsPullPort;
    }

    public void setMetricsPullPort(Integer metricsPullPort) {
        this.metricsPullPort = metricsPullPort;
    }

    public String getZkServers() {
        return zkServers;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }

    public String getZkFlushPeriod() {
        return zkFlushPeriod;
    }

    public void setZkFlushPeriod(String zkFlushPeriod) {
        this.zkFlushPeriod = zkFlushPeriod;
    }

    public String getWithoutNetty() {
        return withoutNetty;
    }

    public void setWithoutNetty(String withoutNetty) {
        this.withoutNetty = withoutNetty;
    }

    public String getServerMode() {
        return serverMode;
    }

    public void setServerMode(String serverMode) {
        this.serverMode = serverMode;
    }

    public String getFileDataDir() {
        return fileDataDir;
    }

    public void setFileDataDir(String fileDataDir) {
        this.fileDataDir = fileDataDir;
    }

    public String getFileFlushPeriod() {
        return fileFlushPeriod;
    }

    public void setFileFlushPeriod(String fileFlushPeriod) {
        this.fileFlushPeriod = fileFlushPeriod;
    }

    public String getAliyunAccessKey() {
        return aliyunAccessKey;
    }

    public void setAliyunAccessKey(String aliyunAccessKey) {
        this.aliyunAccessKey = aliyunAccessKey;
    }

    public String getAliuyunSecretKey() {
        return aliuyunSecretKey;
    }

    public void setAliuyunSecretKey(String aliuyunSecretKey) {
        this.aliuyunSecretKey = aliuyunSecretKey;
    }

    public String getDestinations() {
        return destinations;
    }

    public void setDestinations(String destinations) {
        this.destinations = destinations;
    }

    public String getConfDir() {
        return confDir;
    }

    public void setConfDir(String confDir) {
        this.confDir = confDir;
    }

    public Boolean getAutoScan() {
        return autoScan;
    }

    public void setAutoScan(Boolean autoScan) {
        this.autoScan = autoScan;
    }

    public Integer getAutoScanInterval() {
        return autoScanInterval;
    }

    public void setAutoScanInterval(Integer autoScanInterval) {
        this.autoScanInterval = autoScanInterval;
    }

    public String getInstanceGlobalMode() {
        return instanceGlobalMode;
    }

    public void setInstanceGlobalMode(String instanceGlobalMode) {
        this.instanceGlobalMode = instanceGlobalMode;
    }

    public String getInstanceGlobalLazy() {
        return instanceGlobalLazy;
    }

    public void setInstanceGlobalLazy(String instanceGlobalLazy) {
        this.instanceGlobalLazy = instanceGlobalLazy;
    }

    public String getInstanceGlobalSpringXml() {
        return instanceGlobalSpringXml;
    }

    public void setInstanceGlobalSpringXml(String instanceGlobalSpringXml) {
        this.instanceGlobalSpringXml = instanceGlobalSpringXml;
    }

    public String getMqServers() {
        return mqServers;
    }

    public void setMqServers(String mqServers) {
        this.mqServers = mqServers;
    }

    public String getMqRetries() {
        return mqRetries;
    }

    public void setMqRetries(String mqRetries) {
        this.mqRetries = mqRetries;
    }

    public String getMqBatchSize() {
        return mqBatchSize;
    }

    public void setMqBatchSize(String mqBatchSize) {
        this.mqBatchSize = mqBatchSize;
    }

    public String getMqMaxRequestSize() {
        return mqMaxRequestSize;
    }

    public void setMqMaxRequestSize(String mqMaxRequestSize) {
        this.mqMaxRequestSize = mqMaxRequestSize;
    }

    public String getMqLingerMs() {
        return mqLingerMs;
    }

    public void setMqLingerMs(String mqLingerMs) {
        this.mqLingerMs = mqLingerMs;
    }

    public String getMqBufferMemory() {
        return mqBufferMemory;
    }

    public void setMqBufferMemory(String mqBufferMemory) {
        this.mqBufferMemory = mqBufferMemory;
    }

    public String getMqCanalBatchSize() {
        return mqCanalBatchSize;
    }

    public void setMqCanalBatchSize(String mqCanalBatchSize) {
        this.mqCanalBatchSize = mqCanalBatchSize;
    }

    public String getMqCanalGetTimeout() {
        return mqCanalGetTimeout;
    }

    public void setMqCanalGetTimeout(String mqCanalGetTimeout) {
        this.mqCanalGetTimeout = mqCanalGetTimeout;
    }

    public String getMqFlatMessage() {
        return mqFlatMessage;
    }

    public void setMqFlatMessage(String mqFlatMessage) {
        this.mqFlatMessage = mqFlatMessage;
    }

    public String getMqCompressionType() {
        return mqCompressionType;
    }

    public void setMqCompressionType(String mqCompressionType) {
        this.mqCompressionType = mqCompressionType;
    }

    public String getMqAcks() {
        return mqAcks;
    }

    public void setMqAcks(String mqAcks) {
        this.mqAcks = mqAcks;
    }
}
