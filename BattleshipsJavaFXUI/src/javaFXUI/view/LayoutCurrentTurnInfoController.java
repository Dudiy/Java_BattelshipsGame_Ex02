package javaFXUI.view;

import gameLogic.users.Player;
import javaFXUI.JavaFXManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.Duration;

public class LayoutCurrentTurnInfoController {
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
        addListeners();
    }

    private void addListeners() {
        javaFXManager.getActivePlayerProperty().addListener((observable, oldValue, newValue) -> playerChanged(newValue));
        javaFXManager.getTotalMovesCounterProperty().addListener((observable, oldValue, newValue) -> updateStatistics());
    }

    private void updateStatistics() {
        Player activePlayer = javaFXManager.getActivePlayerProperty().getValue();


        labelCurrentScore.setText(((Integer) activePlayer.getScore()).toString());
        Duration avgDuration = activePlayer.getAvgTurnDuration();
        labelAvgTurnDuration.setText(String.format("%d:%02d", avgDuration.toMinutes(), avgDuration.getSeconds() % 60));
        labelHitsCounter.setText(((Integer) activePlayer.getTimesHit()).toString());
        labelMissCounter.setText(((Integer) activePlayer.getTimesMissed()).toString());
    }


    private void playerChanged(Player currentPlayer) {
        labelCurrentPlayer.setText(currentPlayer.getName());
        updateStatistics();
    }

    @FXML
    private void initialize() {
    }
}
