package cn.ieasy.framework.base.dict.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * 码表上传接口
 * 
 * @author LinWang
 */
public interface DictionaryExcelImportService {

    public void upload(final MultipartFile policyFile) throws IOException;
}
