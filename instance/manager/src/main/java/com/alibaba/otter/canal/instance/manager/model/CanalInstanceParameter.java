package com.alibaba.otter.canal.instance.manager.model;

import java.io.Serializable;

/**
 * NOTE:
 *
 * @author lizhiyang
 * @Date 2019-07-09 17:08
 */
public class CanalInstanceParameter implements Serializable {
    @CanalField("canal.instance.name")
	private String destination;
    @CanalField("canal.instance.sourcingType")
    private String sourcingType;
    @CanalField("canal.instance.mysql.slaveId")
	private Long mysqlSlaveId;
    @CanalField("canal.instance.gtidon")
	private String gtidon;
    @CanalField("canal.instance.rds.accesskey")
	private String rdsAccesskey;
    @CanalField("canal.instance.rds.secretkey")
	private String rdsSecretkey;
    @CanalField("canal.instance.rds.instanceId")
	private String rdsInstanceId;
    @CanalField("canal.instance.master.address")
	private String masterAddress;
    @CanalField("canal.instance.master.journal.name")
	private String masterJournalName;
    @CanalField("canal.instance.master.position")
	private String masterPosition;
    @CanalField("canal.instance.master.timestamp")
    private String masterTimestamp;
    @CanalField("canal.instance.master.gtid")
	private String masterGtid;
    @CanalField("canal.instance.standby.address")
	private String standbyAddress;
    @CanalField("canal.instance.standby.journal.name")
	private String standbyJournalName;
    @CanalField("canal.instance.standby.position")
	private String standbyPosition;
    @CanalField("canal.instance.standby.timestamp")
	private String standbyTimestamp;
    @CanalField("canal.instance.standby.gtid")
	private String standbyGtid;
    @CanalField("canal.instance.dbUsername")
	private String dbUsername;
    @CanalField("canal.instance.dbPassword")
	private String dbPassword;
    @CanalField("canal.instance.connectionCharset")
	private String connectionCharset;
    @CanalField("canal.instance.defaultDatabaseName")
	private String defaultDatabaseName;
    @CanalField("canal.instance.enableDruid")
	private String enableDruid;
    @CanalField("canal.instance.pwdPublicKey")
	private String pwdPublicKey;
    @CanalField("canal.instance.filter.regex")
	private String filterRegex;
    @CanalField("canal.instance.filter.black.regex")
	private String filterBlackRegex;
    @CanalField("canal.mq.topic")
	private String mqTopic;
    @CanalField("canal.mq.partition")
	private String mqPartition;
    @CanalField("canal.mq.partitionsNum")
	private String mqPartitionsNum;
    @CanalField("canal.mq.partitionHash")
	private String mqPartitionHash;
    @CanalField(("canal.instance.storage.mode"))
    private String storageMode;
    @CanalField("canal.instance.memory.buffer.size")
	private Integer memoryBufferSize;
    @CanalField("canal.instance.memory.buffer.memunit")
	private Integer memoryBufferMemunit;
    @CanalField("canal.instance.memory.batch.mode")
	private String memoryBatchMode;
    @CanalField("canal.instance.memory.rawEntry")
	private Boolean memoryRawEntry;
    @CanalField("canal.instance.storage.scavenge.mode")
    private String storageScavengeMode;
    @CanalField("canal.instance.storage.scavenge.schedule")
    private String storageScangengeSchedule;
    @CanalField("canal.instance.detecting.enable")
	private String detectionEnable;
    @CanalField("canal.instance.detecting.sql")
	private String detectingSql;
    @CanalField("canal.instance.detecting.interval.time")
	private Integer detectingInvervalTime;
    @CanalField("canal.instance.ha.mode")
    private String haMode;
    @CanalField("canal.instance.detecting.retry.threshold")
	private Integer detectingRetryThreshold;
    @CanalField("canal.instance.detecting.heartbeatHaEnable")
	private Boolean detectingHeartbeatHaEnable;
    @CanalField("canal.instance.transaction.size")
	private Integer transactionSize;
    @CanalField("canal.instance.fallbackIntervalInSeconds")
	private Integer fallbackIntervalInSeconds;
    @CanalField("canal.instance.network.receiveBufferSize")
	private Integer networkReceiveBufferSize;
    @CanalField("canal.instance.network.sendBufferSize")
	private Integer networkSendBufferSize;
    @CanalField("canal.instance.network.soTimeout")
	private Integer networkSoTimeout;
    @CanalField("canal.instance.filter.druid.ddl")
	private String filterDruidDdl;
    @CanalField("canal.instance.filter.query.dcl")
	private String filterQueryDcl;
    @CanalField("canal.instance.filter.query.dml")
	private String filterQueryDml;
    @CanalField("canal.instance.filter.query.ddl")
	private String filterQueryddl;
    @CanalField("canal.instance.filter.table.error")
	private Boolean filterTableError;
    @CanalField("canal.instance.filter.rows")
	private String filterRows;
    @CanalField("canal.instance.filter.transaction.entry")
	private String filterTransactionEntry;
    @CanalField("canal.instance.binlog.format")
	private String binlogFormat;
    @CanalField("canal.instance.binlog.image")
	private String binlogImage;
    @CanalField("canal.instance.get.ddl.isolation")
	private Boolean ddlIsolation;
    @CanalField("canal.instance.parser.parallel")
	private String parserParallel;
    @CanalField("canal.instance.parser.parallelThreadSize")
	private String parserParallelThreadSize;
    @CanalField("canal.instance.parser.parallelBufferSize")
	private String parserParallelBufferSize;
    @CanalField("canal.instance.tsdb.enable")
	private String tsdbEnable;
    @CanalField("canal.instance.tsdb.dir")
	private String tsdbDir;
    @CanalField("canal.instance.tsdb.url")
	private String tsdbUrl;
    @CanalField("canal.instance.tsdb.dbUsername")
	private String tsdbDbUsername;
    @CanalField("canal.instance.tsdb.dbPassword")
	private String tsdbDbPassword;
    @CanalField("canal.instance.tsdb.snapshot.interval")
	private Integer tsdbSnapshotInterval;
    @CanalField("canal.instance.tsdb.snapshot.expire")
	private Integer tsdbSnapshotExpire;
    @CanalField("canal.instance.meta.mode")
    private String metaMode;
    @CanalField("canal.instance.meta.file.dataDir")
    private String metaFileDataDir;
    @CanalField("canal.instance.meta.file.flushPeriod")
    private Long metaFileFlushPeriod;
    @CanalField("canal.instance.position.index.mode")
    private String positionIndexMode;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSourcingType() {
        return sourcingType;
    }

