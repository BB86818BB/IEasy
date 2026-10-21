package cn.ieasy.framework.base.dict.dto;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import cn.ieasy.framework.common.web.dto.AbstractDTO;

public class DictionaryImportDTO extends AbstractDTO {

    private String key;

    private String description;

    private List<CodeDTO> codes;

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

    public List<CodeDTO> getCodes() {
        return this.codes;
    }

    public void setCodes(final List<CodeDTO> dictionarys) {
        this.codes = dictionarys;
    }

    public static class CodeDTO {
        @NotBlank
        private String key;
        @NotBlank
        private String value;

        private String text;

        private String description;

        private boolean editable = true;

        private String parentKey;

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

        public String getDescription() {
            return this.description;
        }

        public void setDescription(final String description) {
            this.description = description;
        }

        public boolean isEditable() {
            return this.editable;
        }

        public void setEditable(final boolean editable) {
            this.editable = editable;
        }

        public String getParentKey() {
            return this.parentKey;
        }

        public void setParentKey(final String parentKey) {
            this.parentKey = parentKey;
        }
    }

}
