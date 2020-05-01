package com.makki;

import com.makki.functions.ReLuFunction;
import com.makki.suppliers.RandomRangeSupplier;
import org.junit.Assert;
import org.junit.Test;

public class BrainBreedingTest {

	private int mutationPercent = 6;
	private int mutationDivider = 2;

	@Test
	public void testBreeding() {
		BrainBuilder brainBuilder = BrainBuilder.builder()
				.setFunction(new ReLuFunction())
				.setSupplier(RandomRangeSupplier.INSTANCE)
				.addLayer(54)
				.addLayer(60)
				.addLayer(40);

		Brain source1 = brainBuilder.build();
		Brain source2 = brainBuilder.build();
		Brain child = brainBuilder.produceChild(source1, source2);

		for (int i = 0; i < child.getWeightLayerCount(); i++) {
			BrainLayer layer = child.getWeightLayer(i);
			BrainLayer parentLayer1 = source1.getWeightLayer(i);
			BrainLayer parentLayer2 = source2.getWeightLayer(i);

			for (int y = 0; y < layer.getHeight(); y++) {
				for (int x = 0; x < layer.getWidth(); x++) {
					float value = layer.getValue(y, x);
					float parent1Value = parentLayer1.getValue(y, x);
					float parent2Value = parentLayer2.getValue(y, x);
					if (value != parent1Value && value != parent2Value) {
						Assert.fail("MISMATCH in layer: " + i + " with pos -> x: " + x + " y: " + y
								+ " value: " + value + " p1: " + parent1Value + " p2: " + parent2Value);
					}
				}
			}
		}
	}

	@Test
	public void testBreedingMutation() {
		BrainBuilder brainBuilder = BrainBuilder.builder()
				.setFunction(new ReLuFunction())
				.setSupplier(RandomRangeSupplier.INSTANCE)
				.addLayer(54)
				.addLayer(60)
				.addLayer(40);

		Brain source1 = brainBuilder.build();
		Brain source2 = brainBuilder.build();
		Brain child = brainBuilder.produceChild(source1, source2, mutationPercent, mutationDivider);

		int totalCount = 0;
		int mutatedCount = 0;

		for (int i = 0; i < child.getWeightLayerCount(); i++) {
			BrainLayer layer = child.getWeightLayer(i);
			BrainLayer parentLayer1 = source1.getWeightLayer(i);
			BrainLayer parentLayer2 = source2.getWeightLayer(i);

			for (int y = 0; y < layer.getHeight(); y++) {
				for (int x = 0; x < layer.getWidth(); x++) {
					totalCount++;
					float value = layer.getValue(y, x);
					float parent1Value = parentLayer1.getValue(y, x);
					float parent2Value = parentLayer2.getValue(y, x);
					if (value != parent1Value && value != parent2Value) {
						mutatedCount++;
					}
				}
			}
		}

		float percent = (mutatedCount * 100 / (float) totalCount);
		System.out.println("Mutated " + mutatedCount + " out of " + totalCount + " which is about: " + percent + "%");

		Assert.assertTrue(percent <= (mutationPercent / (float) mutationDivider));
	}

	@Test
	public void testCopy() {
		BrainBuilder brainBuilder = BrainBuilder.builder()
				.setFunction(new ReLuFunction())
				.setSupplier(RandomRangeSupplier.INSTANCE)
				.addLayer(54)
				.addLayer(60)
				.addLayer(40);

		Brain source1 = brainBuilder.build();
		Brain child = brainBuilder.copy(source1);

		for (int i = 0; i < child.getWeightLayerCount(); i++) {
			BrainLayer layer = child.getWeightLayer(i);
			BrainLayer parentLayer1 = source1.getWeightLayer(i);

			for (int y = 0; y < layer.getHeight(); y++) {
				for (int x = 0; x < layer.getWidth(); x++) {
					float value = layer.getValue(y, x);
					if (value != parentLayer1.getValue(y, x)) {
						Assert.fail("MISMATCH in layer: " + i + " with pos -> x:" + x + " y:" + y);
					}
				}
			}
		}
	}

	@Test
	public void testCopyMutation() {
		BrainBuilder brainBuilder = BrainBuilder.builder()
				.setFunction(new ReLuFunction())
				.setSupplier(RandomRangeSupplier.INSTANCE)
				.addLayer(54)
				.addLayer(60)
				.addLayer(40);

		Brain source1 = brainBuilder.build();
		Brain child = brainBuilder.copy(source1, mutationPercent, mutationDivider);

		int totalCount = 0;
		int mutatedCount = 0;

		for (int i = 0; i < child.getWeightLayerCount(); i++) {
			BrainLayer layer = child.getWeightLayer(i);
			BrainLayer parentLayer1 = source1.getWeightLayer(i);

			for (int y = 0; y < layer.getHeight(); y++) {
				for (int x = 0; x < layer.getWidth(); x++) {
					totalCount++;
					float value = layer.getValue(y, x);
					if (value != parentLayer1.getValue(y, x)) {
						mutatedCount++;
					}
				}
			}
		}

		float percent = (mutatedCount * 100 / (float) totalCount);
		System.out.println("Mutated " + mutatedCount + " out of " + totalCount + " which is about: " + percent + "%");

		Assert.assertTrue(percent <= (mutationPercent / (float) mutationDivider));
	}

}
