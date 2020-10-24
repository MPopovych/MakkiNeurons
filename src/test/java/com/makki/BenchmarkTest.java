package com.makki;

import com.makki.functions.BrainFunction;
import com.makki.functions.LeakyReLuFunction;
import com.makki.suppliers.RandomRangeSupplier;
import com.makki.suppliers.ValueSupplier;
import org.junit.Test;

public class BenchmarkTest {

    @Test
    public void test() {
        //these test results will be compared to C++ counterpart

        BrainFunction function = new LeakyReLuFunction();
        ValueSupplier supplier = RandomRangeSupplier.INSTANCE;

        Brain brain = new Brain(function, supplier);
        brain.append(4, true);
        brain.append(60, true);
        brain.append(60, true);
        brain.append(60, true);
        brain.append(60, true);
        brain.append(60, true);
        brain.append(60, true);
        brain.append(2);

        float[] input1 = new float[]{0, 0, 0, 0};
        float[] input2 = new float[]{1, 1, 1, 1};
        float[] input3 = new float[]{1, 0, 0, 1};
        float[] input4 = new float[]{1, 1, 0, 0};
        float[] input5 = new float[]{0, 0, 0, 1};
        float[] input6 = new float[]{0, 0, 1, 1};
        float[] output = new float[]{0, 0};

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            testBrain(brain, input1, output);
            testBrain(brain, input2, output);
            testBrain(brain, input3, output);
            testBrain(brain, input4, output);
            testBrain(brain, input5, output);
            testBrain(brain, input6, output);
        }
        long end = System.currentTimeMillis();

        System.out.println("Elapsed time: " + (end - start) + "ms.");
    }

    private void testBrain(Brain brain, float[] input, float[] output) {
        brain.setInput(input);
        brain.calculate(output);
    }
}
