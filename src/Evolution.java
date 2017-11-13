import java.util.List;
import java.util.Random;

public class Evolution {

	double minRange = -0.1;
	double maxRange = 0.1;
	int mutationChance = 1000;
    Chromosome chromosome;
	static Random random = new Random();

    public enum Algorithm {
        GA, ES, DE
    }

    public List<Chromosome> selectParents() {
        return null;
    }

    public Chromosome crossover(List<Network> neuralNetwork){
        return chromosome;
    }

	/*
	* Mutation
	* will mutate a chromosome. it goes through each element in the chromosome 
	* and when a random numer (0, mutationChance] is 0 it will mutate that number.
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
