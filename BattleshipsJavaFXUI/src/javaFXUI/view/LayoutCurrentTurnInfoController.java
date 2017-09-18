package javaFXUI.view;

import gameLogic.game.board.BoardCell;
import gameLogic.game.board.BoardCoordinates;
import gameLogic.game.Game;
import gameLogic.game.board.Board;
import gameLogic.game.eAttackResult;
import gameLogic.game.eGameState;
import gameLogic.game.gameObjects.ship.AbstractShip;
import gameLogic.game.gameObjects.ship.IShipListener;
import gameLogic.users.Player;
import javaFXUI.Constants;
import javaFXUI.JavaFXManager;
import javaFXUI.model.BoardAdapter;
import javaFXUI.model.ImageViewProxy;
import javaFXUI.model.ShipsStateDataModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.TilePane;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class LayoutCurrentTurnInfoController {
    @FXML
    private Label labelCurrentPlayer;
    @FXML
    private ImageView imageViewMinesAvailable;
    @FXML
    private Label labelMinesAvailable;
    @FXML
    private TableView<ShipsStateDataModel> TableShipsState;
    @FXML
    private TableColumn<ShipsStateDataModel, String> columnShipType;
    @FXML
    private TableColumn<ShipsStateDataModel, Integer> columnInitialShips;
    @FXML
    private TableColumn<ShipsStateDataModel, Integer> columnRemainingShips;
    @FXML
    private Label labelCurrentScore;
    @FXML
    private Label labelAvgTurnDuration;
    @FXML
    private Label labelHitsCounter;
    @FXML
    private Label labelMissCounter;
    private JavaFXManager javaFXManager;
    private final Map<String, ShipsStateDataModel> shipsState = new HashMap<>();
    private final Image noMinesAvailableImage = new Image(Constants.NO_MINES_AVAILABLE_IMAGE_URL);
    private final Image oneMineAvailableImage = new Image(Constants.MINE_IMAGE_URL);
    private final Image multipleMinesAvailable = new Image(Constants.MULTIPLE_MINES_IMAGE_URL);

    // ===================================== Init =====================================
    @FXML
    private void initialize() {
        columnShipType.setCellValueFactory(new PropertyValueFactory<>("shipType"));
        columnInitialShips.setCellValueFactory(new PropertyValueFactory<>("initialAmount"));
        columnRemainingShips.setCellValueFactory(new PropertyValueFactory<>("initialAmount"));
        setDragAndDropMine();
    }

    private void setDragAndDropMine() {
        imageViewMinesAvailable.setOnDragDetected((event) -> {
            int boardSize = javaFXManager.getActiveGame().getValue().getActivePlayer().getMyBoard().getBoardSize();
            int cellSize = BoardAdapter.getCellSize(boardSize);
            ImageView draggedImage = new ImageView(new Image(Constants.MINE_IMAGE_URL, cellSize, cellSize, true, true));
            WritableImage snapshot = draggedImage.snapshot(new SnapshotParameters(), null);
            Dragboard db = imageViewMinesAvailable.startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.putImage(draggedImage.getImage());
            db.setContent(content);
            db.setDragView(snapshot, 25, 25);

            event.consume();
        });

        imageViewMinesAvailable.setOnDragDone((event) -> {
            Board myBoard = javaFXManager.getActiveGame().getValue().getActivePlayer().getMyBoard();
            Integer minesAvailable = myBoard.getMinesAvailable();
            if (event.getTransferMode() == TransferMode.MOVE) {
                Dragboard dragboard = event.getDragboard();
                ImageViewProxy selectedBoardCellAsImage =  getSelectedBoardCell(dragboard);
                javaFXManager.plantMine(selectedBoardCellAsImage);
                // player already swap, so we need to go back to the previous player
//                javaFXManager.getActiveGame().getValue().swapPlayers();
                updateMinesAvailableImageView();
//                javaFXManager.getActiveGame().getValue().swapPlayers();
            }
            event.consume();
        });
    }

    private ImageViewProxy getSelectedBoardCell(Dragboard dragboard) {
        ImageViewProxy boardCellAsImage=null;
        for(DataFormat dataFormat : dragboard.getContentTypes()){
            for(String className : dataFormat.getIdentifiers()){
                if(className == "ImageViewProxy"){
                    boardCellAsImage = (ImageViewProxy)dragboard.getContent(dataFormat);
                    break;
                }
            }
        }
        return boardCellAsImage;
    }

    private void updateMinesAvailableImageView() {
        Integer minesAvailable = javaFXManager.getActiveGame().getValue().getActivePlayer().getMyBoard().getMinesAvailable();
        if (minesAvailable <= 0) {
            imageViewMinesAvailable.setImage(noMinesAvailableImage);
            imageViewMinesAvailable.setDisable(true);
        } else if (minesAvailable == 1) {
            imageViewMinesAvailable.setImage(oneMineAvailableImage);
        } else {
            imageViewMinesAvailable.setImage(multipleMinesAvailable);
        }
        labelMinesAvailable.setText(minesAvailable.toString());
    }

    public void setJavaFXManager(JavaFXManager javaFXManager) {
        this.javaFXManager = javaFXManager;
        addListeners();
    }

    private void addListeners() {
        javaFXManager.gameStateProperty().addListener((observable, oldValue, newValue) -> gameStateChanged(newValue));
        javaFXManager.activePlayerProperty().addListener((observable, oldValue, newValue) -> playerChanged(newValue));
        javaFXManager.totalMovesCounterProperty().addListener((observable, oldValue, newValue) -> updateStatistics());
        javaFXManager.attackResultProperty().addListener((observable, oldValue, newValue) -> attackResultUpdated(newValue));
    }

    // ===================================== Game State Property =====================================
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
    }

    private void setShipTypeValues() {
        Map<String, Integer> shipTypes = javaFXManager.getActiveGame().getValue().getActivePlayer().getActiveShipsOnBoard();
        for (Map.Entry<String, Integer> entry : shipTypes.entrySet()) {
            shipsState.put(entry.getKey(), new ShipsStateDataModel(entry.getKey(), entry.getValue()));
        }
    }

    // ===================================== Active Player Property =====================================
    private void playerChanged(Player currentPlayer) {
        labelCurrentPlayer.setText(currentPlayer.getName());
        updateStatistics();
        updateShipsRemainingTable();
    }

    private void updateShipsRemainingTable() {
        ObservableList<ShipsStateDataModel> shipsRemaining = FXCollections.observableArrayList();
        javaFXManager.getActiveGame().getValue().getOtherPlayer().getActiveShipsOnBoard().entrySet()
                .forEach(entry -> shipsRemaining.add(new ShipsStateDataModel(entry.getKey(), entry.getValue())));
        TableShipsState.setItems(shipsRemaining);
    }

    private void updateStatistics() {
        Player activePlayer = javaFXManager.activePlayerProperty().getValue();
        labelCurrentScore.setText(((Integer) activePlayer.getScore()).toString());
        Duration avgDuration = activePlayer.getAvgTurnDuration();
        labelAvgTurnDuration.setText(String.format("%d:%02d", avgDuration.toMinutes(), avgDuration.getSeconds() % 60));
        labelHitsCounter.setText(((Integer) activePlayer.getTimesHit()).toString());
        labelMissCounter.setText(((Integer) activePlayer.getTimesMissed()).toString());
        updateMinesAvailableImageView();
    }

    // ===================================== Attack Result Property =====================================
    public void attackResultUpdated(eAttackResult newValue) {
        switch (newValue) {
            case HIT_SHIP:
                break;
            case HIT_AND_SUNK_SHIP:
                onShipSunk();
                break;
            case CELL_ALREADY_ATTACKED:
                break;
            case HIT_MINE:
                break;
            case HIT_WATER:
                break;
        }
    }

    private void onShipSunk() {
        updateShipsRemainingTable();
        TableShipsState.refresh();
    }
}
