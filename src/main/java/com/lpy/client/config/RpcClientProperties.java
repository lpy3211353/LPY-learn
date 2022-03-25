package com.lpy.client.config;

import org.springframework.beans.factory.annotation.Value;

/**
 * rpc client 配置类
 * @author liangpengyu
 */
public class RpcClientProperties {
    /**
     * zk地址
     */
    @Value("${zk.server}")
    private String zkServer;

    /**
     * 验证环境前缀，已经启用
     */
    @Value("${envi.verify.subfix.name:}")
    private String verifySubFix;

    /**
     * 注册中心: zk会话超时时间，单位毫秒
     */
    @Value("${zk.register.sessionTimeout:3000}")
    private int zkSessionTimeout;
    /**
     * 注册中心: zk连接超时时间，单位毫秒
     */
    @Value("${zk.register.connectionTimeout:10000}")
    private int zkConnectionTimeout;

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

    @Value("${thrift.maxDataSize:16384000}")
    private int maxDataSize;

    public int getMaxDataSize() {
        return maxDataSize;
    }

    public void setMaxDataSize(int maxDataSize) {
        this.maxDataSize = maxDataSize;
    }

    public int getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    public void setZkSessionTimeout(int zkSessionTimeout) {
        this.zkSessionTimeout = zkSessionTimeout;
    }

    public int getZkConnectionTimeout() {
        return zkConnectionTimeout;
    }

    public void setZkConnectionTimeout(int zkConnectionTimeout) {
        this.zkConnectionTimeout = zkConnectionTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getZkServer() {
        return zkServer;
    }

    public void setZkServer(String zkServer) {
        this.zkServer = zkServer;
    }

    public String getVerifySubFix() {
        return verifySubFix;
    }

    public void setVerifySubFix(String verifySubFix) {
        this.verifySubFix = verifySubFix;
    }
}
