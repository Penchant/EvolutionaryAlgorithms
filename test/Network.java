import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.*;

public class Network {

    private List<List<Double>> examples;
    public List<Layer> layers;

    private int hiddenLayers;
    private int dimension;
    private int nodesPerHiddenLayer;
    private boolean isRadialBasis;

    private double learningRate;

    public Network(int hiddenLayers, int nodesPerHiddenLayer, int dimension, boolean isRadialBasis, List<List<Double>> examples) {
        this.hiddenLayers = hiddenLayers;
        this.dimension = dimension;
        this.nodesPerHiddenLayer = nodesPerHiddenLayer;
        this.isRadialBasis = isRadialBasis;
    }

    /**
     * TODO: write a description of forward propogation
     * Used for batch updates, where all examples will have their outputs calculated
     * @return A [List] containing the output for each example in the examples list.
     */
    public List<Double> forwardPropogate() {
        List<Double> output = new ArrayList<Double> ();
        
        // For each example we set the input layer's node's inputs to the example value,
        // then calculate the output for that example.
        for (int i = 0; i < examples.size (); ++i) {
            List<Double> example = examples.get (i);
            Layer input = layers.get (0);
            // for each node in the input layer
            for (int j = 0; j < input.nodes.size(); ++j) {
                Node currentNode = input.nodes.get(j);
                // for each dimension in the example we will have one input
                for (int k = 0; k < example.size (); ++k) {
                    // if the node doesn't have enough inputs, add one.
                    if (currentNode.inputs.size () < k) {
                        currentNode.inputs.add(example.get (k));
                    } else {
                        currentNode.inputs.set (k, example.get (k));
                    }
                }
            }

            // Calculate the output for each layer and pass it into the next layer
            for (int j = 0; j < layers.size (); ++j) {
                Layer currentLayer = layers.get (j);
                List<Double> outputs = currentLayer.calculateNodeOutputs ();
                // If we are not at the output layer, we are going to set the 
                // next layers inputs to the current layers outputs.
                if (j != layers.size () - 1) {
                    Layer nextLayer = layers.get (j + 1);
                    // Grab each node in the layer
                    for (int k = 0; k < nextLayer.nodes.size(); ++k) {
                        Node currentNode = nextLayer.nodes.get(k);
                        // set each node's inputs to the outputs
                        for (int l = 0; l < outputs.size (); ++l) {
                            if (currentNode.inputs.size () < l) {
                                currentNode.inputs.add (outputs.get (l));
                            } else {
                                currentNode.inputs.set(l, outputs.get (l));
                            }
                        }
                    }
                } else { // Else we have hit the output and need to save it
                    // Assume output has only one node. 
                    output.add (outputs.get (0));
                }
            }
        }
        return output;
    }

    /**
     * Use forwardProp to get output layer
     * @param target
     */
    public void backPropogate(List<Double> target) {
        List<Double> delta = new ArrayList<Double>();
        double newWeight = 0;
        Layer currentLayer = layers.get(hiddenLayers+1);
        Layer previousLayer = layers.get(hiddenLayers);
        List<Node> outputs = currentLayer.nodes;

        for(Node outputNode : outputs) {
            int index = outputs.indexOf(outputNode);
            delta.add((outputNode.output - target.get(index)) * outputNode.output * (1 - outputNode.output));

            /**
             * Loops through all Weights attached
             */
            for (Node currentNode : previousLayer.nodes) {
                int i = previousLayer.nodes.indexOf(currentNode);
                Double currentWeight = outputNode.weights.get(i);
                Double weightChange = (delta.get(0)) * currentNode.output;
                outputNode.weights.set(i, currentWeight - learningRate * weightChange);
            }
        }
    }

    public List<Double> calculateError(){return null;}
    private void kMeansCluster(int k){}
    private double calculateSigma(){return 0d;}

    /**
     * Calculates the Rosenbrock function from the given input
     * f(x) = f(x1, x1, ..., xn) = Sum over all elements of [(1-x_i)^2 + 100(x_(i+1) - (x_i)^2)^2]
     * @param values Input values for the function of any dimension
     * @return The result of applying to Rosenbrock function to the given input
     */
    public static double rosenbrock(double ... values) {
        return IntStream.range(0, values.length - 2)
                .boxed()
                .parallel()
                .map(i -> new Double[] {values[i], values[i + 1]})
                .mapToDouble(rosenbrock2D)
                .sum();
    }

    /**
     * Calculates the Rosenbrock function from the given 2D input
     * f(x) = f(x, y) = [(1-x)^2 + 100(y - x^2)^2]
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
                .mapToDouble(i -> 0.5d*(Math.pow((inputs.get(i)-outputs.get(i)), 2)))
                .sum();
    }
}
