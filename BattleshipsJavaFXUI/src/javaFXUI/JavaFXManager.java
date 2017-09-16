package javaFXUI;

import gameLogic.GamesManager;
import gameLogic.IGamesLogic;
import gameLogic.exceptions.CellNotOnBoardException;
import gameLogic.exceptions.InvalidGameObjectPlacementException;
import gameLogic.game.Game;
import gameLogic.game.board.BoardCoordinates;
import gameLogic.game.eAttackResult;
import gameLogic.game.eGameState;
import gameLogic.users.ComputerPlayer;
import gameLogic.users.Player;
import javaFXUI.model.AlertHandlingUtils;
import javaFXUI.view.LayoutCurrentTurnInfoController;
import javaFXUI.view.MainWindowController;
import javaFXUI.view.PauseWindowController;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaFXManager extends Application {
    private IGamesLogic gamesManager = new GamesManager();
    private Property<eGameState> gameStateProperty = new SimpleObjectProperty<>();
    private Property<Game> activeGameProperty = new SimpleObjectProperty<>();
    private Property<Player> activePlayerProperty = new SimpleObjectProperty<>();
    private IntegerProperty totalMovesCounter = new SimpleIntegerProperty();
    private int computerPlayerIndex = 0;
    private boolean exitGameSelected = false;
    private Stage primaryStage;
    private Stage secondaryStage = new Stage();
    private BorderPane mainWindowLayout;
    private VBox pauseWindowLayout;
    private List<Runnable> resetGameEvent = new ArrayList<>();


    // ===================================== Init =====================================
    public static void Run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Battleships by Or and Dudi");
        this.primaryStage.getIcons().add(new Image("/resources/images/gameIcon.ico"));
        initMainWindow();
        initPauseWindow();
    }

    @FXML
    private void initialize() {
        gameStateProperty.addListener((observable, oldValue, newValue) -> gameStateChanged(newValue));
    }

    private void initMainWindow() {
        try {
            // load main window
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/MainWindow.fxml"));
            mainWindowLayout = fxmlLoader.load();
            MainWindowController mainWindowController = fxmlLoader.getController();
            mainWindowController.setJavaFXManager(this);
            resetGameEvent.add(mainWindowController::resetGame);

            // load right pane
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/LayoutCurrentTurnInfo.fxml"));
            ScrollPane rightPane = fxmlLoader.load();
            LayoutCurrentTurnInfoController currentTurnInfoController = fxmlLoader.getController();
            currentTurnInfoController.setJavaFXManager(this);
            mainWindowLayout.setRight(rightPane);

            Scene scene = new Scene(mainWindowLayout);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image
                    (JavaFXManager.class.getResourceAsStream("/resources/images/gameIcon.png")));
            primaryStage.show();
        } catch (IOException e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while loading main window");
        }
    }

    private void initPauseWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/PauseWindow.fxml"));
            pauseWindowLayout = fxmlLoader.load();
            PauseWindowController pauseWindowController = fxmlLoader.getController();
            pauseWindowController.setJavaFXManager(this);

            Scene scene = new Scene(pauseWindowLayout);
            secondaryStage.setScene(scene);
            secondaryStage.initOwner(primaryStage);
            secondaryStage.initModality(Modality.WINDOW_MODAL);
            secondaryStage.setResizable(false);
            secondaryStage.initStyle(StageStyle.UTILITY);
            secondaryStage.setTitle("Game Paused");
            secondaryStage.getIcons().add(new Image(JavaFXManager.class.getResourceAsStream("/resources/images/gameIcon.png")));
            secondaryStage.setOnCloseRequest(Event::consume);
            showPauseMenu();
        } catch (IOException e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while loading pause window");
        }

    }

    // ===================================== Setters =====================================

    // ===================================== Getters =====================================
    public Property<Game> getActiveGameProperty() {
        return activeGameProperty;
    }

    public Property<eGameState> getGameStateProperty() {
        return gameStateProperty;
    }

    public Property<Player> getActivePlayerProperty() {
        return activePlayerProperty;
    }

    public int getTotalMovesCounter() {
        return totalMovesCounter.get();
    }

    public IntegerProperty getTotalMovesCounterProperty() {
        return totalMovesCounter;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Stage getSecondaryStage() {
        return secondaryStage;
    }

    // ===================================== Load Game =====================================
    public void loadGame(String xmlFilePath) throws LoadException {
        Game loadedGame = gamesManager.loadGameFile(xmlFilePath);
        activeGameProperty.setValue(loadedGame);
        gameStateProperty.setValue(loadedGame.getGameState());
    }

    // ===================================== Start Game =====================================
    public void startGame() {
        try {
            Game activeGame = activeGameProperty.getValue();
            ComputerPlayer computerPlayer = computerPlayerIndex == 0 ? null :
                    new ComputerPlayer("ComputerPlayer2", "Computer Player", activeGame.getSizeOfMinimalShipOnBoard());
            Player player1 = computerPlayerIndex == 1 ? computerPlayer : new Player("P1", "Player 1");
            Player player2 = computerPlayerIndex == 2 ? computerPlayer : new Player("P2", "Player 2");

            gamesManager.startGame(activeGame, player1, player2);
            if (computerPlayerIndex == 1) {
                activeGameProperty.getValue().swapPlayers();
            }

            // TODO set mines
            // give each player 2 mines
//            player1.getMyBoard().setMinesAvailable(2);
//            player2.getMyBoard().setMinesAvailable(2);
//            TODO show game state
//            showGameState();
            gameStateProperty.setValue(activeGame.getGameState());
            activePlayerProperty.setValue(activeGame.getActivePlayer());
            hidePauseMenu();
        } catch (InvalidGameObjectPlacementException e) {
            String message = "Cannot place a given " + e.getGameObjectType() + " at position " + e.GetCoordinates() + ".\n" +
                    "reason: " + e.getReason() + "\n";
            AlertHandlingUtils.showErrorMessage(e, "Error while initializing board.", message);
            errorWhileStartingGame();
        } catch (Exception e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while starting game. ");
            errorWhileStartingGame();
        }
    }

    // ===================================== Make Move =====================================
    public eAttackResult makeMove(BoardCoordinates cellToAttack, Runnable updateCellDisplay) {
        eAttackResult attackResult = null;

        try {
            Game activeGame = activeGameProperty.getValue();
            attackResult = gamesManager.makeMove(activeGame, cellToAttack);
            updateCellDisplay.run();
            activePlayerProperty.setValue(activeGame.getActivePlayer());
            totalMovesCounter.setValue(activeGame.getMovesCounter());
            if (activeGame.getGameState() == eGameState.PLAYER_WON) {
//                onGameEnded(eGameState.STARTED,true);
                endGame(true);
            }
            gameStateProperty.setValue(activeGame.getGameState());
        } catch (CellNotOnBoardException e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while making move");
        }

        return attackResult;
    }

    // ===================================== End Game =====================================
    public void endGame(boolean moveFinish) {
        onGameEnded(activeGameProperty.getValue().getGameState(), moveFinish);
    }

    // ===================================== Exit Game =====================================
    public void exitGame() {
        // TODO say goodbye before closing :)
        primaryStage.close();
    }

    // ===================================== Other Methods =====================================
    private void gameStateChanged(eGameState newValue) {
        if (newValue == eGameState.STARTED) {

        }
    }

    private void errorWhileStartingGame() {
        activeGameProperty.setValue(null);
        String message = "game file given was invalid therefor it was not loaded. \nPlease check the file and try again.";
        AlertHandlingUtils.showErrorMessage(new Exception("Invalid game file"), "Game file validation error", message);
    }

    public void showPauseMenu() {
        secondaryStage.showAndWait();
    }

    public void hidePauseMenu() {
        secondaryStage.hide();
    }

    private void onGameEnded(eGameState stateBeforeEndingGame, boolean moveFinish) {
        if (stateBeforeEndingGame.gameHasStarted()) {
            StringBuilder message = new StringBuilder();
            Game activeGame = activeGameProperty.getValue();
            if (activeGame.getWinnerPlayer() != null) {
                message.append("Congratulations!" + activeGame.getWinnerPlayer().getName() + "has won the game!");
            } else {
                if (moveFinish) {
                    activeGame.swapPlayers();
                }
                message.append(activeGame.getActivePlayer().getName() + " was left the game. ");
                activeGame.swapPlayers();
                message.append("So, " + activeGame.getActivePlayer().getName() + " you won the game !!");
            }
            Alert playerWonAlert = new Alert(Alert.AlertType.INFORMATION, message.toString(), ButtonType.OK);
            playerWonAlert.showAndWait();
        }

        // set the game to be as if it was just started
        resetGame(activeGameProperty.getValue());
    }

    private void resetGame(Game activeGame) {
        try {
            activeGame.resetGame();
            gameStateProperty.setValue(activeGame.getGameState());
            resetGameEvent.forEach(Runnable::run);
            if (!secondaryStage.isShowing()) {
                showPauseMenu();
            }
        } catch (Exception e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while reloading the game, Please load the file again or choose another file", e.getMessage());
            activeGame.setGameState(eGameState.INVALID);
            gameStateProperty.setValue(activeGame.getGameState());
        }
    }
}
