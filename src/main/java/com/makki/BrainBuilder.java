package com.makki;

import com.makki.functions.BrainFunction;
import com.makki.functions.ReLuFunction;
import com.makki.suppliers.RandomRangeSupplier;
import com.makki.suppliers.ValueSupplier;
import com.makki.suppliers.ZeroSupplier;

import java.util.Random;

public class BrainBuilder {

	private final Random random = new Random(System.currentTimeMillis());

	private int[] structure;
	private ValueSupplier[] bias_structure;

	private BrainFunction function = new ReLuFunction();
	private ValueSupplier supplier = RandomRangeSupplier.INSTANCE;

	private BrainBuilder() {
		structure = new int[0];
		bias_structure = new ValueSupplier[0];
	}

	public static BrainBuilder builder() {
		return new BrainBuilder();
	}

	public BrainBuilder addLayer(int count) {
		return addLayer(count, null);
	}

	public BrainBuilder addLayer(int count, ValueSupplier biasSupplier) {
		int newSize = structure.length + 1;

		// STRUCTURE
		int[] oldArray = structure;
		structure = new int[newSize];
		System.arraycopy(oldArray, 0, structure, 0, oldArray.length);
		structure[newSize - 1] = count;

		// BIASES
		ValueSupplier[] oldBiasArray = bias_structure;
		bias_structure = new ValueSupplier[newSize];
		System.arraycopy(oldBiasArray, 0, bias_structure, 0, oldBiasArray.length);
		bias_structure[newSize - 1] = biasSupplier;

		return this;
	}

	public BrainBuilder setStructure(int[] structure) {
		this.structure = structure;
		if (structure.length != bias_structure.length) {
			this.setBiasStructure(new ValueSupplier[structure.length]);
		}
		return this;
	}

	public BrainBuilder setBiasStructure(ValueSupplier[] structure) {
		this.bias_structure = structure;
		if (this.structure.length != bias_structure.length) {
			throw new IllegalStateException("Mismatch in bias structure");
		}
		return this;
	}

	public BrainBuilder setFunction(BrainFunction function) {
		this.function = function;
		return this;
	}

	public BrainBuilder setSupplier(ValueSupplier supplier) {
		this.supplier = supplier;
		return this;
	}

	public BrainBuilder branchDestination(Brain destination) {
		BrainBuilder branch = new DestinationBuilder(destination);
		branch.setFunction(this.function);
		branch.setSupplier(this.supplier);
		branch.setStructure(this.structure);
		branch.setBiasStructure(this.bias_structure);
		return branch;
	}

	public Brain build() {
		return build(this.supplier);
	}

	private Brain build(ValueSupplier supplier) {
		Brain brain = new Brain(this.function, supplier);
		for (int i = 0; i < structure.length; i++) {
			int count = structure[i];
			ValueSupplier biasSupplier = bias_structure[i];
			brain.append(count, biasSupplier);
		}

		return brain;
	}

	protected Brain createNewOrFromExtension() {
		return build(ZeroSupplier.INSTANCE);
	}

	public Brain produceChildUnsafe(Brain parent1, Brain parent2) {
		Brain child = createNewOrFromExtension();
		child.setFunction(parent2.getFunction());

		for (int i = 0; i < child.getWeightLayerCount(); i++) {
			BrainLayer layer = child.getWeightLayer(i);
			BrainLayer parentLayer1 = parent1.getWeightLayer(i);
			BrainLayer parentLayer2 = parent2.getWeightLayer(i);
			for (int x = 0; x < layer.getWidth(); x++) {
				for (int y = 0; y < layer.getHeight(); y++) {
					if (random.nextBoolean()) {
						layer.values[x][y] = parentLayer2.values[x][y];
					}
					layer.values[x][y] = parentLayer1.values[x][y];
				}
			}
		}

		for (int i = 0; i < child.getLayerCount(); i++) {
			BrainLayer layer = child.getLayer(i);
			BrainLayer parentLayer1 = parent1.getLayer(i);
			BrainLayer parentLayer2 = parent2.getLayer(i);

			if (parentLayer1.isBiased() && parentLayer2.isBiased()) {
				layer.createBiasZeroed();
				for (int x = 0; x < layer.getWidth(); x++) {
					for (int y = 0; y < layer.getHeight(); y++) {
						if (random.nextBoolean()) {
							layer.bias[x][y] = parentLayer2.bias[x][y];
						}
						layer.bias[x][y] = parentLayer1.bias[x][y];
					}
				}
			} else {
				BrainLayer oneBiased = parentLayer1.isBiased() ? parentLayer1 : (parentLayer2.isBiased() ? parentLayer2 : null);

				if (oneBiased == null) {
					continue;
				}
				for (int x = 0; x < layer.getWidth(); x++) {
					for (int y = 0; y < layer.getHeight(); y++) {
						layer.bias[x][y] = oneBiased.bias[x][y];
					}
				}
			}
		}

		return child;
	}

	public Brain produceChildUnsafe(Brain parent1, Brain parent2, int mutationPercent, int mutationDivider) {
		Brain child = produceChildUnsafe(parent1, parent2);
		return mutate(child, mutationPercent, mutationDivider);
	}

