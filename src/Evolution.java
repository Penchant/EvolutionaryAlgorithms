import java.util.*;
import java.util.stream.IntStream;

public class Evolution {

    double minRange = -0.1;
    double maxRange = 0.1;
    int mutationChance = 1000;

    int anealFactor = 5;
    double epochMultiplier = 0.9;
    double esUpdateParam = 0.5;
    int populationSize;

    List<Chromosome> population;
    public int numParents = 2;
    static Random random = new Random();

    public enum Algorithm {
        GA, ES, DE
    }

    /**
     * Constructor for Chromosomes
     * @param hiddenLayers List containing number of nodes per layer
     * @param dimension Number of input nodes
     * @param outputDimension Number of output nodes
     * @param populationSize Number of individuals to have in population
     * @param numOfChildren Number of children
     */
    public Evolution(final List<Integer> hiddenLayers, int dimension, int outputDimension, int populationSize, int numOfChildren) {

    }

    /**
     * Selects parents for crossover
     * @return The parents to crossover
     */
    private List<Chromosome> selectParents() {
        Collections.sort(population);
        List<Integer> ranges = new ArrayList<>();
        final int size = population.size();
        ranges.add(size);
        IntStream.range(1, population.size()).forEach(index -> ranges.add(ranges.get(index) + size - index));

        List<Chromosome> parents = new ArrayList<>();

        //Create as many parents as desired
        IntStream.range(0, numParents).parallel().forEach((index) -> parents.add(chooseParent(ranges)));

        return parents;
    }

    /**
     * Selects new population based on top fitness (percent correct)
     */
    public void selectNewPopulation() {
        List<Chromosome> sortedPop = new ArrayList<>();

    	for (int i = 0; i < population.size(); i++) {
            sortedPop.add(i, population.get(i));
        }

    	Collections.sort(sortedPop, Comparator.comparing(s -> s.percentCorrect));
    	sortedPop = sortedPop.subList((population.size() - populationSize), sortedPop.size());
    	population = sortedPop;
    }

    /**
     * 
     * @param ranges
     * @return
     */
    private Chromosome chooseParent(List<Integer> ranges) {
        double rand1 = Math.random();
        int decideParent1 = (int)(rand1 * population.size());
        int indexParent1 = Collections.binarySearch(ranges, decideParent1);

        if(indexParent1 < 0) {
            indexParent1 = indexParent1 * -1 - 1;
        }

        return population.get(indexParent1);

    }

    public Chromosome crossover() {
        //Here so it builds
        return population.get(0);
    }

    /**
    * Mutation
    * @param child, evoStrat - Chromosomes. child will be mutated. evoStrat can
    * be null. If not it is used in the mutation.
    * @param epoch - this is the number of generations the child is since the first.
    * @return returns the mutated child Chromosome
    * will mutate a chromosome. it goes through each element in the chromosome
    * and when a random numer pull (0, mutationChance] is equal 0 it will mutate that element.
    *
    * The mutation algorithm depends on whether it is doing creep or evolution strategy
    * based on wether the evoStrat is null or not.
    *
    * epoch is used to aneal the non evoStrat algorithms
    */
    public Chromosome mutation(Chromosome child, Chromosome evoStrat, int epoch) {
        for (int i = 0; i < child.adjacencyMatrix.length; i++) {
            for (int j = i + 1; j < child.adjacencyMatrix[i].length; j++) {
                int randomNum = (int)(Math.random() * mutationChance);
                if (randomNum == 0) {
                    if (evoStrat == null) {
                        double creep = randomInRange(minRange, maxRange);
                        if (epoch != 0) {
                            double aneal = (anealFactor * 1 / (epochMultiplier * epoch) + 1);
                            creep = aneal * creep;
                        }
                        child.adjacencyMatrix[i][j] = child.adjacencyMatrix[i][j] + creep;
                    }
                    else {
                        double creep = normalDeviation();
                        child.adjacencyMatrix[i][j] = child.adjacencyMatrix[i][j] + evoStrat.adjacencyMatrix[i][j] * creep;
                    }
                }
            }
        }
        return child;
    }

    /**
    * randomInRange
    * @param min and max - doubles that define the range of the desired
    * random number
    * @return random number in the given range
    */
    private static double randomInRange(double min, double max) {
        double range = max - min;
        double scaled = random.nextDouble() * range;
        double shifted = scaled + min;
        return shifted; // == (rand.nextDouble() * (max-min)) + min;
    }

    /**
    * updateEvoStrat
    * @param evoStrat - Chromosome defining the evolution strategy parameters
    * used for mutation in the Evolution Strategy algorithm.
    * @return returns the updated evoStrat Chromosome
    */
    public Chromosome updateEvoStrategy(Chromosome evoStrat) {
        for (int i = 0; i < evoStrat.adjacencyMatrix.length; i++) {
            for (int j = i + 1; j < evoStrat.adjacencyMatrix[i].length; j++) {
                evoStrat.adjacencyMatrix [i][j] = evoStrat.adjacencyMatrix[i][j] +
                    esUpdateParam * evoStrat.adjacencyMatrix[i][j] * normalDeviation ();
            }
        }
        return evoStrat;
    }

    /**
    * normalDeviation
    * @return returns a random double that fits in a normal distribution
    * around 0.5 between 0 and 1.
    */
    private double normalDeviation() {
        double x = (random.nextGaussian () + 4) / 8;
        if (x > 1) {
            x = 1;
        }
        if (x < 0) {
            x = 0;
        }
        return x;
    }
}
