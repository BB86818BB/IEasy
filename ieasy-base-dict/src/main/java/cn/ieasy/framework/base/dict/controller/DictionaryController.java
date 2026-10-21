package cn.ieasy.framework.base.dict.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.ieasy.framework.base.dict.bean.DictionariesSyncBean;
import cn.ieasy.framework.base.dict.bean.DictionaryBean;
import cn.ieasy.framework.base.dict.convertor.DictionaryCategoryConvertor;
import cn.ieasy.framework.base.dict.convertor.DictionaryConvertor;
import cn.ieasy.framework.base.dict.dto.DictionaryCategoryDTO;
import cn.ieasy.framework.base.dict.dto.DictionaryDTO;
import cn.ieasy.framework.base.dict.model.Dictionary;
import cn.ieasy.framework.base.dict.model.DictionaryCategory;
import cn.ieasy.framework.base.dict.repository.DictionaryCategoryRepository;
import cn.ieasy.framework.base.dict.repository.DictionaryRepository;
import cn.ieasy.framework.base.dict.service.DictionaryCategoryService;
import cn.ieasy.framework.base.dict.service.DictionarySyncService;
import cn.ieasy.framework.common.web.dto.result.PageResultDTO;
import cn.ieasy.framework.common.web.dto.result.ResultDTO;
import cn.ieasy.framework.common.web.support.Searchable;

/**
 * 数据字典的管理接口
 */
@RestController
@RequestMapping("/w/dicts")
public class DictionaryController {

    @Autowired
    private DictionarySyncService dictionarySyncService;
    @Autowired
    private DictionaryCategoryService dictionaryCategoryService;
    @Autowired
    private DictionaryConvertor dictionaryConvertor;
    @Autowired
    private DictionaryCategoryConvertor dictionaryCategoryConvertor;
    @Autowired
    private DictionaryCategoryRepository dictionaryCategoryRepository;
    @Autowired
    private DictionaryRepository dictionaryRepository;

