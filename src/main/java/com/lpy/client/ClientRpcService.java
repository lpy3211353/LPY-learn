package com.lpy.client;

import com.lpy.client.config.RpcClientProperties;
import com.lpy.client.zookeeper.RefererRegister;
import com.lpy.concurrence.PositiveCycleAtomicInteger;
import com.lpy.protocol.RPCAPIService;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liangpengyu
 * @date 2022/3/22
 */
public class ClientRpcService {

    private static final Logger logger = LoggerFactory.getLogger(ClientRpcService.class);
    /**
     * 用作负载均衡部分
     */
    private static final ConcurrentHashMap<String, PositiveCycleAtomicInteger> COUNT_MAP = new ConcurrentHashMap<>();

    private final RefererRegister refererRegister;
    private final RpcClientProperties properties;


    public ClientRpcService(RefererRegister refererRegister, RpcClientProperties properties) {
        this.refererRegister = refererRegister;
        this.properties = properties;
    }


    public String invoke(String serverName,String request,String logid)throws Exception{
        //获取服务名
        serverName = resolveTargetServerName(serverName);
        //判断服务是否已经添加到内存缓存中了，如果没有添加，则在子节点监听器中添加
        boolean isExist = ThriftServerConstants.THRIFT_SERVER_KEY.containsKey(serverName);
        if (!isExist){
            refererRegister.addRefererChildListener(serverName);
        }
        //获取连接，调用服务
        try(TTransport transport = getTransport(serverName)) {
            TProtocol protocol = new TBinaryProtocol(transport);
            transport.open();
            RPCAPIService.Client client = new RPCAPIService.Client(protocol);
            String jsonText = client.invoke(logid,request);
            logger.info("调用服务:{}",serverName);
            return jsonText;
        }
    }

    private String resolveTargetServerName(String serverName) {
        // 这是一个过时的特性； 验证环境自动加个前缀
        String verifySubFix = properties.getVerifySubFix();
        if (StringUtils.isNotEmpty(verifySubFix)) {
            serverName = serverName.substring(0, serverName.indexOf("/", 1)) + verifySubFix + serverName.substring(serverName.indexOf("/", 1), serverName.length());
        }
        // 去除目标服务末尾的/，如果存在
        serverName = serverName.endsWith(ThriftServerConstants.ZK_NODE_SUFIX) ? serverName.substring(0, serverName.length() - 1) : serverName;
        return serverName;
    }

    /**
     * 创建thirft 链接。准备调用
     * 1、从内存中获取已知的服务地址。
     * ->如果节点中的服务地址都已经不存在，那么直接抛异常。不能进行下一步
     * 2、从注册的服务地址中随机取出一个地址进行连接
     * 3、创建scoket长链接
     *
     * @param target 目标服务注册中心地址
     * @return 目标服务rpc连接
     */
    public TTransport getTransport(String target) {
        // 负载均衡
        IpPort ipPort = loadBalance(target);
        // 创建连接
        TSocket socket = new TSocket(ipPort.ip, ipPort.port);
        // 超时设置
        socket.setConnectTimeout(properties.getConnectTimeout());
        socket.setSocketTimeout(properties.getSocketTimeout());

        int timeout = properties.getTimeout();
        if (timeout > 0) {
            socket.setTimeout(timeout);
        }
        return new TFramedTransport(socket, properties.getMaxDataSize());
    }

    private IpPort loadBalance(String target) {
        // 从注册中心的本地缓存获取目标服务的地址
        List<String> severList = ThriftServerConstants.THRIFT_SERVER.get(target);
        Assert.isTrue(severList.size() > 0, MessageFormat.format("在zookeeper的{0}节点中，没有注册有任何服务，请检查服务 {0} 是否健康", target));

        // 负载均衡：轮询
        PositiveCycleAtomicInteger balanceIndex = COUNT_MAP.get(target);
        if (balanceIndex == null) {
            balanceIndex = new PositiveCycleAtomicInteger(Integer.MAX_VALUE);
            COUNT_MAP.put(target, balanceIndex);
        }
        int index = balanceIndex.getAndIncrement() % severList.size();
        String targetIpPort = severList.get(index);
        logger.info("创建{}服务链接，服务地址为{}", target, targetIpPort);

        // 解析ip port
        String[] serverDescribes = targetIpPort.split(":");
        String host = serverDescribes[0];
        int port = Integer.parseInt(serverDescribes[1]);

        return new IpPort(host, port);
    }

    static final class IpPort implements Serializable {
        private final String ip;
        private final int port;

        public IpPort(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }
    }
}
