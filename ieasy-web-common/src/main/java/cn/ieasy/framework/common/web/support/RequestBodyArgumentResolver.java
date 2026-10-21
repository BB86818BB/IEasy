package cn.ieasy.framework.common.web.support;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.h2.util.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import cn.ieasy.framework.common.web.annotation.BodyVariable;
import cn.ieasy.framework.common.web.utils.json.JsonUtils;

/**
 * Allows resolving the {@link Searchable}.
 * 
 * @author LinWang
 */
public final class RequestBodyArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String BODY_VALUES = "body$json$values";

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        if (!this.isBaseDataType(parameter.getParameterType())) {
            return false;
        }
        final BodyVariable requestBody = parameter.getParameterAnnotation(BodyVariable.class);
        return requestBody != null;
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) webRequest
                .getAttribute(RequestBodyArgumentResolver.BODY_VALUES, RequestAttributes.SCOPE_REQUEST);
        if (body == null) {
            final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(request.getInputStream(), out);
            body = JsonUtils.jsonToMap(out.toString());
            webRequest.setAttribute(RequestBodyArgumentResolver.BODY_VALUES, body, RequestAttributes.SCOPE_REQUEST);
        }
        final Object value = body.get(parameter.getParameterName());
        if (value == null) {
            return value;
        }
        final Class<?> type = parameter.getParameterType();
        if (type.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (type.equals(Byte.class)) {
            return new Byte(String.valueOf(value));
        } else if (type.equals(Long.class)) {
            return new Long(String.valueOf(value));
        } else if (type.equals(Double.class)) {
            return new Double(String.valueOf(value));
        } else if (type.equals(Float.class)) {
            return new Float(String.valueOf(value));
        } else if (type.equals(Short.class)) {
            return new Short(String.valueOf(value));
        } else if (type.equals(BigDecimal.class)) {
            return new BigDecimal(String.valueOf(value));
        } else if (type.equals(BigInteger.class)) {
            return new BigInteger(String.valueOf(value));
        } else if (type.equals(Date.class)) {
            return new Date((Long) value);
        }
        return value;
    }

    private boolean isBaseDataType(final Class<?> clazz) {
        return (clazz.isPrimitive() ||
                clazz.equals(String.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(Float.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(BigDecimal.class) ||
                clazz.equals(BigInteger.class) ||
                clazz.equals(Boolean.class) ||
                clazz.equals(Date.class));
    }
}
