package gameLogic.game.gameObjects.ship;

import gameLogic.game.board.BoardCoordinates;

public class RegularShip extends AbstractShip {

    public RegularShip(ShipType shipType, BoardCoordinates position, eShipDirection direction) {
        super(shipType, position, direction);
        this.hitsRemainingUntilSunk = shipType.getLength();
    }

    // ======================================= setters =======================================
    @Override
    public void setDirection(String direction) throws Exception {
        eShipDirection inputDirection = eShipDirection.valueOf(direction);
        if (inputDirection.isBasicShipDirection()) {
            this.direction = inputDirection;
        } else {
            throw new Exception("direction " + direction + "is invalid for regular ships");
        }
    }

    // ======================================= getters =======================================
    @Override
    public eShipDirection getDirection() {
        return direction;
    }
}
