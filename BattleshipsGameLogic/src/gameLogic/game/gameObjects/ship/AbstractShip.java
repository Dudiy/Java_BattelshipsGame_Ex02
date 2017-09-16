package gameLogic.game.gameObjects.ship;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import gameLogic.game.board.BoardCoordinates;
import gameLogic.game.gameObjects.GameObject;
import gameLogic.game.eAttackResult;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractShip extends GameObject {
    private ShipType shipType;
    protected int hitsRemainingUntilSunk;
    eShipDirection direction;
    private List<IShipListener> shipListeners = new ArrayList<>();

    // TODO implement in exercise 2
//    private int score;

    AbstractShip(ShipType shipType, BoardCoordinates position, eShipDirection direction) {
        super(position, !VISIBLE);
//        this.length = length;
        this.shipType = shipType;
//        this.hitsRemainingUntilSunk = shipType.getLength();
        this.direction = direction;
//        this.score = score;
    }

    protected abstract void setDirection(String direction) throws Exception;

    // ======================================= getters =======================================

    public int getLength() {
        return shipType.getLength();
    }

    public int getScore() {
        return shipType.getScore();
    }

    public String getID() {
        return shipType.getId();
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
            shipSunk();
        } else if (hitsRemainingUntilSunk > 0) {
            attackResult = eAttackResult.HIT_SHIP;
        } else {
            throw new ValueException("Hits remaining until sunk can't be negative");
        }

        return attackResult;
    }

    public void addShipListener(IShipListener shipListener) {
        shipListeners.add(shipListener);
    }

    public void shipSunk() {
        for (IShipListener shipListener : shipListeners) {
            shipListener.whenShipSunk(this);
        }
    }
}
