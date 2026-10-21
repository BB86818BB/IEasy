package cn.ieasy.framework.common.web.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

/**
 * 日历用工具类。
 */
public class CalendarUtil {
    /** 年-月-日 时间格式 */
    public static String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    /** 年-月-日 时:分:秒 时间格式 */
    public static String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final DateFormat dateFormat = new SimpleDateFormat(CalendarUtil.FORMAT_YYYY_MM_DD_HH_MM_SS);

    /**
     * 取本地日期格式（年-月-日）。
     *
     * @param date 时间
     * @return 本地时间字符串
     */
    public static String toLocalDateString(final Date date) {

        return CalendarUtil.toLocalString(date, CalendarUtil.FORMAT_YYYY_MM_DD);
    }

    /**
     * 解析地日期格式（年-月-日）。
     *
     * @param date 时间
     * @return 本地时间字符串
     * @throws ParseException
     */
    public static Date parseLocalString(final String date) throws ParseException {

        return CalendarUtil.parseLocalString(date, CalendarUtil.FORMAT_YYYY_MM_DD);
    }

    /**
     * 取本地时间（年-月-日 时:分:秒）。
     *
     * @param date 时间
     * @return 本地时间字符串
     */
    public static String toLocalDateTimeString(final Date date) {

        return CalendarUtil.toLocalString(date, CalendarUtil.FORMAT_YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 解析地时间（年-月-日 时:分:秒）。
     *
     * @param date 时间
     * @return 本地时间字符串
     * @throws ParseException
     */
    public static Date parseLocalDateTimeString(final String date) throws ParseException {
        return CalendarUtil.parseLocalString(date, CalendarUtil.FORMAT_YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 取本地时间（年-月-日）。
     *
     * @param date 时间
     * @return 本地时间字符串
     */
    public static String toLocalString(final Date date, final String format) {
        final Locale locale = LocaleContextHolder.getLocale();
        return new SimpleDateFormat(format, locale).format(date);
    }

    /**
     * 解析地时间（年-月-日）。
     *
     * @param date 时间
     * @return 本地时间字符串
     * @throws ParseException
     */
    public static Date parseLocalString(final String date, final String format) throws ParseException {
        final Locale locale = LocaleContextHolder.getLocale();
        return new SimpleDateFormat(format, locale).parse(date);
    }

    public synchronized static Date parse(final String dateStr) {
        try {
            return CalendarUtil.dateFormat.parse(dateStr);
        } catch (final ParseException e) {
            return CalendarUtil.buildDate(1970, 1, 1);
        }
    }

    public synchronized static String format(final Date date) {
        if (date == null) {
            return "";
        }
        return CalendarUtil.dateFormat.format(date);
    }

    private static final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public synchronized static String simpleFormat(final Date date) {
        if (date == null) {
            return "";
        }
        return CalendarUtil.simpleDateFormat.format(date);
    }

    public synchronized static Date simpleParse(final String dateStr) {
        try {
            if (StringUtils.isEmpty(dateStr)) {
                return null;
            }
            return CalendarUtil.simpleDateFormat.parse(dateStr);
        } catch (final ParseException e) {
            return CalendarUtil.buildDate(1970, 1, 1);
        }
    }

    /**
     * 是否周末判断
     */
    public static boolean isWeekend(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        return (dayOfWeek == Calendar.SUNDAY) || (dayOfWeek == Calendar.SATURDAY);
    }

    /**
     * 构造日期
     */
    public static Date buildDate(final int year, final int month, final int date) {
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, date);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 构造日期时间
     */
    public static Date buildDate(final int year, final int month, final int date, final int hour, final int minute,
            final int second) {
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, date);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);

        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);

        return calendar.getTime();
    }

    /**
     * 构造日期时间
     */
    public static Date buildDate(final int hour, final int minute) {
        final GregorianCalendar calendar = new GregorianCalendar();

        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);

        return calendar.getTime();
    }

    /**
     * 获取某天的零点时刻。
     *
     * @param date 时刻
     * @return 某天的零点时刻
     */
    public static Date getZeroTimeOfDay(final Date date) {
        if (date == null) {
            return date;
        }
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        final Date result = calendar.getTime();

        return result;
    }

    /**
     * 获取某天的最后时刻。
     *
     * @param date 时刻
     * @return 某天的最后时刻
     */
    public static Date getLastTimeOfDay(final Date date) {
        if (date == null) {
            return date;
        }
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        //        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date addYears(final Date time, final Integer years) {
        if (time == null) {
            return time;
        }
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(time);
        calendar.add(Calendar.YEAR, years);
        return calendar.getTime();
    }

