package javaFXUI.model;

import gameLogic.exceptions.CellNotOnBoardException;
import gameLogic.game.board.Board;
import gameLogic.game.board.BoardCell;
import gameLogic.game.board.BoardCoordinates;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

public class BoardAdapter {
    private final int BOARD_WIDTH = 400;
    private Board board;

    private TilePane boardAsTilePane;
    private boolean isVisible;

    public TilePane getBoardAsTilePane() {
        return boardAsTilePane;
    }

    public BoardAdapter(Board board, boolean isVisible) {
        this.board = board;
        this.isVisible = isVisible;
        generateTilePane();
    }

    private void generateTilePane() {
        boardAsTilePane = new TilePane();
        int boardSize = board.getBoardSize();
        int cellSize = BOARD_WIDTH / boardSize;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                BoardCoordinates coordinates = BoardCoordinates.Parse(i, j);
                try {
                    addCellToTilePane(board.getBoardCellAtCoordinates(coordinates), cellSize, boardAsTilePane);
                } catch (CellNotOnBoardException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addCellToTilePane(BoardCell boardCell, int cellSize, TilePane tilePane) throws Exception {
        if (boardCell != null) {
            ImageViewProxy boardCellAsImageView = new ImageViewProxy(boardCell, cellSize, isVisible);
            tilePane.getChildren().add(boardCellAsImageView);
        } else {
            throw new Exception("input boardCell was not initialized");
        }
    }

    public static void updateImages(TilePane tilePane){
        for (Node image : tilePane.getChildren()){
            if (image instanceof ImageViewProxy){
                ((ImageViewProxy) image).updateImage();
            }
        }
    }
}
