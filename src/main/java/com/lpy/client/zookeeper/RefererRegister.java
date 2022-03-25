package com.lpy.client.zookeeper;

import com.lpy.client.ThriftServerConstants;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author liangpengyu
 */
public class RefererRegister {
    private static final Logger logger = LoggerFactory.getLogger(RefererRegister.class);

    private final ZkClient zkClient;
    private final RefererChildListener refererChildListener;

    public RefererRegister(ZkClient zkClient, RefererChildListener refererChildListener) {
        this.zkClient = zkClient;
        this.refererChildListener = refererChildListener;
    }

    /**
     * 添加节点监听
     * @param serverName 目标节点
     */
    public void addRefererChildListener(String serverName) {
        logger.info("check node {} is exists ", serverName);
        Assert.isTrue(zkClient.exists(serverName), "尚未在zk中创建" + serverName + "节点");
        logger.info("get children of node {} ", serverName);
        // 根据服务名在zk里获取服务列表
        List<String> servers = zkClient.getChildren(serverName);
        // 为空时服务未开启
        if (servers.size() == 0) {
            logger.warn("尚未注册服务{}到zk,请关注{}服务是否开启", serverName, serverName);
        }
        //将节点添加到表中
        ThriftServerConstants.THRIFT_SERVER.put(serverName, servers);
        zkClient.subscribeChildChanges(serverName, refererChildListener);
        ThriftServerConstants.THRIFT_SERVER_KEY.put(serverName, serverName);
    }
}
