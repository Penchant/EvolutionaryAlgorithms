import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class GUIController implements Initializable {

    @FXML public ProgressBar progressBar;
    @FXML private Button selectFileButton;
    @FXML private Button startButton;
    @FXML private TextField dataGenStartTextField;
    @FXML private TextField dataGenEndTextField;
    @FXML private TextField dataGenIncrementTextField;
    @FXML private TextField hiddenLayersTextField;
    @FXML private TextField inputCountTextField;
    @FXML private TextField nodesPerHiddenLayerTextField;
    @FXML private CheckBox isRadialBasisCheckbox;
    @FXML private Label isPausedField;
    @FXML private Button saveWeightsButton;
    @FXML private Button loadWeightsButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setDataGenStartTextField(Main.dataGenStart);
        setDataGenEndTextField(Main.dataGenEnd);
        setDataGenIncrementTextField(Main.dataGenIncrement);
        setHiddenLayersTextField(Main.hiddenLayers);
        setInputCountTextField(Main.dimension);
        setNodesPerHiddenLayerTextField(Network.learningRate);
    }

    public TextField setDataGenStartTextField(double value) {
        dataGenStartTextField.setText("" + value);
        return dataGenStartTextField;
    }

    public TextField setDataGenEndTextField(double value) {
        dataGenEndTextField.setText("" + value);
        return dataGenEndTextField;
    }

    public TextField setDataGenIncrementTextField(double value) {
        dataGenIncrementTextField.setText("" + value);
        return dataGenIncrementTextField;
    }

    public TextField setHiddenLayersTextField(List<Integer> values) {
        String out = "";
        for (int i : values) {
            out += i + ", ";
        }
        out = out.substring(0, out.length() - 2);
        hiddenLayersTextField.setText("" + out);
        return hiddenLayersTextField;
    }

    public TextField setInputCountTextField(int value) {
        inputCountTextField.setText("" + value);
        return inputCountTextField;
    }

    public TextField setNodesPerHiddenLayerTextField(double value) {
        nodesPerHiddenLayerTextField.setText("" + value);
        return nodesPerHiddenLayerTextField;
    }

    @FXML
    private void saveWeights(MouseEvent me) {
        Main.save("");
    }

    @FXML
    private void loadWeights(MouseEvent me) {
        Main.load();
    }

    @FXML
    private void selectFile(MouseEvent event) {
        isPausedField.setText((Main.shouldPause = !Main.shouldPause) ? "Paused" : "Unpaused");
    }

    @FXML
    private void start(MouseEvent event) {
        try {
            double dataGenStart = Double.parseDouble(dataGenStartTextField.getText());
            double dataGenEnd = Double.parseDouble(dataGenEndTextField.getText());
            double dataGenIncrement = Double.parseDouble(dataGenIncrementTextField.getText());
            List<Integer> hiddenLayers = new ArrayList<Integer>();
            String[] args = hiddenLayersTextField.getText().split(",");
            Stream.of(args).map(s -> s.trim()).forEach(s -> hiddenLayers.add(Integer.parseInt(s)));
            int dimension = Integer.parseInt(inputCountTextField.getText());
            boolean isRadialBasis = isRadialBasisCheckbox.isSelected();

            Network.learningRate = Double.parseDouble(nodesPerHiddenLayerTextField.getText());

            Main.start(dataGenStart, dataGenEnd, dataGenIncrement, hiddenLayers, dimension, isRadialBasis);
        } catch (NumberFormatException nfe) {
            System.err.println("Invalid arguments");
        }
    }

}
