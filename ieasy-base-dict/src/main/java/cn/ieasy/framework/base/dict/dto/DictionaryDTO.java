package cn.ieasy.framework.base.dict.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import cn.ieasy.framework.common.web.dto.AbstractDTO;

public class DictionaryDTO extends AbstractDTO {

    @NotNull
    private Long categoryId;
    @NotBlank
    @Length(min = 1, max = 36)
    private String key;
    private String value;
    @NotBlank
    @Length(max = 50)
    private String text;

    private String description;

    private Long parentId;

    private boolean editable = true;

    private boolean discarded = false;

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

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(final Long parentId) {
        this.parentId = parentId;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(final Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setDiscarded(final boolean discarded) {
        this.discarded = discarded;
    }

    public Boolean isDiscarded() {
        return this.discarded;
    }

    public boolean isEditable() {
        return this.editable;
    }

    public void setEditable(final boolean editable) {
        this.editable = editable;
    }
}
