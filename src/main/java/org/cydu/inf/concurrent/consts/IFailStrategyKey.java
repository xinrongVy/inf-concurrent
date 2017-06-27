package org.cydu.inf.concurrent.consts;

import org.cydu.inf.concurrent.strategy.FailFastStrategy;
import org.cydu.inf.concurrent.strategy.FailRetryStrategy;

/**
 * Created by xinrongvy on 17-6-26.
 */
public interface IFailStrategyKey {
    /**@see FailFastStrategy */
    String failFast="failFastStrategy";

    /**@see FailRetryStrategy */
    String failRetry="failRetryStrategy";
}
