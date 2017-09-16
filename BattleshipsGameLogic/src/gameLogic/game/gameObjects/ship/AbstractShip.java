package gameLogic.game.gameObjects.ship;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import gameLogic.game.board.BoardCoordinates;
import gameLogic.game.gameObjects.GameObject;
import gameLogic.game.eAttackResult;

public abstract class AbstractShip extends GameObject {
    private ShipType shipType;
    private int length;
    private int hitsRemainingUntilSunk;
    eShipDirection direction;
    // TODO implement in exercise 2
    private int score;

    AbstractShip(int length, BoardCoordinates position, eShipDirection direction, int score) {
        super(position, !VISIBLE);
        this.length = length;
        this.hitsRemainingUntilSunk = length;
        this.direction = direction;
        this.score = score;
    }

    protected abstract void setDirection(String direction) throws Exception;

    // ======================================= getters =======================================

    public int getLength() {
        return length;
    }

    public int getScore() {
        return score;
    }

    public abstract eShipDirection getDirection();

    public int getHitsRemainingUntilSunk() {
        return hitsRemainingUntilSunk;
    }

    // ======================================= methods =======================================
    @Override
    public eAttackResult getAttackResult() {
        eAttackResult attackResult;

        hitsRemainingUntilSunk--;
        if (hitsRemainingUntilSunk == 0) {
            attackResult = eAttackResult.HIT_AND_SUNK_SHIP;
        } else if (hitsRemainingUntilSunk > 0) {
            attackResult = eAttackResult.HIT_SHIP;
        } else {
            throw new ValueException("Hits remaining until sunk can't be negative");
        }

        return attackResult;
    }
}
