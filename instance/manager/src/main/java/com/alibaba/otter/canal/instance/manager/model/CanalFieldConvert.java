package com.alibaba.otter.canal.instance.manager.model;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * NOTE:
 *
 * @author lizhiyang
 * @Date 2019-07-09 15:30
 */
public class CanalFieldConvert {
    private static Logger logger = LoggerFactory.getLogger(CanalFieldConvert.class);
    public static <T> T convert(Class<T> clazz, Map<String, String> properties) {
        try {
            T instance = clazz.newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            for(PropertyDescriptor pd : pds) {
                Method writeMethod = pd.getWriteMethod();
                if(writeMethod == null) {
                    continue;
                }
                String fieldName = pd.getName();
                Field field = tryGetField(clazz, fieldName);
                if(field == null) {
                    continue;
                }
                CanalField canalField = field.getAnnotation(CanalField.class);
                if(canalField == null) {
                    continue;
                }
                String itemName = canalField.value();
                if(!properties.containsKey(itemName)) {
                    continue;
                }
                String itemValue = properties.get(itemName);
                trySetField(instance, writeMethod, field, itemValue);
            }
            return instance;
        } catch (Exception e) {
            logger.error("convert exception", e);
        }
        return null;
    }

    private static <T> Field tryGetField(Class<T> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field;
        } catch (NoSuchFieldException e) {
            logger.error("tryGetField exception:"+fieldName, e);
        }
        return null;
    }

    private static <T> void trySetField(T instance, Method setMethod, Field field, String value) {
        try {
            Class fieldType = field.getType();
            if(String.class.isAssignableFrom(fieldType)) {
                setMethod.invoke(instance, value);
            } else if(Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType)) {
                setMethod.invoke(instance, Boolean.valueOf(value));
            } else if(Long.class.isAssignableFrom(fieldType) || long.class.isAssignableFrom(fieldType)) {
                if(StringUtils.isNotBlank(value)) {
                    setMethod.invoke(instance, Long.valueOf(value));
                }
            } else if(Integer.class.isAssignableFrom(fieldType) || int.class.isAssignableFrom(fieldType)) {
                if(StringUtils.isNotBlank(value)) {
                    setMethod.invoke(instance, Integer.valueOf(value));
                }
            } else {
                throw new RuntimeException("unSupported type:"+field.getType().getSimpleName()+" "+field.getName());
            }
        } catch (Exception e) {
            logger.error("trySetField exception:" + field.getName(), e);
        }

    }
}
