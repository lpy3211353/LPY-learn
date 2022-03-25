package com.lpy.command;

import com.alibaba.fastjson.annotation.JSONField;

public class DefaultCommandRequest implements CommandRequest{

    @JSONField(name=COMMAND_ID)
    private String commandId;

    @Override
    public String getCommandId() {
        return this.commandId;
    }

    @Override
    public void validate() {

    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }
}
