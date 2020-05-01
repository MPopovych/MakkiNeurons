package com.makki.functions;

public class LeakyReLuFunction implements BrainFunction {
    private static final float LEAK_VALUE = 0.01f;

    @Override
    public float apply(float value) {
        if (value >= 0) {
            return value;
        }

        return LEAK_VALUE * value;
    }
}
