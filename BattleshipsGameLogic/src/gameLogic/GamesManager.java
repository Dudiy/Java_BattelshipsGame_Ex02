package gameLogic;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.LoadException;
import gameLogic.exceptions.*;
import gameLogic.game.board.BoardCoordinates;
import gameLogic.game.Game;
import gameLogic.game.GameSettings;
import gameLogic.game.eGameState;
import gameLogic.users.Player;
import gameLogic.game.eAttackResult;

public class GamesManager implements IGamesLogic {
    private Map<String, Player> allPlayers = new HashMap<>();
    private Map<Integer, Game> allGames = new HashMap<>();

    private void addPlayers(Player[] players) {
        allPlayers.put(players[0].getID(), players[0]);
        allPlayers.put(players[1].getID(), players[1]);
    }

    @Override
    public Game loadGameFile(String path) throws LoadException {
        GameSettings gameSettings = GameSettings.loadGameFile(path);
        Game newGame = new Game(gameSettings);
        allGames.put(newGame.getID(), newGame);

        return newGame;
    }

    @Override
    public void startGame(Game gameToStart, Player player1, Player player2) throws Exception {
        gameToStart.initGame(player1, player2);
        gameToStart.setGameState(eGameState.STARTED);
    }

    @Override
    public eAttackResult makeMove(Game game, BoardCoordinates cellToAttack) throws CellNotOnBoardException {
        return game.attack(cellToAttack);
    }

    @Override
    public Duration getGameDuration(Game game) {
        return game.getTotalGameDuration();
    }


    @Override
    public void plantMine(Game game, BoardCoordinates cell) throws CellNotOnBoardException, InvalidGameObjectPlacementException, NoMinesAvailableException {
        game.plantMineOnActivePlayersBoard(cell);
    }

    @Override
    public void saveGameToFile(Game game, String fileName) throws Exception {
        Game.saveToFile(game, fileName);
    }

    @Override
    public Game loadSavedGameFromFile(String fileName) throws Exception {
        Game game = Game.loadFromFile(fileName);
        allGames.put(game.getID(), game);
        addPlayers(game.getPlayers());
        game.setGameState(eGameState.STARTED);
        return game;
    }

    @Override
    public void endGame(Game game) {
        game.activePlayerForfeit();
    }
}
