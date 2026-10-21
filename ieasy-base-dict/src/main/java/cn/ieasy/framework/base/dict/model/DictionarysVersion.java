package cn.ieasy.framework.base.dict.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.ieasy.framework.common.jpa.model.AbstractModel;

@Entity
public class DictionarysVersion extends AbstractModel {
    private static final long serialVersionUID = 8349324106376934493L;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate = new Date();

    private Long version = 0l;

    public Date getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public void setLastModifiedDate(final Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }

    public void addVersion() {
        this.version++;
        this.lastModifiedDate = new Date();
    }
}
