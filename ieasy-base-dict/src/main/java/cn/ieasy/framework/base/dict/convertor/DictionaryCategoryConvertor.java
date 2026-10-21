package cn.ieasy.framework.base.dict.convertor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.ieasy.framework.base.dict.dto.DictionaryCategoryDTO;
import cn.ieasy.framework.base.dict.model.DictionaryCategory;
import cn.ieasy.framework.base.dict.repository.DictionaryCategoryRepository;
import cn.ieasy.framework.common.web.dto.AbstractConvertor;

@Component
public class DictionaryCategoryConvertor extends AbstractConvertor<DictionaryCategory, DictionaryCategoryDTO> {

    @Autowired
    private DictionaryCategoryRepository dictionaryTypeRepository;

    @Override
    public DictionaryCategory toModel(final DictionaryCategoryDTO dto) {
        DictionaryCategory model = null;
        if (dto.isNew()) {
            model = new DictionaryCategory();
            model.setKey(dto.getKey());
        } else {
            model = this.dictionaryTypeRepository.findOne(dto.getId());
        }
        // 废弃字段不在新建/更新操作处理，有专门的接口处理排序
        model.setDescription(dto.getDescription());
        return model;
    }

    @Override
    public DictionaryCategoryDTO toDTO(final DictionaryCategory model, final boolean forListView) {
        if (model == null) {
            return null;
        }
        final DictionaryCategoryDTO dto = new DictionaryCategoryDTO();

        dto.setId(model.getId());
        dto.setKey(model.getKey());
        dto.setDescription(model.getDescription());
        dto.setDiscarded(model.isDiscarded());
        return dto;
    }

}
