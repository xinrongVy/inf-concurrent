package org.cydu.inf.concurrent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.cydu.inf.concurrent.annotation.Concurrent;
import org.cydu.inf.concurrent.annotation.ConcurrentId;
import org.cydu.inf.concurrent.consts.ConcurrentExceptionCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.cydu.inf.concurrent.consts.ConcurrentExceptionCodes.*;


public class ConcurrentPropertyGenerator {

    private static Logger logger = LoggerFactory.getLogger(ConcurrentPropertyGenerator.class);

    public static ConcurrentProperty genConcurrentProperty(ProceedingJoinPoint jp) {
        Concurrent annotation = findConcurrent(jp);
        String sourceType = annotation.value();
        int expireSeconds = annotation.expireSeconds();
        String failStrategy = annotation.failStrategy();
        int retryCount = annotation.retryCount();
        int retryPeriod = annotation.retryPeriod();
        List<String> sourceKeys = findSourceKeys(jp);

        ConcurrentProperty property = new ConcurrentProperty();
        property.setSourceType(sourceType);
        property.setSourceKeys(sourceKeys);
        property.setExpireSeconds(expireSeconds);
        property.setFailStrategy(failStrategy);
        property.setRetryCount(retryCount);
        property.setRetryPeriod(retryPeriod);

        property.checkSelf();
        return property;
    }

    private static Concurrent findConcurrent(ProceedingJoinPoint jp) {
        Method currentMethod = getCurrentMethod(jp);
        Concurrent annotation = currentMethod.getAnnotation(Concurrent.class);
        return annotation;
    }

    private static List<String> findSourceKeys(ProceedingJoinPoint jp) {
        List<String> sourceKeys = findFromParam(jp);

        if (sourceKeys.isEmpty()) {
            throw new ConcurrentException(NOT_FOUND_CONCURRENT_ID);
        }
        return sourceKeys;
    }

    /**
     * 从方法参数中获取key
     */
    private static List<String> findFromParam(ProceedingJoinPoint jp) {
        List<String> sourceKeys = new ArrayList<>();

        Object[] args = jp.getArgs();
        Method currentMethod = getCurrentMethod(jp);
        final Annotation[][] an = currentMethod.getParameterAnnotations();
        if (an.length > 0) {
            for (int i = 0; i < an.length; i++) {
                for (int j = 0; j < an[i].length; j++) {
                    Annotation annotation = an[i][j];
                    if (annotation instanceof ConcurrentId) {
                        if (args[i] == null) {
                            throw new ConcurrentException(CONCURRENTID_OBJECT_EMPTY);
                        }

                        ConcurrentId concurrentId = (ConcurrentId) annotation;
                        String[] values = concurrentId.value();
                        for (String path : values) {
                            if (StringUtils.isEmpty(path)) {
                                throw new ConcurrentException(CONCURRENTID_OBJECT_EMPTY);
                            }
                            if ("__self".equals(path)) {
                                sourceKeys.add(transObjToStringAndCheckEmpty(args[i]));
                            } else {
                                String propValue = findByXPath(args[i], path);
                                sourceKeys.add(propValue);
                            }
                        }
                    }

                }
            }
        }
        return sourceKeys;
    }

    private static Method getCurrentMethod(ProceedingJoinPoint jp) {
        Object target = jp.getTarget();
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method currentMethod;
        try {
            currentMethod = target.getClass().getMethod(signature.getName(), signature.getParameterTypes());
        } catch (NoSuchMethodException e) {
            logger.error("not found impl method", e);
            throw new ConcurrentException(ConcurrentExceptionCodes.NOT_FOUND_IMPL_METHOD);
        }
        return currentMethod;
    }

    private static String transObjToStringAndCheckEmpty(Object obj) {
        if (obj == null) {
            throw new ConcurrentException(CONCURRENT_PROPERTY_EMPTY);
        }
        String result = obj.toString();
        if (StringUtils.isEmpty(result)) {
            throw new ConcurrentException(CONCURRENT_PROPERTY_EMPTY);
        }
        return result;
    }

    private static String findByXPath(Object obj, String path) {
        BeanWrapper b = PropertyAccessorFactory.forBeanPropertyAccess(obj);
        Object propertyValue;
        try {
            propertyValue = b.getPropertyValue(path);
        } catch (NotReadablePropertyException e) {
            throw new ConcurrentException(XPATH_PROPERTY_NOT_EXIST, e);
        }
        return transObjToStringAndCheckEmpty(propertyValue);
    }

}
