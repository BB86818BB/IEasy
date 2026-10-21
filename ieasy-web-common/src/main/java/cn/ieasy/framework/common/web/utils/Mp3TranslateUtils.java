package cn.ieasy.framework.common.web.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mp3TranslateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Mp3TranslateUtils.class);

    private static boolean checkfile(final String path) {
        final File file = new File(path);
        if (!file.isFile()) {
            return false;
        }
        return true;
    }

    public static boolean changeAmr2Mp3(final String ffmpegPath, final String amrFilePath, final String mp3FilePath) {

        if (!Mp3TranslateUtils.checkfile(amrFilePath)) {
            Mp3TranslateUtils.LOGGER.error(amrFilePath + " is not file");
            return false;
        }

        final List<String> commend = new ArrayList<String>();
        commend.add(ffmpegPath);
        commend.add("-i");
        commend.add(amrFilePath);
        commend.add("-f");
        commend.add("mp3");
        commend.add("-acodec");
        commend.add("lieasyp3lame");
        commend.add("-y");
        commend.add(mp3FilePath);
        try {
            final ProcessBuilder builder = new ProcessBuilder();
            builder.redirectErrorStream(true);
            builder.command(commend);
            final Process process = builder.start();
            try {
                process.waitFor();
            } catch (final InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            process.getErrorStream().close();
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean changeWav2Mp3(final String ffmpegPath, final String wavFilePath, final String mp3FilePath) {
        if (!Mp3TranslateUtils.checkfile(wavFilePath)) {
            Mp3TranslateUtils.LOGGER.error(wavFilePath + " is not file");
            return false;
        }
        final List<String> commend = new ArrayList<String>();
        commend.add(ffmpegPath);
        commend.add("-i");
        commend.add(wavFilePath);
        commend.add("-f");
        commend.add("mp3");
        commend.add("-acodec");
        commend.add("lieasyp3lame");
        commend.add("-y");
        commend.add(mp3FilePath);
        try {
            final ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            builder.start();
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean changeWma2Mp3(final String ffmpegPath, final String wmaFilePath, final String mp3FilePath) {
        if (!Mp3TranslateUtils.checkfile(wmaFilePath)) {
            Mp3TranslateUtils.LOGGER.error(wmaFilePath + " is not file");
            return false;
        }
        final List<String> commend = new ArrayList<String>();
        commend.add(ffmpegPath);
        commend.add("-i");
        commend.add(wmaFilePath);
        commend.add("-f");
        commend.add("mp3");
        commend.add("-acodec");
        commend.add("lieasyp3lame");
        commend.add("-y");
        commend.add(mp3FilePath);
        try {
            final ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            builder.start();
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean changeSilk2Mp3(final String silkPath, final String mp3Path) {
        final List<String> commend = new ArrayList<String>();
        commend.add("silkconverter.sh");
        commend.add(silkPath);
        commend.add("mp3");
        try {
            final ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            final Process ps = builder.start();
            final BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            final StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            final String result = sb.toString();
            Mp3TranslateUtils.log.info(result);
            final String mp3FilePath = silkPath.substring(0, silkPath.length() - 4) + "mp3";
            if (result.contains("[OK]")) {

                //Mp3TranslateUtils.move(mp3FilePath, mp3Path);
                new File(mp3FilePath).renameTo(new File(mp3Path));
                // Files.move(new File(mp3FilePath), new File(mp3Path));
                return true;
            } else {
                return false;
            }
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //    private static void move(final String sourceFile, final String targetFile) {
    //        final List<String> commend = new ArrayList<String>();
    //        commend.add("mv");
    //        commend.add(sourceFile);
    //        commend.add(targetFile);
    //        try {
    //            final ProcessBuilder builder = new ProcessBuilder();
    //            builder.command(commend);
    //            final Process ps = builder.start();
    //            final BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
    //            final StringBuffer sb = new StringBuffer();
    //            String line;
    //            while ((line = br.readLine()) != null) {
    //                sb.append(line).append("\n");
    //            }
    //            final String result = sb.toString();
    //            Mp3TranslateUtils.log.info(result);
    //        } catch (final Exception e) {
    //            Mp3TranslateUtils.log.error("mv 文件失败", e);
    //        }
    //    }
    //
    //    public static void main(final String[] args) {
    //
    //        Mp3TranslateUtils.changeSilk2Mp3("/home/wl/mtms-ca/f.silk", "/home/wl/film005.mp3");
    //    }
}
