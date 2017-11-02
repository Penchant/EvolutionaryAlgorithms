import java.util.ArrayList;
import java.util.List;

public class Example {

    public List<Double> inputs = new ArrayList<>();
    public List<Double> outputs = new ArrayList<>();

    public Example() {}

    public Example(List<Double> inputs) {
        this.inputs = inputs;
    }

    public Example(List<Double> inputs, List<Double> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }
}
