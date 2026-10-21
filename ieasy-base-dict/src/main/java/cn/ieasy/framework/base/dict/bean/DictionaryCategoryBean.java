package cn.ieasy.framework.base.dict.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DictionaryCategoryBean {

    private String categoryKey;

    private List<DictionaryBean> dictionarys = new ArrayList<>();

    private Date updatedTime;

    public String getCategoryKey() {
        return this.categoryKey;
    }

    public void setCategoryKey(final String categoryKey) {
        this.categoryKey = categoryKey;
    }

    public List<DictionaryBean> getDictionarys() {
        return this.dictionarys;
    }

    public void setDictionarys(final List<DictionaryBean> dictionarys) {
        this.dictionarys = dictionarys;
    }

    public Date getUpdatedTime() {
        return this.updatedTime;
    }

    public void setUpdatedTime(final Date lastUpdatedTime) {
        this.updatedTime = lastUpdatedTime;
    }
}
