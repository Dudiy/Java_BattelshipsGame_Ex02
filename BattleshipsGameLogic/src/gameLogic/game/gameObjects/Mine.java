package gameLogic.game.gameObjects;

import gameLogic.game.board.BoardCoordinates;
import gameLogic.game.eAttackResult;

public class Mine extends GameObject {
    private eAttackResult explosionResult;

    public Mine(BoardCoordinates position) {
        super(position, !VISIBLE);
    }

    // ======================================= setters =======================================
    public void setExplosionResult(eAttackResult explosionResult) {
        this.explosionResult = explosionResult;
    }

    // ======================================= getters =======================================
    public eAttackResult getExplosionResult() {
        return explosionResult;
    }

    @Override
    public eAttackResult getAttackResult() {
        return eAttackResult.HIT_MINE;
    }
}
