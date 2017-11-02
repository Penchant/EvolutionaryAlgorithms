import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class GUIController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setDataGenStartTextField(Main.dataGenStart);
        setDataGenEndTextField(Main.dataGenEnd);
        setDataGenIncrementTextField(Main.dataGenIncrement);
        setHiddenLayersTextField(Main.hiddenLayers);
        setNodesPerHiddenLayerTextField(Main.nodesPerHiddenLayer);
        setInputCountTextField(Main.dimension);
    }

    @FXML private Button selectFileButton;
    @FXML private Button startButton;
    @FXML private TextField dataGenStartTextField;
    @FXML private TextField dataGenEndTextField;
    @FXML private TextField dataGenIncrementTextField;
    @FXML private TextField hiddenLayersTextField;
    @FXML private TextField inputCountTextField;
    @FXML private TextField nodesPerHiddenLayerTextField;
    @FXML private CheckBox isRadialBasisCheckbox;

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

    public TextField setHiddenLayersTextField(int value) {
        hiddenLayersTextField.setText("" + value);
        return hiddenLayersTextField;
    }

    public TextField setInputCountTextField(int value) {
        inputCountTextField.setText("" + value);
        return inputCountTextField;
    }

    public TextField setNodesPerHiddenLayerTextField(int value) {
        nodesPerHiddenLayerTextField.setText("" + value);
        return nodesPerHiddenLayerTextField;
    }

    @FXML public ProgressBar progressBar;

    @FXML
    private void selectFile(MouseEvent event) {
        Main.save("");
    }

    @FXML
    private void start(MouseEvent event) {
        try {
            double dataGenStart = Double.parseDouble(dataGenStartTextField.getText());
            double dataGenEnd = Double.parseDouble(dataGenEndTextField.getText());
            double dataGenIncrement = Double.parseDouble(dataGenIncrementTextField.getText());
            int hiddenLayers = Integer.parseInt(hiddenLayersTextField.getText());
            int dimension = Integer.parseInt(inputCountTextField.getText());
            int nodesPerHiddenLater = Integer.parseInt(nodesPerHiddenLayerTextField.getText());
            boolean isRadialBasis = isRadialBasisCheckbox.isSelected();

            Main.start(dataGenStart, dataGenEnd, dataGenIncrement, hiddenLayers, dimension, nodesPerHiddenLater, isRadialBasis);
        } catch (NumberFormatException nfe) {
            System.err.println("Invalid arguments");
        }
    }

}
