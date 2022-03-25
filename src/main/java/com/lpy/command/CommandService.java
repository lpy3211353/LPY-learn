package com.lpy.command;

/**
 * @author liangpengyu
 */
public interface CommandService {
    /***
     * 执行RPC命令
     * @param commandRequest
     * @return
     * @throws Exception
     */
    public CommandResponse execute(CommandRequest commandRequest)throws Exception;
    /**
     * 获取RPC命令实体类型
     * @return
     */
    public Class<? extends CommandRequest> getCommandRequestClass();
}
