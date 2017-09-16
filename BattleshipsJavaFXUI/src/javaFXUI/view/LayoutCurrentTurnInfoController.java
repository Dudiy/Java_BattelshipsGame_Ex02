package javaFXUI.view;

import gameLogic.game.GameSettings;
import gameLogic.game.eGameState;
import gameLogic.users.Player;
import javaFXUI.Constants;
import javaFXUI.JavaFXManager;
import javaFXUI.model.ShipsStateDataModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.time.Duration;
import java.util.Map;

public class LayoutCurrentTurnInfoController {
    @FXML
    private Label labelCurrentPlayer;

    @FXML
    private ImageView imageViewMinesAvailable;

    @FXML
    private Label labelMinesAvailable;

    @FXML
    private TableView TableShipsState;

    @FXML
    private TableColumn columnShipType;

    @FXML
    private TableColumn columnInitialShips;

    @FXML
    private TableColumn columnRemainingShips;

    @FXML
    private Label labelCurrentScore;

    @FXML
    private Label labelAvgTurnDuration;

    @FXML
    private Label labelHitsCounter;

    @FXML
    private Label labelMissCounter;

    private JavaFXManager javaFXManager;
    private final ObservableList<ShipsStateDataModel> shipsState = FXCollections.observableArrayList();
    private final Image noMinesAvailableImage = new Image(Constants.NO_MINES_AVAILABLE_IMAGE_URL);
    private final Image oneMineAvailableImage = new Image(Constants.MINE_IMAGE_URL);
    private final Image multipleMinesAvailable = new Image(Constants.MULTIPLE_MINES_IMAGE_URL);

    @FXML
    private void initialize(){
        columnShipType.setCellValueFactory(
                new PropertyValueFactory<ShipsStateDataModel,String>("shipType")
        );
        columnInitialShips.setCellValueFactory(
                new PropertyValueFactory<ShipsStateDataModel, Integer>("initialAmount")
        );
        columnRemainingShips.setCellValueFactory(
                new PropertyValueFactory<ShipsStateDataModel, Integer>("initialAmount")
        );
    }

    public void setJavaFXManager(JavaFXManager javaFXManager) {
        this.javaFXManager = javaFXManager;
        addListeners();

    }

    private void setShipTypeValues() {
        GameSettings gameSettings = javaFXManager.getActiveGameProperty().getValue().getGameSettings();
        Map<String, Integer> shipTypes = gameSettings.getShipAmountsOnBoard();
        for (Map.Entry<String, Integer> entry : shipTypes.entrySet()){
            shipsState.add(new ShipsStateDataModel(entry.getKey(),entry.getValue()));
        }
    }

    private void addListeners() {
        javaFXManager.getGameStateProperty().addListener((observable, oldValue, newValue) -> gameStateChanged(newValue));
        javaFXManager.getActivePlayerProperty().addListener((observable, oldValue, newValue) -> playerChanged(newValue));
        javaFXManager.getTotalMovesCounterProperty().addListener((observable, oldValue, newValue) -> updateStatistics());
    }

    private void gameStateChanged(eGameState newValue) {
        switch (newValue) {
            case INVALID:
                break;
            case LOADED:
                break;
            case INITIALIZED:
                break;
            case STARTED:
                gameStarted();
                break;
            case PLAYER_WON:
                break;
            case PLAYER_QUIT:
                break;
        }
    }

    private void gameStarted() {
        setShipTypeValues();
        TableShipsState.setItems(shipsState);
    }

    private void updateStatistics() {
        Player activePlayer = javaFXManager.getActivePlayerProperty().getValue();

        labelCurrentScore.setText(((Integer) activePlayer.getScore()).toString());
        Duration avgDuration = activePlayer.getAvgTurnDuration();
        labelAvgTurnDuration.setText(String.format("%d:%02d", avgDuration.toMinutes(), avgDuration.getSeconds() % 60));
        labelHitsCounter.setText(((Integer) activePlayer.getTimesHit()).toString());
        labelMissCounter.setText(((Integer) activePlayer.getTimesMissed()).toString());
        Integer minesAvailable = (activePlayer.getMyBoard().getMinesAvailable());
        labelMinesAvailable.setText(minesAvailable.toString());
        setMinesAvailableImageView(minesAvailable);
    }

    private void setMinesAvailableImageView(Integer minesAvailable) {
        if (minesAvailable <= 0){
            imageViewMinesAvailable.setImage(noMinesAvailableImage);
        }
        else if (minesAvailable == 1){
            imageViewMinesAvailable.setImage(oneMineAvailableImage);
        }
        else{
            imageViewMinesAvailable.setImage(multipleMinesAvailable);
        }
    }


    private void playerChanged(Player currentPlayer) {
        labelCurrentPlayer.setText(currentPlayer.getName());
        updateStatistics();
    }
}
