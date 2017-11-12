import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Example {

    public List<Double> inputs = new ArrayList<>();
    public List<Integer> outputs = new ArrayList<>();

    public String classOutput;

    public Example() {}

    public Example(List<Double> inputs) {
        this.inputs = inputs;
    }

    public Example(List<Double> inputs, List<Integer> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }
}
