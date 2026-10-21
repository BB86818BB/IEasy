package cn.ieasy.framework.base.dict.repository.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.querydsl.core.BooleanBuilder;

import cn.ieasy.framework.base.dict.model.DictionaryCategory;
import cn.ieasy.framework.base.dict.model.QDictionaryCategory;
import cn.ieasy.framework.base.dict.repository.DictionaryCategoryRepositoryCustom;
import cn.ieasy.framework.common.jpa.repository.AbstractRepository;
import cn.ieasy.framework.common.web.support.Searchable;

@Component
public class DictionaryCategoryRepositoryImpl extends AbstractRepository
        implements DictionaryCategoryRepositoryCustom {

    @Override
    public Page<DictionaryCategory> serach(final Searchable searchable,
            final Pageable pageable) {
        final QDictionaryCategory qModel = QDictionaryCategory.dictionaryCategory;
        final BooleanBuilder where = new BooleanBuilder();

        if (searchable.hasKey("key")) {
            final String searchKey = searchable.getStrValue("key");
            where.and(qModel.key.contains(searchKey));
        }

        return this.search(where, pageable, qModel);
    }

    @Override
    protected Class<?> getModelClass() {
        return DictionaryCategory.class;
    }

}
