package com.lpy.command;

public interface CommandRequest {
    /**
     * 命令的ID
     */
    public final static String COMMAND_ID="command_id";

    /**
     * 获取命令
     * @return
     */

    public String getCommandId();
    /**
     * 验证命令参数的完整性
     */
    public void validate();
}
