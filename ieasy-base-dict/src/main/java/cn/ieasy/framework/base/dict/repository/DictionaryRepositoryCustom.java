package cn.ieasy.framework.base.dict.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.ieasy.framework.base.dict.model.Dictionary;
import cn.ieasy.framework.common.web.support.Searchable;

public interface DictionaryRepositoryCustom {

    Page<Dictionary> findAllByCategoryId(Long categoryId, Searchable searchable, Pageable pageable);
}
