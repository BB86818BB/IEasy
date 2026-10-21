package cn.ieasy.framework.common.webauth.access;

public interface CurrentUserService {

    Object loadUserByUsername(String username);
}
