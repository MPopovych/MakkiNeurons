package com.makki;

import com.makki.functions.BrainFunction;
import com.makki.functions.ReLuFunction;
import com.makki.suppliers.RandomRangeSupplier;
import com.makki.suppliers.ValueSupplier;
import com.makki.suppliers.ZeroSupplier;

import java.util.Random;

public class BrainBuilder {

	private int[] structure;
	private ValueSupplier[] bias_structure;

	private BrainFunction function = new ReLuFunction();
	private ValueSupplier supplier = RandomRangeSupplier.INSTANCE;

	private Random random = new Random(System.currentTimeMillis());

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
		branch.setStructure(this.structure);
		branch.setBiasStructure(this.bias_structure);
		branch.setFunction(this.function);
		branch.setSupplier(this.supplier);
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
			layer.setValues(new CrossLayerMerger(parentLayer1, parentLayer2));
		}

		return child;
	}

	public Brain produceChild(Brain parent1, Brain parent2, int mutationPercent, int mutationDivider) {
		Brain child = produceChild(parent1, parent2);
		mutate(child, mutationPercent, mutationDivider);
		return child;
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
			layer.setValues(new SimpleLayerMerger(parentLayer1));

			for (int x = 0; x < layer.getWidth(); x++) {
				for (int y = 0; y < layer.getHeight(); y++) {
					layer.values[x][y] = parentLayer1.values[x][y];
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

	private void mutate(Brain brain, int mutationPercent, int mutationDivider) {
		if (mutationPercent == 0) return;
		if (mutationDivider == 0) {
			mutationDivider = 1;
		}

		for (int i = 0; i < brain.getWeightLayerCount(); i++) {
			BrainLayer layer = brain.getWeightLayer(i);
			int count = layer.getNodeCount() * mutationPercent / 100;
			count = Math.max(count, 1) / Math.max(mutationDivider, 1);

			for (int j = 0; j < count; j++) {
				int x = random.nextInt(layer.getWidth());
				int y = random.nextInt(layer.getHeight());
				layer.setValue(x, y, supplier);
			}
		}
	}

	private static class LayerMerger implements ValueSupplier {
		private Random random = new Random(System.currentTimeMillis());

		private BrainLayer parentLayer1;
		private BrainLayer parentLayer2;

		LayerMerger(BrainLayer parentLayer1, BrainLayer parentLayer2) {
			this.parentLayer1 = parentLayer1;
			this.parentLayer2 = parentLayer2;
		}

		@Override
		public float supply(int x, int y) {
			if (random.nextBoolean()) {
				return parentLayer2.values[x][y];
			}
			return parentLayer1.values[x][y];
		}
	}

	private static class CrossLayerMerger implements ValueSupplier {
		private Random random = new Random(System.currentTimeMillis());

		private BrainLayer parentLayer1;
		private BrainLayer parentLayer2;

		private int crossCount = 0;

		CrossLayerMerger(BrainLayer parentLayer1, BrainLayer parentLayer2) {
			this.parentLayer1 = parentLayer1;
			this.parentLayer2 = parentLayer2;
		}

		@Override
		public float supply(int x, int y) {
			if (crossCount == 0) {
				crossCount = random.nextInt(Math.max(parentLayer1.getNodeCount() / 4, 1)) * (random.nextBoolean() ? 1 : -1);
			}

			if (crossCount > 0) {
				crossCount--;
				return parentLayer1.values[x][y];
			} else if (crossCount < 0) {
				crossCount++;
				return parentLayer2.values[x][y];
			}

			if (random.nextBoolean()) {
				return parentLayer2.values[x][y];
			}
			return parentLayer1.values[x][y];
		}
	}

	private static class SimpleLayerMerger implements ValueSupplier {
		private BrainLayer parentLayer1;

		SimpleLayerMerger(BrainLayer parentLayer1) {
			this.parentLayer1 = parentLayer1;
		}

		@Override
		public float supply(int x, int y) {
			return parentLayer1.values[x][y];
		}
	}

	private static class DestinationBuilder extends BrainBuilder {
		private Brain destination;

		DestinationBuilder(Brain destination) {
			this.destination = destination;
		}

		@Override
		protected Brain createNewOrFromExtension() {
			return destination;
		}
	}

}
