package cn.ieasy.framework.base.filestorage.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import cn.ieasy.framework.base.filestorage.model.StorageRecord.ObjectType;
import cn.ieasy.framework.common.web.dto.AbstractDTO;

/**
 * 对象数据存储记录类
 */
public class StorageRecordDTO extends AbstractDTO {

    private static final long serialVersionUID = -1538901115520003737L;

    /**
     * 存储对象的编号（不用long ID是为了文件ID不容易推测出来）
     */
    private String no;

    @NotNull
    protected ObjectType objectType;

    /**
     * 文件路径，包含访问文件的全部信息
     */
    @NotBlank
    @Length(max = 1024)
    private String filePath;

    /**
     * 文件原始名称
     */
    @NotBlank
    @Length(max = 255)
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize = 0l;

    /**
     * 缩略图文件路径，包含访问文件的全部相对信息
     */
    @Length(max = 1024)
    private String thumbnailFilePath;

    /**
     * 原始文件路径，包含访问文件的全部相对信息
     */
    @NotBlank
    @Length(max = 1024)
    private String originalFilePath;

    /**
     * 播放时长, 单位秒
     */
    private Integer duration = 0;

    public Integer getDuration() {
        return this.duration;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public String getThumbnailFilePath() {
        return this.thumbnailFilePath;
    }

    public void setThumbnailFilePath(final String thumbnailFilePath) {
        this.thumbnailFilePath = thumbnailFilePath;
    }

    public String getOriginalFilePath() {
        return this.originalFilePath;
    }

    public void setOriginalFilePath(final String originalFilePath) {
        this.originalFilePath = originalFilePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(final Long fileSize) {
        this.fileSize = fileSize;
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public void setObjectType(final ObjectType storeType) {
        this.objectType = storeType;
    }

    public String getNo() {
        return this.no;
    }

    public void setNo(final String no) {
        this.no = no;
    }
}
