package cn.ieasy.framework.common.web.exception;

import cn.ieasy.framework.common.web.dto.result.ResultError;

/**
 * Rest请求结果运行时异常<br>
 * 主要为了包装ResultDTO中的异常
 * 
 * @author LinWang
 */
public class RestResultRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 7913293827793560253L;

    private ResultError[] errors;

    public RestResultRuntimeException(final ResultError[] errors, final String message) {
        super(message);
        this.setErrors(errors);
    }

    public ResultError[] getErrors() {
        return this.errors;
    }

    public void setErrors(final ResultError[] errors) {
        this.errors = errors;
    }
}
