package cn.ieasy.framework.base.filestorage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import cn.ieasy.framework.common.jpa.model.AbstractAuditModel;

/**
 * 对象数据存储记录类
 */
@Entity
public class StorageRecord extends AbstractAuditModel {
    /**
     *
     */
    private static final long serialVersionUID = -2489034286586203090L;

    //private static final long serialVersionUID = -7593309719311640878L;

    public enum ObjectType {
        file, audio, picture, video
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    protected ObjectType objectType;

    /**
     * 文件路径，包含访问文件的全部信息
     */
    @NotBlank
    @Length(max = 1024)
    @Column(length = 1024, nullable = false)
    private String filePath;

    /**
     * 文件原始名称
     */
    @NotBlank
    @Length(max = 255)
    @Column(length = 255, nullable = false)
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize = 0l;

    /**
     * 缩略图文件路径，包含访问文件的全部相对信息
     */
    @Length(max = 1024)
    @Column(length = 1024)
    private String thumbnailFilePath;

    /**
     * 原始文件路径，包含访问文件的全部相对信息
     */
    @NotBlank
    @Length(max = 1024)
    @Column(length = 1024, nullable = false)
    private String originalFilePath;

    /**
     * 播放时长, 单位秒
     */
    private Integer duration = 0;

    public Integer getDuration() {
        return this.duration;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
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

    //    public String getStorageServer() {
    //        return this.storageServer;
    //    }
    //
    //    public void setStorageServer(final String storageServer) {
    //        this.storageServer = storageServer;
    //    }

}
