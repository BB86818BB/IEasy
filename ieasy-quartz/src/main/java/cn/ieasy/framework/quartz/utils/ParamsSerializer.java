
package cn.ieasy.framework.quartz.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.ieasy.framework.quartz.core.managment.JobsManager;

/**
 * Job参数序列化类。
 *
 * @version 1.0
 */
@Component
public class ParamsSerializer {

    /** className->Class cache */
    private transient final Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();
    /** class->TypeReference cache */
    private transient final Map<Class<?>, TypeReference<?>> typeReferenceCache =
            new HashMap<Class<?>, TypeReference<?>>();
    /** logger */
    private static final Log LOGGER = LogFactory.getLog(ParamsSerializer.class);

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    static {
        ParamsSerializer.jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ParamsSerializer.jsonMapper
                .setVisibilityChecker(ParamsSerializer.jsonMapper.getSerializationConfig().getDefaultVisibilityChecker()
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    }

    /**
     * 将Job的参数序列化后，赋值到jobDataMap。
     *
     * @param jobDataMap jobDataMap
     * @param jobId jobId
     * @param params Job的参数
     * @param paramTypes Job的参数类型
     */
    public void serializeParamsToMap(final JobDataMap jobDataMap, final String jobId, final Object[] params,
            final TypeReference<?>[] paramTypes) {

        if ((params == null) || (params.length == 0)) {
            return;
        }
        final boolean isTypeReference = (paramTypes != null) && (paramTypes.length > 0);
        jobDataMap.put(JobsManager.TYPE_IS_REFERANCE, isTypeReference);
        final List<String> jsonParams = new ArrayList<String>(params.length);
        final List<String> strParamTypes = new ArrayList<String>(params.length);
        for (int i = 0; i < params.length; i++) {
            try {
                jsonParams.add(ParamsSerializer.jsonMapper.writeValueAsString(params[i]));
            } catch (final Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            if (isTypeReference) {
                // 类型引用，直接使用类型引用的类名
                strParamTypes.add(paramTypes[i].getClass().getName());
            } else {
                // 非类型引用，使用对象实例的类名
                strParamTypes.add(ClassUtils.getUserClass(params[i]).getName());
            }
        }
        jobDataMap.put(JobsManager.JOB_PARAMS, jsonParams);
        jobDataMap.put(JobsManager.JOB_PARAMS_TYPE, strParamTypes);
    }

    /**
     * 反序列化JobDataMap里的参数。
     *
     * @param jobDataMap jobDataMap
     * @return JobDataMap里的参数
     */
    public Object[] deserializeParamsFromMap(final JobDataMap jobDataMap) {
        @SuppressWarnings("unchecked")
        final List<String> jobParams = (List<String>) jobDataMap.get(JobsManager.JOB_PARAMS);
        if ((jobParams == null) || (jobParams.size() == 0)) {
            return null;
        }
        final Object[] params = new Object[jobParams.size()];
        @SuppressWarnings("unchecked")
        final List<String> paramClassesName = (List<String>) jobDataMap.get(JobsManager.JOB_PARAMS_TYPE);
        final boolean isTypeReference = jobDataMap.getBoolean(JobsManager.TYPE_IS_REFERANCE);

        // 根据Json值和参数类型，反序列化job的参数
        for (int i = 0; i < jobParams.size(); i++) {
            final Class<?> paramClass = this.getArgumentClass(paramClassesName.get(i));
            if (isTypeReference) {
                try {
                    params[i] =
                            ParamsSerializer.jsonMapper.readValue(jobParams.get(i),
                                    this.getTypeReferenceInstance(paramClass));
                } catch (final Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            } else {
                try {
                    params[i] = ParamsSerializer.jsonMapper.readValue(jobParams.get(i), paramClass);
                } catch (final Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return params;
    }

    /**
     * 获得类名对应的类。
     *
     * @param className 类名
     * @return 类名对应的类
     */
    private synchronized Class<?> getArgumentClass(final String className) {
        if (this.classCache.containsKey(className)) {
            return this.classCache.get(className);
        }
        try {
            final Class<?> clazz = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
            this.classCache.put(className, clazz);
            return clazz;
        } catch (final ClassNotFoundException e) {
            ParamsSerializer.LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得类型引用的实例。
     *
     * @param paramClass 类型引用类
     * @return 类型引用的实例
     */
    private synchronized TypeReference<?> getTypeReferenceInstance(final Class<?> paramClass) {
        if (this.typeReferenceCache.containsKey(paramClass)) {
            return this.typeReferenceCache.get(paramClass);
        }
        try {
            final TypeReference<?> type = (TypeReference<?>) paramClass.newInstance();
            this.typeReferenceCache.put(paramClass, type);
            return type;
        } catch (final Exception e) {
            ParamsSerializer.LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }
}
