package cn.ieasy.framework.base.dict.bean;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class DictionaryBean {

    /** 唯一标识KEY */
    @Length(max = 36)
    private String key;

    /** 字典value */
    @Length(max = 50)
    @NotBlank
    private String value;

    /** 字典text */
    @Length(max = 50)
    private String text;

    /** 父级字典：最上层字典为null */
    private String parentKey;

    /** 同一级字典的序号 */
    private int order;

    @Length(max = 255)
    private String description;

    /** 下级字典 可以为null */
    private List<DictionaryBean> children = new ArrayList<>();

    public String getKey() {
        return this.key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getParentKey() {
        return this.parentKey;
    }

    public void setParentKey(final String parentKey) {
        this.parentKey = parentKey;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<DictionaryBean> getChildren() {
        return this.children;
    }

    public void setChildren(final List<DictionaryBean> children) {
        this.children = children;
    }
}
