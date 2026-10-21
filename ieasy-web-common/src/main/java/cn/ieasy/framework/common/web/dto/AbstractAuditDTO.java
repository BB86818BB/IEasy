package cn.ieasy.framework.common.web.dto;

import java.util.Date;

import cn.ieasy.framework.common.web.audit.Auditable;
import io.swagger.annotations.ApiModelProperty;

/**
 * 包含审计信息的抽象 DTO 类
 */
public abstract class AbstractAuditDTO extends AbstractDTO implements Auditable {

    private static final long serialVersionUID = 132020791921886009L;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建人", position = 100)
    private String createdBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", position = 101)
    private Date createdDate;

    /**
     * 修改者
     */
    @ApiModelProperty(value = "最后修改人", position = 102)
    private String lastModifiedBy;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "最后修改时间", position = 103)
    private Date lastModifiedDate;

    //////////////////////////////////////////////////
    /// Getter and Setter
    //////////////////////////////////////////////////
    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    public final void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Date getCreatedDate() {
        return this.createdDate;
    }

    public final void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public final void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public Date getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public final void setLastModifiedDate(final Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
