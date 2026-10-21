package cn.ieasy.framework.base.dict.dto;

public class DictChangedQueryDTO {
    private String category;
    private Long updatedTime;

    public Long getUpdatedTime() {
        return this.updatedTime;
    }

    public void setUpdatedTime(final Long lastUpdatedTime) {
        this.updatedTime = lastUpdatedTime;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }
}
