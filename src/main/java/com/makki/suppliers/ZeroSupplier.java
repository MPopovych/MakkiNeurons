package com.makki.suppliers;

public class ZeroSupplier extends SingleValueSupplier {

    public static final ZeroSupplier INSTANCE = new ZeroSupplier();

    public ZeroSupplier() {
        super(0);
    }

    @Override
    public float supply(int x, int y) {
        return 0f;
    }
}
