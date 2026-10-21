package cn.ieasy.framework.common.jpa.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

/**
 * 抽象模型类（不带审计字段）
 *
 * @author LinWang
 */
@MappedSuperclass
public abstract class AbstractModel2 implements Persistable<test> {
    private static final long serialVersionUID = 7205853521241442700L;

    // TODO 需要实现集群化的ID生成机制
    @Id
    private test id;

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "a", column = @Column(name = "a") ),
            @AttributeOverride(name = "b", column = @Column(name = "b") ) })

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Persistable#getId()
     */
    @Override
    public test getId() {
        return this.id;
    }

    /**
     * Sets the id of the entity.
     *
     * @param id the id to set
     */
    protected void setId(final test id) {
        this.id = id;
    }

    /**
     * Must be {@link Transient} in order to ensure that no JPA provider
     * complains because of a missing setter.
     *
     * @see DATAJPA-622
     * @see org.springframework.data.domain.Persistable#isNew()
     */
    @Override
    @Transient
    public boolean isNew() {
        return null == this.getId();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), this.getId());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }

        final Persistable<?> that = (Persistable<?>) obj;

        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int hashCode = 17;

        hashCode += null == this.getId() ? 0 : this.getId().hashCode() * 31;

        return hashCode;
    }
}
