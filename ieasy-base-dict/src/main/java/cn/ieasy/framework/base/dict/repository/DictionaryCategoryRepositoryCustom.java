package cn.ieasy.framework.base.dict.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.ieasy.framework.base.dict.model.DictionaryCategory;
import cn.ieasy.framework.common.web.support.Searchable;

public interface DictionaryCategoryRepositoryCustom {

    Page<DictionaryCategory> serach(final Searchable searchable, final Pageable pageable);
}
