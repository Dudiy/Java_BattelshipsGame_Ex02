package javaFXUI;

import gameLogic.game.eGameState;

import java.util.EnumSet;

public enum eButtonOption {
    LOAD_GAME(EnumSet.of(eGameState.INVALID, eGameState.INITIALIZED, eGameState.LOADED)),
    START_GAME(EnumSet.of(eGameState.INITIALIZED, eGameState.LOADED)),
    END_GAME(EnumSet.of(eGameState.STARTED)),
    CONTINUE_GAME(EnumSet.of(eGameState.STARTED)),
    EXIT(EnumSet.allOf(eGameState.class));

    private EnumSet<eGameState> displayedConditions;

    eButtonOption(EnumSet<eGameState> displayConditions) {
        this.displayedConditions = displayConditions;
    }

    public Boolean isVisibleAtGameState(eGameState gameState){
        return displayedConditions.contains(gameState);
    }
}
