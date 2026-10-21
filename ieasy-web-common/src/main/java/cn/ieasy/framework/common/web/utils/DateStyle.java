package cn.ieasy.framework.common.web.utils;

/**
 * 时间格式类
 *
 * @author Administrator
 */
public enum DateStyle {

    YYYY_MM("yyyy-MM", false),
    YYYY_MM_DD("yyyy-MM-dd", false),
    YYYY_MM_DD_HH("yyyy-MM-dd HH", false),
    YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm", false),
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss", false),
    YYYY_MM_DD_T_HH_MM_SS("yyyy-MM-dd'T'HH:mm:ss", false),

    YYYYMM("yyyyMM", false),
    YYYYMMDD("yyyyMMdd", false),
    YYYYMMDDHH("yyyyMMddHH", false),
    YYYYMMDDHHMM("yyyyMMddHHmm", false),
    YYYYMMDDHHMMSS("yyyyMMddHHmmss", false),

    YYYY_MM_EN("yyyy/MM", false),
    YYYY_MM_DD_EN("yyyy/MM/dd", false),
    YYYY_MM_DD_HH_MM_EN("yyyy/MM/dd HH:mm", false),
    YYYY_MM_DD_HH_MM_SS_EN("yyyy/MM/dd HH:mm:ss", false),

    YYYY_MM_CN("yyyy年MM月", false),
    YYYY_MM_DD_CN("yyyy年MM月dd日", false),
    YYYY_MM_DD_HH_CN("yyyy年MM月dd日HH时", false),
    YYYY_MM_DD_HH_MM_CN("yyyy年MM月dd日 HH:mm", false),
    YYYY_MM_DD_HH_MM_SS_CN("yyyy年MM月dd日 HH:mm:ss", false),

    EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY("EEE MMM dd HH:mm:ss zzz yyyy", false),

    HH_MM("HH:mm", true),
    HH_MM_SS("HH:mm:ss", true),

    MM_DD("MM-dd", true),
    MM_DD_HH_MM("MM-dd HH:mm", true),
    MM_DD_HH_MM_SS("MM-dd HH:mm:ss", true),

    MM_DD_EN("MM/dd", true),
    MM_DD_HH_MM_EN("MM/dd HH:mm", true),
    MM_DD_HH_MM_SS_EN("MM/dd HH:mm:ss", true),

    MM_DD_CN("MM月dd日", true),
    MM_DD_HH_MM_CN("MM月dd日 HH:mm", true),
    MM_DD_HH_MM_SS_CN("MM月dd日 HH:mm:ss", true);

    private String value;

    private boolean isShowOnly;

    DateStyle(final String value, final boolean isShowOnly) {
        this.value = value;
        this.isShowOnly = isShowOnly;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isShowOnly() {
        return this.isShowOnly;
    }
}
