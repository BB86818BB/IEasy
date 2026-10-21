package cn.ieasy.framework.base.dict.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import cn.ieasy.framework.base.dict.model.DictionaryCategory;

public interface DictionaryCategoryRepository
        extends Repository<DictionaryCategory, Long>, DictionaryCategoryRepositoryCustom {

    DictionaryCategory findOne(Long id);

    DictionaryCategory findOneByKey(String key);

    Page<DictionaryCategory> findAll(Pageable pageable);

    DictionaryCategory save(DictionaryCategory model);

    void deleteAll();

    List<DictionaryCategory> findAll();
}