	public Brain produceChild(Brain parent1, Brain parent2) {
		//match signature
		int[] sign1 = parent1.getStructure();
		int[] sign2 = parent2.getStructure();

		boolean success = false;
		if (sign1.length == sign2.length) {
			success = true;
			for (int i = 0; i < sign1.length; i++) {
				success = success && (sign1[i] == sign2[i]);
			}
		}
		if (!success) {
			throw new IllegalStateException("MISMATCH OF BRAIN SIGNATURES");
		}

		boolean thisSignatureMatch = false;
		if (structure.length == sign2.length) {
			thisSignatureMatch = true;
			for (int i = 0; i < structure.length; i++) {
				thisSignatureMatch = thisSignatureMatch && (structure[i] == sign2[i]);
			}
		}
		if (!thisSignatureMatch) {
			structure = new int[sign2.length];
			System.arraycopy(sign2, 0, structure, 0, sign2.length);
		}

		Brain child = createNewOrFromExtension();
		child.setFunction(parent2.getFunction());

		for (int i = 0; i < child.getWeightLayerCount(); i++) {
			BrainLayer layer = child.getWeightLayer(i);
			BrainLayer parentLayer1 = parent1.getWeightLayer(i);
			BrainLayer parentLayer2 = parent2.getWeightLayer(i);
			for (int x = 0; x < layer.getWidth(); x++) {
				for (int y = 0; y < layer.getHeight(); y++) {
					if (random.nextBoolean()) {
						layer.values[x][y] = parentLayer2.values[x][y];
					}
					layer.values[x][y] = parentLayer1.values[x][y];
				}
			}
		}

		for (int i = 0; i < child.getLayerCount(); i++) {
			BrainLayer layer = child.getLayer(i);
			BrainLayer parentLayer1 = parent1.getLayer(i);
			BrainLayer parentLayer2 = parent2.getLayer(i);

			if (parentLayer1.isBiased() && parentLayer2.isBiased()) {
				layer.createBiasZeroed();
				for (int x = 0; x < layer.getWidth(); x++) {
					for (int y = 0; y < layer.getHeight(); y++) {
						if (random.nextBoolean()) {
							layer.bias[x][y] = parentLayer2.bias[x][y];
						}
						layer.bias[x][y] = parentLayer1.bias[x][y];
					}
				}
			} else {
				BrainLayer oneBiased = parentLayer1.isBiased() ? parentLayer1 : (parentLayer2.isBiased() ? parentLayer2 : null);

				if (oneBiased == null) {
					continue;
				}
				for (int x = 0; x < layer.getWidth(); x++) {
					for (int y = 0; y < layer.getHeight(); y++) {
						layer.bias[x][y] = oneBiased.bias[x][y];
					}
				}
			}
		}

		return child;
	}

	public Brain produceChild(Brain parent1, Brain parent2, int mutationPercent, int mutationDivider) {
		Brain child = produceChild(parent1, parent2);
		return mutate(child, mutationPercent, mutationDivider);
	}

	public Brain copy(Brain parent1) {
		int[] sign1 = parent1.getStructure();

		boolean thisSignatureMatch = false;
		if (structure.length == sign1.length) {
			thisSignatureMatch = true;
			for (int i = 0; i < structure.length; i++) {
				thisSignatureMatch = thisSignatureMatch && (structure[i] == sign1[i]);
			}
		}
		if (!thisSignatureMatch) {
			structure = new int[sign1.length];
			System.arraycopy(sign1, 0, structure, 0, sign1.length);
		}

		Brain child = createNewOrFromExtension();
		child.setFunction(parent1.getFunction());

		for (int i = 0; i < child.getWeightLayerCount(); i++) {
			BrainLayer layer = child.getWeightLayer(i);
			BrainLayer parentLayer1 = parent1.getWeightLayer(i);

			for (int x = 0; x < layer.getWidth(); x++) {
				for (int y = 0; y < layer.getHeight(); y++) {
					layer.values[x][y] = parentLayer1.values[x][y];
				}
			}
		}

		for (int i = 0; i < child.getLayerCount(); i++) {
			BrainLayer parentLayer1 = parent1.getLayer(i);
			if (!parentLayer1.isBiased()) {
				continue;
			}
			BrainLayer layer = child.getLayer(i);

			for (int x = 0; x < layer.getWidth(); x++) {
				for (int y = 0; y < layer.getHeight(); y++) {
					layer.bias[x][y] = parentLayer1.bias[x][y];
				}
			}
		}

		return child;
	}

	public Brain copy(Brain parent1, int mutationPercent, int mutationDivider) {
		Brain child = copy(parent1);
		mutate(child, mutationPercent, mutationDivider);
		return child;
	}

	public Brain mutate(Brain brain, int mutationPercent, int mutationDivider) {
		if (mutationPercent == 0) return brain;
		if (mutationDivider == 0) {
			mutationDivider = 1;
		}

		for (int i = 0; i < brain.getWeightLayerCount(); i++) {
			BrainLayer layer = brain.getWeightLayer(i);
			int count = (layer.getNodeCount() * mutationPercent / Math.max(mutationDivider, 1)) / 100;
			count = Math.max(count, 1);

			for (int j = 0; j < count; j++) {
				int x = random.nextInt(layer.getWidth());
				int y = random.nextInt(layer.getHeight());
				layer.values[x][y] = supplier.supply(x, y);
			}
		}

		for (int i = 0; i < brain.getLayerCount(); i++) {
			BrainLayer layer = brain.getLayer(i);
			if (!layer.isBiased()) {
				continue;
			}

			int count = (layer.getNodeCount() * mutationPercent / mutationDivider) / 100;
			count = Math.max(count, 1);

			for (int j = 0; j < count; j++) {
				int x = random.nextInt(layer.getWidth());
				int y = random.nextInt(layer.getHeight());
				layer.bias[x][y] = supplier.supply(x, y);
			}
		}
		return brain;
	}

	private static class DestinationBuilder extends BrainBuilder {
		private final Brain destination;

		DestinationBuilder(Brain destination) {
			this.destination = destination;
		}

		@Override
		protected Brain createNewOrFromExtension() {
			return destination;
		}
	}

}
