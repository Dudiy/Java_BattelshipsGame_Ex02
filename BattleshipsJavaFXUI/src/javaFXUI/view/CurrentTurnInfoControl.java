package javaFXUI.view;

import javaFXUI.Program;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.naming.Binding;

public class CurrentTurnInfoControl {
    @FXML
    private Label labelCurrentPlayer;

    @FXML
    private TableView<?> TableShipsState;

    @FXML
    private TableColumn<?, ?> columnShipType;

    @FXML
    private TableColumn<?, ?> columnInitialShips;

    @FXML
    private TableColumn<?, ?> columnRemainingShips;

    @FXML
    private Label labelCurrentScore;

    @FXML
    private Label labelAvgTurnDuration;

    @FXML
    private Label labelHitsCounter;

    @FXML
    private Label labelMissCounter;

    private Program program;

    public void setProgram(Program program) {
        this.program = program;
    }

    @FXML
    private void initialize() {
    }
}
