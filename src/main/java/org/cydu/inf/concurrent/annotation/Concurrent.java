package org.cydu.inf.concurrent.annotation;



import org.cydu.inf.concurrent.consts.IFailStrategyKey;

import java.lang.annotation.*;

/**
 * 被并发控制的方法
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Concurrent {
    String value();

    int expireSeconds() default 60;

    String failStrategy() default IFailStrategyKey.failFast;

    int retryCount() default 3;

    int retryPeriod() default 3;

}
