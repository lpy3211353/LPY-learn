package com.lpy.client.config;

import com.lpy.client.ClientRpcService;
import com.lpy.client.zookeeper.RefererChildListener;
import com.lpy.client.zookeeper.RefererRegister;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.Serializable;

/**
 * @author liangpengyu
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "com.lpy.client", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(RpcClientAutoConfig.RpcClientProp.class)
public class RpcClientAutoConfig {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientAutoConfig.class);

    @Bean
    @ConditionalOnMissingBean
    public RpcClientProperties rpcClientProp() {
        return new RpcClientProperties();
    }

    private static final int MAX_SESSION_TIMEOUT = 3000;

    /**
     * 初始化zk
     * @param prop zk参数
     * @return 初始化好的zk客户端
     */
    @Bean
    @ConditionalOnMissingBean
    public ZkClient zkClient(RpcClientProperties prop) {
        logger.info("RPC客户端: 初始化 zk client中");
        SerializableSerializer serializer = new SerializableSerializer();

        // 会话超时
        int sessionTimeout = prop.getZkSessionTimeout();
        if (sessionTimeout > MAX_SESSION_TIMEOUT) {
            sessionTimeout = MAX_SESSION_TIMEOUT;
        }
        ZkClient zkClient = new ZkClient(prop.getZkServer(), sessionTimeout, prop.getZkConnectionTimeout(), serializer);
        logger.info("RPC客户端: 初始化 zk client 成功");
        return zkClient;
    }


    /**
     * 客户端bean
     * @param refererRegister 注册监听
     * @param prop 参数
     * @return 客户端bean
     */
    @Bean
    @ConditionalOnMissingBean
    public ClientRpcService clientRpcService(RefererRegister refererRegister,
                                             RpcClientProperties prop) {
        logger.info("BASE(rpc): rpc client enabled");
        return new ClientRpcService(refererRegister, prop);
    }

    /**
     * zk节点变化监听器
     */
    @Bean
    @ConditionalOnMissingBean
    public RefererChildListener refererChildListener() {
        return new RefererChildListener();
    }

    /**
     * zk节点变化注册
     */
    @Bean
    @ConditionalOnMissingBean
    public RefererRegister refererRegister(ZkClient zkClient, RefererChildListener refererChildListener) {
        return new RefererRegister(zkClient, refererChildListener);
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientRpcService rpcAdapterService(RefererRegister refererRegister,
                                              RpcClientProperties prop) {
        logger.info("BASE(rpc): rpc client enabled");
        return new ClientRpcService(refererRegister, prop);
    }

    @ConfigurationProperties("com.lpy.client")
    public static class RpcClientProp implements Serializable {

        private Boolean enabled;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }
}