    public void setSourcingType(String sourcingType) {
        this.sourcingType = sourcingType;
    }

    public Long getMysqlSlaveId() {
        return mysqlSlaveId;
    }

    public void setMysqlSlaveId(Long mysqlSlaveId) {
        this.mysqlSlaveId = mysqlSlaveId;
    }

    public String getGtidon() {
        return gtidon;
    }

    public void setGtidon(String gtidon) {
        this.gtidon = gtidon;
    }

    public String getRdsAccesskey() {
        return rdsAccesskey;
    }

    public void setRdsAccesskey(String rdsAccesskey) {
        this.rdsAccesskey = rdsAccesskey;
    }

    public String getRdsSecretkey() {
        return rdsSecretkey;
    }

    public void setRdsSecretkey(String rdsSecretkey) {
        this.rdsSecretkey = rdsSecretkey;
    }

    public String getRdsInstanceId() {
        return rdsInstanceId;
    }

    public void setRdsInstanceId(String rdsInstanceId) {
        this.rdsInstanceId = rdsInstanceId;
    }

    public String getMasterAddress() {
        return masterAddress;
    }

    public void setMasterAddress(String masterAddress) {
        this.masterAddress = masterAddress;
    }

    public String getMasterJournalName() {
        return masterJournalName;
    }

    public void setMasterJournalName(String masterJournalName) {
        this.masterJournalName = masterJournalName;
    }

