package com.makki;


import com.makki.suppliers.ValueSupplier;
import com.makki.suppliers.ZeroSupplier;

public class BrainLayer {

    private final int height;
    private final int width;
    float[][] values;
    float[][] bias = null;

    public BrainLayer(int height, int width) {
        this(height, width, ZeroSupplier.INSTANCE, false);
    }

    public BrainLayer(int height, int width, boolean biased) {
        this(height, width, ZeroSupplier.INSTANCE, biased);
    }

    public BrainLayer(int height, int width, ValueSupplier supplier){
        this(height, width, supplier, false);
    }
    public BrainLayer(int height, int width, ValueSupplier supplier, boolean biased) {
        this.height = height;
        this.width = width;

        values = new float[height][];
        for (int y = 0; y < height; y++) {
            values[y] = new float[width];
            for (int x = 0; x < width; x++) {
                values[y][x] = supplier.supply(y, x);
            }
        }
        if (biased) {
            bias = new float[height][];
            for (int y = 0; y < height; y++) {
                bias[y] = new float[width];
                for (int x = 0; x < width; x++) {
                    bias[y][x] = supplier.supply(y, x);
                }
            }
        }
    }

    public BrainLayer(int height, int width, float[] source) {
        this(height, width, source, false);
    }

    public BrainLayer(int height, int width, float[] source, boolean biased) {
        this.height = height;
        this.width = width;

        values = new float[height][];
        for (int y = 0; y < height; y++) {
            values[y] = new float[width];
            System.arraycopy(source, y * width, values[y], 0, width);
        }
        if (biased) {
            bias = new float[height][];
            for (int y = 0; y < height; y++) {
                bias[y] = new float[width];
                System.arraycopy(source, y * width, bias[y], 0, width);
            }
        }
    }

    public void setValues(ValueSupplier supplier) {
        values = new float[height][];
        for (int y = 0; y < height; y++) {
            values[y] = new float[width];
            for (int x = 0; x < width; x++) {
                values[y][x] = supplier.supply(y, x);
            }
        }
    }

    public void setValues(float[] source) {
        values = new float[height][];
        for (int y = 0; y < height; y++) {
            values[y] = new float[width];
            System.arraycopy(source, y * width, values[y], 0, width);
        }
    }

    public void setToZeroes() {
        for (int y = 0; y < values.length; y++) {
            for (int x = 0; x < values[0].length; x++) {
                values[y][x] = 0;
            }
        }
    }

    public void setToBias() {
        if (bias == null) {
            return;
        }
        for (int y = 0; y < values.length; y++) {
            System.arraycopy(bias[y], 0, values[y], 0, values[0].length);
        }
    }

    public void multiply(float[][] target, float[][] destination) {
        int thisRows = values.length;
        int thisColumns = values[0].length;
        int targetRows = target.length;
        int targetColumns = target[0].length;

        if (thisColumns != targetRows) {
            throw new IllegalArgumentException("CONFLICT OF " + thisColumns + " TARGET " + targetRows + ".");
        }

        for (int i = 0; i < thisRows; i++) { // aRow
            for (int j = 0; j < targetColumns; j++) { // bColumn
                for (int k = 0; k < thisColumns; k++) { // aColumn
                    destination[i][j] += values[i][k] * target[k][j];
                }
            }
        }
    }

    public void multiply(BrainLayer target, BrainLayer destination) {
        multiply(target.values, destination.values);
    }

    public float getValue(int y, int x) {
        return values[y][x];
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getNodeCount() {
        return height * width;
    }

    public void print() {
        for (float[] value : values) {
            for (float v : value) {
                System.out.print(v + " ");
            }
            System.out.println();
        }
    }

    void setSingleValue(int y, int x, ValueSupplier supplier) {
        values[y][x] = supplier.supply(y, x);
    }
}
