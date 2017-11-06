import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Network implements Runnable {

    public List<Layer> layers = new ArrayList<>();
    public List<Chromosome> chromosomes;

    private Layer inputLayer;
    private List<Example> examples;
    private List<Example> fullSet;
    private List<Example> verifySet;
    private List<Example> testSet;
    private int hiddenLayers;
    private int dimension;

    public static double learningRate = .000001d;


    public Network(final List<Integer> hiddenLayers, int dimension, boolean isRadialBasis, List<Example> examples) {
        if (hiddenLayers.get(0)== 0){
            this.hiddenLayers = 0;
        } else {
            this.hiddenLayers = hiddenLayers.size();
        }
        this.dimension = dimension;
        this.learningRate = learningRate / examples.size();

        Layer.network = this;


        layers.add(inputLayer = new Layer(dimension, Type.INPUT));

        this.fullSet = examples;
        setupExamples();

        if (!isRadialBasis) {
            if (hiddenLayers.get(0) != 0){
                for (int i : hiddenLayers) {
                    layers.add(new Layer(i, Type.HIDDEN));
                }
            }
        } else {
            this.hiddenLayers = 1;

            Layer rbfHidden = new Layer(examples.size(), Type.RBFHIDDEN);

            examples.forEach(example ->
                rbfHidden.nodes.forEach(current -> {
                    current.weights = new ArrayList<Double>(example.inputs);
                    current.mu = example.outputs.get(0);
                })
            );

            layers.add(rbfHidden);
        }

        layers.add(new Layer(examples.get(0).outputs.size(), Type.OUTPUT));
    }

    public void setupExamples () {
        examples = new ArrayList<Example> ();
        verifySet = new ArrayList<Example> ();
        testSet = new ArrayList<Example> ();

        // Test set will be 10% of the total example size
        int testSize = fullSet.size() / 10;
        // Verify set will be 5% of the total example size
        int verifySize = fullSet.size() / 20;
        // setup the test examples
        for (int i = 0; i < testSize; i++) {
            int index = ThreadLocalRandom.current().nextInt(0, fullSet.size() - 1);
            testSet.add(fullSet.get(index));
            fullSet.remove(index);
        }
        // setup the verify examples
        for (int i = 0; i < verifySize; i++) {
            int index = ThreadLocalRandom.current().nextInt(0, fullSet.size() - 1);
            verifySet.add(fullSet.get(index));
            fullSet.remove(index);
        }
        // Once test and verify values are pulled out, set examples to remainder.
        examples = fullSet;
        Node.sigma = calculateSigma();
    }

    @Override
    public void run() {
        try {
            File file = new File(System.currentTimeMillis() + ".csv");

            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter writer = new PrintWriter(file);

            int run_count = 0;
            LinkedList<Double> verifyError = new LinkedList<Double>();
            boolean shouldRun = true;
            while (shouldRun) {
                List<Double> output = new ArrayList<Double>();

                while (Main.shouldPause) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // For each example we set the input layer's node's inputs to the example value,
                // then calculate the output for that example.
                examples.forEach(example -> {
                    Double networkOutput = forwardPropagate(example);
                    output.add(networkOutput);

                    if (Double.isNaN(networkOutput)) {
                        System.err.println("NaN");
                        System.exit(1);
                    }

                    backPropagate(example.outputs);
                });

                layers.parallelStream().forEach(Layer::updateNodeWeights);


                List<Double> outputs = examples
                        .stream()
                        .map(example -> example.outputs.get(0))
                        .collect(Collectors.toList());

                System.out.println("Average error is " + calculateAverageError(output, outputs));

                run_count++;
                // If we have done 5 runs, do a verify check to see how error is coming along
                if (run_count % 100 == 0) {
                    double total = 0;
                    // calculate error for each example in the verifySet
                    for (int i = 0; i < verifySet.size(); i++){
                        Example example = verifySet.get(i);
                        Double networkOutput = forwardPropagate(example);
                        Double exampleError = Math.abs(example.outputs.get(0) - networkOutput);
                        total += exampleError;
                    }
                    // average error across verifySet
                    Double error = total / verifySet.size();

                    writer.print(error + ", ");
                    writer.flush();

                    System.out.println("Verify Error " + error);
                    verifyError.offer(error);

                    // if verifyError is full check slope
                    if (verifyError.size() == 20) {
                        double first = verifyError.getFirst();
                        double last = verifyError.getLast();
                        // if slope is positive stop experiment
                        if (last - first > 0) {
                            shouldRun = false;
                        }
                        // pop off oldest error and add new error
                        verifyError.remove();
                    }
                }
            }

        System.out.println("Run Ended");
        List<Double> errors = new ArrayList<Double>();
        List<Boolean> correctApproximations = new ArrayList<Boolean>();
        for (int i = 0; i < testSet.size (); i++) {
            Example example = testSet.get(i);
            Double networkOutput = forwardPropagate(example);
            Double exampleError = Math.abs(example.outputs.get(0) - networkOutput);
            errors.add(exampleError);
            if (exampleError <= 0.001) {
                correctApproximations.add(true);
            } else {
                correctApproximations.add(false);
            }
        }

        writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO: write a description of forward propagation
     * Used for batch updates, where all examples will have their outputs calculated
     *
     * @return A [List] containing the output for each example in the examples list.
     */
    public Double forwardPropagate(Example example) {
        Layer input = layers.get(0);

        // For each node in the input layer, set the input to the node
        input.nodes.parallelStream().forEach(node -> {
            node.inputs.clear();
            node.inputs.addAll(example.inputs);
        });

        // Calculate the output for each layer and pass it into the next layer
        for (int j = 0; j < layers.size() - 1; j++) {
            Layer currentLayer = layers.get(j);
            List<Double> outputs = currentLayer.calculateNodeOutputs();
            // If we are not at the output layer, we are going to set the
            // Next layers inputs to the current layers outputs.
            Layer nextLayer = layers.get(j + 1);
            // Grab each node in the layer
            nextLayer.nodes.parallelStream().forEach(node -> {
                // Set each node's inputs to the outputs
                node.inputs.clear();
                node.inputs.addAll(outputs);
            });
        }

        // We have hit the output and need to save it - Assume output has only one node.
        return layers.get(layers.size() - 1).calculateNodeOutputs().get(0);
    }

    /**
     * Use forwardProp to get output layer // TODO: ??????
     *
     * @param target
     */
    public void backPropagate(List<Double> target) {
        Layer currentLayer = layers.get(hiddenLayers + 1);
        Layer previousLayer = layers.get(hiddenLayers);
        List<Node> outputNodes = currentLayer.nodes;

        // Updating weights on output layer
        for (int i = 0; i < outputNodes.size(); i++) {
            Node outputNode = outputNodes.get(i);
            outputNode.delta = -1 * (target.get(i) - outputNode.output);

            for (int j = 0; j < outputNode.newWeights.size(); j++) {
                double weightChange = outputNode.delta * previousLayer.nodes.get(j).output;

                if (Double.isNaN(weightChange)){
                    System.err.println("weightChange is not a number");
                }
                if (outputNode.delta == 0) {
                    System.err.println("delta is zero");
                }

                outputNode.newWeights.set(j, outputNode.newWeights.get(j) - learningRate * weightChange);
            }
        }

        // Iterating over all hidden layers to calculate weight change
        for(int x = hiddenLayers; x > 0; x--) {
            outputNodes = currentLayer.nodes;
            currentLayer = layers.get(x);
            previousLayer = layers.get(x - 1);

            // Updates the weights of each node in the layer
            for (int i = 0; i < currentLayer.nodes.size(); i++) {
                final int index = i;
                Node currentNode = currentLayer.nodes.get(i);

                double weightedDeltaSum = outputNodes.parallelStream().mapToDouble(node -> node.delta * node.weights.get(index)).sum();
                currentNode.delta = weightedDeltaSum * currentNode.output * (1 - currentNode.output);

                // Updating each weight in the node
                for (int j = 0; j < currentNode.newWeights.size(); j++) {
                    double weightChange = currentNode.delta * previousLayer.nodes.get(j).output;
                    if (Double.isNaN(weightChange)){
                        System.err.println("weightChange is not a number");
                    }

                    currentNode.newWeights.set(j, currentNode.newWeights.get(j) - learningRate * weightChange);
                }
            }
        }
    }

    public List<Double> calculateError() {return null;}

    private double calculateSigma() {
        double maxDistance = 0;

        for (int i = 0; i < examples.size(); i++) {
            double sum = 0;
            Example current = examples.get(i);
            for (int j = i+1; j < examples.size(); j++) {
                Example otherExample = examples.get(j);
                for (int k = 0; k < current.inputs.size(); k++) {

                    double inputVal = current.inputs.get(k);
                    double exampleVal = otherExample.inputs.get(k);
                    sum += (inputVal - exampleVal) * (inputVal - exampleVal);
                }
            }

            if (sum > maxDistance) {
                maxDistance = sum;
            }
        }

        return Math.sqrt(maxDistance);
    }

    /**
     * Calculates the Rosenbrock function from the given input
     * f(x) = f(x1, x1, ..., xn) = Sum over all elements of [(1-x_i)^2 + 100(x_(i+1) - (x_i)^2)^2]
     *
     * @param values Input values for the function of any dimension
     * @return The result of applying to Rosenbrock function to the given input
     */
    public static double rosenbrock(double... values) {
        return IntStream.range(0, values.length - 1)
                .boxed()
                .parallel()
                .map(i -> new Double[]{values[i], values[i + 1]})
                .mapToDouble(rosenbrock2D)
                .sum();
    }

    /**
     * Calculates the Rosenbrock function from the given 2D input
     * f(x) = f(x, y) = [(1-x)^2 + 100(y - x^2)^2]
     *
     * @param values 2D input values for the function
     * @return The result of applying to Rosenbrock function to the given input
     */
    private static ToDoubleFunction<Double[]> rosenbrock2D = values -> Math.pow(Math.pow(1 - values[0], 2) + 100 * (values[1] - Math.pow(values[0], 2)), 2);

    /**
     * Calculates total error from Rosenbrock inputs and output from nodes
     * f(x) = sum(.5(expected-output)^2)
     * @param outputs from calculated node output
     * @param inputs from rosenBrock
     * @return squared error result
     */
    public double calculateTotalError(List<Double> outputs, List<Double> inputs) {
        return IntStream.range(0, outputs.size())
                .mapToDouble(i -> 0.5d * Math.pow(inputs.get(i) - outputs.get(i), 2))
                .sum();
    }

    public double calculateAverageError(List<Double> outputs, List<Double> inputs) {
        return IntStream.range(0, outputs.size())
                .mapToDouble(i -> Math.abs(inputs.get(i) - outputs.get(i)))
                .sum() / outputs.size();
    }
}
