package com.makki.suppliers;

import java.util.Random;

public class RandomSupplier implements ValueSupplier {

    public static final RandomSupplier INSTANCE = new RandomSupplier(new Random(System.currentTimeMillis()));

    private Random random;

    public RandomSupplier(Random random) {
        this.random = random;
    }

    @Override
    public float supply(int y, int x) {
        return random.nextFloat();
    }
}
