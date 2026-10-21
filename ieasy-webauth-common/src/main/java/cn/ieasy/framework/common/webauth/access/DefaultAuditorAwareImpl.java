package cn.ieasy.framework.common.webauth.access;

import org.springframework.data.domain.AuditorAware;

import cn.ieasy.framework.common.webauth.util.SecurityUtils;

/**
 * 默认的Model审计实现
 * @author LinWang
 *
 */
public class DefaultAuditorAwareImpl implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        final String userNo = SecurityUtils.getCurrentUserLogin();
        if (userNo != null) {
            return userNo;
        }
        return "";
    }

}
