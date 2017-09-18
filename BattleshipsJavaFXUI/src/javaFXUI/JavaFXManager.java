package javaFXUI;

import gameLogic.*;
import gameLogic.exceptions.*;
import gameLogic.game.*;
import gameLogic.game.board.BoardCoordinates;
import gameLogic.users.*;
import javaFXUI.model.AlertHandlingUtils;
import javaFXUI.model.ImageViewProxy;
import javaFXUI.model.PlayerAdapter;
import javaFXUI.view.LayoutCurrentTurnInfoController;
import javaFXUI.view.MainWindowController;
import javaFXUI.view.PauseWindowController;
import javaFXUI.view.PlayerInitializerController;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class JavaFXManager extends Application {
    // ============== stages and layouts ==============
    private Stage primaryStage;
    private final Stage secondaryStage = new Stage();
    private final Stage playerInitializerStage = new Stage();
    private AnchorPane welcomeScreen;
    private BorderPane mainWindowLayout;
    // ============== controllers ==============
    private MainWindowController mainWindowController;
    private LayoutCurrentTurnInfoController currentTurnInfoController;
    private PlayerInitializerController playerInitializerController;
    // ============== properties ==============
    private final Property<eGameState> gameState = new SimpleObjectProperty<>();
    private final Property<Game> activeGame = new SimpleObjectProperty<>();
    private final Property<Player> activePlayer = new SimpleObjectProperty<>();
    private final Property<eAttackResult> attackResult = new SimpleObjectProperty<>();
    private final IntegerProperty totalMovesCounter = new SimpleIntegerProperty();
    // ============== other members ==============
    private final IGamesLogic gamesManager = new GamesManager();
    private FXMLLoader fxmlLoader;
    private final List<Runnable> resetGameEvent = new ArrayList<>();
    private Instant turnPlayerTimer;

    // ===================================== Init =====================================

    static void Run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Battleships by Or and Dudi");
        this.primaryStage.getIcons().add(new Image("/resources/images/gameIcon.ico"));
        initMainWindow();
        initPlayerInitializerWindow();
        initPauseWindow();
    }

    private void initMainWindow() {
        try {
            loadMainWindow();
            loadRightPane();
            loadWelcomeScreen();

            Scene scene = new Scene(welcomeScreen);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image(JavaFXManager.class.getResourceAsStream(Constants.GAME_ICON_IMAGE_URL)));
            primaryStage.show();
        } catch (IOException e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while loading main window");
        }
    }

    private void loadRightPane() throws IOException {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/LayoutCurrentTurnInfo.fxml"));
        ScrollPane rightPane = fxmlLoader.load();
        currentTurnInfoController = fxmlLoader.getController();
        currentTurnInfoController.setJavaFXManager(this);
        mainWindowLayout.setRight(rightPane);
    }

    private void initPlayerInitializerWindow() throws IOException {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/PlayerInitializer.fxml"));
        AnchorPane playerInitializerLayout = fxmlLoader.load();
        playerInitializerController = fxmlLoader.getController();
        playerInitializerController.setOwnerWindow(primaryStage, this);

        Scene scene = new Scene(playerInitializerLayout);
        playerInitializerStage.setScene(scene);
        playerInitializerStage.setOnCloseRequest(event -> playerInitializerController.updatePlayerInfo());
        playerInitializerStage.initOwner(primaryStage);
        // TODO set the correct style
        playerInitializerStage.initStyle(StageStyle.UTILITY);
        playerInitializerStage.initModality(Modality.WINDOW_MODAL);
    }

    private void loadWelcomeScreen() throws IOException {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/WelcomeScene.fxml"));
        welcomeScreen = fxmlLoader.load();
    }

    private void loadMainWindow() throws IOException {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/MainWindow.fxml"));
        mainWindowLayout = fxmlLoader.load();
        mainWindowController = fxmlLoader.getController();
        mainWindowController.setJavaFXManager(this);
        resetGameEvent.add(mainWindowController::resetGame);
    }

    private void initPauseWindow() {
        try {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/PauseWindow.fxml"));
            VBox pauseWindowLayout = fxmlLoader.load();
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

    // ===================================== Getters =====================================

    public Property<Game> getActiveGame() {
        return activeGame;
    }

    public Property<eGameState> gameStateProperty() {
        return gameState;
    }

    public Property<Player> activePlayerProperty() {
        return activePlayer;
    }

    public IntegerProperty totalMovesCounterProperty() {
        return totalMovesCounter;
    }

    public Stage getSecondaryStage() {
        return secondaryStage;
    }

    public Property<eAttackResult> attackResultProperty() {
        return attackResult;
    }

    public int getTotalMovesCounter() {
        return totalMovesCounter.get();
    }

    // ===================================== Load Game =====================================

    public void loadGame(String xmlFilePath) throws LoadException {
        Game loadedGame = gamesManager.loadGameFile(xmlFilePath);
        activeGame.setValue(loadedGame);
        gameState.setValue(loadedGame.getGameState());
    }
    // ===================================== Start Game =====================================

    public void startGame() {
        try {
            // TODO use task to show "Loading game"
            primaryStage.setScene(new Scene(mainWindowLayout));
            // TODO select player names....and pictures?!
            Game activeGame = this.activeGame.getValue();
            // TODO add computer player?!
            Player player1 = initializePlayer(1);
            Player player2 = initializePlayer(2);
            gamesManager.startGame(activeGame, player1, player2);
            setInitialValuesForPlayers();
            gameState.setValue(activeGame.getGameState());
            updateActivePlayer();
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

    // display the initialize player window and return the player according to the data
    // TODO check for bugs when not closing correctly, currently there is a problem with the images
    private Player initializePlayer(int playerNumber) {
        PlayerAdapter newPlayer = new PlayerAdapter("Player" + playerNumber, "Player " + playerNumber);
        playerInitializerController.setPlayer(newPlayer, playerNumber);
        playerInitializerStage.showAndWait();
        return newPlayer;
    }

    public void playerWindowOkClicked() {
        playerInitializerStage.close();
    }

    private void updateActivePlayer() {
        Player activePlayerInActiveGame = activeGame.getValue().getActivePlayer();
        if (activePlayer.getValue() != activePlayerInActiveGame) {
            activePlayer.setValue(activePlayerInActiveGame);
            turnPlayerTimer = Instant.now();
        }
    }

    private void setInitialValuesForPlayers() {
        Game activeGame = this.activeGame.getValue();
        Player[] players = activeGame.getPlayers();

        for (Player player : players) {
            player.getMyBoard().setMinesAvailable(activeGame.getGameSettings().getMinesPerPlayer());
            player.setActiveShipsOnBoard(activeGame.getGameSettings().getShipAmountsOnBoard());
        }
    }

    // ===================================== Make Move =====================================

    public eAttackResult makeMove(BoardCoordinates cellToAttack, Runnable updateCellDisplay) {
        eAttackResult attackResult = null;

        try {
            Game activeGame = this.activeGame.getValue();
            attackResult = gamesManager.makeMove(activeGame, cellToAttack);
            updateActivePlayerAttackResult(attackResult, updateCellDisplay);
            updateActivePlayer();
            gameState.setValue(activeGame.getGameState());
        } catch (CellNotOnBoardException e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while making move");
        }

        return attackResult;
    }

    private void updateActivePlayerAttackResult(eAttackResult attackResult, Runnable updateCellDisplay) {
        Game activeGame = this.activeGame.getValue();
        updateCellDisplay.run();
        currentTurnInfoController.attackResultUpdated(attackResult);
        if (attackResult.moveEnded() || activeGame.getGameState() == eGameState.PLAYER_WON) {
            Duration turnTime = Duration.between(turnPlayerTimer, Instant.now());
            activePlayer.getValue().addTurnDurationToTotal(turnTime);
        }
        totalMovesCounter.setValue(activeGame.getMovesCounter());
        Alert moveResult = new Alert(Alert.AlertType.INFORMATION, attackResult.toString());
        moveResult.showAndWait();
        if (activeGame.getGameState() == eGameState.PLAYER_WON) {
            endGame(true);
        }
    }

    public void plantMine(ImageViewProxy boardCellAsImage) {
        try {
            BoardCoordinates minePosition = boardCellAsImage.getBoardCell().getPosition();
            activeGame.getValue().plantMineOnActivePlayersBoard(minePosition);
            mainWindowController.plantMine(boardCellAsImage);
            updateActivePlayer();
        } catch (Exception e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while plant mine");
        }
    }

    // ===================================== End Game =====================================

    public void endGame(boolean moveFinish) {
        onGameEnded(activeGame.getValue().getGameState(), moveFinish);
    }

    private void onGameEnded(eGameState stateBeforeEndingGame, boolean moveFinish) {
        if (stateBeforeEndingGame.gameHasStarted()) {
            StringBuilder message = new StringBuilder();
            Game activeGame = this.activeGame.getValue();
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
        resetGame(activeGame.getValue());
    }

    // ===================================== Exit Game =====================================

    public void exitGame() {
        // TODO say goodbye before closing :)
        primaryStage.close();
    }

    // ===================================== Other Methods =====================================

    private void errorWhileStartingGame() {
        activeGame.setValue(null);
        String message = "game file given was invalid therefor it was not loaded. \nPlease check the file and try again.";
        AlertHandlingUtils.showErrorMessage(new Exception("Invalid game file"), "Game file validation error", message);
    }

    public void showPauseMenu() {
        secondaryStage.showAndWait();
    }

    public void hidePauseMenu() {
        secondaryStage.hide();
    }

    private void resetGame(Game activeGame) {
        try {
            activeGame.resetGame();
            gameState.setValue(activeGame.getGameState());
            resetGameEvent.forEach(Runnable::run);
            if (!secondaryStage.isShowing()) {
                showPauseMenu();
            }
        } catch (Exception e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while reloading the game, Please load the file again or choose another file", e.getMessage());
            activeGame.setGameState(eGameState.INVALID);
            gameState.setValue(activeGame.getGameState());
        }
    }
}


// TODO delete - there is no view here so this is never called
    /*
    @FXML
    private void initialize() {
        gameState.addListener((observable, oldValue, newValue) -> gameStateChanged(newValue));
    }*/
// TODO delete this is never used
/*
    private void gameStateChanged(eGameState newValue) {
        if (newValue == eGameState.STARTED) {

        }
    }
*/
