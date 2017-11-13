import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Evolution implements Runnable {

    static double mutationChance ;
    static double learningRate;
    static double beta;
    double minRange = -0.1;
    double maxRange = 0.1;

    int annealFactor = 5;

    double epochMultiplier = 0.9;
    double esUpdateParam = 0.5;
    int populationSize;
    int epoch = 0;

    List<Chromosome> population;
    public int numParents = 2;
    public int numOfChildren;
    public Algorithm algorithm;
    static Random random = new Random();

    public enum Algorithm {
        GA, ES, DE, BP
    }

    /**
     * Constructor for Chromosomes
     * @param hiddenLayers List containing number of nodes per layer
     * @param populationSize Number of individuals to have in population
     * @param numOfChildren Number of children
     */
    public Evolution(Algorithm algorithm, final List<Integer> hiddenLayers, List<Example> examples, int populationSize, int numOfChildren) {
        this.populationSize = populationSize;
        this.population = IntStream.range(0, populationSize)
                .mapToObj(i -> new Network(hiddenLayers, examples.get(0).inputs.size(), examples.get(0).outputs.size()))
                .map(network -> network.toChromosome())
                .collect(Collectors.toList());

        this.numOfChildren = numOfChildren;
        this.algorithm = algorithm;
    }

    /**
     * Run the evolutionary algorithm
     */
    public void run(){
        boolean converged = false;
        while (!converged) {
            if (algorithm.equals(Algorithm.GA)) {
                geneticAlgorithm();
            } else if (algorithm.equals(Algorithm.ES)) {
                evolutionStrategies();
            } else if (algorithm.equals(Algorithm.DE)) {
                differentialEvolution();
            } else {
                backpropagation();
            }
        }
    }

    /**
     *
     */
    private void geneticAlgorithm() {
        IntStream.range(0, numOfChildren)
                .mapToObj(i-> crossover(this.selectParents()))
                .peek(child -> this.mutation(child, null, epoch))
                .forEach(child -> this.population.add(child));
        this.population = this.selectNewPopulation(this.population);
    }

    private void evolutionStrategies() {

        //IntStream.range(0, numOfChildren).parallel().mapToObj(i -> )
    }

    private void differentialEvolution() {

    }

    private void backpropagation() {

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
        Logger.log("Selecting Parents", Logger.Level.shout);
        Collections.sort(population);
        List<Integer> ranges = new ArrayList<>();
        final int size = population.size();
        ranges.add(1);
        IntStream.range(1, population.size()).forEach(index -> ranges.add(ranges.get(index - 1) + index + 1));

        List<Chromosome> parents = new ArrayList<>();

        //Create as many parents as desired
        IntStream.range(0, numParents).forEach((index) -> parents.add(chooseParent(ranges)));

        return parents;
    }

    /**
    * selectESParents
    * @return returns a list of parents. One is a weighted random individual based
    * on rank. The other three are random individuals that must be mutually exclusive.
    */
    private List<Chromosome> selectESParents() {
        Collections.sort(population);
        List<Integer> ranges = new ArrayList<>();
        final int size = population.size();
        ranges.add(1);
        IntStream.range(1, population.size()).forEach(index -> ranges.add(ranges.get(index) + index + 1));

        List<Integer> parentIndexes = new ArrayList<>();

        // Create one parent
        IntStream.range(0, 1).parallel().forEach((index) -> parentIndexes.add(chooseParentIndexes(ranges)));
        int x1 = 0, x2 = 0, x3 = 0, x;
        x = parentIndexes.get (0);
        do {
            x1 = (int) (Math.random () * population.size());
        } while (x1 == x);

        do {
            x2 = (int) (Math.random () * population.size());
        } while (x2 == x || x2 == x1);

        do {
            x3 = (int) (Math.random () * population.size());
        } while (x3 == x || x3 == x1 || x3 == x2);

        List<Chromosome> parents = new ArrayList<>();
        parents.add (population.get (x));
        parents.add (population.get (x1));
        parents.add (population.get (x2));
        parents.add (population.get (x3));

        return parents;
    }

    /**
     * Selects new population based on top fitness (percent correct)
     */
    public List<Chromosome> selectNewPopulation(List<Chromosome> population) {
        Logger.log("Selecting New Population", Logger.Level.shout);
        List<Chromosome> sortedPop = new ArrayList<>();

        for (int i = 0; i < population.size(); i++) {
            sortedPop.add(i, population.get(i));
        }

        Collections.sort(sortedPop, Comparator.comparing(s -> s.percentCorrect));
        sortedPop = sortedPop.subList((population.size() - populationSize), sortedPop.size());
        return sortedPop;
    }

     /**
     * Chooses a parent for crossover probabilistically
     * @param ranges Ranges determining probabilities
     * @return Parent chromosome
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

    /**
     * Chooses a parent index for crossover probabilistically
     * @param ranges Ranges determining probabilities
     * @return Parent chromosome index
     */
    private Integer chooseParentIndexes(List<Integer> ranges) {
        double rand1 = Math.random();
        int decideParent1 = (int) (rand1 * population.size());
        int indexParent1 = Collections.binarySearch(ranges, decideParent1);

        if (indexParent1 < 0) {
            indexParent1 = indexParent1 * -1 - 1;
        }

        return new Integer(indexParent1);
    }

    /**
     * Creates a Chromosome from 2 parents created during crossover
     * @param parents parents to create child
     * @return Returns child chromosome
     */
    public Chromosome crossover(List<Chromosome> parents) {
        List<Integer> fromParent = new ArrayList();
        IntStream.range(0, parents.get(0).adjacencyMatrix.length).forEach((index) -> {
                double gene = Math.random();
                if(gene >= .5) {
                    fromParent.add(0);
                } else {
                    fromParent.add(1);
                }
            }
        );

        Chromosome chromosome = new Chromosome();
        chromosome.adjacencyMatrix = new double[parents.get(0).adjacencyMatrix.length][parents.get(0).adjacencyMatrix[0].length];

        IntStream.range(0, parents.get(0).adjacencyMatrix.length).parallel().forEach((index) ->
            IntStream.range(0, parents.get(0).adjacencyMatrix.length).parallel().forEach((i) ->
                    chromosome.adjacencyMatrix[i][index] = parents.get(fromParent.get(index)).adjacencyMatrix[i][index])
        );

        return chromosome;
    }

    /**
    * crossoverES
    * @params parents - parents that are used to create an offspring
    * @return an offspring;
    * Creates a trial vector from three random parents and then multiplies the trial by the
    * original parent.
    */
    public Chromosome crossoverES (List<Chromosome> parents) {
        Chromosome trial = new Chromosome ();
        Chromosome offspring = new Chromosome ();
        for (int i = 0; i < trial.adjacencyMatrix.length; i++) {
            for (int j = i + 1; j < trial.adjacencyMatrix[i].length; j++) {
                trial.adjacencyMatrix[i][j] = parents.get (1).adjacencyMatrix [i][j] +
                    beta * (parents.get (2).adjacencyMatrix[i][j] - parents.get (3).adjacencyMatrix[i][j]);
                offspring.adjacencyMatrix[i][j] = parents.get (0).adjacencyMatrix[i][j] * trial.adjacencyMatrix[i][j];
            }
        }
        return offspring;
    }

    /**
    * Mutation
    * @param child, evoStrat - Chromosomes. child will be mutated. evoStrat can
    * be null. If not it is used in the mutation.
    * @param epoch - this is the number of generations the child is since the first.
    * @return returns the mutated child Chromosome
    * will mutate a chromosome. it goes through each element in the chromosome
    * and when a random number (0, mutationChance] is 0 it will mutate that number.
    *
    * The mutation algorithm depends on whether it is doing creep or evolution strategy
    * based on wether the evoStrat is null or not.
    *
    * epoch is used to anneal the non evoStrat algorithms
    */
    public Chromosome mutation(Chromosome child, Chromosome evoStrategy, int epoch) {
        Logger.log("Mutating", Logger.Level.shout);
        for (int i = 0; i < child.adjacencyMatrix.length; i++) {
            for (int j = i + 1; j < child.adjacencyMatrix[i].length; j++) {
                if (Math.random() < mutationChance) {
                    if (evoStrategy == null) {
                        double creep = randomInRange(minRange, maxRange);
                        if (epoch != 0) {
                            double anneal = (annealFactor * 1 / (epochMultiplier * epoch) + 1);
                            creep = anneal * creep;
                        }
                        child.adjacencyMatrix[i][j] = child.adjacencyMatrix[i][j] + creep;
                    }
                    else {
                        double creep = normalDeviation();
                        child.adjacencyMatrix[i][j] = child.adjacencyMatrix[i][j] + evoStrategy.adjacencyMatrix[i][j] * creep;
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
