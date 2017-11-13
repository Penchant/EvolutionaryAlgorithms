import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class Evolution {

    double minRange = -0.1;
    double maxRange = 0.1;
    int mutationChance = 1000;
    List<Chromosome> population;
    public int numParents = 2;
    static Random random = new Random();

    public enum Algorithm {
        GA, ES, DE
    }

    /**
     * Selects parents for crossover
     * @return The parents to crossover
     */
    private List<Chromosome> selectParents() {
        Collections.sort(population);
        List<Integer> ranges = new ArrayList<>();
        final int size = population.size();
        ranges.add(1);
        IntStream.range(1, population.size()).forEach(index -> ranges.add(ranges.get(index) + index + 1));

        List<Chromosome> parents = new ArrayList<>();

        //Create as many parents as desired
        IntStream.range(0, numParents).parallel().forEach((index) -> parents.add(chooseParent(ranges)));

        return parents;
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
     * Creates a Chromosome from 2 parents created during crossover
     * @param parents parents to create child
     * @return Returns child chromosome
     */
    public Chromosome crossover(List<Chromosome> parents) {

        List<Integer> fromParent = new ArrayList<>();
        IntStream.range(0, parents.get(0).adjacencyMatrix.length).parallel().forEach((index) -> {
            double gene = Math.random();
            if(gene >= .5){
                fromParent.set(index, 0);
            }
            else {
                fromParent.set(index, 1);
            }
                }
        );

        Chromosome chromosome = new Chromosome();
        chromosome.adjacencyMatrix = new double[parents.get(0).adjacencyMatrix.length][parents.get(0).adjacencyMatrix[0].length];

        IntStream.range(0, parents.get(0).adjacencyMatrix.length).parallel().forEach((index) ->{
            IntStream.range(0, parents.get(0).adjacencyMatrix.length).parallel().forEach((i) ->
                    chromosome.adjacencyMatrix[i][index] = parents.get(fromParent.get(index)).adjacencyMatrix[i][index]);
        });

        //Here so it builds
        return chromosome;
    }

    /*
    * Mutation
    * will mutate a chromosome. it goes through each element in the chromosome
    * and when a random number (0, mutationChance] is 0 it will mutate that number.
    *
    * The mutation algorithm depends on whether it is doing creep or evolution strategy
    * based on wether the evoStrat is null or not.
    */
    public Chromosome mutation (Chromosome child, Chromosome evoStrat) {
        for (int i = 0; i < child.adjacencyMatrix.length; i++) {
            for (int j = i + 1; j < child.adjacencyMatrix[i].length; j++) {
                int randomNum = (int)(Math.random() * mutationChance);
                if (randomNum == 0) {
                    if (evoStrat == null) {
                        double creep = randomInRange(minRange, maxRange);
                        child.adjacencyMatrix[i][j] = child.adjacencyMatrix[i][j] + creep;
                    }
                    else {
                        double creep = randomInRange(0, 1);
                        child.adjacencyMatrix[i][j] = child.adjacencyMatrix[i][j] + evoStrat.adjacencyMatrix[i][j] * creep;
                    }
                }
            }
        }
        return child;
    }

    private static double randomInRange(double min, double max) {
        double range = max - min;
        double scaled = random.nextDouble() * range;
        double shifted = scaled + min;
        return shifted; // == (rand.nextDouble() * (max-min)) + min;
    }

}
