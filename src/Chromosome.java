import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Chromosome {

    public double[][] adjacencyMatrix;

    public Chromosome(){}

    /**
     * Create layer from adjacency matrix
     * @param adjacencyMatrix An adjacency matrix representing the network
     */
    public Chromosome(double[][] adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
    }

    /**
     * Creates a chromosome from a network
     * @param network network to make chromosome from
     */
    public Chromosome(Network network){
        List<List<Node>> multiNodes = new ArrayList<>();
        for(Layer layer : network.layers){
            multiNodes.add(layer.nodes);
        }
        List<Node> nodes = multiNodes.
                parallelStream().
                flatMap(Collection::stream).
                collect(Collectors.toList());
        IntStream.
                range(0,nodes.size()).
                parallel().
                forEach(index -> nodes.get(index).id = index);
        double[][] adjacencyMatrix = new double[nodes.size()][nodes.size()];
        nodes.
                stream().
                parallel().
                forEach(
                        (j) ->
                                IntStream.
                                        range(0, j.inputNodes.size()).
                                        parallel().
                                        forEach( index -> adjacencyMatrix[j.inputNodes.get(index).id][j.id] = j.inputs.get(index))
                );

        this.adjacencyMatrix = adjacencyMatrix;
    }

    /**
     * Converts a Chromosome to a network
     * @return Network made based off the chromosome
     */
    public Network ToNetwork(){
        return new Network(this);
    }

    /**
     * Gets the indices of the next layer
     * @param node Index of the column in the adjacency matrix of a node in the current layer
     * @return the indices of the next layer
     */
    public List<Integer> getNextLayerIndices(int node) {
        double[] nextLayer = adjacencyMatrix[node];

        return IntStream.range(0, nextLayer.length).
                parallel().
                filter(index -> nextLayer[index] != 0).
                mapToObj(index -> index).
                collect(Collectors.toList());
    }

    /**
     * Gets the weights of the node
     * @param index of the node in terms of its column
     * @return the weights of the node
     */
    public List<Double> getWeights(final int index){
        return Arrays.
                stream(adjacencyMatrix).
                parallel().
                map(array -> array[index]).
                filter(weight -> weight != 0).
                collect(Collectors.toList());
    }

    /**
     * Adds a node to the layer
     * @param index of the
     * @param layer
     */
    public void addNodeToLayer(int index, Layer layer){
        List<Double> weights = getWeights(index);
        layer.add(new Node(layer.layerType, weights));
    }
}

