import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class ReadData {
    public static Example data = new Example();
    public static List<Example> dataIn = new ArrayList<>();
    public static List<String> classList = new ArrayList<>();

    public List<Example> getExamples(){
        return dataIn;
    }
    public static void main(String[] args) throws IOException {
        // open file input stream
        BufferedReader reader = new BufferedReader(new FileReader("res/abalone.data"));

        // read file line by line
        String line = null;
        Scanner scanner = null;
        int index = 0;

        while ((line = reader.readLine()) != null) {
            scanner = new Scanner(line);
            scanner.useDelimiter(",");
            while (scanner.hasNextLine()) {
                while (scanner.hasNext()) {
                    String d = scanner.next();
                    if (!scanner.hasNext()) {
                        if(!classList.contains(d)){
                            classList.add(d);
                        }
                        data.classOutput = d;
                        break;
                    }
                    data.inputs.add(Double.parseDouble(d));
                    index++;
                }
                dataIn.add(data);
                data = new Example();
            }
        }

        dataIn.stream().parallel().forEach((node)-> {
            int pos = classList.indexOf(node.classOutput);
            IntStream.range(0, classList.size()).parallel().forEach((count) -> node.outputs.add(0d));
            data.outputs.set(pos, 1d);
        });

        //close reader
        reader.close();
        scanner.close();
    }
}
