package com.alibaba.otter.canal.server;

import java.net.InetSocketAddress;
import java.util.Arrays;

import com.alibaba.otter.canal.instance.manager.model.Canal;
import com.alibaba.otter.canal.instance.manager.model.CanalCoreParameter;
import com.alibaba.otter.canal.instance.manager.model.CanalInstanceParameter;
import com.alibaba.otter.canal.instance.manager.model.CanalParameter;
import com.alibaba.otter.canal.instance.manager.model.CanalParameter.HAMode;
import com.alibaba.otter.canal.instance.manager.model.CanalParameter.IndexMode;
import com.alibaba.otter.canal.instance.manager.model.CanalParameter.MetaMode;
import com.alibaba.otter.canal.instance.manager.model.CanalParameter.SourcingType;
import com.alibaba.otter.canal.instance.manager.model.CanalParameter.StorageMode;

public class CanalServerWithEmbedded_StandaloneTest extends BaseCanalServerWithEmbededTest {

    protected Canal buildCanal() {
        Canal canal = new Canal();
        canal.setId(1L);
        canal.setName(DESTINATION);
        canal.setDesc("test");

        CanalParameter parameter = new CanalParameter();

        parameter.setZkClusters(Arrays.asList("127.0.0.1:2188"));
        parameter.setMetaMode(MetaMode.MEMORY);
        parameter.setHaMode(HAMode.HEARTBEAT);
        parameter.setIndexMode(IndexMode.MEMORY);

        parameter.setStorageMode(StorageMode.MEMORY);
        parameter.setMemoryStorageBufferSize(32 * 1024);

        parameter.setSourcingType(SourcingType.MYSQL);
        parameter.setDbAddresses(Arrays.asList(new InetSocketAddress(MYSQL_ADDRESS, 3306),
            new InetSocketAddress(MYSQL_ADDRESS, 3306)));
        parameter.setDbUsername(USERNAME);
        parameter.setDbPassword(PASSWORD);
        parameter.setPositions(Arrays.asList("{\"journalName\":\"mysql-bin.000003\",\"position\":14217L,\"timestamp\":\"1505998863000\"}",
            "{\"journalName\":\"mysql-bin.000003\",\"position\":14377L,\"timestamp\":\"1505998863000\"}"));

        parameter.setSlaveId(1234L);

        parameter.setDefaultConnectionTimeoutInSeconds(30);
        parameter.setConnectionCharset("UTF-8");
        parameter.setConnectionCharsetNumber((byte) 33);
        parameter.setReceiveBufferSize(8 * 1024);
        parameter.setSendBufferSize(8 * 1024);

        parameter.setDetectingEnable(false);
        parameter.setDetectingIntervalInSeconds(10);
        parameter.setDetectingRetryTimes(3);
        parameter.setDetectingSQL(DETECTING_SQL);

        canal.setCanalParameter(parameter);
        return canal;
    }

    @Override
    protected CanalCoreParameter buildCore() {
        return null;
    }

    @Override
    protected CanalInstanceParameter buildInstance() {
        return null;
    }
}
