package cn.ieasy.framework.base.dict.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.ieasy.framework.base.dict.bean.DictionariesSyncBean;
import cn.ieasy.framework.base.dict.bean.DictionaryBean;
import cn.ieasy.framework.base.dict.bean.DictionaryCategoryBean;
import cn.ieasy.framework.base.dict.model.Dictionary;
import cn.ieasy.framework.base.dict.model.DictionaryCategory;
import cn.ieasy.framework.base.dict.repository.DictionaryCategoryRepository;
import cn.ieasy.framework.base.dict.repository.DictionaryRepository;
import cn.ieasy.framework.base.dict.service.DictionarySyncService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class DictionarySyncServiceImpl implements DictionarySyncService {

    private final Map<String /* 码表类型KEY */, DictionaryCategoryBean> allDicts = new HashMap<>();

    private final Map<String /* 码表类型KEY */, Long> latestLoadCategoriesVersion = new HashMap<>();

    private long categoriesLoadVersion = -1;

    @Autowired
    private DictionaryCategoryRepository dictionaryCategoryRepository;
    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Override
    public DictionaryCategoryBean getChangedDictionary(final String categoryKey, final Long lastSyncTime) {
        final DictionaryCategoryBean category = this.getAllDictionarys().get(categoryKey);
        if ((lastSyncTime == null) || (category == null)) {
            return category;
        }
        if (category.getUpdatedTime().getTime() > lastSyncTime) {
            return category;
        }
        return null;
    }

    @Override
    public List<DictionaryBean> getDictionaryByCategory(final String categoryKey) {
        final DictionaryCategoryBean category = this.getAllDictionarys().get(categoryKey);
        if (category != null) {
            return category.getDictionarys();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, DictionaryCategoryBean> getAllDictionarys() {
        if (this.allDicts.isEmpty()) {
            this.loadDictionarys();
        }
        return this.allDicts;
    }

    @Override
    public DictionariesSyncBean getAllDictionarys(final long syncVersion) {
        Map<String, DictionaryCategoryBean> map = Collections.emptyMap();
        if (this.categoriesLoadVersion != syncVersion) {
            map = this.getAllDictionarys();
        }
        final DictionariesSyncBean bean = new DictionariesSyncBean();
        bean.setDictionaries(map);
        bean.setVersion(this.categoriesLoadVersion);
        return bean;
    }

    @Override
    @Scheduled(cron = "0 0 * * * *")
    public void loadDictionarys() {
        DictionarySyncServiceImpl.log.info("开始加载码表数据到内存...");
        final List<DictionaryCategory> changedCategories = this.getChangedCategories();
        if (changedCategories.isEmpty()) {
            DictionarySyncServiceImpl.log.info("数据库版本未变化，加载处理取消执行...");
            return;
        }
        changedCategories.forEach(category -> {
            final List<Dictionary> dicts = this.dictionaryRepository
                    .findByCategoryAndParentIsNullOrderBySortIndexAsc(category);
            if (!this.allDicts.containsKey(category.getKey())) {
                this.allDicts.put(category.getKey(), new DictionaryCategoryBean());
            }
            final DictionaryCategoryBean categoryBean = this.allDicts.get(category.getKey());
            categoryBean.setCategoryKey(category.getKey());
            categoryBean.setUpdatedTime(category.getCategoryVersion().getLastModifiedDate());
            categoryBean.getDictionarys().clear();
            dicts.forEach(dict -> {
                categoryBean.getDictionarys().add(this.convertToDictionaryBean(dict));
            });
            this.latestLoadCategoriesVersion.put(category.getKey(), category.getCategoryVersion().getVersion());
        });
        this.categoriesLoadVersion = this.latestLoadCategoriesVersion.values().stream().mapToLong(Long::longValue)
                .sum();
        DictionarySyncServiceImpl.log.info("已加载码表数据到内存");
    }

    private DictionaryBean convertToDictionaryBean(final Dictionary dict) {
        final DictionaryBean bean = new DictionaryBean();
        bean.setKey(dict.getKey());
        bean.setValue(dict.getValue());
        bean.setText(dict.getText());
        bean.setOrder(dict.getSortIndex());
        if (dict.getParent() != null) {
            bean.setParentKey(dict.getParent().getKey());
        }
        bean.setDescription(dict.getDescription());
        if (!dict.getChildren().isEmpty()) {
            for (final Dictionary child : dict.getChildren()) {
                bean.getChildren().add(this.convertToDictionaryBean(child));
            }
        }
        return bean;
    }

    private List<DictionaryCategory> getChangedCategories() {
        final List<DictionaryCategory> categories = this.dictionaryCategoryRepository.findAll();
        if (categories.isEmpty()) {
            return categories;
        } else {
            final List<DictionaryCategory> changedList = new ArrayList<>();
            for (final DictionaryCategory category : categories) {
                final Long lastUpdateVersion = this.latestLoadCategoriesVersion.get(category.getKey());
                if (lastUpdateVersion == null) {
                    // 未加载过
                    changedList.add(category);
                } else if (lastUpdateVersion > category.getCategoryVersion().getVersion()) {
                    // 数据重新导入过或数据错误，导致数据库版本比上次同步的版本还低，重新导入
                    this.allDicts.clear();
                    return categories;
                } else if (lastUpdateVersion < category.getCategoryVersion().getVersion()) {
                    changedList.add(category);
                }
            }
            return changedList;
        }
    }
}
