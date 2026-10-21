package cn.ieasy.framework.base.dict.service;

import cn.ieasy.framework.base.dict.model.Dictionary;

/**
 * 码表管理服务接口
 */
public interface DictionaryService {

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
    Dictionary discardDictionary(Long id);

    /**
     * 删除(管理接口)
     *
     * @param id
     */
    public void deleteDictionary(Long id);

    /**
     * 修改码表排序(管理接口)
     *
     * @param id
     * @param order
     * @return
     */
    public Dictionary changeDictionaryOrder(Long id, int order);
}
