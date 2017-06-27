package org.cydu.inf.concurrent.strategy;

import org.cydu.inf.concurrent.ConcurrentException;
import org.cydu.inf.concurrent.consts.ConcurrentExceptionCodes;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FailStrategyFactory implements ApplicationContextAware {

    private Map<String, FailStrategy> strategyMap = new HashMap<>();

    private ApplicationContext applicationContext;

    public FailStrategy getFailStrategy(String failStrategy) {
        FailStrategy strategy = strategyMap.get(failStrategy);
        if (strategy == null) {
            try {
                Object bean = applicationContext.getBean(failStrategy,FailStrategy.class);
                if (!(bean instanceof FailStrategy)) {
                    throw new ConcurrentException(ConcurrentExceptionCodes.CUSTOM_STRATEGY_TYPE_ERROR);
                }
                strategy = (FailStrategy) bean;
                strategyMap.put(failStrategy,strategy);
            } catch (BeansException e) {
                throw new ConcurrentException(ConcurrentExceptionCodes.NOT_FOUND_CUSTOM_STRATEGY, e);
            }
        }
        return strategy;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}