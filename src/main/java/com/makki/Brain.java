package com.makki;

import com.makki.functions.BrainFunction;
import com.makki.suppliers.ValueSupplier;

public class Brain {

    private boolean debug = false;
    private int layerCount = 0;

    private BrainFunction function;
    private ValueSupplier supplier;
    private BrainLayer[] brainLayers = new BrainLayer[0];

    Brain(BrainFunction function, ValueSupplier supplier) {
        this.function = function;
        this.supplier = supplier;
    }

    public int[] getStructure() {
        int[] struct = new int[(brainLayers.length + 1) / 2];

        for (int i = 0; i < struct.length; i++) {
            struct[i] = brainLayers[i * 2].getWidth();
        }
        return struct;
    }

    public BrainLayer getWeightLayer(int index) {
        return brainLayers[index * 2 + 1];
    }

    public BrainLayer getLayer(int index) {
        return brainLayers[(index + 1) / 2];
    }

    public int getLayerCount() {
        return (brainLayers.length + 1) / 2;
    }

    public int getWeightLayerCount() {
        return (brainLayers.length - 1) / 2;
    }

    void setDebug(boolean debug) {
        this.debug = debug;
    }

    void append(int count) {
        layerCount++;

        BrainLayer weightLayer = null;
        if (brainLayers.length > 0) {
            BrainLayer layer = brainLayers[brainLayers.length - 1];
            weightLayer = new BrainLayer(layer.getWidth(), count, supplier);
        }

        BrainLayer nextLayer = new BrainLayer(1, count);

        int newSize = brainLayers.length + 1 + (weightLayer == null ? 0 : 1);

        BrainLayer[] oldArray = brainLayers;
        brainLayers = new BrainLayer[newSize];
        System.arraycopy(oldArray, 0, brainLayers, 0, oldArray.length);
        if (weightLayer != null) {
            brainLayers[oldArray.length] = weightLayer;
        }
        brainLayers[brainLayers.length - 1] = nextLayer;
    }

    BrainFunction getFunction() {
        return this.function;
    }

    void setFunction(BrainFunction function) {
        this.function = function;
    }

    public void setInput(float[] values) {
        if (brainLayers.length <= 0) {
            throw new IllegalStateException("NO LAYERS");
        }

        BrainLayer first = brainLayers[0];
        if (first.getNodeCount() != values.length) {
            throw new IllegalStateException("MISMATCH OF NODE: " + first.getNodeCount() + " INPUT: " + values.length);
        }

        for (int i = 0; i < first.getWidth(); i++) {
            first.values[0][i] = values[i];
        }
    }

    public void setInput(ValueSupplier supplier) {
        if (brainLayers.length <= 0) {
            throw new IllegalStateException("NO LAYERS");
        }

        BrainLayer first = brainLayers[0];
        for (int i = 0; i < first.getWidth(); i++) {
            first.values[0][i] = supplier.supply(0, i);
        }
    }

    public float[] calculate() {
        float[] result = new float[brainLayers[brainLayers.length - 1].getWidth()];
        return calculate(result);
    }

    public float[] calculate(float[] outDest) {
        int layer;
        int weight = 0;

        for (int i = 0; i < layerCount - 1; i++) {
            layer = i * 2;
            weight = layer + 1;

            if (debug) {
                brainLayers[layer].print();
                brainLayers[weight].print();
            }

            BrainLayer target = brainLayers[weight + 1];
            brainLayers[layer].multiply(brainLayers[weight], target);

            for (int j = 0; j < target.getWidth(); j++) {
                target.values[0][j] = function.apply(target.values[0][j]);
            }
        }

        BrainLayer last = brainLayers[weight + 1];

        for (int i = 0; i < outDest.length; i++) {
            outDest[i] = last.values[0][i];
        }
        return outDest;
    }

}
