# MakkiNeurons

## Project description

A simple java library to work with neural networks. Current implementation is based on matrix multiplication and a basic genetic crossover between networks.
For references - check tests.
Project is under development, so changes will come.

### Brain creation

``` java
BrainBuilder template = BrainBuilder.builder()
                .setFunction(new ReLuFunction())
                .setInitialSupply(RandomRangeSupplier.INSTANCE) // all layers without unspecified supplier will use this
                .addLayerWeight(54, ZeroSupplier.INSTANCE) // layer(*) weights will be zeroes on creating
                .addLayerWeight(40, RandomRangeSupplier.INSTANCE) // layer(*) weights will be random (-1 to 1) on creating
                .addLayer(80)
                .addLayerBias(60, ZeroSupplier.INSTANCE) // layer(+) biases will be zeroes on creation
                .addLayer(40);
Brain brain = template.build();

String signature = BrainInstanceManager.getSignature(brain); //String can be saved to file

Brain target = template.build();
BrainInstanceManager.loadFromSignature(signature, target); // and loaded from string, use a template with the same configuration

```

### Brain usage

Check BrainTraining test, where two goals are achieved:
1. Bruteforce and invert one set of values
2. Train a brain to invert all input values like 1 and -1. There are not that many optimal solution for this task,
one of which is to have a zeroed weight matrix with '-1' weights placed diagonally.

``` java

BrainBuilder template = BrainBuilder.builder()
            .setFunction(new ReLuFunction())
            .setInitialSupply(RandomRangeSupplier.INSTANCE)
            .addLayer(54)
            .addLayer(30)
            .addLayer(20);

Brain brain = template.build();
float[] input = new float[54];

for (int i = 0; i < 54; i++) {
    input[i] = RandomSupplier.INSTANCE.supply(0, 0);
}

brain.setInput(input);
float[] result = brainSource.calculate();

// or allocation friendly option
float[] alloc = new float[20]; 
brainSource.calculate(alloc);

```

Plans:
Finish the C++ implementation and include it as optional functionality via JNI. This should give a huge boost in performance for large matricies.
