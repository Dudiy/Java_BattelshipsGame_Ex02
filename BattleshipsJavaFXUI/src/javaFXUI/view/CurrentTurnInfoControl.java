package javaFXUI.view;

import gameLogic.users.Player;
import javaFXUI.JavaFXManager;
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

    private JavaFXManager program;

    public void setProgram(JavaFXManager program) {
        this.program = program;
        program.getActivePlayerProperty().addListener((observable, oldValue, newValue) -> playerChanged(newValue));
    }

    private void playerChanged(Player newPlayer){
        labelCurrentPlayer.setText(newPlayer.getName());
    }

    @FXML
    private void initialize() {
    }
}
