package gameLogic.game;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import javafx.fxml.LoadException;
import jaxb.generated.BattleShipGame;
import gameLogic.users.*;
import gameLogic.exceptions.*;
import gameLogic.game.board.*;
import gameLogic.game.gameObjects.ship.*;
import gameLogic.game.gameObjects.*;

public class Game implements Serializable {
    private static int IDGenerator = 1000;
    private final int ID;
    private int activePlayerIndex;
    private int movesCounter;
    private Player winner;
    private Player[] players = new Player[2];
    private Instant gameStartTime;
    private ShipFactory shipFactory;
    private eGameState gameState = eGameState.INVALID;
    private GameSettings gameSettings;

    public Game(GameSettings gameSettings) {
        this.ID = IDGenerator++;
        this.gameSettings = gameSettings;
        shipFactory = new ShipFactory(gameSettings);
        gameState = eGameState.LOADED;
    }

    // ======================================= setters =======================================

    public void setGameState(eGameState gameState) {
        this.gameState = gameState;
    }

    public void setGameStartTime(Instant startTime) {
        this.gameStartTime = startTime;
    }

    // ======================================= getters =======================================
    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public int getBoardSize() {
        return gameSettings.getBoardSize();
    }

    public int getID() {
        return ID;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Player getActivePlayer() {
        return players[activePlayerIndex];
    }

    public int getMovesCounter() {
        return movesCounter;
    }

    public Player getOtherPlayer() {
        return players[(activePlayerIndex + 1) % 2];
    }

    public Player getWinnerPlayer() {
        return winner;
    }

    public eGameState getGameState() {
        return gameState;
    }

    public Duration getTotalGameDuration() {
        return Duration.between(gameStartTime, Instant.now());
    }

    // ======================================= Methods =======================================
    public void initGame(Player player1, Player player2) throws Exception {
        if (player1 == null || player2 == null) {
            throw new Exception("Players cannot be null");
        }

        activePlayerIndex = 0;
        movesCounter = 0;
        players[0] = player1;
        players[1] = player2;
        initBoards();
        gameState = eGameState.INITIALIZED;
        gameStartTime = Instant.now();
    }

    private void initBoards() throws Exception {
        int currentBoardIndex = 0;
        BattleShipGame gameLoadedFromXml = gameSettings.getGameLoadedFromXml();

        if (gameLoadedFromXml == null) {
            // game was loaded from save game and not a file
            throw new LoadException("This game was loaded from a file, no data from the original xml is available for resetting");
        } else {
            for (BattleShipGame.Boards.Board board : gameLoadedFromXml.getBoards().getBoard()) {
                Board currentBoard = addAllShipsToBoard(getActivePlayer(), board);
                players[currentBoardIndex].setMyBoard(currentBoard);
                players[(currentBoardIndex + 1) % 2].setOpponentBoard(currentBoard);
                currentBoardIndex++;
            }
        }
    }

    private Board addAllShipsToBoard(Player currentPlayer, BattleShipGame.Boards.Board board) throws Exception {
        Board currentBoard = new Board(gameSettings.getBoardSize());
        Map<String, Integer> shipAmountsOnBoard = gameSettings.getShipAmountsOnBoard();
        currentBoard.setMinesAvailable(gameSettings.getMinesPerPlayer());

        for (BattleShipGame.Boards.Board.Ship ship : board.getShip()) {
            try {
                AbstractShip shipObject = shipFactory.createShip(ship);
                currentBoard.addShipToBoard(shipObject);
                int currShipTypeAmount = shipAmountsOnBoard.get(ship.getShipTypeId()) - 1;
                shipAmountsOnBoard.put(ship.getShipTypeId(), currShipTypeAmount);
            } catch (InvalidGameObjectPlacementException e) {
                throw e;
            } catch (Exception e) {
                String message = "Error while initializing " + currentPlayer.getName() + "'s board, inner exception: " + e.getMessage();
                throw new Exception(message);
            }
        }

        if (!allRequiredShipsAdded(shipAmountsOnBoard)) {
            throw new InputMismatchException("Error: amount of ships added to " + currentPlayer.getName() + "'s board is invalid.");
        }

        return currentBoard;
    }

    private boolean allRequiredShipsAdded(Map<String, Integer> shipTypes) {
        Boolean allShipsWereAdded = true;

        for (Map.Entry<String, Integer> shipType : shipTypes.entrySet()) {
            if (shipType.getValue() != 0) {
                allShipsWereAdded = false;
                break;
            }
        }

        return allShipsWereAdded;
    }

    public eAttackResult attack(BoardCoordinates position) throws CellNotOnBoardException {
        eAttackResult attackResult = getActivePlayer().attack(position);

        if (attackResult == eAttackResult.HIT_AND_SUNK_SHIP) {
            AbstractShip shipSunk = (AbstractShip) getOtherPlayer().getMyBoard().getBoardCellAtCoordinates(position).getCellValue();
            getOtherPlayer().OnShipSunk(shipSunk);
            getActivePlayer().addToScore(shipSunk.getScore());
            if (playerSunkAllShips(getActivePlayer())) {
                activePlayerWon();
            }
        } else if (attackResult == eAttackResult.HIT_MINE) {
            BoardCell myCell = getActivePlayer().getMyBoard().getBoardCellAtCoordinates(position);
            GameObject gameObjectMineHit = myCell.getCellValue();

            if (gameObjectMineHit instanceof Mine) {
                myCell.removeGameObjectFromCell();
//                ((Mine)gameObjectMineHit).setExplosionResult(eAttackResult.HIT_MINE);
            } else if (gameObjectMineHit instanceof AbstractShip) {
                if (((AbstractShip) gameObjectMineHit).getHitsRemainingUntilSunk() == 0) {
                    AbstractShip shipSunk = (AbstractShip) getActivePlayer().getMyBoard().getBoardCellAtCoordinates(position).getCellValue();
                    getActivePlayer().OnShipSunk(shipSunk);
                    getOtherPlayer().addToScore(shipSunk.getScore());
                    if (playerSunkAllShips(getOtherPlayer())) {
                        otherPlayerWon();
                    }
                }
            }
        }

        if (attackResult != eAttackResult.CELL_ALREADY_ATTACKED) {
            movesCounter++;
        }
        if (attackResult.moveEnded()) {
            swapPlayers();
        }

        return attackResult;
    }

    public void swapPlayers() {
        activePlayerIndex = (activePlayerIndex + 1) % 2;
    }

    public void plantMineOnActivePlayersBoard(BoardCoordinates cell) throws CellNotOnBoardException, InvalidGameObjectPlacementException, NoMinesAvailableException {
        getActivePlayer().plantMine(cell);
        swapPlayers();
    }

    private boolean playerSunkAllShips(Player player) {
        return player.getOpponentBoard().allShipsWereSunk();
    }

    public void activePlayerForfeit() {
        winner = getOtherPlayer();
        gameState = eGameState.PLAYER_QUIT;
    }

    private void activePlayerWon() {
        winner = getActivePlayer();
        gameState = eGameState.PLAYER_WON;
    }

    private void otherPlayerWon() {
        winner = getActivePlayer();
        gameState = eGameState.PLAYER_WON;
    }

    public void resetGame() throws Exception {
        activePlayerIndex = 0;
        try {
            initBoards();
        } catch (Exception e) {
            throw new Exception("Error while resetting game. internal error: " + e.getMessage());
        }
        winner = null;
        gameState = eGameState.INITIALIZED;
        gameStartTime = Instant.now();
    }

    public static void saveToFile(Game game, final String fileName) throws Exception {
        File file = new File(fileName);

        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new Exception("\"Saved Games\" directory was not found and could not be created");
            }
        }

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             new FileOutputStream(fileName))) {
            outputStream.writeObject(game);
            outputStream.flush();
        }
    }

    public static Game loadFromFile(final String fileName) throws Exception {
        Game game;

        try (ObjectInputStream inStream =
                     new ObjectInputStream(
                             new FileInputStream(fileName))) {
            game = (Game) inStream.readObject();
        }

        return game;
    }

    public int getSizeOfMinimalShipOnBoard() {
        return gameSettings.getMinimalShipSize();
    }
}