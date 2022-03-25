package com.lpy.command;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 命令制实现工厂类，实现命令接口execute方法，实现方法时，将命令实体转化成实现类的实现方法，并且调用validate方法，进行数据校验。
 * @author lujianyuan
 *
 * @param <T>
 */
public abstract class AbstractCommandServiceFactory<T extends CommandRequest> implements CommandService {

    @SuppressWarnings("unchecked")
    @Override
    public CommandResponse execute(CommandRequest commandRequest) throws Exception {
        commandRequest.validate();
        validate((T) commandRequest);
        return executeCommand((T) commandRequest);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends CommandRequest> getCommandRequestClass() {
        Type genType = this.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class<? extends CommandRequest>) params[0];
    }

    protected void validate(T commandRequest){
        //在此处进行数据校验
    }
    /**
     * 执行命令将正确参数传给实现类
     * @param commandRequest
     * @return
     * @throws Exception
     */
    protected abstract CommandResponse executeCommand(T commandRequest)throws Exception;
}