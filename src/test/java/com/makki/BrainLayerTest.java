package com.makki;

import org.junit.Assert;
import org.junit.Test;

public class BrainLayerTest {

    @Test
    public void testOutput1() {
        long start = System.currentTimeMillis();

        float[] output = new float[]{114, 114};
        BrainLayer a = new BrainLayer(6, 1, new float[]{1, 2, 0, 7, 0, 7});
        BrainLayer b = new BrainLayer(2, 6, new float[]{4, 6, 6, 7, 0, 7, 4, 6, 6, 7, 0, 7});
        BrainLayer c = new BrainLayer(2, 1);

        c.setToZeroes();
        a.multiply(b, c);

        for (int i = 0; i < output.length; i++) {
            Assert.assertEquals(c.getValue(i, 0), output[i], 0.0);
        }

        long end = System.currentTimeMillis();

        System.out.println("[testOutput1] Elapsed time: " + (end - start) + "ms");
    }

    @Test
    public void performanceTest1() {
        long start = System.currentTimeMillis();
        BrainLayer a = new BrainLayer(3, 3, new float[]{1, 1, 1, 2, 2, 2, 3, 3, 3});
        BrainLayer b = new BrainLayer(3, 3, new float[]{1, 1, 1, 2, 2, 2, 3, 3, 3});
        BrainLayer c = new BrainLayer(3, 3, false);

        for (int i = 0; i < 4000000; i++) {
            c.setToZeroes();
            a.multiply(b, c);
        }

        long end = System.currentTimeMillis();

        System.out.println("[performanceTest1] Elapsed time: " + (end - start));
    }

    @Test
    public void performanceTest2() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 4000000; i++) {
            BrainLayer a = new BrainLayer(2, 3, new float[]{5f, 4f, 6f, 1f, 7f, 7f});
            BrainLayer b = new BrainLayer(1, 2, new float[]{2f, 2f});
            BrainLayer c = new BrainLayer(1, 3);

            c.setToZeroes();
            a.multiply(b, c);
        }

        long end = System.currentTimeMillis();

        System.out.println("[performanceTest2] Elapsed time: " + (end - start));
    }

    @Test
    public void performanceTest3() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 4000000; i++) {
            BrainLayer a = new BrainLayer(3, 1, new float[]{5f, 4f, 6f});
            BrainLayer b = new BrainLayer(3, 3, new float[]{5f, 4f, 6f, 1f, 7f, 7f, 5f, 4f, 6f});
            BrainLayer c = new BrainLayer(3, 1);

            c.setToZeroes();
            a.multiply(b, c);
        }

        long end = System.currentTimeMillis();

        System.out.println("[performanceTest3] Elapsed time: " + (end - start));
    }

}
