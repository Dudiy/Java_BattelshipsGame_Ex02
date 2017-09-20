package javaFXUI.model;

import gameLogic.exceptions.CellNotOnBoardException;
import gameLogic.game.board.BoardCell;
import gameLogic.game.board.BoardCoordinates;
import gameLogic.game.eAttackResult;
import gameLogic.users.Player;

import java.awt.*;
import java.time.Instant;

// save the game state BEFORE move attack
public class ReplayGame {
    // board
    private BoardCoordinates positionWasAttacked;
    private boolean myBoardCellAttacked;
    private boolean opponentBoardCellAttacked;
    // statistic
    private Player activePlayer;
    //TODO private Image playerPicture;
    private Integer minesAmount;
    private String currentScore;
    private String averageTimeDuration;
    private String hitNum;
    private String missNum;
    private eAttackResult attackResult = null;

    public ReplayGame(Player activePlayer, BoardCoordinates positionWasAttacked) throws CellNotOnBoardException {
        this.positionWasAttacked=positionWasAttacked;
        this.myBoardCellAttacked = activePlayer.getMyBoard().getBoardCellAtCoordinates(positionWasAttacked).wasAttacked();
        this.opponentBoardCellAttacked = activePlayer.getOpponentBoard().getBoardCellAtCoordinates(positionWasAttacked).wasAttacked();
        this.activePlayer = activePlayer;
        minesAmount = activePlayer.getMyBoard().getMinesAvailable();
        currentScore = String.valueOf(activePlayer.getScore());
        averageTimeDuration = activePlayer.getAvgTurnDuration().toString();
        hitNum = String.valueOf(activePlayer.getTimesHit());
        missNum = String.valueOf(activePlayer.getTimesMissed());
    }
    // ===================================== Setter =====================================
    public void setAttackResult(eAttackResult attackResult) {
        this.attackResult = attackResult;
    }

    // ===================================== Getter =====================================

    public BoardCoordinates getPositionWasAttacked() {
        return positionWasAttacked;
    }

    public boolean isMyBoardCellAttacked() {
        return myBoardCellAttacked;
    }

    public boolean isOpponentBoardCellAttacked() {
        return opponentBoardCellAttacked;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public Integer getMinesAmount() {
        return minesAmount;
    }

    public String getCurrentScore() {
        return currentScore;
    }

    public String getAverageTimeDuration() {
        return averageTimeDuration;
    }

    public String getHitNum() {
        return hitNum;
    }

    public String getMissNum() {
        return missNum;
    }
}
