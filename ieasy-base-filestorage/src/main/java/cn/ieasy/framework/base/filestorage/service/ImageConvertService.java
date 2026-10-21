package cn.ieasy.framework.base.filestorage.service;

import java.io.File;
import java.util.List;

/**
 * Created by 尚晓琼 on 2017/8/25.
 */
public interface ImageConvertService {

    /**
     * <p>
     * Title: thumbnailImage
     * </p>
     * <p>
     * Description: 根据图片路径生成缩略图
     * </p>
     *
     * @param originalImage 原图片
     * @param targetImage 目标图
     * @param w 缩略图宽
     * @param h 缩略图高
     * @param sync 是否同步执行
     */
    public boolean convert(final File originalImage, final File targetImage, final int w, final int h,
            final boolean sync);

    /**
     * 取得上传的视频文件的第一帧图片
     *
     * @param videoPath 视频路径
     * @param imagePath 生成的图片的路径
     * @return
     */
    public String getVideoFirstFramePic(final String ffmpegPath, final String videoPath,
            final String imagePath);

    /**
     * @param files 待合并图片列表
     * @param outFile 生成的图片路径
     * @param imageWidth 图片的髋
     * @param imageHeight 图片的高
     * @throws Exception
     */
    public boolean createCombinationImage(final List<File> files, final File outFile, final int imageWidth,
            final int imageHeight);

}