    public String getMasterPosition() {
        return masterPosition;
    }

    public void setMasterPosition(String masterPosition) {
        this.masterPosition = masterPosition;
    }

    public String getMasterTimestamp() {
        return masterTimestamp;
    }

    public void setMasterTimestamp(String masterTimestamp) {
        this.masterTimestamp = masterTimestamp;
    }

    public String getMasterGtid() {
        return masterGtid;
    }

    public void setMasterGtid(String masterGtid) {
        this.masterGtid = masterGtid;
    }

    public String getStandbyAddress() {
        return standbyAddress;
    }

    public void setStandbyAddress(String standbyAddress) {
        this.standbyAddress = standbyAddress;
    }

    public String getStandbyJournalName() {
        return standbyJournalName;
    }

    public void setStandbyJournalName(String standbyJournalName) {
        this.standbyJournalName = standbyJournalName;
    }

    public String getStandbyPosition() {
        return standbyPosition;
    }

    public void setStandbyPosition(String standbyPosition) {
        this.standbyPosition = standbyPosition;
    }

    public String getStandbyTimestamp() {
        return standbyTimestamp;
    }

    public void setStandbyTimestamp(String standbyTimestamp) {
        this.standbyTimestamp = standbyTimestamp;
    }

    public String getStandbyGtid() {
        return standbyGtid;
    }

