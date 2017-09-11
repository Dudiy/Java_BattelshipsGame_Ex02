package javaFXUI.view;

import gameLogic.game.Game;
import gameLogic.game.board.BoardCoordinates;
import gameLogic.game.eAttackResult;
import gameLogic.game.eGameState;
import gameLogic.users.Player;
import javaFXUI.JavaFXManager;
import javaFXUI.model.AlertHandlingUtils;
import javaFXUI.model.BoardAdapter;
import javaFXUI.model.ImageViewProxy;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class MainWindowController {

    private Map<Player, TilePane> myBoardAsTilePane = new HashMap<>();
    private Map<Player, TilePane> opponentsBoardAsTilePane = new HashMap<>();
    private JavaFXManager javaFXManager;
    private boolean boardsInitialized;

    @FXML
    private VBox vBoxMyBoard;
    @FXML
    private VBox vBoxOpponentsBoard;
    @FXML
    private TilePane tilePaneMyBoard;
    @FXML
    private TilePane tilePaneOpponentsBoard;
    @FXML
    private MenuItem buttonPauseGame;
    @FXML
    private MenuItem buttonEndCurrentGame;
    @FXML
    private MenuItem buttonExit;

    // ===================================== Init =====================================
    public void setJavaFXManager(JavaFXManager javaFXManager) {
        this.javaFXManager = javaFXManager;
        javaFXManager.getGameStateProperty().addListener((observable, oldValue, newValue) -> gameStateChanged(newValue));
        javaFXManager.getActivePlayerProperty().addListener((observable, oldValue, newValue) -> activePlayerChanged(newValue));
        javaFXManager.getTotalMovesCounterProperty().addListener((observable, oldValue, newValue) -> movePlayed());
    }

    private void initBoards() {
        Game activeGame = javaFXManager.getActiveGameProperty().getValue();
        for (Player currentPlayer : activeGame.getPlayers()) {
            try {
                if (!myBoardAsTilePane.containsKey(currentPlayer)) {
                    BoardAdapter boardAdapter = new BoardAdapter(currentPlayer.getMyBoard(), true);
                    myBoardAsTilePane.put(currentPlayer, boardAdapter.getBoardAsTilePane());
                }
                if (!opponentsBoardAsTilePane.containsKey(currentPlayer)) {
                    BoardAdapter boardAdapter = new BoardAdapter(currentPlayer.getOpponentBoard(), false);
                    // set onClick event
                    for (Node imageView : boardAdapter.getBoardAsTilePane().getChildren()) {
                        if (imageView instanceof ImageViewProxy) {
                            imageView.setOnMouseClicked(event -> makeMove((ImageViewProxy) imageView));
                        }
                    }

                    opponentsBoardAsTilePane.put(currentPlayer, boardAdapter.getBoardAsTilePane());
                }
            } catch (Exception e) {
                AlertHandlingUtils.showErrorMessage(e, "Error while drawing boards");
            }
        }

        boardsInitialized = true;
        redrawBoards(activeGame.getActivePlayer());
    }

    // ===================================== Other Methods =====================================
    private void gameStateChanged(eGameState newValue) {
        switch (newValue) {

            case INVALID:
                break;
            case LOADED:
                break;
            case INITIALIZED:
                break;
            case STARTED:
                initBoards();
                break;
            case PLAYER_WON:
                break;
            case PLAYER_QUIT:
                break;
        }
    }

    private void activePlayerChanged(Player activePlayer) {
        try {
            if (boardsInitialized) {
                redrawBoards(activePlayer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void redrawBoards(Player activePlayer) {
        BoardAdapter.updateImages(myBoardAsTilePane.get(activePlayer));
        vBoxMyBoard.getChildren().set(2, myBoardAsTilePane.get(activePlayer));
        vBoxOpponentsBoard.getChildren().set(2, opponentsBoardAsTilePane.get(activePlayer));
    }

    private void movePlayed() {
    }

    private void makeMove(ImageViewProxy imageView) {
        eAttackResult attackResult = javaFXManager.makeMove(BoardCoordinates.Parse(imageView.getId()),
                ()->imageView.updateImage());
    }


    public void resetGame(){
        myBoardAsTilePane.clear();
        opponentsBoardAsTilePane.clear();
    }
}
