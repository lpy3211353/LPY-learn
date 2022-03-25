package com.lpy.server;

import com.alibaba.fastjson.JSON;
import com.lpy.command.DefaultCommandResponse;
import com.lpy.protocol.RPCAPIService;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.MDC;

/**
 * @author liangpengyu
 */
public class RpcApiServiceImpl implements RPCAPIService.Iface {
    private Dispatcher dispatcher;
    @Override
    public String invoke(String logid, String request) throws TException {
        //logid为空时，设置默认值
        if (StringUtils.isBlank(logid)) {
            DefaultCommandResponse commandResponse = new DefaultCommandResponse();
            commandResponse.setRetCode("V0");
            commandResponse.setRetMsg("日志跟踪ID");
            return JSON.toJSONString(commandResponse);
        }
        try {
            MDC.put("logid", logid);
            //否则正常进入分发器
            return dispatcher.dispatch(logid, request);
        } finally {
            MDC.remove("logid");
        }
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}
