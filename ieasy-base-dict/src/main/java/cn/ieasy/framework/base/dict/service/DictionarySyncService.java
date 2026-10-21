package cn.ieasy.framework.base.dict.service;

import java.util.List;
import java.util.Map;

import cn.ieasy.framework.base.dict.bean.DictionariesSyncBean;
import cn.ieasy.framework.base.dict.bean.DictionaryBean;
import cn.ieasy.framework.base.dict.bean.DictionaryCategoryBean;

public interface DictionarySyncService {

    /**
     * 获取所有的码表数据(系统间同步用)
     *
     * @return
     */
    public Map<String, DictionaryCategoryBean> getAllDictionarys();

    /**
     * 根据同步版本号，获取版本变更后的所有的码表数据(系统间同步用)
     *
     * @return 同步版本号未变更的，返回空
     */
    public DictionariesSyncBean getAllDictionarys(final long syncVersion);

    /**
     * 根据码表类别(Key)获取该码表完整信息(APP端接口)
     *
     * @param categoryKey 码表类型KEY
     * @param lastSyncTime 上次同步时间
     * @return
     */
    public DictionaryCategoryBean getChangedDictionary(final String categoryKey, final Long lastSyncTime);

    /**
     * 根据码表类别(Key)获取该码表完整信息(APP端接口)
     *
     * @param categoryKey 码表类型KEY
     * @return
     */
    public List<DictionaryBean> getDictionaryByCategory(final String categoryKey);

    /**
     * 加载码表数据到内存（后台任务）
     */
    public void loadDictionarys();
}
