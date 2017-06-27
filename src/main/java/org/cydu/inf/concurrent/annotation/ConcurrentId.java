package org.cydu.inf.concurrent.annotation;

import java.lang.annotation.*;

/**
 * 被控制的sourceId
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConcurrentId {
    String[] value() default "__self";
}