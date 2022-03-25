package com.lpy.server.config;

import com.lpy.command.CommandService;
import com.lpy.server.Dispatcher;
import com.lpy.server.HealthListener;
import com.lpy.server.RpcApiServiceImpl;
import com.lpy.server.ServerRpcService;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.Serializable;
import java.util.Map;


@Configuration
@EnableScheduling
/**
 * @param prefix 前缀
 * @param name 键名
 * @param havingValue 键值
 */
@ConditionalOnProperty(prefix = "com.lpy.server",name = "enabled",havingValue = "true")
@EnableConfigurationProperties(RpcServerAutoConfig.RpcServerProp.class)
/**
 * @deprecated 自动生成bean
 * @author liangpengyu
 */
public class RpcServerAutoConfig {
    private static final Logger logger = LoggerFactory.getLogger(RpcServerAutoConfig.class);
    /**
     * 最大zk会话超时时间， 毫秒
     */
    private static final int MAX_SESSION_TIMEOUT = 3000;

    @Bean
    @ConditionalOnMissingBean
    public RpcServerProperties rpcServerProperties(){
        return new RpcServerProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public ZkClient zkClient(RpcServerProperties properties){
        logger.info("PRC服务配置:初始化 zk client");
        SerializableSerializer serializer = new SerializableSerializer();

        int sessionTimeout = properties.getZkSessionTimeout();
        if (sessionTimeout > MAX_SESSION_TIMEOUT) {
            sessionTimeout = MAX_SESSION_TIMEOUT;
        }
        ZkClient zkClient = new ZkClient(properties.getZkServer(),sessionTimeout,properties.getZkConnectionTimeout(),serializer);
        logger.info("RPC服务配置:初始化 zk client 成功");
        return zkClient;
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcApiServiceImpl rpcAdapterImpl(Dispatcher dispatcher) {
        RpcApiServiceImpl rpcAdapter = new RpcApiServiceImpl();
        rpcAdapter.setDispatcher(dispatcher);
        return rpcAdapter;
    }



    /**
     * 分发器
     * @param prop 参数
     * @param commandServiceProvider 服务名对应服务
     * @return rpc分发器
     */
    @Bean
    @ConditionalOnMissingBean
    public Dispatcher dispatcher(RpcServerProperties prop,
                                 ObjectProvider<Map<String, CommandService>> commandServiceProvider) {
        return new Dispatcher(prop, commandServiceProvider.getIfAvailable());
    }

    /**
     * rpc服务类； 负责监听端口，提供rpc服务
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    public ServerRpcService serverRpcService(RpcApiServiceImpl rpcApiService, ZkClient zkClient, RpcServerProperties serverProp) {
        logger.info("BASE(rpc): rpc server enabled");
        return new ServerRpcService(rpcApiService, zkClient, serverProp);
    }


    /**
     * rpc server断线重连配置
     */
    @Bean
    @ConditionalOnBean({ZkClient.class, ServerRpcService.class, RpcServerProperties.class})
    @ConditionalOnProperty(prefix = "com.lpy.server", name = "health", havingValue = "true")
    public HealthListener offlineReconnect(ZkClient zkClient, ServerRpcService thriftRpcService, RpcServerProperties prop) {
        logger.info("BASE(rpc server): 断线重连已打开");
        return new HealthListener(zkClient, prop.getNodePath(), prop.getThriftPort(), thriftRpcService);
    }

    @ConfigurationProperties("com.lpy.server")
    public static class RpcServerProp implements Serializable {

        /**
         * 是否启用rpc server
         */
        private Boolean enabled;

        private Boolean health;

        public Boolean getHealth() {
            return health;
        }

        public void setHealth(Boolean health) {
            this.health = health;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class EnableEntity implements Serializable {

        private Boolean enabled;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }
}
