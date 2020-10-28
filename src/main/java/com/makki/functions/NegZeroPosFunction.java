package com.makki.functions;

public class NegZeroPosFunction implements BrainFunction {
    @Override
    public float apply(float value) {
        return value > 0.99 ? 1 : (value < -0.99 ? -1 : 0);
    }
}
