package cn.ieasy.framework.base.dict.convertor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.ieasy.framework.base.dict.dto.DictionaryDTO;
import cn.ieasy.framework.base.dict.model.Dictionary;
import cn.ieasy.framework.base.dict.model.DictionaryCategory;
import cn.ieasy.framework.base.dict.repository.DictionaryCategoryRepository;
import cn.ieasy.framework.base.dict.repository.DictionaryRepository;
import cn.ieasy.framework.common.web.dto.AbstractConvertor;
import cn.ieasy.framework.common.web.exception.CustomRuntimeException;

@Component
public class DictionaryConvertor extends AbstractConvertor<Dictionary, DictionaryDTO> {

    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private DictionaryCategoryRepository dictionaryCategoryRepository;

    @Override
    public Dictionary toModel(final DictionaryDTO dto) {
        Dictionary model = null;
        if (dto.isNew()) {
            model = this.createModel(dto);
        } else {
            model = this.updateModel(dto);
        }
        return model;
    }

    private Dictionary updateModel(final DictionaryDTO dto) {
        Dictionary model;
        model = this.dictionaryRepository.findOne(dto.getId());
        if (model == null) {
            throw new CustomRuntimeException("404", "要更新的资源没有找到.");
        }
        // 排序、是否可编辑、废弃 不在新建/更新操作处理，有专门的接口处理
        final Dictionary parent = this.getParentDict(dto);
        model.setParent(parent);
        model.setValue(dto.getValue());
        model.setText(dto.getText());
        model.setDescription(dto.getDescription());
        return model;
    }

    private Dictionary createModel(final DictionaryDTO dto) {
        Dictionary model;
        model = new Dictionary();
        model.setKey(dto.getKey());
        final DictionaryCategory category = this.dictionaryCategoryRepository.findOne(dto.getCategoryId());
        model.setCategory(category);
        model.setEditable(dto.isEditable());
        final Dictionary parent = this.getParentDict(dto);
        model.setParent(parent);
        model.setValue(dto.getValue());
        model.setText(dto.getText());
        model.setDescription(dto.getDescription());
        return model;
    }

    @Override
    public DictionaryDTO toDTO(final Dictionary model, final boolean forListView) {
        final DictionaryDTO dto = new DictionaryDTO();

        dto.setId(model.getId());
        if (model.getParent() != null) {
            dto.setParentId(model.getParent().getId());
        }
        dto.setCategoryId(model.getCategory().getId());
        dto.setText(model.getText());
        dto.setValue(model.getValue());
        dto.setKey(model.getKey());
        dto.setDescription(model.getDescription());
        dto.setDiscarded(model.isDiscarded());
        dto.setEditable(model.isEditable());
        return dto;
    }

    private Dictionary getParentDict(final DictionaryDTO dto) {
        final Long parentId = dto.getParentId();
        if (parentId != null) {
            final Dictionary parent = this.dictionaryRepository.findOne(parentId);
            if (parent == null) {
                throw new CustomRuntimeException("404", "Parent is not found. parentId:" + parentId);
            }
            return parent;
        } else {
            return null;
        }
    }

}
