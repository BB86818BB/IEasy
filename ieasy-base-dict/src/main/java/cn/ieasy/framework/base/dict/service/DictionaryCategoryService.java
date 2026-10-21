package cn.ieasy.framework.base.dict.service;

import java.util.List;

import cn.ieasy.framework.base.dict.model.Dictionary;
import cn.ieasy.framework.base.dict.model.DictionaryCategory;

public interface DictionaryCategoryService {

    DictionaryCategory createCategory(DictionaryCategory model);

    DictionaryCategory discardCategory(final Long id);

    DictionaryCategory updateCategory(DictionaryCategory model);

    void deleteAll();

    List<DictionaryCategory> findAll();

    /**
     * 创建(管理接口)
     *
     * @param dict
     * @return
     */
    public Dictionary createDictionary(Dictionary dict);

    /**
     * 更新(管理接口)
     *
     * @param dict
     * @return
     */
    public Dictionary updateDictionary(Dictionary dict);

    /**
     * 禁用(管理接口)
     *
     * @param id
     * @return
     */
    Dictionary discardDictionary(Dictionary dict);

    /**
     * 删除(管理接口)
     *
     * @param id
     */
    public void deleteDictionary(Dictionary dict);

    /**
     * 修改码表排序(管理接口)
     *
     * @param id
     * @param order
     * @return
     */
    public Dictionary changeDictionaryOrder(Dictionary dict, int order);
}
