package cn.ieasy.framework.common.web.support;

/**
 * 获取登录用户实例的工厂接口
 * <p>
 * 业务扩展接口。根据具体业务，提供对该工厂Bean的实现，获取自定义类型的登录用户对象实例。
 */
public interface CurrentUserFactoryBean {

    Object getCurrentUser();
}
