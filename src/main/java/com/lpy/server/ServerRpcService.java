package com.lpy.server;


import com.lpy.client.ThriftServerConstants;
import com.lpy.protocol.RPCAPIService;
import com.lpy.server.config.RpcServerProperties;
import org.I0Itec.zkclient.ZkClient;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.net.InetAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liangpengyu
 */
public class ServerRpcService {

    /**
     * 日志输出
     */
    private static final Logger logger = LoggerFactory.getLogger(ServerRpcService.class);

    private boolean registeAble = false;

    private TThreadedSelectorServer server;

    private final RPCAPIService.Iface rpcAdapter;

    private final ZkClient zkClient;

    private final RpcServerProperties properties;

    /**
     * 注册路径
     */
    private String registerPath;

    public ServerRpcService(RPCAPIService.Iface rpcAdapter, ZkClient zkClient, RpcServerProperties properties) {
        this.rpcAdapter = rpcAdapter;
        this.zkClient = zkClient;
        this.properties = properties;
        String nodePath = properties.getNodePath();
        // 判断是否是"/"结尾，如果是则直接用，不是则加上"/"
        this.registerPath =nodePath.endsWith(ThriftServerConstants.ZK_NODE_SUFIX) ? nodePath:nodePath + ThriftServerConstants.ZK_NODE_SUFIX;
    }

    public boolean isServing(){
        return server.isServing();
    }

    /**
     * 创建监听以及线程
     * @throws TTransportException 异常事件流
     */
    private void start()throws TTransportException {


        // 协议处理器
        TProcessor processor = new RPCAPIService.Processor<>(rpcAdapter);
        // 非阻塞式的，配合TFramedTransport使用
        TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(properties.getThriftPort());
        // 参数设置
        TThreadedSelectorServer.Args args = thriftServerArgs(processor, serverTransport);
        server = new TThreadedSelectorServer(args);
        logger.info("rpc server is starting server on port {}",properties.getThriftPort());

        // 创建线程池 异步启动
        Executor executor = new ThreadPoolExecutor(1, 1,
                0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> {
            Thread thread = new Thread(r);
            thread.setName("thrift-server-");
            return thread;
        });
        logger.info("run here!~!");
        executor.execute(() -> server.serve());
    }

    private TThreadedSelectorServer.Args thriftServerArgs(TProcessor processor,TNonblockingServerTransport serverTransport){
        // 参数设置
        TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(serverTransport);
        args.processor(processor);
        // 设置协议工厂，高效率的、密集的二进制编码格式进行数据传输协议
        args.protocolFactory(new TBinaryProtocol.Factory());
        // 设置传输工厂，使用非阻塞方式，按块的大小进行传输，类似于Java中的NIO
        args.transportFactory(new TFramedTransport.Factory());
        // 设置处理器工厂,只返回一个单例实例
        args.processorFactory(new TProcessorFactory(processor));

        return args;
    }
    public boolean isRegisteAble(){
        return registeAble;
    }

    /**
     * spring容器初始化完成后将服务注册到zk
     */
    @EventListener(ApplicationReadyEvent.class)
    public void registerZookeeper(){
        // 检测是否运行中
        while (!server.isServing()) {
            try {
                Thread.sleep(1L);
            } catch (Exception e) {
                logger.error("rpc server:CPU 等待异常");
            }
        }
            try {
                //创建节点,第一个参数是节点目录，是从第一个到倒数第二个，因为倒数第一个带有/
                zkClient.createPersistent(registerPath.substring(0,registerPath.length() - 1),true);
                String ip = getHostAndPort();
                String path = registerPath + ip;
                logger.info("rpc server : 注册服务 {} 到 zk {}",path,properties.getZkServer());
                if (zkClient.exists(path)){
                    //假设节点已经存在，则删除节点
                    zkClient.delete(path);
                    logger.info("rpc server : 删除已存在节点{}",path);
                }
                // 创建节点
                zkClient.createEphemeral(path);
                // 标记为已注册
                registeAble = true;
                logger.info("rpc server : 成功注册服务{}到zk服务中{}",path,properties.getZkServer());
            }catch (Exception e){
                logger.error("rpc server : 节点注册失败");
            }
    }

    /**
     * 获取本机IP地址和端口
     *
     * @return 本机IP地址和端口: ip:port
     * @throws Exception 异常事件
     */
    private String getHostAndPort() throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        //获得本机IP
        String ip = addr.getHostAddress();
        return ip + ":" + properties.getThriftPort();
    }

    public void stop() {
        try {
            logger.info("rpc server: 关闭RPC服务");
            // 关闭服务
            server.stop();
            String ip = getHostAndPort();
            String path = registerPath + ip;
            // 删除节点
            zkClient.delete(path);
        } catch (Exception e) {
            logger.error("rpc server: rpc服务关闭失败", e);
        }
    }
}
