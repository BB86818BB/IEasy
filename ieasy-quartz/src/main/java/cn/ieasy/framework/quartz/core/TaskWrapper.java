package cn.ieasy.framework.quartz.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 支持回调的异步接口
 */
public interface TaskWrapper {
    /**
     * 执行业务逻辑
     */
    @JsonIgnore
    void execute();

    /**
     * 执行回调处理
     *
     * @param isFinished true:执行完成 flase:超出最大尝试次数
     */
    @JsonIgnore
    void callback(boolean isFinished);

    /**
     * 判断业务是否完成
     *
     * @return
     */
    @JsonIgnore
    boolean isFinished();
}
