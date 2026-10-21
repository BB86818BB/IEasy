package cn.ieasy.framework.common.web.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mp3DurationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Mp3DurationUtils.class);

    private final static String TIME_LEN = "timelen";

    private static boolean checkfile(final String path) {
        final File file = new File(path);
        if (!file.isFile()) {
            return false;
        }
        return true;
    }

    public static final int getMp3TrackLength(final String ffmpegPath, final String mp3FilePath) {
        int duration = 0;
        if (!Mp3DurationUtils.checkfile(mp3FilePath)) {
            Mp3DurationUtils.LOGGER.error(mp3FilePath + " is not file");
            return 0;
        }
        final List<String> commend = new ArrayList<String>();
        commend.add(ffmpegPath);
        commend.add("-i");
        commend.add(mp3FilePath);
        final ProcessBuilder builder = new ProcessBuilder();
        builder.redirectErrorStream(true);
        builder.command(commend);
        try {
            final Process process = builder.start();
            //开启单独的线程来处理输入和输出流，避免缓冲区满导致线程阻塞.
            try {
                process.waitFor();
                final InputStream in = process.getInputStream();
                final Map<String, Object> map = Mp3DurationUtils.pattInfo(in);
                duration = (int) map.get(Mp3DurationUtils.TIME_LEN) * 1000;
            } catch (final InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            process.getErrorStream().close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return duration;
    }

    // 负责从返回的内容中解析
    /**
     * Input #0, avi, from 'c:\join.avi': Duration: 00:00:10.68(时长), start:
     * 0.000000(开始时间), bitrate: 166 kb/s(码率) Stream #0:0: Video: msrle
     * ([1][0][0][0] / 0x0001)(编码格式), pal8(视频格式), 165x97(分辨率), 33.33 tbr, 33.33
     * tbn, 33.33 tbc Metadata: title : AVI6700.tmp.avi Video #1
     */
    private static Map<String, Object> pattInfo(final InputStream is) {
        final String info = Mp3DurationUtils.read(is);
        Mp3DurationUtils.LOGGER.warn("Mp3DurationUtils info ：" + info);
        final String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
        final Pattern pattern = Pattern.compile(regexDuration);
        final Matcher matcher = pattern.matcher(info);
        Map<String, Object> map = null;
        if (matcher.find()) {
            map = new HashMap<String, Object>();
            map.put(Mp3DurationUtils.TIME_LEN, Mp3DurationUtils.getTimelen(matcher.group(1)));
        }
        return map;
    }

    // 负责从返回信息中读取内容
    private static String read(final InputStream is) {
        BufferedReader br = null;
        final StringBuffer sb = new StringBuffer();
        try {
            br = new BufferedReader(new InputStreamReader(is), 500);

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
        } catch (final Exception e) {
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (final Exception e) {
            }
        }
        return sb.toString();
    }

    //格式:"00:00:10.68"
    private static int getTimelen(final String timelen) {
        int min = 0;
        final String strs[] = timelen.split(":");
        if (strs[0].compareTo("0") > 0) {
            min += Integer.valueOf(strs[0]) * 60 * 60;//秒
        }
        if (strs[1].compareTo("0") > 0) {
            min += Integer.valueOf(strs[1]) * 60;
        }
        if (strs[2].compareTo("0") > 0) {
            min += Math.round(Float.valueOf(strs[2]));
        }
        return min;

    }

    public static void main(final String[] args) {
        final int length = Mp3DurationUtils.getMp3TrackLength("ffmpeg", "/home/wl/tmp/sintel.mp4");
        System.out.println(length);
    }
}
