package com.makki.functions;

public class ReLuFunction implements BrainFunction {
    @Override
    public float apply(float value) {
        return Math.max(value, 0);
    }
}
