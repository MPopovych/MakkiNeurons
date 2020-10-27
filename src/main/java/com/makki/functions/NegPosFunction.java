package com.makki.functions;

public class NegPosFunction implements BrainFunction {
    @Override
    public float apply(float value) {
        return value > 0 ? 1 : -1;
    }
}
