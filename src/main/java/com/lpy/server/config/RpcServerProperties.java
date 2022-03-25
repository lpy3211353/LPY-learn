package com.lpy.server.config;

import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

/**
 * rpc服务配置
 * @author liangpengyu
 * @date 2022/3/22
 */
public class RpcServerProperties implements Serializable {

    /**
     * zk地址
     */
    @Value("${zk.server}")
    private String zkServer;

    /**
     * 服务监听端口
     */
    @Value("${thrift.register.port}")
    private int thriftPort;

    /**
     * 节点路径
     */
    @Value("${thrift.register.node}")
    private String nodePath;

    /**
     * 可配置返回码: 默认返回码
     */
    @Value("${thrift.system.error.code:ZZ}")
    private String defaultErrCode;

    /**
     * 可配置返回码: 默认返回信息
     */
    @Value("${thrift.system.error.msg:网络请求失败}")
    private String defaultErrMsg;

    /**
     * zk会话超时时间
     */
    @Value("${zk.register.sessionTimeout:3000}")
    private int zkSessionTimeout;

    /**
     * 注册中心: zk连接超时时间，单位毫秒
     */
    @Value("${zk.register.connectionTimeout:10000}")
    private int zkConnectionTimeout;

    /**
     * 验证环境前缀，已经启用
     */
    @Value("${envi.verify.subfix.name:}")
    private String verifySubFix;

    /**
     * rpc请求连接超时，单位毫秒
     */
    @Value("${thrift.connectTimeout:10000}")
    private int connectTimeout;
    /**
     * rpc请求读取超时，单位毫秒
     */
    @Value("${thrift.socketTimeout:30000}")
    private int socketTimeout;
    /**
     * rpc请求连接、读取超时，单位毫秒；若设置，将覆盖以上 connectTimeout 以及 socketTimeout
     */
    @Value("${thrift.timeout:0}")
    private int timeout;



    public String getZkServer() {
        return zkServer;
    }

    public int getThriftPort() {
        return thriftPort;
    }

    public String getNodePath() {
        return nodePath;
    }

    public String getDefaultErrCode() {
        return defaultErrCode;
    }

    public String getDefaultErrMsg() {
        return defaultErrMsg;
    }

    public int getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    public int getZkConnectionTimeout() {
        return zkConnectionTimeout;
    }

    public String getVerifySubFix() {
        return verifySubFix;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public int getTimeout() {
        return timeout;
    }
}
