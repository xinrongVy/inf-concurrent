package org.cydu.inf.concurrent;

import java.util.List;

import static org.cydu.inf.concurrent.consts.ConcurrentExceptionCodes.RETRY_COUNT_PARAM_ERROR;
import static org.cydu.inf.concurrent.consts.ConcurrentExceptionCodes.RETRY_PERIOD_PARAM_ERROR;

public class ConcurrentProperty {

    private String sourceType;

    List<String> sourceKeys;

    private Integer expireSeconds;

    private String failStrategy;

    private int retryCount;

    private int retryPeriod;

    public String getFailStrategy() {
        return failStrategy;
    }

    public void setFailStrategy(String failStrategy) {
        this.failStrategy = failStrategy;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public List<String> getSourceKeys() {
        return sourceKeys;
    }

    public void setSourceKeys(List<String> sourceKeys) {
        this.sourceKeys = sourceKeys;
    }

    public Integer getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(Integer expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getRetryPeriod() {
        return retryPeriod;
    }

    public void setRetryPeriod(int retryPeriod) {
        this.retryPeriod = retryPeriod;
    }

    @Override
    public String toString() {
        return "ConcurrentProperty{" +
                "sourceType='" + sourceType + '\'' +
                ", sourceKeys=" + sourceKeys +
                ", expireSeconds=" + expireSeconds +
                ", failStrategy=" + failStrategy +
                ", retryCount=" + retryCount +
                ", retryPeriod=" + retryPeriod +
                '}';
    }

    public void checkSelf() {
        if (retryCount < 0) {
            throw new ConcurrentException(RETRY_COUNT_PARAM_ERROR);
        }

        if (retryPeriod < 0) {
            throw new ConcurrentException(RETRY_PERIOD_PARAM_ERROR);
        }
    }
}