    /**
     * 取得所有的码表分类(管理接口)
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public PageResultDTO<DictionaryCategoryDTO> getCategories(final Searchable searchable,
            final Pageable pageable) {
        final Page<DictionaryCategory> modelPage = this.dictionaryCategoryRepository.serach(searchable, pageable);
        final PageResultDTO<DictionaryCategoryDTO> rs = this.dictionaryCategoryConvertor.toResultDTO(modelPage);
        return rs;
    }

    /**
     * 取得指定码表分类(管理接口)
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.GET)
    public ResultDTO<DictionaryCategoryDTO> getCategory(@PathVariable final Long id) {
        final DictionaryCategory model = this.dictionaryCategoryRepository.findOne(id);
        final ResultDTO<DictionaryCategoryDTO> rs = this.dictionaryCategoryConvertor.toResultDTO(model);
        return rs;
    }

    /**
     * 新建码表分类(管理接口)
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/categories", method = RequestMethod.POST)
    public ResultDTO<DictionaryCategoryDTO> createCategory(@RequestBody @Valid final DictionaryCategoryDTO dto) {
        final DictionaryCategory model = this.dictionaryCategoryConvertor.toModel(dto);
        this.dictionaryCategoryService.createCategory(model);
        final ResultDTO<DictionaryCategoryDTO> rs = this.dictionaryCategoryConvertor.toResultDTO(model);
        return rs;
    }

    /**
     * 废弃指定的码表类型(管理接口)
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/categories/{id}/discard", method = RequestMethod.PUT)
    public ResultDTO<DictionaryCategoryDTO> discardCategory(@PathVariable final Long id) {
        final DictionaryCategory category = this.dictionaryCategoryService.discardCategory(id);
        final ResultDTO<DictionaryCategoryDTO> rs = this.dictionaryCategoryConvertor.toResultDTO(category);
        return rs;
    }

    /**
     * 更新指定的码表类型(管理接口)
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.PUT)
    public ResultDTO<DictionaryCategoryDTO> updateCategory(@RequestBody @Valid final DictionaryCategoryDTO dto,
            @PathVariable final Long id) {
        dto.setId(id);
        final DictionaryCategory model = this.dictionaryCategoryConvertor.toModel(dto);
        this.dictionaryCategoryService.updateCategory(model);
        final ResultDTO<DictionaryCategoryDTO> rs = this.dictionaryCategoryConvertor.toResultDTO(model);
        return rs;
    }

    /**
     * 取得特定类型的码表数据(管理接口)
     *
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/categories/{categoryId}/codes", method = RequestMethod.GET)
    public PageResultDTO<DictionaryDTO> getDictsByCategory(@PathVariable final Long categoryId,
            final Searchable searchable, final Pageable pageable) {
        final Page<Dictionary> dictPage =
                this.dictionaryRepository.findAllByCategoryId(categoryId, searchable, pageable);
        final PageResultDTO<DictionaryDTO> resultDTO = this.dictionaryConvertor.toResultDTO(dictPage);
        return resultDTO;
    }

    /**
     * 取得码表数据(管理接口)
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResultDTO<DictionaryDTO> get(@PathVariable final Long id) {
        final Dictionary dict = this.dictionaryRepository.findOne(id);
        final ResultDTO<DictionaryDTO> resultDTO = this.dictionaryConvertor.toResultDTO(dict);
        return resultDTO;
    }

    /**
     * 新建码表(管理接口)
     *
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResultDTO<DictionaryDTO> create(@RequestBody @Valid final DictionaryDTO dto) {
        final Dictionary dict = this.dictionaryConvertor.toModel(dto);
        this.dictionaryCategoryService.createDictionary(dict);
        final ResultDTO<DictionaryDTO> resultDTO = this.dictionaryConvertor.toResultDTO(dict);
        return resultDTO;
    }

    /**
     * 更新码表(管理接口)
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResultDTO<DictionaryDTO> update(@RequestBody @Valid final DictionaryDTO dto,
            @PathVariable final Long id) {
        dto.setId(id);
        final Dictionary dict = this.dictionaryConvertor.toModel(dto);
        this.dictionaryCategoryService.updateDictionary(dict);
        final ResultDTO<DictionaryDTO> resultDTO = this.dictionaryConvertor.toResultDTO(dict);
        return resultDTO;
    }

    /**
     * 删除码表(管理接口)
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResultDTO<Void> delete(@PathVariable final Long id) {
        final Dictionary dictionary = this.dictionaryRepository.findOne(id);
        this.dictionaryCategoryService.deleteDictionary(dictionary);
        return ResultDTO.success();
    }

    /**
     * 废弃码表(管理接口)
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/discard", method = RequestMethod.PUT)
    public ResultDTO<DictionaryDTO> discard(@PathVariable final Long id) {
        final Dictionary dictionary = this.dictionaryRepository.findOne(id);
        final Dictionary dict = this.dictionaryCategoryService.discardDictionary(dictionary);
        final ResultDTO<DictionaryDTO> resultDTO = this.dictionaryConvertor.toResultDTO(dict);
        return resultDTO;
    }

    /**
     * 码表记录的排序上移一行(管理接口)
     * <p>
     * 将该码表记录上移指定行数
     *
     * @param id
     * @param offset 上移的行数
     * @return
     */
    @RequestMapping(value = "/{id}/moveup", method = RequestMethod.PUT)
    public ResultDTO<DictionaryDTO> moveUp(@PathVariable final Long id, @RequestParam final int offset) {
        final Dictionary dictionary = this.dictionaryRepository.findOne(id);
        final Dictionary dict = this.dictionaryCategoryService.changeDictionaryOrder(dictionary, offset);
        final ResultDTO<DictionaryDTO> resultDTO = this.dictionaryConvertor.toResultDTO(dict);
        return resultDTO;
    }

    /**
     * 码表记录的排序下移一位(管理接口)
     * <p>
     * 将该码表记录下移指定行数
     *
     * @param id
     * @param order
     * @return
     */
    @RequestMapping(value = "/{id}/movedown", method = RequestMethod.PUT)
    public ResultDTO<DictionaryDTO> moveDown(@PathVariable final Long id, @RequestParam final int offset) {
        final Dictionary dictionary = this.dictionaryRepository.findOne(id);
        final Dictionary dict = this.dictionaryCategoryService.changeDictionaryOrder(dictionary, -offset);
        final ResultDTO<DictionaryDTO> resultDTO = this.dictionaryConvertor.toResultDTO(dict);
        return resultDTO;
    }

    /**
     * 获取所有码表数据(系统间同步用接口)
     *
     * @return
     */
    @RequestMapping(value = "/sync", method = RequestMethod.GET)
    public ResultDTO<Map<String, List<DictionaryBean>>> getAllDicts(
            @RequestParam(defaultValue = "0", value = "t") final Long syncVersion) {
        final DictionariesSyncBean allDicts =
                this.dictionarySyncService.getAllDictionarys(syncVersion);

        final Map<String, List<DictionaryBean>> result = new HashMap<>();
        allDicts.getDictionaries().forEach((key, bean) -> {
            result.put(key, bean.getDictionarys());
        });
        final ResultDTO<Map<String, List<DictionaryBean>>> rs = ResultDTO.success(result);
        rs.setTimestamp(new Date(allDicts.getVersion()));
        return rs;
    }
}
