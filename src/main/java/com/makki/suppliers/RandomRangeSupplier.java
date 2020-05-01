package com.makki.suppliers;

import java.util.Random;

public class RandomRangeSupplier implements ValueSupplier {

    public static final RandomRangeSupplier INSTANCE = new RandomRangeSupplier(new Random(System.currentTimeMillis()));

    private Random random;

    public RandomRangeSupplier(Random random) {
        this.random = random;
    }

    @Override
    public float supply(int y, int x) {
        return random.nextFloat() * 2 - 1;
    }
}