    public void setStandbyGtid(String standbyGtid) {
        this.standbyGtid = standbyGtid;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getConnectionCharset() {
        return connectionCharset;
    }

    public void setConnectionCharset(String connectionCharset) {
        this.connectionCharset = connectionCharset;
    }

    public String getDefaultDatabaseName() {
        return defaultDatabaseName;
    }

    public void setDefaultDatabaseName(String defaultDatabaseName) {
        this.defaultDatabaseName = defaultDatabaseName;
    }

    public String getEnableDruid() {
        return enableDruid;
    }

    public void setEnableDruid(String enableDruid) {
        this.enableDruid = enableDruid;
    }

    public String getPwdPublicKey() {
        return pwdPublicKey;
    }

    public void setPwdPublicKey(String pwdPublicKey) {
        this.pwdPublicKey = pwdPublicKey;
    }

    public String getFilterRegex() {
        return filterRegex;
    }

    public void setFilterRegex(String filterRegex) {
        this.filterRegex = filterRegex;
    }

    public String getFilterBlackRegex() {
        return filterBlackRegex;
    }

    public void setFilterBlackRegex(String filterBlackRegex) {
        this.filterBlackRegex = filterBlackRegex;
    }

    public String getMqTopic() {
        return mqTopic;
    }

    public void setMqTopic(String mqTopic) {
        this.mqTopic = mqTopic;
    }

    public String getMqPartition() {
        return mqPartition;
    }

    public void setMqPartition(String mqPartition) {
        this.mqPartition = mqPartition;
    }

    public String getMqPartitionsNum() {
        return mqPartitionsNum;
    }

    public void setMqPartitionsNum(String mqPartitionsNum) {
        this.mqPartitionsNum = mqPartitionsNum;
    }

    public String getMqPartitionHash() {
        return mqPartitionHash;
    }

    public void setMqPartitionHash(String mqPartitionHash) {
        this.mqPartitionHash = mqPartitionHash;
    }

    public String getStorageMode() {
        return storageMode;
    }

    public void setStorageMode(String storageMode) {
        this.storageMode = storageMode;
    }

    public Integer getMemoryBufferSize() {
        return memoryBufferSize;
    }

    public void setMemoryBufferSize(Integer memoryBufferSize) {
        this.memoryBufferSize = memoryBufferSize;
    }

    public Integer getMemoryBufferMemunit() {
        return memoryBufferMemunit;
    }

    public void setMemoryBufferMemunit(Integer memoryBufferMemunit) {
        this.memoryBufferMemunit = memoryBufferMemunit;
    }

    public String getMemoryBatchMode() {
        return memoryBatchMode;
    }

    public void setMemoryBatchMode(String memoryBatchMode) {
        this.memoryBatchMode = memoryBatchMode;
    }

    public Boolean getMemoryRawEntry() {
        return memoryRawEntry;
    }

    public void setMemoryRawEntry(Boolean memoryRawEntry) {
        this.memoryRawEntry = memoryRawEntry;
    }

    public String getStorageScavengeMode() {
        return storageScavengeMode;
    }

    public void setStorageScavengeMode(String storageScavengeMode) {
        this.storageScavengeMode = storageScavengeMode;
    }

    public String getStorageScangengeSchedule() {
        return storageScangengeSchedule;
    }

    public void setStorageScangengeSchedule(String storageScangengeSchedule) {
        this.storageScangengeSchedule = storageScangengeSchedule;
    }

    public String getDetectionEnable() {
        return detectionEnable;
    }

    public void setDetectionEnable(String detectionEnable) {
        this.detectionEnable = detectionEnable;
    }

    public String getDetectingSql() {
        return detectingSql;
    }

    public void setDetectingSql(String detectingSql) {
        this.detectingSql = detectingSql;
    }

    public Integer getDetectingInvervalTime() {
        return detectingInvervalTime;
    }

    public void setDetectingInvervalTime(Integer detectingInvervalTime) {
        this.detectingInvervalTime = detectingInvervalTime;
    }

    public String getHaMode() {
        return haMode;
    }

    public void setHaMode(String haMode) {
        this.haMode = haMode;
    }

    public Integer getDetectingRetryThreshold() {
        return detectingRetryThreshold;
    }

    public void setDetectingRetryThreshold(Integer detectingRetryThreshold) {
        this.detectingRetryThreshold = detectingRetryThreshold;
    }

    public Boolean getDetectingHeartbeatHaEnable() {
        return detectingHeartbeatHaEnable;
    }

    public void setDetectingHeartbeatHaEnable(Boolean detectingHeartbeatHaEnable) {
        this.detectingHeartbeatHaEnable = detectingHeartbeatHaEnable;
    }

    public Integer getTransactionSize() {
        return transactionSize;
    }

    public void setTransactionSize(Integer transactionSize) {
        this.transactionSize = transactionSize;
    }

    public Integer getFallbackIntervalInSeconds() {
        return fallbackIntervalInSeconds;
    }

    public void setFallbackIntervalInSeconds(Integer fallbackIntervalInSeconds) {
        this.fallbackIntervalInSeconds = fallbackIntervalInSeconds;
    }

    public Integer getNetworkReceiveBufferSize() {
        return networkReceiveBufferSize;
    }

    public void setNetworkReceiveBufferSize(Integer networkReceiveBufferSize) {
        this.networkReceiveBufferSize = networkReceiveBufferSize;
    }

    public Integer getNetworkSendBufferSize() {
        return networkSendBufferSize;
    }

    public void setNetworkSendBufferSize(Integer networkSendBufferSize) {
        this.networkSendBufferSize = networkSendBufferSize;
    }

    public Integer getNetworkSoTimeout() {
        return networkSoTimeout;
    }

    public void setNetworkSoTimeout(Integer networkSoTimeout) {
        this.networkSoTimeout = networkSoTimeout;
    }

    public String getFilterDruidDdl() {
        return filterDruidDdl;
    }

    public void setFilterDruidDdl(String filterDruidDdl) {
        this.filterDruidDdl = filterDruidDdl;
    }

    public String getFilterQueryDcl() {
        return filterQueryDcl;
    }

    public void setFilterQueryDcl(String filterQueryDcl) {
        this.filterQueryDcl = filterQueryDcl;
    }

    public String getFilterQueryDml() {
        return filterQueryDml;
    }

    public void setFilterQueryDml(String filterQueryDml) {
        this.filterQueryDml = filterQueryDml;
    }

    public String getFilterQueryddl() {
        return filterQueryddl;
    }

    public void setFilterQueryddl(String filterQueryddl) {
        this.filterQueryddl = filterQueryddl;
    }

    public Boolean getFilterTableError() {
        return filterTableError;
    }

    public void setFilterTableError(Boolean filterTableError) {
        this.filterTableError = filterTableError;
    }

    public String getFilterRows() {
        return filterRows;
    }

    public void setFilterRows(String filterRows) {
        this.filterRows = filterRows;
    }

    public String getFilterTransactionEntry() {
        return filterTransactionEntry;
    }

    public void setFilterTransactionEntry(String filterTransactionEntry) {
        this.filterTransactionEntry = filterTransactionEntry;
    }

    public String getBinlogFormat() {
        return binlogFormat;
    }

    public void setBinlogFormat(String binlogFormat) {
        this.binlogFormat = binlogFormat;
    }

    public String getBinlogImage() {
        return binlogImage;
    }

    public void setBinlogImage(String binlogImage) {
        this.binlogImage = binlogImage;
    }

    public Boolean getDdlIsolation() {
        return ddlIsolation;
    }

    public void setDdlIsolation(Boolean ddlIsolation) {
        this.ddlIsolation = ddlIsolation;
    }

    public String getParserParallel() {
        return parserParallel;
    }

    public void setParserParallel(String parserParallel) {
        this.parserParallel = parserParallel;
    }

    public String getParserParallelThreadSize() {
        return parserParallelThreadSize;
    }

    public void setParserParallelThreadSize(String parserParallelThreadSize) {
        this.parserParallelThreadSize = parserParallelThreadSize;
    }

    public String getParserParallelBufferSize() {
        return parserParallelBufferSize;
    }

    public void setParserParallelBufferSize(String parserParallelBufferSize) {
        this.parserParallelBufferSize = parserParallelBufferSize;
    }

    public String getTsdbEnable() {
        return tsdbEnable;
    }

    public void setTsdbEnable(String tsdbEnable) {
        this.tsdbEnable = tsdbEnable;
    }

    public String getTsdbDir() {
        return tsdbDir;
    }

    public void setTsdbDir(String tsdbDir) {
        this.tsdbDir = tsdbDir;
    }

    public String getTsdbUrl() {
        return tsdbUrl;
    }

    public void setTsdbUrl(String tsdbUrl) {
        this.tsdbUrl = tsdbUrl;
    }

    public String getTsdbDbUsername() {
        return tsdbDbUsername;
    }

    public void setTsdbDbUsername(String tsdbDbUsername) {
        this.tsdbDbUsername = tsdbDbUsername;
    }

    public String getTsdbDbPassword() {
        return tsdbDbPassword;
    }

    public void setTsdbDbPassword(String tsdbDbPassword) {
        this.tsdbDbPassword = tsdbDbPassword;
    }

    public Integer getTsdbSnapshotInterval() {
        return tsdbSnapshotInterval;
    }

    public void setTsdbSnapshotInterval(Integer tsdbSnapshotInterval) {
        this.tsdbSnapshotInterval = tsdbSnapshotInterval;
    }

    public Integer getTsdbSnapshotExpire() {
        return tsdbSnapshotExpire;
    }

    public void setTsdbSnapshotExpire(Integer tsdbSnapshotExpire) {
        this.tsdbSnapshotExpire = tsdbSnapshotExpire;
    }

    public String getMetaMode() {
        return metaMode;
    }

    public void setMetaMode(String metaMode) {
        this.metaMode = metaMode;
    }

    public String getMetaFileDataDir() {
        return metaFileDataDir;
    }

    public void setMetaFileDataDir(String metaFileDataDir) {
        this.metaFileDataDir = metaFileDataDir;
    }

    public Long getMetaFileFlushPeriod() {
        return metaFileFlushPeriod;
    }

    public void setMetaFileFlushPeriod(Long metaFileFlushPeriod) {
        this.metaFileFlushPeriod = metaFileFlushPeriod;
    }

    public String getPositionIndexMode() {
        return positionIndexMode;
    }

    public void setPositionIndexMode(String positionIndexMode) {
        this.positionIndexMode = positionIndexMode;
    }
}
