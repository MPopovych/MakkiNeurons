package com.makki.functions;

public class ZeroOneFunction implements BrainFunction {
    @Override
    public float apply(float value) {
        return value > 0.5 ? 1 : 0;
    }
}
