import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ReadData {
    public static List<Example> dataIn = new ArrayList<>();
    public static List<String> classList = new ArrayList<>();

    public static List<Example> getExamples() {
        return dataIn;
    }

    public static boolean load(String path) {
        try {
            Scanner scanner = new Scanner(path);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                String[] data = line.split(",");

                List<Double> inputs = new ArrayList<Double>();

                List<String> classes = new ArrayList();

                if (!classes.contains(data[data.length - 1])) {
                    classes.add(data[data.length - 1]);
                }

                Stream.of(data).limit(data.length - 1).forEach((element) -> {
                    inputs.add(Double.parseDouble(element));
                });

                List<Double> output = new ArrayList<>();

                output.add((double) classes.indexOf(data[data.length - 1]));

                dataIn.add(new Example(inputs, output));
            }

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
