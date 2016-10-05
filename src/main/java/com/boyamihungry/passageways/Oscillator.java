package com.boyamihungry.passageways;

import java.util.function.Function;

/**
 * Created by patwheaton on 9/24/16.
 */
public interface Oscillator {

    public String getDescription();

    public int getFrequency();

    public float getValue();

    default Object getComputedValue(Function<Float,Object> computation) {
        return computation.apply(getValue());
    }


}
