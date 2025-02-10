package com.silence004.fault.tolerant;

import com.silence004.fault.tolerant.impl.FailFastTolerantStrategy;
import com.silence004.spi.SpiLoader;

public class TolerantStrategyFactory {
    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY=new FailFastTolerantStrategy();

    public static TolerantStrategy getInstance(String key){
       return SpiLoader.getInstance(TolerantStrategy.class,key);
    }
}
