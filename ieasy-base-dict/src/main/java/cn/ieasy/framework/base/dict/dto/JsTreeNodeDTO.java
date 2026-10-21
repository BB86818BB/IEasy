package cn.ieasy.framework.base.dict.dto;

import java.util.HashMap;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JsTreeNodeDTO {

    @NotNull
    private Long id;

    private Long parent;

    private String text;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final HashMap<String, Object> state = new HashMap<>();

    @JsonProperty
    public String getId() {
        return this.id.toString();
    }

    @JsonIgnore
    public void setId(final Long id) {
        this.id = id;
    }

    @JsonProperty
    public String getParent() {
        return this.parent != null ? this.parent.toString() : "#";
    }

    @JsonIgnore
    public void setParent(final Long parent) {
        this.parent = parent;
    }

    @JsonProperty
    public String getText() {
        return this.text;
    }

    @JsonIgnore
    public void setText(final String text) {
        this.text = text;
    }

    @JsonProperty
    public HashMap<String, Object> getState() {
        return this.state;
    }
}
