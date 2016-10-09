package com.boyamihungry.passageways;

import java.util.function.Function;

/**
 * Created by patwheaton on 9/24/16.
 */
public interface Oscillator {

    static final float DEFAULT_FREQUENCY = 60000f;
    public String getDescription();

    public float getFrequency();

    /**
     * Gives a value between -1 and 1.
     * @return
     */
    public float getValue();

    default Object getComputedValue(Function<Float,Object> computation) {
        return computation.apply(getValue());
    }
}
