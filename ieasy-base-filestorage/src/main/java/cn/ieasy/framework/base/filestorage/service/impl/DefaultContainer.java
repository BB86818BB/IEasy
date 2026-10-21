package cn.ieasy.framework.base.filestorage.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Files;

import cn.ieasy.framework.base.filestorage.service.Container;
import cn.ieasy.framework.common.web.exception.CustomRuntimeException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultContainer implements Container {
    String path;
    String name;
    int number;

    public static DefaultContainer getContainer(final String rootPath, final String filePath) {
        final int containerIndex = filePath.indexOf("_");
        if (containerIndex != -1) {
            final DefaultContainer c = new DefaultContainer(filePath.substring(0, containerIndex), rootPath);
            return c;
        }
        return null;
    }

    public DefaultContainer(final String name, final String rootPath) {
        this.path = rootPath + File.separator + name;
        final File path = new File(this.path);
        if (!path.exists()) {
            path.mkdirs();
        }
        this.name = name;
    }

    public String addFileChunks(final File[] files, final String fileName) {
        InputStream iStream = null;
        OutputStream oStream = null;
        final String extension = FilenameUtils.getExtension(fileName);
        final String filePath = this.createFilePath(extension);
        try {
            final File saveFile = this.getFile(filePath);
            oStream = new FileOutputStream(saveFile, true);
            for (final File file : files) {
                iStream = new FileInputStream(file);
                IOUtils.copy(iStream, oStream);
                IOUtils.closeQuietly(iStream);
            }
            return filePath;
        } catch (final IOException e) {
            DefaultContainer.log.error("文件存储失败.", e);
            throw new CustomRuntimeException("文件处理失败");
        } finally {
            IOUtils.closeQuietly(oStream);
        }
    }

    @Override
    public String addFile(final File file) {
        final String extension = FilenameUtils.getExtension(file.getName());
        final String filePath = this.createFilePath(extension);
        this.addFile(file, filePath);
        return filePath;
    }

    @Override
    public void addFile(final File from, final String filePath) {
        try {
            Files.copy(from, this.getFile(filePath));
            //copy(new FileInputStream(from), filePath);
        } catch (final IOException e) {
            DefaultContainer.log.error("文件存储失败.", e);
            throw new CustomRuntimeException("文件处理失败");
        }
    }

    public String addFile(final MultipartFile file) {
        final String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        final String filePath = this.createFilePath(extension);
        try {
            file.transferTo(this.getFile(filePath));
            //copy(file.getInputStream(), filePath);
            return filePath;
        } catch (final IOException e) {
            throw new CustomRuntimeException("文件上传失败");
        }
    }

    @Override
    public void deleteFile(final String fileId) {
        final File file = this.getFile(fileId);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public File getFile(final String filePath) {
        final File path = new File(this.path);
        if (!path.exists()) {
            path.mkdirs();
        }
        final File file = new File(this.path, filePath);
        return file;
    }

    private String createFilePath(final String extension) {
        final StringBuilder id = new StringBuilder();
        id.append(this.name)
                .append("_")
                .append(String.valueOf(System.currentTimeMillis()))
                .append(StringUtils.leftPad(String.valueOf((int) (Math.random() * 10000)), 4))
                .append(".")
                .append(extension);
        return id.toString();
    }

    public int getFileCounts() {
        return new File(this.path).list().length;
    }

    //	private void copy(InputStream iStream, String filePath) {
    //        OutputStream oStream = null;
    //        try {
    //            final File saveFile = this.getFile(filePath);
    //            oStream = new FileOutputStream(saveFile, false);
    //            IOUtils.copy(iStream, oStream);
    //        } catch (final IOException e) {
    //            throw new CustomRuntimeException("文件上传失败");
    //        } finally {
    //            IOUtils.closeQuietly(iStream);
    //            IOUtils.closeQuietly(oStream);
    //        }
    //	}
}
