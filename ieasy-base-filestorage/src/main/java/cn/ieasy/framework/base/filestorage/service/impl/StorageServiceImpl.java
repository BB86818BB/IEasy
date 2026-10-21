package cn.ieasy.framework.base.filestorage.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cn.ieasy.framework.base.filestorage.enums.NormalizationType;
import cn.ieasy.framework.base.filestorage.model.StorageRecord;
import cn.ieasy.framework.base.filestorage.model.StorageRecord.ObjectType;
import cn.ieasy.framework.base.filestorage.service.Container;
import cn.ieasy.framework.base.filestorage.service.ImageConvertService;
import cn.ieasy.framework.base.filestorage.service.StorageRecordService;
import cn.ieasy.framework.common.web.exception.CustomRuntimeException;
import cn.ieasy.framework.common.web.utils.Mp3DurationUtils;
import cn.ieasy.framework.common.web.utils.Mp3TranslateUtils;
import cn.ieasy.framework.common.web.utils.ZipUtils;

@Service
@Transactional
public class StorageServiceImpl extends AbstractStorageService {

    @Autowired
    private StorageRecordService storageRecordService;

    @Autowired
    private ImageConvertService imageConvertService;

    @Value("${local.storage.normalization.tool.ffmpeg}")
    private String ffmpeg;
    @Value("${local.storage.normalization.tool.silkconverter:/}")
    private String silkConverterPath;

    private DefaultContainer currentContainer;
    private final Object lock = new Object();
    private SimpleFilePathContainer simpleFilePathContainer;

    @PostConstruct
    public void init() {
        this.simpleFilePathContainer = new SimpleFilePathContainer(this.rootPath);
    }

    public List<File> getFiles(final List<Long> ids, final NormalizationType type) {
        final List<StorageRecord> records = this.storageRecordService.getStorageRecords(ids);
        return records.stream().map(record -> this.getFile(record, type)).collect(Collectors.toList());
    }

    @Override
    public File getFile(final Long id, final NormalizationType type) {
        final StorageRecord record = this.getFileStorageRecord(id);
        return this.getFile(record, type);
    }

    @Override
    public File getFile(final StorageRecord record, final NormalizationType type) {
        final String filePath = this.getFilePathInContainer(record, type);
        // C + yyyyMMdd + 自增数值
        final File file = this.getFileContainer(type, filePath).getFile(filePath);
        if (file.exists()) {
            return file;
        } else if (type == NormalizationType.original) {
            throw new CustomRuntimeException("404", "文件不存在.");
        } else {
            final String path = this.rootPath + "/" + NormalizationType.original;
            this.normalize(record.getId(), true); // TODO：
            final DefaultContainer c = DefaultContainer.getContainer(path, filePath);
            return c.getFile(filePath);
        }
    }

    @Override
    public StorageRecord getFileStorageRecord(final Long id) {
        return this.storageRecordService.getStorageRecord(id);
    }

    @Override
    public File getFileStorageRecordsTo7Zip(final List<Long> ids) {

        final File targetZip = this.createLocalTempFile("7z");
        final List<File> files = this.getFiles(ids, NormalizationType.original);
        final List<String> filePaths = files.stream().map(File::getPath).collect(Collectors.toList());
        ZipUtils.exec7zip(filePaths, targetZip);
        return targetZip;
    }

    @Override
    public StorageRecord save(final File file) {
        final String filePath = this.getActiveContainer().addFile(file);

        return this.registerStorageRecord(filePath, file.getName(), file.length());
    }

    @Override
    public StorageRecord save(final MultipartFile file) {
        if (file == null) {
            throw new CustomRuntimeException("文件上传失败");
        }
        final String filePath = this.getActiveContainer().addFile(file);
        return this.registerStorageRecord(filePath, file.getOriginalFilename(), file.getSize());
    }

    @Override
    public StorageRecord save(final File[] files, final String originalFilename, final long fileTotalSize) {
        final String filePath = this.getActiveContainer().addFileChunks(files, originalFilename);
        return this.registerStorageRecord(filePath, originalFilename, fileTotalSize);
    }

    @Override
    public void delete(final Long id) {
        final StorageRecord record = this.storageRecordService.getStorageRecord(id);
        final Container c = this.getFileContainer(NormalizationType.original, record.getFilePath());
        if (c != null) {
            c.deleteFile(record.getFilePath());
        }
    }

    @Override
    public StorageRecord normalize(final Long id, final boolean sync) {
        // 根据ID从Core获取信息
        final StorageRecord storageRecord = this.storageRecordService.getStorageRecord(id);

        // 标准化处理：
        // 标准图、缩略图的生成和存储
        // 根据Type选取适当的数据存储位置
        final ObjectType storeType = storageRecord.getObjectType();
        switch (storeType) {
        case picture:
            this.normalizePicture(storageRecord, sync);
            break;
        case video:
            this.normalizeVideo(storageRecord, sync);
            break;
        case audio:
            this.normalizeAudio(storageRecord, sync);
            break;
        default:
            break;
        }
        return storageRecord;
    }

