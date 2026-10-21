package cn.ieasy.framework.common.web.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 压缩、解压缩工具类
 */
@Slf4j
public class ZipUtils {

    /**
     * 执行7zip压缩
     *
     * @param filePaths
     * @param targetZip
     */
    public static void exec7zip(final List<String> filePaths, final File targetZip) {
        final StringBuilder command = new StringBuilder();
        command.append("7za a -tzip ")
                .append(targetZip.getAbsolutePath())
                .append(" ");
        for (final String filePath : filePaths) {
            command.append(filePath).append(" ");
        }
        try {
            final Process ps = Runtime.getRuntime().exec(command.toString());
            final BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            final StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            final String result = sb.toString();
            ZipUtils.log.info(result);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
