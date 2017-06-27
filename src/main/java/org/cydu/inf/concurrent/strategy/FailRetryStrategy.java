package org.cydu.inf.concurrent.strategy;

import org.cydu.inf.concurrent.ConcurrentCtrl;
import org.cydu.inf.concurrent.ConcurrentException;
import org.cydu.inf.concurrent.ConcurrentProperty;
import org.cydu.inf.concurrent.consts.ConcurrentExceptionCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FailRetryStrategy implements FailStrategy {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConcurrentCtrl concurrentCtrl;

    @Override
    public boolean operate(String currentSourceKey, ConcurrentProperty property) {
        boolean continueFlag = false;

        int retryCount = property.getRetryCount();
        for (int i = 0; i < retryCount; i++) {
            try {
                Thread.sleep(property.getRetryPeriod() * 1000);
                boolean concurrent = concurrentCtrl.isConcurrent(property.getSourceType(), currentSourceKey, property.getExpireSeconds());
                logger.debug("fail retrying , current count is " + i + " com.tcl.iread.biz.concurrent is " + concurrent + " property=" + property.toString());
                if (!concurrent) {
                    continueFlag = true;
                    break;
                }
            } catch (InterruptedException e) {
                throw new ConcurrentException(ConcurrentExceptionCodes.RETRY_ERROR, e);
            }
        }

        logger.debug("fail retry end, result continueFlag is " + continueFlag);
        return continueFlag;
    }

}