    @Override
    public StorageRecord createCombinationImage(final List<Long> storageIds, final NormalizationType type) {

        final int width = 132;
        final int height = 132;
        final List<File> images = this.getFiles(storageIds, type);
        // 执行标准化处理，合成图片
        final File tempImage = this.createLocalTempFile(ExtensionType.jpg.name());
        if (NormalizationType.thumbnail != type) {
            // 只支持缩略图
            throw new UnsupportedOperationException();
        }

        this.imageConvertService.createCombinationImage(images, tempImage, width, height);
        // 存储
        final String combinationImage = this.getActiveContainer().addFile(tempImage);

        // 更新到
        final StorageRecord record = new StorageRecord();
        record.setObjectType(ObjectType.picture);
        record.setFilePath(combinationImage);
        record.setFileName(tempImage.getName());
        record.setFileSize(tempImage.length());
        this.storageRecordService.createStorageRecord(record);
        return record;
    }

    /**
     * 图片的标准化处理
     * <p>
     * <li>生成标准图、缩略图
     * <li>标准图、缩略图保存到存储
     * <li>更新存储记录
     *
     * @param record
     * @param sync
     */
    private StorageRecord normalizePicture(final StorageRecord record, final boolean sync) {
        final File originalImage = this.getFile(record, NormalizationType.original);

        final String filePath = record.getFilePath();
        final String extension = FilenameUtils.getExtension(record.getFileName());
        final int standardWidth = 900;
        final int standardHeight = 500;
        final int thumbnailWidth = 180;
        final int thumbnailHeight = 100;
        // 执行标准化处理
        File standardImage = this.createLocalTempFile(extension);
        if (!this.imageConvertService.convert(originalImage, standardImage, standardWidth, standardHeight, sync)) {
            standardImage = originalImage;
        }
        this.getFileContainer(NormalizationType.standard, filePath).addFile(standardImage, filePath);
        // 缩略图处理
        File thumbnailImage = this.createLocalTempFile(extension);
        if (!this.imageConvertService.convert(originalImage, thumbnailImage, thumbnailWidth, thumbnailHeight, sync)) {
            thumbnailImage = originalImage;
        }
        this.getFileContainer(NormalizationType.thumbnail, filePath).addFile(thumbnailImage, filePath);

        return record;
    }

    /**
     * 视频文件的标准化处理
     * <p>
     * <li>生成视频封面
     * <li>视频封面保存到存储
     * <li>更新存储记录
     *
     * @param record
     * @param sync
     */
    private void normalizeVideo(final StorageRecord record, final boolean sync) {
        final File originalFile = this.getFile(record, NormalizationType.original);

        // 执行标准化处理，生成视频的封面图片
        final File tempCoverImage = this.createLocalTempFile(ExtensionType.jpg.name());
        this.imageConvertService.getVideoFirstFramePic(this.ffmpeg, originalFile.getAbsolutePath(),
                tempCoverImage.getAbsolutePath());

        // 存储
        final String thumbnailPath = this.getFilePathInContainer(record, NormalizationType.thumbnail);
        this.getFileContainer(NormalizationType.thumbnail, thumbnailPath).addFile(tempCoverImage, thumbnailPath);
    }

    /**
     * 音频文件的标准化处理
     * <p>
     * <li>如果原始文件是amr音频格式，转成标准的MP3格式音频
     * <li>转换后的MP3音频保存到存储
     * <li>计算音频时长
     * <li>更新存储记录
     *
     * @param record
     * @param sync
     */
    private void normalizeAudio(final StorageRecord record, final boolean sync) {
        final File originalFile = this.getFile(record, NormalizationType.original);
        final String extension = FilenameUtils.getExtension(record.getFileName());
        File standardMp3File = null;

        if (ExtensionType.mp3.name().equalsIgnoreCase(extension)) {
            standardMp3File = originalFile;
        } else if (ExtensionType.amr.name().equalsIgnoreCase(extension)) {
            // 执行标准化处理，转成标准的MP3格式音频
            standardMp3File = this.createLocalTempFile(ExtensionType.mp3.name());
            Mp3TranslateUtils.changeAmr2Mp3(this.ffmpeg, originalFile.getAbsolutePath(),
                    standardMp3File.getAbsolutePath());
        } else if (ExtensionType.silk.name().equalsIgnoreCase(extension)) {
            // 执行标准化处理，转成标准的MP3格式音频
            standardMp3File = this.createLocalTempFile(ExtensionType.mp3.name());
            Mp3TranslateUtils.changeSilk2Mp3(originalFile.getAbsolutePath(), standardMp3File.getAbsolutePath());
        } else {
            // 不支持的音频格式，不做处理
        }
        if (standardMp3File != null) {
            // 存储
            final String standardMp3Path = this.getFilePathInContainer(record, NormalizationType.standard);
            this.getFileContainer(NormalizationType.standard, standardMp3Path).addFile(standardMp3File,
                    standardMp3Path);

            // 计算MP3音频时长
            final int duration = Mp3DurationUtils.getMp3TrackLength(this.ffmpeg, standardMp3File.getAbsolutePath());
            record.setDuration(duration);

            // 更新
            record.setFileSize(originalFile.length());
            this.storageRecordService.updateStorageRecord(record);
        }
    }

