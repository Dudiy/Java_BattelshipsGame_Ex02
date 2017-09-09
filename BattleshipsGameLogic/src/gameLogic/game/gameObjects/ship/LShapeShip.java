package gameLogic.game.gameObjects.ship;

import gameLogic.game.board.BoardCoordinates;

public class LShapeShip extends AbstractShip {
    public LShapeShip(int length, BoardCoordinates position, eShipDirection direction, int score) {
        super(length, position, direction, score);
    }
    // ======================================= setters =======================================

    @Override
    public void setDirection(String direction) throws Exception {
        eShipDirection inputDirection = eShipDirection.valueOf(direction);
        if (!inputDirection.isLShapeShipDirection()) {
            this.direction = inputDirection;
        } else {
            throw new Exception("direction " + direction + "is invalid for L shaped ships");
        }
    }

    // ======================================= getters =======================================
    @Override
    public eShipDirection getDirection() {
        return direction;
    }
}
