package cn.ieasy.framework.base.filestorage.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import cn.ieasy.framework.base.filestorage.enums.NormalizationType;
import cn.ieasy.framework.base.filestorage.model.StorageRecord;

/**
 * 存储服务
 */
public interface StorageService {

    /**
     * 获取本地文件，支持数据文件标准化操作。
     *
     * @param id 存储标识
     * @param type
     * @return 文件
     */
    File getFile(Long id, NormalizationType type);

    /**
     * 获取本地文件，支持数据文件标准化操作。
     *
     * @param record 存储记录
     * @param type
     * @return 文件
     */
    File getFile(StorageRecord record, NormalizationType type);

    /**
     * 获取文件存储记录
     *
     * @param id
     * @return
     */
    StorageRecord getFileStorageRecord(Long id);

    /**
     * 获取文件存储记录
     *
     * @param ids
     * @return
     */
    File getFileStorageRecordsTo7Zip(List<Long> ids);

    /**
     * 在本地创建临时文件
     *
     * @param extension 文件扩展名
     * @return 临时文件
     */
    File createLocalTempFile(final String extension);

    /**
     * 在本地创建临时文件
     *
     * @param relativePath 文件相对目录
     * @param filename 文件名
     * @return 临时文件
     */
    File createLocalTempFile(String relativePath, String filename);

    /**
     * 获取指定的临时文件
     *
     * @param relativePath
     * @param filename
     * @return
     */
    File getLocalTempFile(String relativePath, String filename);

    /**
     * 保存文件
     *
     * @param file 文件
     * @return 文件存储记录
     */
    StorageRecord save(File file);

    /**
     * 保存HTTP上传的文件内容到本地存储<br/>
     *
     * @param file HTTP请求的文件内容
     * @return 文件相对路径
     */
    StorageRecord save(MultipartFile file);

    /**
     * 分片文件保存
     *
     * @param files
     * @param originalFilename
     * @return
     */
    StorageRecord save(File[] files, String originalFilename, long fileTotalSize);

    /**
     * 删除后端存储的文件
     *
     * @param id 文件ID
     */
    void delete(Long id);

    /**
     * 对存储的文件执行标准化处理
     *
     * @param id 存储记录No
     * @param sync 同步处理
     * @throws IOException
     */
    StorageRecord normalize(Long id, boolean sync);

    /**
     * 合成多张图片为一张图片，最多支持九宫格样式（九张图合成）
     *
     * @param imageIds
     * @return
     */
    StorageRecord createCombinationImage(List<Long> imageIds, NormalizationType type);

}
