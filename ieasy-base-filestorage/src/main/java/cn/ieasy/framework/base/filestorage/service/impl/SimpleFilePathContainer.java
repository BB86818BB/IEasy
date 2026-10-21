package cn.ieasy.framework.base.filestorage.service.impl;

import java.io.File;

import cn.ieasy.framework.base.filestorage.service.Container;

/**
 * 简单路径的文件存储容器
 */
public class SimpleFilePathContainer implements Container {
    private final File parent;

    public SimpleFilePathContainer(final String rootPath) {
        this.parent = new File(rootPath);
        if (!this.parent.exists()) {
            this.parent.mkdirs();
        }
    }

    @Override
    public File getFile(final String filePath) {
        return new File(this.parent, filePath);
    }

    @Override
    public void deleteFile(final String filePath) {
        final File file = this.getFile(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public String addFile(final File file) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addFile(final File from, final String filePath) {
        throw new UnsupportedOperationException();
    }
}
