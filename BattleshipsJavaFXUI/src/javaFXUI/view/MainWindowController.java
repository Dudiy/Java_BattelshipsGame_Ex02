package javaFXUI.view;

import gameLogic.exceptions.CellNotOnBoardException;
import gameLogic.game.Game;
import gameLogic.game.board.Board;
import gameLogic.game.board.BoardCell;
import gameLogic.game.board.BoardCoordinates;
import gameLogic.users.Player;
import javaFXUI.JavaFXManager;
import javaFXUI.model.AlertHandlingUtils;
import javaFXUI.model.BoardCellAdapter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

public class MainWindowController {
    private final int BOARD_WIDTH = 400;
    @FXML
    private TilePane tilePaneMyBoard;

    @FXML
    private TilePane tilePaneOpponentsBoard;

    private JavaFXManager javaFXManager;

    @FXML
    private void initialize() {
//        // TODO this doesnt work...add columns and rows
//        for (int i = 0; i < program.getActiveGame().getActivePlayer().getMyBoard().getBoardSize() - 5; i++) {
//            TableColumn column = new TableColumn("Test");
//            tablViewTest.setEditable(true);
//            tablViewTest.getColumns().add(column);
//        }
    }

    public void setProgram(JavaFXManager javaFXManager) {
        this.javaFXManager = javaFXManager;
        javaFXManager.getActivePlayerProperty().addListener((observable, oldValue, newValue) -> activeGameChanged(newValue));
    }

    private void activeGameChanged(Player activePlayer) {
        try {
            redrawBoards(activePlayer);
        } catch (Exception e) {
            AlertHandlingUtils.showErrorMessage(e,"Error while drawing boards");
        }
    }

    private void redrawBoards(Player activePlayer) throws Exception {
        int boardSize = activePlayer.getMyBoard().getBoardSize();
        int cellSize = BOARD_WIDTH / boardSize;
        Board MyBoard = activePlayer.getMyBoard();
        Board OpponentsBoard = activePlayer.getOpponentBoard();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                BoardCoordinates coordinates = BoardCoordinates.Parse(i, j);
                drawCell(MyBoard.getBoardCellAtCoordinates(coordinates), cellSize, tilePaneMyBoard);
                drawCell(OpponentsBoard.getBoardCellAtCoordinates(coordinates), cellSize, tilePaneOpponentsBoard);
            }
        }
    }

    private void drawCell(BoardCell boardCell, int cellSize, TilePane tilePane) throws Exception {
        if (boardCell != null) {
            BoardCellAdapter boardCellAdapter = new BoardCellAdapter(boardCell, tilePane == tilePaneMyBoard);
            ImageView cell = boardCellAdapter.getImageView(cellSize);
            tilePane.getChildren().add(cell);
        } else {
            throw new Exception("input boardCell was not initialized");
        }
    }
}
