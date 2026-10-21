package cn.ieasy.framework.base.dict.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import cn.ieasy.framework.base.dict.model.DictionarysVersion;

public interface DictionarysVersionRepository extends Repository<DictionarysVersion, Long> {

    List<DictionarysVersion> findAll();

    DictionarysVersion save(DictionarysVersion version);

}
