package org.cydu.inf.concurrent.strategy;


import org.cydu.inf.concurrent.ConcurrentProperty;

/**
 * 失败策略
 */
public interface FailStrategy {
    /**
     * 被并发后的操作
     * @return 是否正常进行
     */
    boolean operate(String currentSourceKey, ConcurrentProperty property);
}
