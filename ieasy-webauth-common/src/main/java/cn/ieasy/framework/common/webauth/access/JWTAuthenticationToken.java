package cn.ieasy.framework.common.webauth.access;

public class JWTAuthenticationToken extends DefaultAuthenticationToken {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -582042481965863044L;

    private String token;

    private long expireTime;

    public String getToken() {
        if (System.currentTimeMillis() > this.expireTime) {
            this.token = null;
        }
        return this.token;
    }

    public void setToken(final String token, final long expireTime) {
        this.token = token;
        this.expireTime = expireTime;
    }
}
