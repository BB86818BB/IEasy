package cn.ieasy.framework.common.web.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.util.StringUtils;

import cn.ieasy.framework.common.web.exception.CustomRuntimeException;

/**
 * 时间处理工具类
 *
 * @author Administrator
 */
public class DateUtils {

    private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();

    private static final Object object = new Object();

    /**
     * 获取SimpleDateFormat
     *
     * @param pattern 日期格式
     * @return SimpleDateFormat对象
     * @throws RuntimeException 异常：非法日期格式
     */
    private static SimpleDateFormat getDateFormat(final String pattern) throws RuntimeException {
        SimpleDateFormat dateFormat = DateUtils.threadLocal.get();
        if (dateFormat == null) {
            synchronized (DateUtils.object) {
                if (dateFormat == null) {
                    dateFormat = new SimpleDateFormat(pattern);
                    dateFormat.setLenient(false);
                    DateUtils.threadLocal.set(dateFormat);
                }
            }
        }
        dateFormat.applyPattern(pattern);
        return dateFormat;
    }

    /**
     * 将日期字符串转化为日期。失败返回null。
     *
     * @param date 日期字符串
     * @param pattern 日期格式
     * @return 日期
     */
    public static Date StringToDate(final String date, final String pattern) {
        Date myDate = null;
        if ((date != null) && StringUtils.hasText(date) && !"".equals(date)) {
            try {
                myDate = DateUtils.getDateFormat(pattern).parse(date);
            } catch (final Exception e) {
                throw new CustomRuntimeException("1", String.format("时间字符串格式无法转换为时间格式."));
            }
        }
        return myDate;
    }

    /**
     * 将日期字符串转化为日期。失败返回null。
     *
     * @param date 日期字符串
     * @param dateStyle 日期风格
     * @return 日期
     */
    public static Date StringToDate(final String date, final DateStyle dateStyle) {
        Date myDate = null;
        if (dateStyle != null) {
            myDate = DateUtils.StringToDate(date, dateStyle.getValue());
        }
        return myDate;
    }

    /**
     * 将日期转化为日期字符串。失败返回null。
     *
     * @param date 日期
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String DateToString(final Date date, final String pattern) {
        String dateString = null;
        if (date != null) {
            try {
                dateString = DateUtils.getDateFormat(pattern).format(date);
            } catch (final Exception e) {
                throw new CustomRuntimeException("1", String.format("时间格式无法转换为字符串格式."));
            }
        }
        return dateString;
    }

    /**
     * 将日期转化为日期字符串。失败返回null。
     *
     * @param date 日期
     * @param dateStyle 日期风格
     * @return 日期字符串
     */
    public static String DateToString(final Date date, final DateStyle dateStyle) {
        String dateString = null;
        if ((date != null) && (dateStyle != null)) {
            dateString = DateUtils.DateToString(date, dateStyle.getValue());
        }
        return dateString;
    }

    /**
     * 将日期转化为日期字符串。失败返回""。
     *
     * @param date 日期
     * @param dateStyle 日期风格
     * @return 日期字符串
     */
    public static String DateToNotNullString(final Date date, final DateStyle dateStyle) {
        String dateString = "";
        if ((date != null) && (dateStyle != null)) {
            dateString = DateUtils.DateToString(date, dateStyle.getValue());
        }
        return dateString;
    }

    /**
     * 当前时间增加一个小时
     *
     * @param date
     * @param n
     * @return
     */
    public static Date addHours(final Date date, final int n) {
        try {
            final Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            cd.add(Calendar.HOUR_OF_DAY, n);//增加n个小时
            return cd.getTime();

        } catch (final Exception e) {
            return null;
        }

    }

    /**
     * 获得某月的最大天数
     *
     * @param year 四位年份
     * @param month 一位或两位月份
     * @return int 某月的总天数
     */
    public static int getDayNumOfMonth(final String year, final String month) {
        final int[] nDayOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        final GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();

        if (calendar.isLeapYear(Integer.parseInt(year))) {
            nDayOfMonth[1] = 29;
        }
        final int nDayMonth = Integer.parseInt(month);

        return nDayOfMonth[nDayMonth - 1];
    }

    /**
     * 获取某年第一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getYearFirst(final int year) {
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        final Date currYearFirst = calendar.getTime();
        return currYearFirst;
    }

    /**
     * 获取某年最后一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getYearLast(final int year) {
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        final Date currYearLast = calendar.getTime();

        return currYearLast;
    }

    /**
     * 获取当天开始时间
     *
     * @param date
     * @return
     */
    public static Date getStartTime(final Date date) {
        final Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(date);
        todayStart.set(Calendar.HOUR, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取当天结束时间
     *
     * @param date
     * @return
     */
    public static Date getEndTime(final Date date) {
        final Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTime(date);
        todayEnd.set(Calendar.HOUR, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    public static String string2YMDString(final String date) {
        if ((date == null) || "".equals(date)) {
            return date;
        } else if (date.length() >= 10) {
            return date.substring(0, 10);
        }
        return date;
    }

    public static int getYearOfDate(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static int getYearOfDate(final String date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtils.StringToDate(date, DateStyle.YYYY_MM_DD));
        return cal.get(Calendar.YEAR);
    }

    public static String getMondayOfWeekForDate(final String date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtils.StringToDate(date, DateStyle.YYYY_MM_DD));
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        final int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        final int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值

        return DateUtils.DateToString(cal.getTime(), DateStyle.YYYY_MM_DD);
    }

    /**
     * 获取日期所在周的周一日期
     *
     * @param date
     * @return
     */
    public static Date getMondayOfWeekForDate(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        final int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        final int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值

        return cal.getTime();
    }

    public static int getDayOfWeek(final Date date) {
        final Calendar now = Calendar.getInstance();
        now.setTime(date);
        // 一周第一天是否为星期天
        final boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
        // 获取周几
        int weekDay = now.get(Calendar.DAY_OF_WEEK);
        // 若一周第一天为星期天，则-1
        if (isFirstSunday) {
            weekDay = weekDay - 1;
            if (weekDay == 0) {
                weekDay = 7;
            }
        }
        return weekDay;
    }

    public static Date addYear(final Date date, final int year) {
        try {
            final Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            cd.add(Calendar.YEAR, year);//增加年
            return cd.getTime();

        } catch (final Exception e) {
            return null;
        }

    }

    public static Date addWeekOfYear(final Date date, final int week) {
        try {
            final Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            cd.add(Calendar.WEEK_OF_YEAR, week);//增加周
            return cd.getTime();

        } catch (final Exception e) {
            return null;
        }

    }

    public static Date addDays(final Date date, final int day) {
        try {
            final Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            cd.add(Calendar.DATE, day);//增加day天
            return cd.getTime();

        } catch (final Exception e) {
            return null;
        }

    }

}
