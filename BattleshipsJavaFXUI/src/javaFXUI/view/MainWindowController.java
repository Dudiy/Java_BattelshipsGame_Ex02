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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
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
    public TilePane tilePaneMines;
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
//                addMine(activePlayer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    // TODO just a test
//    private void addMine(Player activePlayer) {
//        // TODO use manger prefix
////        ImageView mineImage = new ImageView("/resources/images/Bomb.png");
//        int minesToAdd=activePlayer.getMyBoard().getMinesAvailable();
//        tilePaneMines.getChildren().removeAll();
//        for(int i=0;i<minesToAdd;i++){
//            final ImageView mineImage = new ImageView("/resources/images/Bomb.png");
//
//            mineImage.setOnDragDetected(event -> {
//                Dragboard db = mineImage.startDragAndDrop(TransferMode.ANY);
//                event.consume();
//                //Alert playerWonAlert = new Alert(Alert.AlertType.INFORMATION, "a", ButtonType.OK);
//               // playerWonAlert.showAndWait();
//            });
//            mineImage.setOnMouseDragged(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//                    mineImage.setX(mineImage.getX()+90);
//                }
//            });
////            vboxMines.getChildren().add(new ImageView("/resources/images/Bomb.png"));
//            tilePaneMines.getChildren().add(mineImage);
//        }
//    }

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

    // ===================================== ToolBar Methods =====================================
    @FXML
    public void OnClickPauseMenu(ActionEvent actionEvent) {
        javaFXManager.showPauseMenu();
    }

    @FXML
    public void OnClickEndCurrentGame(ActionEvent actionEvent) {
        javaFXManager.endGame(false);
    }

    @FXML
    public void OnClickExit(ActionEvent actionEvent) {
        javaFXManager.exitGame();
    }


    // ===================================== Mines Methods =====================================
    @FXML
    public void OnMouseDragEntered(MouseDragEvent mouseDragEvent) {
        /* drag was detected, start drag-and-drop gesture*/
        System.out.println("onDragDetected");

                /* allow any transfer mode */
        Dragboard db = tilePaneMines.startDragAndDrop(TransferMode.ANY);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("!!");

        mouseDragEvent.consume();
    }

    @FXML
    public void OnMouseDragExited(MouseDragEvent mouseDragEvent) {

    }
}
