package cn.ieasy.framework.common.web.exception;

/**
 * 自定义异常类
 * 
 * @author LinWang
 */
public class CustomRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -6882178806561789418L;

    private final String code;
    private Object[] params;

    public CustomRuntimeException(final String code) {
        this.code = code;
    }

    public CustomRuntimeException(final String code, final Object... params) {
        this.code = code;
        this.params = params;
    }

    public CustomRuntimeException(final String code, final String defaultMessage) {
        super(defaultMessage);
        this.code = code;
    }

    public CustomRuntimeException(final String code, final String defaultMessage, final Object... params) {
        super(defaultMessage);
        this.code = code;
        this.params = params;
    }

    public CustomRuntimeException(final String code, final String defaultMessage, final Throwable cause) {
        super(defaultMessage, cause);
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public Object[] getParams() {
        return this.params;
    }
}
