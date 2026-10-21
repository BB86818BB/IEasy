package cn.ieasy.framework.base.filestorage.dto;

import java.util.Date;

import cn.ieasy.framework.base.filestorage.model.StorageRecord.ObjectType;
import cn.ieasy.framework.common.web.dto.AbstractDTO;

/**
 * 文件下载DTO
 */
public class DownloadUrlDTO extends AbstractDTO {

    private static final long serialVersionUID = 5226994476183164161L;

    /**
     * 存储对象的编号（不用long ID是为了文件ID不容易推测出来）
     */
    private String no;

    private ObjectType objectType;

    private String fileName;

    private Long fileSize;

    private Integer duration; // 视频播放时长

    private String originalFileUrl; // 原始文件URL

    private String thumbnailFileUrl; // 缩略文件URL

    private String standardFileUrl; // 标准文件URL

    private Date date; // 生成日期

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public void setObjectType(final ObjectType objectType) {
        this.objectType = objectType;
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

    public Integer getDuration() {
        return this.duration;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public String getOriginalFileUrl() {
        return this.originalFileUrl;
    }

    public void setOriginalFileUrl(final String originalFileUrl) {
        this.originalFileUrl = originalFileUrl;
    }

    public String getThumbnailFileUrl() {
        return this.thumbnailFileUrl;
    }

    public void setThumbnailFileUrl(final String thumbnailFileUrl) {
        this.thumbnailFileUrl = thumbnailFileUrl;
    }

    public String getStandardFileUrl() {
        return this.standardFileUrl;
    }

    public void setStandardFileUrl(final String standardFileUrl) {
        this.standardFileUrl = standardFileUrl;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public String getNo() {
        return this.no;
    }

    public void setNo(final String no) {
        this.no = no;
    }
}
