package cn.ieasy.framework.common.webauth.access;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CurrentUserBean {

    /**
     * 用户的身份编码
     */
    private String userPrincipal;

    /**
     * 用户角色列表
     */
    private Collection<String> roles;

    /**
     * 调用方的应用ID
     */
    private String appId;

    /**
     * 用户详细，此处限定为：只允许放字符串类型的内容
     */
    private Map<String, String> details;

    public String getUserPrincipal() {
        return this.userPrincipal;
    }

    public void setUserPrincipal(final String userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    public Collection<String> getRoles() {
        return this.roles != null ? this.roles : Collections.emptyList();
    }

    public void setRoles(final Collection<String> roles) {
        this.roles = roles;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(final String appId) {
        this.appId = appId;
    }

    public Map<String, String> getDetails() {
        return this.details;
    }

    public void setDetails(final Map<String, String> details) {
        this.details = details;
    }
}
