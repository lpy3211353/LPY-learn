package com.lpy.server;

import com.lpy.client.ThriftServerConstants;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.InetAddress;

/**
 * 心跳检查
 * @author liangpengyu
 */
public class HealthListener {
    private static final Logger logger = LoggerFactory.getLogger(HealthListener.class);

    private final ZkClient zkClient;

    private final String nodePath;

    private final int port;

    private final ServerRpcService serverRpcService;

    public HealthListener(ZkClient zkClient, String nodePath, int port, ServerRpcService serverRpcService) {
        this.zkClient = zkClient;
        this.nodePath = nodePath.endsWith(ThriftServerConstants.ZK_NODE_SUFIX) ? nodePath:nodePath + ThriftServerConstants.ZK_NODE_SUFIX;;
        this.port = port;
        this.serverRpcService = serverRpcService;
    }

    @Scheduled(cron = "*/10 * * * * ?")
    public void listen()throws Exception{
        String ipPort = getHostAndPort();
        String path = nodePath + ipPort;
        if (logger.isDebugEnabled()){
            logger.debug("rpc 服务运行中：{}",path);
        }
        if (!serverRpcService.isServing()){
            logger.info("rpc 服务未运行");
        }
        if (!zkClient.exists(path)){
            logger.info("rpc 服务{}已掉线，尝试重连...",path);
            zkClient.createEphemeral(path);
            logger.info("rpc 服务{}重连成功",path);
        }
    }

    /**
     * 获取主机名称
     *
     * @return ip:port
     * @throws Exception 异常事件流
     */
    private String getHostAndPort() throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        String ip = addr.getHostAddress();
        return ip + ":" + port;
    }
}
