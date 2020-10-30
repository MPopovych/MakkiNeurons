# MakkiNeurons

## Project description

A simple java library to work with neural networks. Current implementation is based on matrix multiplication and a basic genetic crossover between networks.
For references - check tests.
Project is under development, so changes will come.

### Usage

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

Plans:
Finish the C++ implementation and include it as optional functionality via JNI. This should give a huge boost in performance for large matricies.
