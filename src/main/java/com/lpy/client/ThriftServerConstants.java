package com.lpy.client;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * thrift 服务状态
 * @author liangpengyu
 * @date 2022/3/22
 */
public class ThriftServerConstants {
    private ThriftServerConstants() {
        throw new IllegalStateException("Utility class.");
    }

    /**
     * 通过服务名拿到服务地址列表
     */
    public static final ConcurrentMap<String, List<String>> THRIFT_SERVER = new ConcurrentHashMap<>();
    /**
     * !non!ssss=/non/ssss
     */
    public static final ConcurrentMap<String, String> THRIFT_SERVER_KEY = new ConcurrentHashMap<>();

    public static final String ZK_NODE_NAMESPACE = "!";
    public static final String ZK_NODE_DELIMITER = "/";

    public static final String ZK_NODE_SUFIX = "/";
    public static final String SUCCESS = "00";
    public static final String RET_CODE = "ret_code";
    public static final String RET_MSG = "ret_msg";
}
