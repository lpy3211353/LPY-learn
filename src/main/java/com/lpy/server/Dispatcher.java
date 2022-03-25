package com.lpy.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lpy.command.CommandRequest;
import com.lpy.command.CommandResponse;
import com.lpy.command.CommandService;
import com.lpy.command.DefaultCommandResponse;
import com.lpy.server.config.RpcServerProperties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * thrift RPC分发器
 *
 * @author lujianyuan
 */
public class Dispatcher {

    private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    private static final String COMMAND_NOT_SET = "cmdId not set";

    private final RpcServerProperties serverProp;
    private final Map<String, CommandService> serviceMap;

    public Dispatcher(RpcServerProperties serverProp,
                      Map<String, CommandService> serviceMap) {
        this.serverProp = serverProp;
        this.serviceMap = null != serviceMap ? serviceMap : new HashMap<>();
    }

    /**
     * 分发请求
     *
     * @param logId   日志id
     * @param request 请求信息
     * @return rpc处理响应
     */
    public String dispatch(String logId, String request) {
        // rpc请求响应
        CommandResponse response;
        //默认值为COMMAND_NOT_SET 即便 没有设置对应服务
        String commandId = COMMAND_NOT_SET;
        try {
            // 解析commandId
            commandId = resolveCommandId(request);
            // 查询commandId对应的处理服务
            CommandService commandService = serviceMap.get(commandId);
            //没有查询到服务则抛出异常
            if (null == commandService) {
                throw new IllegalStateException("没有找到处理服务");
            }
            // 解析为处理服务对应的请求类型
            CommandRequest rpcRequest = JSON.parseObject(request, commandService.getCommandRequestClass());
            // 请求处理
            response = commandService.execute(rpcRequest);
        } catch (Exception e) {
            logger.error("thrift服务处理异常, 异常为: {}", e.getMessage());
            response=null;
        } catch (Throwable e) {
            logger.error("thrift服务处理错误", e);
            response=null;
        }

        return JSON.toJSONString(response);
    }

    /**
     * 获取本地IP地址
     * @return 返回本地ip地址，如果拿不到那就是127.0.0.1
     */
    private String getLocalIp() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostAddress();
        } catch (Exception e) {
            logger.error("本地ip获取失败", e);
        }
        return "127.0.0.1";
    }

    /**
     * 通过请求解析commandId
     * @param request 请求信息
     * @return commandId
     * @throws Exception 空的commandId的时候抛出异常，请检查是否缺少commandId
     */
    private String resolveCommandId(String request) throws Exception {
        JSONObject requestJson = JSON.parseObject(request);
        String commandId = requestJson.getString(CommandRequest.COMMAND_ID);
        if (StringUtils.isBlank(commandId)) {
            // 抛出缺少命令字的异常
            throw new Exception();
        }
        return commandId;
    }

    private DefaultCommandResponse buildDefaultResponse() {
        return buildResponse(serverProp.getDefaultErrCode(), serverProp.getDefaultErrMsg());
    }

    private DefaultCommandResponse buildResponse(String retCode, String retMsg) {
        DefaultCommandResponse resp = new DefaultCommandResponse();
        resp.setRetCode(retCode);
        resp.setRetMsg(retMsg);
        return resp;
    }
}