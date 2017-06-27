package org.cydu.inf.concurrent;

import org.cydu.inf.concurrent.strategy.FailStrategy;
import org.cydu.inf.concurrent.strategy.FailStrategyFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.cydu.inf.concurrent.consts.ConcurrentExceptionCodes.CURRENT_REQUEST;

@Aspect
@Component
@Order(-1)
public class ConcurrentInterceptor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("infConcurrentCtrl")
    private ConcurrentCtrl concurrentCtrl;

    @Autowired
    private FailStrategyFactory failStrategyFactory;

    @Pointcut("@annotation(Concurrent)")
    public void concurrentAspect() {}

    @Around("concurrentAspect()")
    private Object doAround(ProceedingJoinPoint jp) throws Throwable {
        ConcurrentProperty property = null;
        boolean getLocked=false;
        try {
            long start = System.currentTimeMillis();

            property = ConcurrentPropertyGenerator.genConcurrentProperty(jp);
            getLocked= doGetConcurrentLock(property);

            logger.debug("ConcurrentProperty property = " + property.toString()
                    + " times = " + String.valueOf(System.currentTimeMillis() - start) + " ms");
            return jp.proceed();
        } catch (Throwable e) {
            throw e;
        } finally {
            if (getLocked) {
                List<String> sourceKeys = property.getSourceKeys();
                if (sourceKeys != null) {
                    for (String sourceKey : sourceKeys) {
                        concurrentCtrl.removeConcurrentCtrl(property.getSourceType(), sourceKey);
                        logger.debug("clear lock by sourceKey, sourceType = " + property.getSourceType() + " sourceKey = " + sourceKey);
                    }
                }
            }
        }
    }

    private boolean doGetConcurrentLock(ConcurrentProperty property) {
        List<String> sourceKeys = property.getSourceKeys();
        String sourceType = property.getSourceType();
        Integer expireSeconds = property.getExpireSeconds();
        String failStrategy = property.getFailStrategy();

        for (String sourceKey : sourceKeys) {
            if (concurrentCtrl.isConcurrent(sourceType, sourceKey, expireSeconds)) {
                FailStrategy strategy = failStrategyFactory.getFailStrategy(failStrategy);
                boolean continueFlag = strategy.operate(sourceKey, property);
                if (!continueFlag) {
                    throw new ConcurrentException(CURRENT_REQUEST);
                }
            }
        }
        return true;
    }

}
