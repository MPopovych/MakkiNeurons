package com.makki;

import com.makki.functions.Functions;
import com.makki.suppliers.RandomRangeSupplier;
import org.junit.Test;

import java.util.Arrays;

public class BrainTrainingTest {

    private static final int BRAIN_COUNT = 6;
    private static final int IO_COUNT = 90;

    @Test
    public void test() {
        int testCount = 1;
        long total = 0;

        long start = System.currentTimeMillis();
        for (int i = 0; i < testCount; i++) {
            total += testOnce();
        }
        long end = System.currentTimeMillis();
        System.out.println("[BrainTrainingTest] Elapsed time: " + total / testCount + "ms.");
        System.out.println("[testRealisticLoad] Elapsed time: " + (end - start) + "ms.");

    }

    public long testOnce() {
        Brain[] brainPool = new Brain[BRAIN_COUNT];
        int[] brainResults = new int[BRAIN_COUNT];

        BrainBuilder template = BrainBuilder.builder()
                .setFunction(Functions.NegPos)
                .addLayer(IO_COUNT)
                .addLayer(20, RandomRangeSupplier.INSTANCE)
                .addLayer(20)
                .addLayer(20, RandomRangeSupplier.INSTANCE)
                .addLayer(10)
                .addLayer(IO_COUNT);

        for (int i = 0; i < brainPool.length; i++) {
            brainPool[i] = template.build();
        }

        float[] input = new float[IO_COUNT];
        Arrays.fill(input, -1f);

        float[] output = new float[IO_COUNT];
        int bestGlobalResult = 0;

        long start = System.currentTimeMillis();
        while (bestGlobalResult < IO_COUNT) {
            //iterate brains and get results
            for (int b = 0; b < brainPool.length; b++) {
                Brain brain = brainPool[b];
                brain.setInput(input);
                brain.calculate(output);

                int matched = 0;
                for (float v : output) {
                    if (v == 1f) {
                        matched++;
                    }
                }
                brainResults[b] = matched;
            }

            //find 1 best result
            int bestIndex = 0;
            int bestResult = 0;
            for (int b = 0; b < brainResults.length; b++) {
                if (brainResults[b] > bestResult) {
                    bestIndex = b;
                    bestResult = brainResults[b];
                    if (bestResult > bestGlobalResult) {
                        bestGlobalResult = bestResult;
                    }
                }
            }
            if (bestGlobalResult == IO_COUNT) break;

            Brain bestBrain = brainPool[bestIndex];
            int lastNotBest = 0;
            for (int b = 0; b < brainPool.length; b++) {
                if (bestIndex == b) continue;

                template.branchDestination(brainPool[b])
                        .copy(bestBrain, IO_COUNT, bestGlobalResult); //the closer to the goal - the less mutation
//                        .copy(bestBrain, 3, 4);
                lastNotBest = b;
            }
            //this one to ensure a good start, so called fresh blood
            brainPool[lastNotBest] = template.build();
            System.out.println("[test iterate] Best so far: " + bestGlobalResult);
        }
        long end = System.currentTimeMillis();
        return (end - start);
    }

}
