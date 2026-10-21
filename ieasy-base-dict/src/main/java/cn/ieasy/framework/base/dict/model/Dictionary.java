package cn.ieasy.framework.base.dict.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import cn.ieasy.framework.common.jpa.model.AbstractAuditModel;

/**
 * 码表的数据字典
 */
@Entity
@Table(indexes = {@Index(columnList = "dict_key") })
public class Dictionary extends AbstractAuditModel {

    private static final long serialVersionUID = -364688751618320356L;

    /**
     * 所属分类（创建后不可变更）
     */
    @NotNull
    @ManyToOne
    private DictionaryCategory category;

    /**
     * 字典key（创建后不可变更）
     * <p>
     * <li>英文字符
     * <li>同一个码表内不能重复
     */
    @Column(name = "dict_key", length = 36, nullable = false)
    @Length(max = 36)
    private String key;

    /**
     * 字典关联信息value
     */
    @Column(length = 50, nullable = true)
    @Length(max = 50)
    private String value;

    /**
     * 字典text
     */
    @Length(max = 50)
    @Column(length = 50, nullable = false)
    private String text;

    /**
     * 父级字典：最上层字典为null
     */
    @ManyToOne
    private Dictionary parent;

    /**
     * 同一级字典的序号（升序）
     */
    private int sortIndex;

    /**
     * 字典描述
     */
    @Length(max = 255)
    @Column(length = 255)
    private String description;

    /**
     * 下级字典 可以为null
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @OrderBy("sortIndex ASC")
    private List<Dictionary> children = new ArrayList<>();

    /**
     * 是否可编辑
     * <p>
     * 若为否，创建后（editable，discarded）也不可编辑
     */
    private boolean editable = true;

    /**
     * 是否已废弃
     * <p>
     * 若editable为true，则不可编辑
     */
    private boolean discarded;

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

    public Dictionary getParent() {
        return this.parent;
    }

    public void setParent(final Dictionary parent) {
        this.parent = parent;
    }

    public int getSortIndex() {
        return this.sortIndex;
    }

    public void setSortIndex(final int order) {
        this.sortIndex = order;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public List<Dictionary> getChildren() {
        return this.children;
    }

    public void setChildren(final List<Dictionary> children) {
        this.children = children;
    }

    public boolean isDiscarded() {
        return this.discarded;
    }

    public void setDiscarded(final boolean discarded) {
        this.discarded = discarded;
    }

    public DictionaryCategory getCategory() {
        return this.category;
    }

    public void setCategory(final DictionaryCategory category) {
        this.category = category;
    }

    public boolean isEditable() {
        return this.editable;
    }

    public void setEditable(final boolean editable) {
        this.editable = editable;
    }
}
