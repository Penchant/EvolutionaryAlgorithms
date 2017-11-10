import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;
import java.util.Optional;

public class Evolution {

	double minRange = -0.1;
	double maxRange = 0.1;
	int mutationChance = 1000;
    Chromosome chromosome;
	static Random random = new Random();

    public enum Algorithm {
        GA, ES, DE
    }

    public List selectParents() {
        return selectParents();
    }

    public Chromosome crossover(List<Network> neuralNetwork){
        return chromosome;
    }
	
	public Chromosome mutation (Chromosome child, Chromosome evoStrat) {
		for (int i = 0; i < child.adjacencyMatrix.length; ++i) {
			for (int j = i + 1; j < child.adjacencyMatrix [i].length; ++j) {
				int randomNum = ThreadLocalRandom.current().nextInt(0, mutationChance);
				if (randomNum == 0) {
					if (evoStrat == null) {
						double creep = randomInRange (minRange, maxRange);
						child.adjacencyMatrix [i][j] = child.adjacencyMatrix [i][j] + creep;
					}
					else {
						double creep = randomInRange (0, 1);
						child.adjacencyMatrix [i][j] = child.adjacencyMatrix [i][j] + evoStrat.adjacencyMatrix [i][j] * creep;
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
