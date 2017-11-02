import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Layer {

    public List<Node> nodes;
    public int nodeCount;
    private Type layerType;

    public Layer(int nodeCount, Type layerType) {
        this.nodeCount = nodeCount;
        this.layerType = layerType;

        nodes = IntStream.range(0, nodeCount)
                .boxed()
                .parallel()
                .map(i -> new Node(layerType))
                .collect(Collectors.toList());
    }

    public void updateNodeWeights(List<List<Double>> weights) {
        IntStream.range(0, weights.size())
                .boxed()
                .parallel()
                .forEach(i -> nodes.get(i).updateWeights(weights.get(i)));
    }

    public List<Double> calculateNodeOutputs() {
        return nodes.stream()
                .parallel()
                .map(Node::calculateOutput)
                .collect(Collectors.toList());
    }

}
