package com.boyamihungry.passageways;

import static processing.core.PApplet.sin;
import static processing.core.PConstants.TWO_PI;

/**
 * Created by patwheaton on 9/25/16.
 */
public class SinusoidalOscillator implements Oscillator {

    final String description = "A sinusoidal oscillator.";
    final int frequency;

    public SinusoidalOscillator(int frequency) {
        if ( frequency != 0 ) {
            this.frequency = frequency;
        } else {
            throw new IllegalArgumentException("Can't have a zero frequency oscillator");
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getFrequency() {
        return frequency;
    }

    @Override
    public float getValue() {
        return sin( System.currentTimeMillis() % frequency / TWO_PI);
    }

}
