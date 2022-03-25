package com.lpy.command;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author liangpengyu
 */
public class DefaultCommandResponse implements CommandResponse{
    @JSONField(name=CommandResponse.RET_CODE_NAME)
    private String retCode="00";
    @JSONField(name=CommandResponse.RET_MSG_NAME)
    private String retMsg="处理成功";
    @JSONField(serialize=false)
    private transient boolean success;

    /**
     * 成功响应
     */
    public void buildSuccess(){
        retCode="00";
        retMsg="处理成功";
    }

    /**
     * 获取响应码
     * @return
     */
    public String getRetCode() {
        return retCode;
    }

    /**
     * 获取响应参数
     * @return
     */
    public String getRetMsg() {
        return retMsg;
    }

    @Override
    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }
    @Override
    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    @JSONField(serialize=false)
    public boolean isSuccess(){
        return "00".equals(retCode);
    }
}
