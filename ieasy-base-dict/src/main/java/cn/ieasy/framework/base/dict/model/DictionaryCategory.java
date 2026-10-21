package cn.ieasy.framework.base.dict.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.validator.constraints.Length;

import cn.ieasy.framework.common.jpa.model.AbstractAuditModel;

/**
 * 码表类型
 */
@Entity
public class DictionaryCategory extends AbstractAuditModel {

    /**
     *
     */
    private static final long serialVersionUID = 3369360305424639573L;

    /**
     * 码表类型的key
     * <p>
     * <li>英文字符
     * <li>同一个码表内不能重复
     * <li>生成后不可变更，可以标记废弃
     */
    @Column(name = "dict_key", length = 36, nullable = false, unique = true)
    @Length(max = 36)
    private String key;

    /**
     * 码表的描述
     */
    @Length(max = 255)
    @Column(length = 255)
    private String description;

    /**
     * 是否已废弃
     */
    private boolean discarded = false;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private final List<Dictionary> dictionaries = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DictionarysVersion categoryVersion = new DictionarysVersion();

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

    public boolean isDiscarded() {
        return this.discarded;
    }

    public void setDiscarded(final boolean discarded) {
        this.discarded = discarded;
    }

    public List<Dictionary> getDictionaries() {
        return this.dictionaries;
    }

    public DictionarysVersion getCategoryVersion() {
        return this.categoryVersion;
    }

    public void setCategoryVersion(final DictionarysVersion categoryVersion) {
        this.categoryVersion = categoryVersion;
    }

    public void addVersion() {
        this.categoryVersion.addVersion();
    }
}
