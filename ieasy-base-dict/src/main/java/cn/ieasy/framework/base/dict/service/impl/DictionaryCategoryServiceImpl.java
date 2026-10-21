package cn.ieasy.framework.base.dict.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.ieasy.framework.base.dict.model.Dictionary;
import cn.ieasy.framework.base.dict.model.DictionaryCategory;
import cn.ieasy.framework.base.dict.repository.DictionaryCategoryRepository;
import cn.ieasy.framework.base.dict.service.DictionaryCategoryService;
import cn.ieasy.framework.base.dict.service.DictionaryService;
import cn.ieasy.framework.common.web.exception.CustomRuntimeException;

@Service
@Transactional
public class DictionaryCategoryServiceImpl implements DictionaryCategoryService {

    @Autowired
    private DictionaryCategoryRepository dictionaryCategoryRepository;
    @Autowired
    private DictionaryService dictionaryService;

    @Override
    public DictionaryCategory createCategory(final DictionaryCategory model) {
        final DictionaryCategory existingModel = this.dictionaryCategoryRepository.findOneByKey(model.getKey());
        if (existingModel != null) {
            throw new CustomRuntimeException("406", String.format("Key值为%s的数据已经存在", model.getKey()));
        }
        model.addVersion();
        this.dictionaryCategoryRepository.save(model);
        return model;
    }

    @Override
    public DictionaryCategory discardCategory(final Long id) {
        DictionaryCategory model = this.dictionaryCategoryRepository.findOne(id);
        if (model == null) {
            throw new CustomRuntimeException("404", "更新的数据不存在.");
        }
        model.setDiscarded(true);
        model.getDictionaries().forEach(dict -> {
            this.dictionaryService.discardDictionary(dict.getId());
        });
        model.addVersion();
        model = this.dictionaryCategoryRepository.save(model);
        return model;
    }

    @Override
    public DictionaryCategory updateCategory(final DictionaryCategory model) {
        model.addVersion();
        this.dictionaryCategoryRepository.save(model);
        return model;
    }

    @Override
    public void deleteAll() {
        this.dictionaryCategoryRepository.deleteAll();
    }

    @Override
    public List<DictionaryCategory> findAll() {
        return this.dictionaryCategoryRepository.findAll();
    }

    @Override
    public Dictionary createDictionary(final Dictionary dict) {
        this.dictionaryService.createDictionary(dict);
        final DictionaryCategory category = dict.getCategory();
        category.getDictionaries().add(dict);
        category.addVersion();
        this.dictionaryCategoryRepository.save(category);
        return dict;
    }

    @Override
    public Dictionary updateDictionary(final Dictionary dict) {
        this.dictionaryService.updateDictionary(dict);
        this.updateCategoryVersion(dict);
        return dict;
    }

    @Override
    public Dictionary discardDictionary(final Dictionary dict) {
        this.dictionaryService.discardDictionary(dict.getId());
        this.updateCategoryVersion(dict);
        return dict;
    }

    @Override
    public void deleteDictionary(final Dictionary dict) {
        if (dict == null) {
            return;
        }
        this.dictionaryService.deleteDictionary(dict.getId());
        this.updateCategoryVersion(dict);
    }

    @Override
    public Dictionary changeDictionaryOrder(final Dictionary dict, final int order) {
        this.dictionaryService.changeDictionaryOrder(dict.getId(), order);
        this.updateCategoryVersion(dict);
        return dict;
    }

    private void updateCategoryVersion(final Dictionary dict) {
        final DictionaryCategory category = dict.getCategory();
        category.addVersion();
        this.dictionaryCategoryRepository.save(category);
    }
}
