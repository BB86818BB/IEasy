package cn.ieasy.framework.common.web.dto.result;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cn.ieasy.framework.common.web.exception.CustomRuntimeException;
import io.swagger.annotations.ApiModelProperty;

public class ListResultDTO<T> extends AbstractResultDTO {

    private static final long serialVersionUID = 8756487352760469154L;
    /**
     * 列表数据
     */
    @ApiModelProperty(value = "业务数据（List）", position = 1)
    protected List<T> data;

    ///////////////////////////////////////
    // Getter
    ///////////////////////////////////////
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "data", index = 3)
    public List<T> getData() {
        return this.data;
    }

    ///////////////////////////////////////
    // Setter
    ///////////////////////////////////////
    public void setData(final List<T> data) {
        this.data = data;
    }

    ///////////////////////////////////////
    // Constructor
    ///////////////////////////////////////
    public ListResultDTO() {
    }

    ListResultDTO(final Status status) {
        this.status = status;
    }

    ///////////////////////////////////////
    // Builder
    ///////////////////////////////////////
    public static <T> ListResultDTO<T> success(final List<T> listData) {
        if (listData == null) {
            throw new CustomRuntimeException("NullPointerException", "The formal parameter 'listData' cannot be null");
        }

        final ListResultDTO<T> result = new ListResultDTO<>(Status.success);
        result.setData(listData);
        return result;
    }

    public static <T> ListResultDTO<T> failure(final List<T> listData, final ResultError... errors) {
        final ListResultDTO<T> result = new ListResultDTO<>(Status.failure);
        result.setData(listData);
        result.setErrors(errors);
        return result;
    }
}
