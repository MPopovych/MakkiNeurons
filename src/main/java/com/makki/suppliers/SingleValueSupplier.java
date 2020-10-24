package com.makki.suppliers;

public class SingleValueSupplier implements ValueSupplier {

    private final float value;

    public SingleValueSupplier(float value) {
        this.value = value;
    }

    @Override
    public float supply(int y, int x) {
        return value;
    }
}
