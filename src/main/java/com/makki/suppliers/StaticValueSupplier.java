package com.makki.suppliers;

public class StaticValueSupplier implements ValueSupplier {

    private float value;

    public StaticValueSupplier(float value) {
        this.value = value;
    }

    @Override
    public float supply(int y, int x) {
        return value;
    }
}
