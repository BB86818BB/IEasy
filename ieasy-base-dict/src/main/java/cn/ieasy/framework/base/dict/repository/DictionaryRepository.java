package cn.ieasy.framework.base.dict.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import cn.ieasy.framework.base.dict.model.Dictionary;
import cn.ieasy.framework.base.dict.model.DictionaryCategory;

public interface DictionaryRepository extends Repository<Dictionary, Long>, DictionaryRepositoryCustom {

    Dictionary findOne(Long id);

    Dictionary findOneByKey(String key);

    List<Dictionary> findByCategoryAndParentIsNullOrderBySortIndexAsc(DictionaryCategory category);

    List<Dictionary> findByParentIsNullOrderBySortIndexAsc();

    Long countByParentAndKey(Dictionary parent, String key);

    Dictionary save(Dictionary dict);

    void delete(Dictionary dict);
}
