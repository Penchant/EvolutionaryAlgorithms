import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadData {
    public static Example data = new Example();
    public static List<Example> dataIn = new ArrayList<>();
    public static List<String> classList = new ArrayList<>();

    public static List<Example> getExamples() {
        return dataIn;
    }

    public static boolean load(String path) {
        try {
            // Open file input stream
            BufferedReader reader = new BufferedReader(new FileReader(path));

            // Read file line by line
            String line;
            Scanner scanner = null;

            while ((line = reader.readLine()) != null) {
                scanner = new Scanner(line);
                scanner.useDelimiter(",");
                while (scanner.hasNextLine()) {
                    for (int i = 0; i < 8; i++) {
                        data.outputs.add(0d);
                    }
                    while (scanner.hasNext()) {
                        String d = scanner.next();
                        if (!scanner.hasNext()) {
                            if (!classList.contains(d)) {
                                classList.add(d);
                                break;
                            }
                            // Comparing class value to index of classIndex, set to 1
                            int pos = classList.indexOf(d);
                            data.outputs.set(pos, 1d);
                            break;
                        }
                        data.inputs.add(Double.parseDouble(d));
                    }
                    dataIn.add(data);
                    data = new Example();
                }
            }
            // Close reader
            reader.close();
            scanner.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
