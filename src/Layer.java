import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Layer {

    public static int count = 0;
    public static Network network;

    public List<Node> nodes;
    public Type layerType;
    public int id;

    /**
     * Creates a layer of a given type with a specified number of nodes
     * @param nodeCount number of nodes initially in layer
     * @param layerType type of layer
     */
    public Layer(int nodeCount, Type layerType) {
        this.layerType = layerType;
        this.id = count++;

        nodes = IntStream.range(0, nodeCount)
                .boxed()
                .parallel()
                .map(i -> new Node(layerType, nodeCount))
                .collect(Collectors.toList());
    }

    /**
     * Create layer with layerType and initially no nodes
     * @param layerType The type of layer to be made
     */
    public Layer(Type layerType) {
        this(0, layerType);
        nodes = new ArrayList<>();
    }

    public boolean add(Node newNode) {
        return nodes.add(newNode);
    }

    public void updateNodeWeights() {
        nodes.parallelStream().forEach(Node::updateWeights);
    }

    public List<Double> calculateNodeOutputs() {
        return nodes.stream()
                .parallel()
                .map(Node::calculateOutput)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "layer ID: " + id;
    }

}