    public enum ExtensionType {
        mp3, wav, wma, amr, mp4, png, jpg, jpeg, silk, other;

        public static ExtensionType value(final String name) {
            for (final ExtensionType type : ExtensionType.values()) {
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return other;
        }
    }

    private StorageRecord registerStorageRecord(final String filePath, final String fileName, final long size) {

        // Register File Info to Core
        final String extension = FilenameUtils.getExtension(fileName);
        final StorageRecord record = new StorageRecord();
        switch (extension) {
        case "jpg":
        case "jpeg":
        case "png":
            record.setObjectType(ObjectType.picture);
            break;
        case "mp3":
        case "amr":
        case "silk":
            record.setObjectType(ObjectType.audio);
            break;
        case "mp4":
        case "wav":
        case "wma":
        case "3gp":
            record.setObjectType(ObjectType.video);
            break;
        default:
            record.setObjectType(ObjectType.file);
            break;
        }
        int pathMarkIndex = fileName.lastIndexOf("/");
        if (pathMarkIndex == -1) {
            pathMarkIndex = fileName.lastIndexOf("\\");
        }
        if (pathMarkIndex >= 0) {
            record.setFileName(fileName.substring(pathMarkIndex + 1));
        } else {
            record.setFileName(fileName);
        }
        record.setFilePath(filePath);
        record.setFileSize(size);
        record.setOriginalFilePath(filePath);

        return this.storageRecordService.createStorageRecord(record);
    }

    /**
     * 容器生成规则：
     * <li>文件数量超过10000生成新的容器
     * <li>新容器命名规则：C + yyyyMMdd + 自增数值
     *
     * @return
     */
    private DefaultContainer getActiveContainer() {
        if ((this.currentContainer != null) && (this.currentContainer.getFileCounts() < 10000)) {
            return this.currentContainer;
        }
        synchronized (this.lock) {
            if ((this.currentContainer != null) && (this.currentContainer.getFileCounts() < 10000)) {
                return this.currentContainer;
            }
            if (this.currentContainer == null) {
                this.currentContainer = this.loadOriginalContainer();
            }
            if (this.currentContainer.getFileCounts() >= 10000) {

                String name = "C" + DateFormatUtils.format(new Date(), "yyyyMMdd");
                int number = 0;
                if (name.substring(0, 9).equals(this.currentContainer.name.substring(0, 9))) {
                    number = this.currentContainer.number + 1;
                    name += number;
                } else {
                    name += "0";
                }
                final DefaultContainer container =
                        new DefaultContainer(name, this.rootPath + File.separatorChar + "original");
                container.number = number;
                this.currentContainer = container;
            }
            return this.currentContainer;
        }
    }

    private DefaultContainer loadOriginalContainer() {
        final String originalRootPath = this.rootPath + File.separatorChar + "original";
        final File root = new File(originalRootPath);
        if (!root.exists()) {
            root.mkdirs();
        }

        final File[] containers = root.listFiles(dir -> {
            return dir.isDirectory() && dir.getName().startsWith("C");
        });
        if (containers.length == 0) {
            final String name = "C" + DateFormatUtils.format(new Date(), "yyyyMMdd") + "0";
            final DefaultContainer container = new DefaultContainer(name, originalRootPath);
            container.number = 0;
            return container;
        } else {
            String latestContainer = null;
            for (final File c : containers) {
                final String tempContainer = c.getName();
                if (latestContainer == null) {
                    latestContainer = tempContainer;
                } else {
                    if (Integer.parseInt(tempContainer.substring(1)) > Integer.parseInt(latestContainer.substring(1))) {
                        latestContainer = tempContainer;
                    }
                }
            }
            final DefaultContainer container = new DefaultContainer(latestContainer, originalRootPath);
            final File containerFile = new File(originalRootPath, container.name);
            container.number = containerFile.list().length;
            return container;
        }
    }

    private Container getFileContainer(final NormalizationType type, final String filePath) {
        if (filePath.matches("C20\\d+_.*")) {
            final String path = this.rootPath + File.separatorChar + type.name();
            final DefaultContainer c = DefaultContainer.getContainer(path, filePath);
            if (c == null) {
                throw new CustomRuntimeException("404", "文件不存在.");
            }
            return c;
        } else {
            return this.simpleFilePathContainer;
        }
    }

    private String getFilePathInContainer(final StorageRecord record, final NormalizationType type) {
        final String filePath = record.getFilePath();
        if (type == NormalizationType.original) {
            return filePath;
        }
        if (record.getObjectType() == ObjectType.picture) {
            return filePath;
        } else if (record.getObjectType() == ObjectType.audio) {
            return FilenameUtils.getBaseName(filePath) + "." + ExtensionType.mp3.name();
        } else if (record.getObjectType() == ObjectType.video) {
            return FilenameUtils.getBaseName(filePath) + "." + ExtensionType.jpg.name();
        } else {
            return filePath;
        }
    }
}
