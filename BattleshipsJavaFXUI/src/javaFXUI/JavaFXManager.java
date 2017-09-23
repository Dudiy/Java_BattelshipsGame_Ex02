package javaFXUI;

import gameLogic.*;
import gameLogic.exceptions.*;
import gameLogic.game.*;
import gameLogic.game.board.BoardCell;
import gameLogic.game.board.BoardCoordinates;
import gameLogic.game.gameObjects.Mine;
import gameLogic.game.gameObjects.ship.AbstractShip;
import gameLogic.users.*;
import javaFXUI.model.AlertHandlingUtils;
import javaFXUI.model.ImageViewProxy;
import javaFXUI.model.PlayerAdapter;
import javaFXUI.model.ReplayGame;
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
import java.util.LinkedList;
import java.util.List;

public class JavaFXManager extends Application {
    // ============== stages and layouts ==============
    private Stage primaryStage;
    private final Stage secondaryStage = new Stage();
    private final Stage playerInitializerStage = new Stage();
    private AnchorPane welcomeScreen;
    private BorderPane mainWindowLayout;
    private Scene mainWindowScene;
    //    private Scene welcomeScreenScene;
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
    private LinkedList<ReplayGame> previousMoves;
    private LinkedList<ReplayGame> nextMoves;
    private int currReplayIndex;
    private ReplayGame.eReplayStatus lastReplayCommand;

