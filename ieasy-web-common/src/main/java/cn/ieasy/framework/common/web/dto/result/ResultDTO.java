package cn.ieasy.framework.common.web.dto.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * 返回结果
 */
public class ResultDTO<T> extends AbstractResultDTO {

    private static final long serialVersionUID = 1668914867578552488L;
    /**
     * 对象数据
     */
    @ApiModelProperty(value = "业务数据", position = 1)
    private T data;

    ///////////////////////////////////////
    // Getter
    ///////////////////////////////////////
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "data", index = 2)
    public T getData() {
        return this.data;
    }

    ///////////////////////////////////////
    // Setter
    ///////////////////////////////////////
    private void setData(final T data) {
        this.data = data;
    }

    ///////////////////////////////////////
    // Constructor
    ///////////////////////////////////////
    public ResultDTO() {
    }

    ResultDTO(final Status status) {
        this.status = status;
    }

    ///////////////////////////////////////
    // Builder
    ///////////////////////////////////////
    public static ResultDTO<Void> success() {
        final ResultDTO<Void> result = new ResultDTO<>(Status.success);
        return result;
    }

    public static ResultDTO<Void> failure(final ResultError... errors) {
        final ResultDTO<Void> result = new ResultDTO<>(Status.failure);
        result.setErrors(errors);
        return result;
    }

    public static <T> ResultDTO<T> success(final T data) {
        final ResultDTO<T> result = new ResultDTO<>(Status.success);
        result.setData(data);
        return result;
    }

    public static <T> ResultDTO<T> failure(final T data, final ResultError... errors) {
        final ResultDTO<T> result = new ResultDTO<>(Status.failure);
        result.setData(data);
        result.setErrors(errors);
        return result;
    }
}
