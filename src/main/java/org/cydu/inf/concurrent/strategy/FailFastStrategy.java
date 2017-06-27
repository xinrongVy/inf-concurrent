package org.cydu.inf.concurrent.strategy;

import org.cydu.inf.concurrent.ConcurrentProperty;
import org.springframework.stereotype.Service;

@Service
public class FailFastStrategy implements FailStrategy {

    @Override
    public boolean operate(String currentSourceKey, ConcurrentProperty property) {
        return false;
    }

}
