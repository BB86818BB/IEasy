package cn.ieasy.framework.base.filestorage.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.io.Files;

import cn.ieasy.framework.base.filestorage.service.ImageConvertService;
import cn.ieasy.framework.common.web.exception.CustomRuntimeException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageConvertServiceImpl implements ImageConvertService {

    //ImageMagick安装路径
    @Value("${local.storage.magic:}")
    private String magicPath;
    //private static final int STANDARD_HIGHT = 500;
    private static final int STANDARD_WIDTH = 500;

    @Override
    public boolean convert(final File originalImage, final File targetImage, final int w, final int h,
            final boolean sync) {
        if (!originalImage.exists()) {
            ImageConvertServiceImpl.log
                    .error(String.format("The image [%s] is not exist.", originalImage.getAbsolutePath()));
            throw new CustomRuntimeException("标准化处理的文件不存在.");
        }
        try {
            // 计算获得标准图片的长宽
            final BufferedImage source = ImageIO.read(originalImage);
            final int standard = Math.min(w, h);
            final int width = source.getWidth();
            final int height = source.getHeight();
            Integer targetWidth = null;
            Integer targetHeight = null;
            if (width >= height) {
                targetHeight = standard;
                if (targetHeight > height) {
                    targetHeight = height;
                }
            } else {
                targetWidth = standard;
                if (targetWidth > width) {
                    targetWidth = width;
                }
            }
            final IMOperation op = new IMOperation();
            op.addImage(originalImage.getPath());
            if (Files.getFileExtension(targetImage.getName()).toLowerCase().equals("jpg")) {
                op.interlace("Plane"); // 渐进式JPEG 格式
            }

            op.resize(targetWidth, targetHeight);
            op.addImage(targetImage.getPath());
            final ConvertCmd convert = this.getConverter();
            convert.setAsyncMode(!sync);
            convert.run(op);
            return true;
        } catch (final Exception e) {
            ImageConvertServiceImpl.log.warn("图片标准化处理失败，图片地址：" + originalImage.getPath(), e);
            return false;
        }
    }

    @Override
    public String getVideoFirstFramePic(final String ffmpegPath, final String videoPath,
            final String imagePath) {

        final File file = new File(videoPath);

        if (!file.exists()) {

            ImageConvertServiceImpl.log.error("路径[" + videoPath + "]对应的视频文件不存在!");

            return null;

        }

        final List<String> commands = new ArrayList<String>();

        commands.add(ffmpegPath);
        commands.add("-i");
        commands.add(videoPath);
        commands.add("-y");
        commands.add("-f");
        commands.add("image2");
        commands.add("-t");
        commands.add("0.001");
        commands.add("-s");
        commands.add("320x240");
        commands.add(imagePath);

        try {
            final ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            final Process process = builder.start();
            process.waitFor();
            return imagePath;
        } catch (final Exception e) {
            ImageConvertServiceImpl.log.error("生成视频文件[" + videoPath + "]对应的第一帧图片失败!");
            ImageConvertServiceImpl.log.error("错误信息：" + e.getMessage());
            return null;
        }

    }

    @Override
    public boolean createCombinationImage(final List<File> files, final File outFile, final int imageWidth,
            final int imageHeight) {
        final String[] imageCoordinates = this.getXY(files.size());
        final int width = this.getWidth(files.size());
        final String backgorundColor = "#e7e7e7";

        final IMOperation op = new IMOperation();
        op.size(imageWidth, imageHeight);
        op.addRawArgs("xc:" + backgorundColor);
        for (int i = 0; i < imageCoordinates.length; i++) {
            final String[] sizeArr = imageCoordinates[i].split(",");
            final int x = Integer.valueOf(sizeArr[0]);
            final int y = Integer.valueOf(sizeArr[1]);
            op.addImage(files.get(i).getPath());
            op.geometry(width, width, x, y);
            op.composite();
        }
        op.addImage(outFile.getPath());
        final ConvertCmd convert = this.getConverter();

        try {
            convert.run(op);
            convert.run(op);
            return true;
        } catch (final Exception e) {
            ImageConvertServiceImpl.log.error("图片合成失败", e);
            return false;
        }
    }

    private String[] getXY(final int size) {
        final String[] s = new String[size];
        int _x = 0;
        int _y = 0;
        if (size == 1) {
            _x = _y = 6;
            s[0] = "6,6";
        }
        if (size == 2) {
            _x = _y = 4;
            s[0] = "4," + ((132 / 2) - (60 / 2));
            s[1] = 60 + (2 * _x) + "," + ((132 / 2) - (60 / 2));
        }
        if (size == 3) {
            _x = _y = 4;
            s[0] = ((132 / 2) - (60 / 2)) + "," + _y;
            s[1] = _x + "," + (60 + (2 * _y));
            s[2] = (60 + (2 * _y)) + "," + (60 + (2 * _y));
        }
        if (size == 4) {
            _x = _y = 4;
            s[0] = _x + "," + _y;
            s[1] = ((_x * 2) + 60) + "," + _y;
            s[2] = _x + "," + (60 + (2 * _y));
            s[3] = (60 + (2 * _y)) + "," + (60 + (2 * _y));
        }
        if (size == 5) {
            _x = _y = 3;
            s[0] = ((132 - (40 * 2) - _x) / 2) + "," + ((132 - (40 * 2) - _y) / 2);
            s[1] = (((132 - (40 * 2) - _x) / 2) + 40 + _x) + "," + ((132 - (40 * 2) - _y) / 2);
            s[2] = _x + "," + (((132 - (40 * 2) - _x) / 2) + 40 + _y);
            s[3] = ((_x * 2) + 40) + "," + (((132 - (40 * 2) - _x) / 2) + 40 + _y);
            s[4] = ((_x * 3) + (40 * 2)) + "," + (((132 - (40 * 2) - _x) / 2) + 40 + _y);
        }
        if (size == 6) {
            _x = _y = 3;
            s[0] = _x + "," + ((132 - (40 * 2) - _x) / 2);
            s[1] = ((_x * 2) + 40) + "," + ((132 - (40 * 2) - _x) / 2);
            s[2] = ((_x * 3) + (40 * 2)) + "," + ((132 - (40 * 2) - _x) / 2);
            s[3] = _x + "," + (((132 - (40 * 2) - _x) / 2) + 40 + _y);
            s[4] = ((_x * 2) + 40) + "," + (((132 - (40 * 2) - _x) / 2) + 40 + _y);
            s[5] = ((_x * 3) + (40 * 2)) + "," + (((132 - (40 * 2) - _x) / 2) + 40 + _y);
        }
        if (size == 7) {
            _x = _y = 3;
            s[0] = ((132 - 40) / 2) + "," + _y;
            s[1] = _x + "," + ((_y * 2) + 40);
            s[2] = ((_x * 2) + 40) + "," + ((_y * 2) + 40);
            s[3] = ((_x * 3) + (40 * 2)) + "," + ((_y * 2) + 40);
            s[4] = _x + "," + ((_y * 3) + (40 * 2));
            s[5] = ((_x * 2) + 40) + "," + ((_y * 3) + (40 * 2));
            s[6] = ((_x * 3) + (40 * 2)) + "," + ((_y * 3) + (40 * 2));
        }
        if (size == 8) {
            _x = _y = 3;
            s[0] = ((132 - 80 - _x) / 2) + "," + _y;
            s[1] = (((132 - 80 - _x) / 2) + _x + 40) + "," + _y;
            s[2] = _x + "," + ((_y * 2) + 40);
            s[3] = ((_x * 2) + 40) + "," + ((_y * 2) + 40);
            s[4] = ((_x * 3) + (40 * 2)) + "," + ((_y * 2) + 40);
            s[5] = _x + "," + ((_y * 3) + (40 * 2));
            s[6] = ((_x * 2) + 40) + "," + ((_y * 3) + (40 * 2));
            s[7] = ((_x * 3) + (40 * 2)) + "," + ((_y * 3) + (40 * 2));
        }
        if (size == 9) {
            _x = _y = 3;
            s[0] = _x + "," + _y;
            s[1] = (_x * 2) + 40 + "," + _y;
            s[2] = (_x * 3) + (40 * 2) + "," + _y;
            s[3] = _x + "," + ((_y * 2) + 40);
            s[4] = ((_x * 2) + 40) + "," + ((_y * 2) + 40);
            s[5] = ((_x * 3) + (40 * 2)) + "," + ((_y * 2) + 40);
            s[6] = _x + "," + ((_y * 3) + (40 * 2));
            s[7] = ((_x * 2) + 40) + "," + ((_y * 3) + (40 * 2));
            s[8] = ((_x * 3) + (40 * 2)) + "," + ((_y * 3) + (40 * 2));
        }
        return s;
    }

    private int getWidth(final int size) {
        int width = 0;
        if (size == 1) {
            width = 120;
        }
        if ((size > 1) && (size <= 4)) {
            width = 60;
        }
        if (size >= 5) {
            width = 40;
        }
        return width;
    }

    private ConvertCmd getConverter() {
        if (!StringUtils.hasText(this.magicPath)) {
            return new ConvertCmd();
        }
        final ConvertCmd convertCmd = new ConvertCmd();
        convertCmd.setSearchPath(this.magicPath);
        return convertCmd;
    }
}
