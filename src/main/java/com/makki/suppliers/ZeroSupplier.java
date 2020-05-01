package com.makki.suppliers;

public class ZeroSupplier implements ValueSupplier {

    public static final ZeroSupplier INSTANCE = new ZeroSupplier();

    @Override
    public float supply(int y, int x) {
        return 0f;
    }
}
