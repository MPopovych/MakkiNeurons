package com.makki;

import java.nio.ByteBuffer;
import java.util.Base64;

public class BrainInstanceManager {

    public static String getSignature(Brain brain) {
        int weightSum = 0;
        int weightLayerCount = brain.getWeightLayerCount();
        for (int i = 0; i < weightLayerCount; i++) {
            weightSum += brain.getWeightLayer(i).getNodeCount();
        }

        int[] structure = brain.getStructure();

        ByteBuffer buff = ByteBuffer.allocate(4 + structure.length * 4 + weightSum * 4 + weightLayerCount * 4);

        buff.putInt(structure.length); //STRUCTURE SIZE
        for (int count : structure) {
            buff.putInt(count); //STRUCTURE ELEMENTS
        }

        for (int i = 0; i < weightLayerCount; i++) {
            BrainLayer layer = brain.getWeightLayer(i);
            int count = layer.getNodeCount();

            buff.putInt(count); //WEIGHT COUNT

            for (int x = 0; x < layer.getWidth(); x++) {
                for (int y = 0; y < layer.getHeight(); y++) {
                    buff.putFloat(layer.getValue(x, y)); //WEIGHT VALUES
                }
            }
        }

        return Base64.getEncoder().encodeToString(buff.array());
    }

    public static Brain loadFromSignature(String signature) {
        ByteBuffer buff = ByteBuffer.wrap(Base64.getDecoder().decode(signature));

        int structureSize = buff.getInt();
        int[] structure = new int[structureSize];
        for (int i = 0; i < structureSize; i++) {
            structure[i] = buff.getInt();
        }

        Brain brain = BrainBuilder.builder()
                .setStructure(structure)
                .build();

        int brainLayerIndex = 0;
        while (buff.hasRemaining()) {
            int nextFloats = buff.getInt();

            BrainLayer layer = brain.getWeightLayer(brainLayerIndex);

            float[] values = new float[nextFloats];
            for (int i = 0; i < nextFloats; i++) {
                values[i] = buff.getFloat();
            }

            layer.setValues(values);
            brainLayerIndex++;
        }

        return brain;
    }
}
