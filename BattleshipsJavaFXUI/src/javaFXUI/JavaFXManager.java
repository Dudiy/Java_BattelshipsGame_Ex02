package javaFXUI;

import gameLogic.GamesManager;
import gameLogic.IGamesLogic;
import gameLogic.exceptions.InvalidGameObjectPlacementException;
import gameLogic.game.Game;
import gameLogic.game.eGameState;
import gameLogic.users.ComputerPlayer;
import gameLogic.users.Player;
import javaFXUI.model.AlertHandlingUtils;
import javaFXUI.view.MainWindowController;
import javaFXUI.view.PauseWindowController;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class JavaFXManager extends Application {
    private IGamesLogic gamesManager = new GamesManager();

    private Property<eGameState> eGameStateProperty = new SimpleObjectProperty<>();

    private Property<Player> activePlayerProperty = new SimpleObjectProperty<>();

    public Property<Player> getActivePlayerProperty() {
        return activePlayerProperty;
    }

    public Property<eGameState> getGameStateProperty() {
        return eGameStateProperty;
    }

    // JavaFX application may have only 1 game

    private Game activeGame;
    private boolean exitGameSelected = false;
    private int computerPlayerIndex = 0;
    private Stage primaryStage;
    private Property<Game> activeGameProperty = new SimpleObjectProperty<>();

    private Stage secondaryStage = new Stage();

    private BorderPane mainWindowLayout;
    private VBox pauseWindowLayout;

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

    private void initMainWindow() {
        try {
            // load main window
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/MainWindow.fxml"));
            mainWindowLayout = loader.load();
            MainWindowController controller = loader.getController();
            controller.setProgram(this);

            // load right pane
            loader = new FXMLLoader();
            loader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/LayoutCurrentTurnInfo.fxml"));
            ScrollPane rightPane = loader.load();
            mainWindowLayout.setRight(rightPane);

            Scene scene = new Scene(mainWindowLayout);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(
                    new Image(JavaFXManager.class.getResourceAsStream("/resources/images/gameIcon.png")));
            primaryStage.show();
        } catch (IOException e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while loading main window");
        }
    }

    private void initPauseWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/PauseWindow.fxml"));
            pauseWindowLayout = loader.load();

            Scene scene = new Scene(pauseWindowLayout);
            secondaryStage.setScene(scene);
            secondaryStage.initOwner(primaryStage);
            secondaryStage.initModality(Modality.WINDOW_MODAL);
            secondaryStage.setResizable(false);
            secondaryStage.initStyle(StageStyle.UTILITY);
            PauseWindowController controller = loader.getController();
            controller.setProgram(this);
            secondaryStage.setTitle("Game Paused");
            secondaryStage.getIcons().add(new Image(JavaFXManager.class.getResourceAsStream("/resources/images/gameIcon.png")));
            secondaryStage.setOnShown(event -> {
                eGameState gameState = activeGame != null ? activeGame.getGameState() : eGameState.INVALID;
                controller.updateButtonState(gameState);
            });
            secondaryStage.setOnCloseRequest(Event::consume);
            secondaryStage.showAndWait();
        } catch (IOException e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while loading pause window");
        }

    }

    // ===================================== Setters =====================================

    public void setActiveGame(Game activeGame) {
        this.activeGame = activeGame;
    }
    // ===================================== Getters =====================================

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Stage getSecondaryStage() {
        return secondaryStage;
    }

    public Game getActiveGame() {
        return activeGame;
    }

    public IGamesLogic getGamesManager() {
        return gamesManager;
    }

    // ===================================== menu option handlers =====================================
    public void loadGame(String xmlFilePath) throws LoadException {
        activeGame = gamesManager.loadGameFile(xmlFilePath);
        eGameStateProperty.setValue(activeGame.getGameState());
    }

    public void startGame() {
        try {
            ComputerPlayer computerPlayer = computerPlayerIndex == 0 ? null :
                    new ComputerPlayer("ComputerPlayer2", "Computer Player", activeGame.getSizeOfMinimalShipOnBoard());
            Player player1 = computerPlayerIndex == 1 ?
                    computerPlayer :
                    new Player("P1", "Player 1");
            Player player2 = computerPlayerIndex == 2 ?
                    computerPlayer :
                    new Player("P2", "Player 2");

            gamesManager.startGame(activeGame, player1, player2);
            if (computerPlayerIndex == 1) {
                activeGame.swapPlayers();
            }

            drawBoards();
            // TODO set mines
            // give each player 2 mines
            player1.getMyBoard().setMinesAvailable(2);
            player2.getMyBoard().setMinesAvailable(2);
//            TODO show game state
//            showGameState();
            secondaryStage.close();
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

    private void drawBoards() {
    }

    public void exitGame() {
        // TODO say goodbye before closing :)
        primaryStage.close();
    }

    private void errorWhileStartingGame() {
        activeGame = null;
        String message = "game file given was invalid therefor it was not loaded. \nPlease check the file and try again.";
        AlertHandlingUtils.showErrorMessage(new Exception("Invalid game file"), "Game file validation error", message);
    }
}
