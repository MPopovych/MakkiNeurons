package com.makki;

import org.junit.Test;

public class BrainLayerTest {

    @Test
    public void test() {
        long start = System.currentTimeMillis();
        BrainLayer a = new BrainLayer(3, 3, new float[]{1, 1, 1, 2, 2, 2, 3, 3, 3});
        BrainLayer b = new BrainLayer(3, 3, new float[]{1, 1, 1, 2, 2, 2, 3, 3, 3});
        BrainLayer c = new BrainLayer(3, 3);

        for (int i = 0; i < 4000000; i++) {
            a.multiply(b, c);
        }

        long end = System.currentTimeMillis();

        System.out.println("Elapsed time: " + (end - start));
    }

    @Test
    public void test2() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 4000000; i++) {
            BrainLayer a = new BrainLayer(3, 2, new float[]{5f, 4f, 6f, 1f, 7f, 7f});
            BrainLayer b = new BrainLayer(2, 1, new float[]{2f, 2f});
            BrainLayer c = new BrainLayer(3, 1);

            a.multiply(b, c);
        }

        long end = System.currentTimeMillis();

        System.out.println("Elapsed time: " + (end - start));
    }

    @Test
    public void test3() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 4000000; i++) {
            BrainLayer a = new BrainLayer(1, 3, new float[]{5f, 4f, 6f});
            BrainLayer b = new BrainLayer(3, 3, new float[]{5f, 4f, 6f, 1f, 7f, 7f, 5f, 4f, 6f});
            BrainLayer c = new BrainLayer(1, 3);

            a.multiply(b, c);
        }

        long end = System.currentTimeMillis();

        System.out.println("Elapsed time: " + (end - start));
    }

}
