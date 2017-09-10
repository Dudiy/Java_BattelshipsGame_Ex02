package javaFXUI.view;

import gameLogic.game.Game;
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

    private JavaFXManager javaFXManager;

    public void setJavaFXManager(JavaFXManager javaFXManager) {
        this.javaFXManager = javaFXManager;
        javaFXManager.getActivePlayerProperty().addListener((observable, oldValue, newValue) -> playerChanged(newValue));
    }


    private void playerChanged(Player currentPlayer) {
        labelCurrentPlayer.setText(currentPlayer.getName());
        labelCurrentScore.setText(((Integer) currentPlayer.getScore()).toString());
        labelAvgTurnDuration.setText(currentPlayer.getAvgTurnDuration().toString());
        labelHitsCounter.setText(((Integer) currentPlayer.getTimesHit()).toString());
        labelMissCounter.setText(((Integer) currentPlayer.getTimesMissed()).toString());
    }

    @FXML
    private void initialize() {
    }
}
