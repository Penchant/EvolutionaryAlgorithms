import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
     * Converts a Chromosome to a network
     * @return Network made based off the chromosome
     */
    public Network ToNetwork(){
        int numCols = adjacencyMatrix[0].length;
        int numRows = adjacencyMatrix.length;
        int priorLayerNode = -1;
        Network net = new Network();
        Layer inputLayer = new Layer(Type.INPUT);

        List inputLayerIndices = IntStream.
                range(0, numCols).
                parallel().
                filter((j) ->IntStream.range(0, numRows).parallel().allMatch(i -> adjacencyMatrix[i][j] != 0)).
                mapToObj(obj -> obj).
                collect(Collectors.toList());

        // Adding nodes to layers must be done sequentially because the order is assumed to be maintained
        // so to allow getting the indices to be done in parallel, list is collected and then nodes are added
        inputLayerIndices.stream().
                forEach(index -> addNodeToLayer((Integer) index, inputLayer));

        net.layers.add(inputLayer);
        boolean isOutputLayer = true;

        while(!isOutputLayer){
            List<Integer> nextLayerIndices = getNextLayerIndices(priorLayerNode);

            //Update priorLayerNode
            priorLayerNode = nextLayerIndices.get(0);
            isOutputLayer = getNextLayerIndices(priorLayerNode).isEmpty();

            Layer newLayer = new Layer(0, isOutputLayer ? Type.OUTPUT : Type.HIDDEN);
            nextLayerIndices.stream().parallel().forEach((index) -> addNodeToLayer(index, newLayer));
        }

        net.setNodeConnections();

        return net;
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

