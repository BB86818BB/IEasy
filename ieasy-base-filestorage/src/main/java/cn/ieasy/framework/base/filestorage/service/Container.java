package cn.ieasy.framework.base.filestorage.service;

import java.io.File;

public interface Container {

    String addFile(File file);

    void addFile(File from, String filePath);

    File getFile(final String filePath);

    void deleteFile(final String filePath);

}