    public static Date addMonths(final Date time, final Integer months) {
        if (time == null) {
            return time;
        }
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(time);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    public static Date addDays(final Date time, final Integer days) {
        if (time == null) {
            return time;
        }
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(time);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    /**
     * 计算N小时后的时刻。
     *
     * @param date 时刻
     * @param hours 小时
     * @return N小时后的时刻
     */
    public static Date addHours(final Date time, final Integer hours) {
        if (time == null) {
            return time;
        }
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(time);
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTime();
    }

    /**
     * 计算N分钟后的时刻。
     *
     * @param date 时刻
     * @param hours 分钟
     * @return N分钟后的时刻
     */
    public static Date addMinutes(final Date time, final Integer minutes) {
        if (time == null) {
            return time;
        }
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(time);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    /**
     * 计算N秒后的时刻。
     *
     * @param date 秒
     * @param hours 分钟
     * @return N秒后的时刻
     */
    public static Date addSeconds(final Date time, final Integer seconds) {
        if (time == null) {
            return time;
        }
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(time);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    /**
     * 计算N毫秒后的时刻。
     *
     * @param date 时刻
     * @param hours 毫秒
     * @return N毫秒后的时刻
     */
    public static Date addMilliSeconds(final Date time, final Integer milliSeconds) {
        if (time == null) {
            return time;
        }
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(time);
        calendar.add(Calendar.MILLISECOND, milliSeconds);
        return calendar.getTime();
    }

    /**
     * 获取某月的零点时刻。
     *
     * @param date 时刻
     * @return 某月的零点时刻
     */
    public static Date getZeroTimeOfMonth(final Date date) {
        if (date == null) {
            return date;
        }
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取某月的最后时刻。
     *
     * @param date 时刻
     * @return 某月的最后时刻
     */
    public static Date getLastTimeOfMonth(final Date date) {
        if (date == null) {
            return date;
        }

        return CalendarUtil.getLastTimeOfDay(
                CalendarUtil.addDays(CalendarUtil.getZeroTimeOfMonth(CalendarUtil.addMonths(date, 1)), -1));
    }

    /**
     * 获取某年的零点时刻。
     *
     * @param date 时刻
     * @return 某年的零点时刻
     */
    public static Date getZeroTimeOfYear(final Date date) {
        if (date == null) {
            return date;
        }
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取某年的最后时刻。
     *
     * @param date 时刻
     * @return 某年的最后时刻
     */
    public static Date getLastTimeOfYear(final Date date) {
        if (date == null) {
            return date;
        }

        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return CalendarUtil.getLastTimeOfMonth(calendar.getTime());
    }

    /**
     * 计算两天之间日期的差值。
     */
    public static int computeDays(final Date startDate, final Date endDate) {
        final int days = (int) ((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));
        return days;
    }

    /**
     * 获取日期的星期数（ 1/2/3/4/5/6/7）。
     */
    public static int getWeekday(final Date date) {
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        weekday = weekday - 1;
        if (weekday == 0) {
            weekday = 7;
        }
        return weekday;
    }

    public static boolean isValidDate(final String str) {
        boolean success = true;
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            format.setLenient(false);
            format.parse(str);
        } catch (final ParseException e) {
            success = false;
        }
        return success;
    }

    //    public static void main(final String[] args) {
    //
    //        System.out.println(TimeZone.getDefault());
    //        System.out.println(Locale.getDefault());
    //
    //        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
    //        System.out.println(format.format(CalendarUtil.buildDate(2099, 12, 31)));
    //
    //        System.out.println(CalendarUtil.getWeekday(CalendarUtil.buildDate(2013, 10, 6)));
    //        System.out.println(CalendarUtil.getWeekday(CalendarUtil.buildDate(2013, 10, 7)));
    //        System.out.println(CalendarUtil.getWeekday(CalendarUtil.buildDate(2013, 10, 11)));
    //        System.out.println(CalendarUtil.getWeekday(CalendarUtil.buildDate(2013, 10, 12)));
    //        System.out.println(CalendarUtil.getWeekday(CalendarUtil.buildDate(2013, 10, 13)));
    //        System.out.println(CalendarUtil.getWeekday(CalendarUtil.buildDate(2013, 10, 14)));
    //
    //        //        final Date date = new Date();
    //        //        for (int i = 0; i < 30; i++) {
    //        //            System.out.println(CalendarUtil.format(CalendarUtil.addDays(date, i)) + "  "
    //        //                    + CalendarUtil.isWeekend(CalendarUtil.addDays(date, i)));
    //        //        }
    //
    //        System.out.println(CalendarUtil.getZeroTimeOfMonth(CalendarUtil.buildDate(2013, 9, 14)));
    //        System.out.println(CalendarUtil.getLastTimeOfMonth(CalendarUtil.buildDate(2013, 9, 14)));
    //
    //        System.out.println(CalendarUtil.getZeroTimeOfYear(CalendarUtil.buildDate(2013, 9, 14)));
    //        System.out.println(CalendarUtil.getLastTimeOfYear(CalendarUtil.buildDate(2013, 9, 14)));
    //    }

    public static int getDaysOfMonth(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

}
