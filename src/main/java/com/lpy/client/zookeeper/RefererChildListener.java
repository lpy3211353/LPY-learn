package com.lpy.client.zookeeper;

import com.lpy.client.ThriftServerConstants;
import org.I0Itec.zkclient.IZkChildListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * 子节点监听器
 * @author liangpengyu
 */
public class RefererChildListener implements IZkChildListener {

    private static final Logger logger = LoggerFactory.getLogger(RefererChildListener.class);

    @Override
    public void handleChildChange(String s, List<String> list) throws Exception {
        if (logger.isDebugEnabled()){
            logger.debug("监听到子节点{}的环境变化",s);
        }
        if (null == list||list.isEmpty()){
            logger.warn("{}服务已全部移除。请检查{}服务是否正常，如果{}没有正常启动，调用时会导致{}服务不可用",s,s,s,s);
            return;
        }
        logger.info("{}服务已开启",s);
        //将服务和节点信息写入rpc状态中
        ThriftServerConstants.THRIFT_SERVER.put(s,list);
    }
}
