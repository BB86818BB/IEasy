package cn.ieasy.framework.base.filestorage.dto;

import cn.ieasy.framework.base.filestorage.model.StorageRecord.ObjectType;
import cn.ieasy.framework.common.web.dto.AbstractDTO;

/**
 * 存储的详细信息
 */
public class StorageDetailsDTO extends AbstractDTO {

    private static final long serialVersionUID = 5625417965803110556L;

    /**
     * 存储对象的编号（不用long ID是为了文件ID不容易推测出来）
     */
    private String no;
    /**
     * 存储类型
     */
    private ObjectType type;

    /** 文件名称 */
    private String fileName;

    /** 文件大小 */
    private Long fileSize;

    /** 时长 */
    private Integer duration;
    /**
     * 缩略图Base64编码值
     */
    private String base64Thumbnail;

    /**
     * 图像宽度（像素）
     */
    private Integer thumbnailWidth;

    /**
     * 图像高度（像素）
     */
    private Integer thumbnailHeight;

    public ObjectType getType() {
        return this.type;
    }

    public void setType(final ObjectType type) {
        this.type = type;
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

    public String getBase64Thumbnail() {
        return this.base64Thumbnail;
    }

    public void setBase64Thumbnail(final String base64Thumbnail) {
        this.base64Thumbnail = base64Thumbnail;
    }

    public Integer getThumbnailWidth() {
        return this.thumbnailWidth;
    }

    public void setThumbnailWidth(final Integer thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    public Integer getThumbnailHeight() {
        return this.thumbnailHeight;
    }

    public void setThumbnailHeight(final Integer thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public String getNo() {
        return this.no;
    }

    public void setNo(final String no) {
        this.no = no;
    }

}