    // ===================================== Init =====================================
    static void Run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Battleships by Or and Dudi");
        this.primaryStage.getIcons().add(new Image(Constants.GAME_ICON_URL));
        initMainWindow();
        initPlayerInitializerWindow();
        initPauseWindow();
        previousMoves = new LinkedList<>();
        nextMoves = new LinkedList<>();
    }

    private void initMainWindow() {
        try {
            loadMainWindow();
            loadRightPane();
            mainWindowScene = new Scene(mainWindowLayout);

            loadWelcomeScreen();
            Scene scene = new Scene(welcomeScreen);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image(JavaFXManager.class.getResourceAsStream(Constants.GAME_ICON_IMAGE_URL)));
            primaryStage.show();
        } catch (IOException e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while loading main window");
        }
    }

    private void loadMainWindow() throws IOException {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/MainWindow.fxml"));
        mainWindowLayout = fxmlLoader.load();
        mainWindowController = fxmlLoader.getController();
        mainWindowController.setJavaFXManager(this);
        resetGameEvent.add(mainWindowController::resetGame);
        resetGameEvent.add(() -> {
            previousMoves.clear();
            nextMoves.clear();
            mainWindowController.setReplayMode(false);
            currentTurnInfoController.setReplayMode(false);
        });
    }

    private void loadRightPane() throws IOException {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/LayoutCurrentTurnInfo.fxml"));
        ScrollPane rightPane = fxmlLoader.load();
        currentTurnInfoController = fxmlLoader.getController();
        currentTurnInfoController.setJavaFXManager(this);
        mainWindowLayout.setRight(rightPane);
    }

    private void loadWelcomeScreen() throws IOException {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/WelcomeScene.fxml"));
        welcomeScreen = fxmlLoader.load();
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
            secondaryStage.setOnCloseRequest(event -> exitGame());
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

    // ===================================== Load Game =====================================
    public void loadGame(String xmlFilePath) throws LoadException {
        Game loadedGame = gamesManager.loadGameFile(xmlFilePath);
        activeGame.setValue(loadedGame);
        gameState.setValue(loadedGame.getGameState());
    }

    // ===================================== Start Game =====================================
    public void startGame() {
        try {
            gameState.setValue(activeGame.getValue().getGameState());
            if (gameState.getValue() != eGameState.LOADED) {
                // set the game to be as if it was just started
                resetGame(activeGame.getValue());
            }
            // TODO use task to show "Loading game"
            // TODO del
//            primaryStage.setScene(new Scene(mainWindowLayout));
            primaryStage.setScene(mainWindowScene);
            currentTurnInfoController.setReplayMode(false);
            // TODO select player names....and pictures?!
            Game activeGame = this.activeGame.getValue();
            // TODO add computer player?! NO !!
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
    public eAttackResult makeMove(ImageViewProxy cellAsImageView) {
        eAttackResult attackResult = null;
        try {
            Game activeGame = this.activeGame.getValue();
            BoardCoordinates coordinatesOfTheCell = BoardCoordinates.Parse(cellAsImageView.getId());
            updateActivePlayer();
            ReplayGame replayMove = new ReplayGame(activePlayer.getValue(), coordinatesOfTheCell);
            attackResult = gamesManager.makeMove(activeGame, coordinatesOfTheCell);
            // BEFORE move save to previous replay
            if (attackResult != eAttackResult.CELL_ALREADY_ATTACKED) {
                previousMoves.addLast(replayMove);
            }
            updateActivePlayerAttackResult(cellAsImageView, attackResult);
            updateActivePlayer();
            // AFTER move save to next replay
            gameState.setValue(activeGame.getGameState());
            if (gameState.getValue() != eGameState.PLAYER_QUIT) {
                if (attackResult != eAttackResult.CELL_ALREADY_ATTACKED) {
                    replayMove = new ReplayGame(activePlayer.getValue(), coordinatesOfTheCell);
                    nextMoves.addLast(replayMove);
                    replayMove.setAttackResult(attackResult);
                }
            }
            if (gameState.getValue() == eGameState.PLAYER_WON) {
                endGame(true);
            }
        } catch (CellNotOnBoardException e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while making move");
        }

        return attackResult;
    }

    private void updateActivePlayerAttackResult(ImageViewProxy cellAsImageView, eAttackResult attackResult) throws CellNotOnBoardException {
        Game activeGame = this.activeGame.getValue();
        // cell update
        cellAsImageView.updateImageWithTransition();
        // statistic update
        currentTurnInfoController.attackResultUpdated(attackResult);
        if (attackResult.moveEnded() || activeGame.getGameState() == eGameState.PLAYER_WON) {
            Duration turnTime = Duration.between(turnPlayerTimer, Instant.now());
            activePlayer.getValue().addTurnDurationToTotal(turnTime);
        }
        totalMovesCounter.setValue(activeGame.getMovesCounter());

        Alert moveResult = new Alert(Alert.AlertType.INFORMATION, attackResult.toString()
                + "\nPress enter OR space to continue");
        moveResult.showAndWait();
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

    // ===================================== Replay Mode =====================================
    public void startReplay() {
        // it is more than the max index
        currReplayIndex = previousMoves.size() - 1;
        mainWindowController.setReplayMode(true);
        currentTurnInfoController.setReplayMode(true);
        hidePauseMenu();
        // because move stop within move attack
        if (activeGame.getValue().getGameState() == eGameState.PLAYER_QUIT) {
            swapPlayer();
        }
        if (previousMoves.isEmpty()) {
            currentTurnInfoController.setEnablePreviousReplay(false);
        }
        currentTurnInfoController.setEnableNextReplay(false);
        if(currReplayIndex>=0){
            currentTurnInfoController.updateReplayMove(nextMoves.peekLast(), lastReplayCommand);
        }
        lastReplayCommand = ReplayGame.eReplayStatus.END_LIST;
    }

    public void replayLastMove() {
        ReplayGame currentMoveBeforeAttack = previousMoves.get(currReplayIndex);
        ReplayGame previousMoveAfterAttack = null;

        setReplayActivePlayer(currentMoveBeforeAttack);
        updateLastMoveChanges(currentMoveBeforeAttack);
        if (currReplayIndex > 0) {
            currReplayIndex--;
            previousMoveAfterAttack = nextMoves.get(currReplayIndex);
            setReplayActivePlayer(previousMoveAfterAttack);
            updateLastMoveChanges(previousMoveAfterAttack);
            lastReplayCommand = ReplayGame.eReplayStatus.PREV;
        } else {
            currentTurnInfoController.setEnablePreviousReplay(false);
            lastReplayCommand = ReplayGame.eReplayStatus.START_LIST;
        }
        currentTurnInfoController.setEnableNextReplay(true);
        mainWindowController.redrawBoards(activePlayer.getValue());
    }

    private void setReplayActivePlayer(ReplayGame replayMove) {
        if (activePlayer.getValue() != replayMove.getActivePlayer()) {
            swapPlayer();
        }
    }

    private void swapPlayer() {
        activeGame.getValue().swapPlayers();
        updateActivePlayer();
    }

    private void updateLastMoveChanges(ReplayGame replayMoveBack) {
        boardsReplayChanges(replayMoveBack, ReplayGame.eReplayStatus.PREV);
        statisticReplayChanges(replayMoveBack);
    }

    private void boardsReplayChanges(ReplayGame replayMove, ReplayGame.eReplayStatus replayStatus) {
        try {
            BoardCoordinates positionThatAttacked = replayMove.getPositionWasAttacked();
            BoardCell myBoardCell = activePlayer.getValue().getMyBoard().getBoardCellAtCoordinates(positionThatAttacked);
            myBoardCell.setWasAttacked(replayMove.isMyBoardCellAttacked());
            BoardCell opponentBoardCell = activePlayer.getValue().getOpponentBoard().getBoardCellAtCoordinates(positionThatAttacked);
            opponentBoardCell.setWasAttacked(replayMove.isOpponentBoardCellAttacked());
            if (opponentBoardCell.getCellValue() instanceof AbstractShip) {
                replayShipCell(replayMove, replayStatus, opponentBoardCell);
            } else if (opponentBoardCell.getCellValue() instanceof Mine && myBoardCell.getCellValue() instanceof AbstractShip) {
                if (replayStatus == ReplayGame.eReplayStatus.PREV) {
                    ((AbstractShip) myBoardCell.getCellValue()).increaseHitsRemainingUntilSunk();
                } else if (replayStatus == ReplayGame.eReplayStatus.NEXT) {
                    ((AbstractShip) myBoardCell.getCellValue()).decreaseHitsRemainingUntilSunk();
                }
            }
        } catch (CellNotOnBoardException e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while try to get previous move");
        }
    }

    private void replayShipCell(ReplayGame replayMove, ReplayGame.eReplayStatus replayStatus, BoardCell opponentBoardCell) {
        AbstractShip ship = (AbstractShip) opponentBoardCell.getCellValue();
        Player opponentPlayer = activeGame.getValue().getOtherPlayer();
        boolean prev = replayStatus == ReplayGame.eReplayStatus.PREV || replayStatus == ReplayGame.eReplayStatus.START_LIST;
        boolean next = replayStatus == ReplayGame.eReplayStatus.NEXT || replayStatus == ReplayGame.eReplayStatus.END_LIST;
        // when update BEFORE state
        if (replayMove.getAttackResult() == null) {
            if (prev) {
                if (ship.getHitsRemainingUntilSunk() == 0) {
                    opponentPlayer.OnShipComeBackToLife(ship);
                }
                ship.increaseHitsRemainingUntilSunk();
            }
        } else {
            if (next) {
                ship.decreaseHitsRemainingUntilSunk();
                if (ship.getHitsRemainingUntilSunk() == 0) {
                    opponentPlayer.OnShipSunk(ship);
                }
            }
        }
        currentTurnInfoController.updateShipsRemainingTable();
    }

    private void statisticReplayChanges(ReplayGame replayMove) {
        activePlayer.getValue().getMyBoard().setMinesAvailable(replayMove.getMinesAmount());
        activePlayer.getValue().setScore(replayMove.getCurrentScore());
        activePlayer.getValue().setTotalTurnsDuration(replayMove.getTotalPlayerTurnsDuration());
        activePlayer.getValue().setNumTurnsPlayed(replayMove.getNumPlayerTurnsPlayed());
        activePlayer.getValue().setTimesHit(replayMove.getHitNum());
        activePlayer.getValue().setTimesMissed(replayMove.getMissNum());
        currentTurnInfoController.updateReplayMove(replayMove, lastReplayCommand);
    }

    public void replayNextMove() {
        ReplayGame nextMoveAfterAttack;
        if (lastReplayCommand != ReplayGame.eReplayStatus.START_LIST) {
            currReplayIndex++;
            nextMoveAfterAttack = nextMoves.get(currReplayIndex);
        } else {
            nextMoveAfterAttack = nextMoves.get(currReplayIndex);
        }
        if (currReplayIndex < nextMoves.size() - 1) {
            lastReplayCommand = ReplayGame.eReplayStatus.NEXT;
        } else {
            currentTurnInfoController.setEnableNextReplay(false);
            lastReplayCommand = ReplayGame.eReplayStatus.END_LIST;
        }
        setReplayActivePlayer(nextMoveAfterAttack);
        updateNextMoveChanges(nextMoveAfterAttack);
        currentTurnInfoController.setEnablePreviousReplay(true);
        mainWindowController.redrawBoards(activePlayer.getValue());
    }

    private void updateNextMoveChanges(ReplayGame replayMoveForward) {
        boardsReplayChanges(replayMoveForward, ReplayGame.eReplayStatus.NEXT);
        statisticReplayChanges(replayMoveForward);
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
                activeGame.setGameState(eGameState.PLAYER_WON);
                gameState.setValue(eGameState.PLAYER_WON);
                message.append("Congratulations!" + activeGame.getWinnerPlayer().getName() + "has won the game!");
            } else {
                activeGame.setGameState(eGameState.PLAYER_QUIT);
                gameState.setValue(eGameState.PLAYER_QUIT);
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
        showPauseMenu();
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
        if(!secondaryStage.isShowing()){
            secondaryStage.showAndWait();
        }
    }

    public void hidePauseMenu() {
        secondaryStage.hide();
    }

    private void resetGame(Game activeGame) {
        try {
            activeGame.resetGame();
            gameState.setValue(activeGame.getGameState());
            resetGameEvent.forEach(Runnable::run);
//            }
        } catch (Exception e) {
            AlertHandlingUtils.showErrorMessage(e, "Error while reloading the game, Please load the file again or choose another file", e.getMessage());
            activeGame.setGameState(eGameState.INVALID);
            gameState.setValue(activeGame.getGameState());
        }
    }
}
