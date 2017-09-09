package gameLogic.game.gameObjects.ship;

public enum eShipDirection {
    ROW,
    COLUMN,
    RIGHT_DOWN,
    RIGHT_UP,
    UP_RIGHT,
    DOWN_RIGHT;

    public boolean isBasicShipDirection() {
        return this == ROW || this == COLUMN;
    }

    public boolean isLShapeShipDirection() {
        return this == RIGHT_DOWN ||
                this == RIGHT_UP ||
                this == UP_RIGHT ||
                this == DOWN_RIGHT;
    }
}
