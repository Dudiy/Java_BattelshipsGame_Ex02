package javaFXUI.model;

import gameLogic.game.board.BoardCell;
import gameLogic.game.gameObjects.GameObject;
import gameLogic.game.gameObjects.Mine;
import gameLogic.game.gameObjects.Water;
import gameLogic.game.gameObjects.ship.AbstractShip;
import javaFXUI.Constants;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewProxy extends ImageView {
    private BoardCell boardCell;
    private int cellSize;
    private boolean isVisible;
    //TODO put in manager
    private static final Image WATER_IMAGE = new Image(Constants.WATER_IMAGE_URL);
    private static final Image SHIP_IMAGE = new Image(Constants.SHIP_IMAGE_URL);
    private static final Image MINE_IMAGE = new Image(Constants.MINE_ON_WATER_IMAGE_URL);
    private static final Image HIT_IMAGE = new Image(Constants.HIT_IMAGE_URL);
    private static final Image MISS_IMAGE = new Image(Constants.MISS_IMAGE_URL);
    private static final Image PROBLEM_IMAGE = new Image(Constants.PROBLEM_IMAGE_URL);

    public ImageViewProxy(BoardCell boardCell, int cellSize, boolean isVisible) {
        this.boardCell = boardCell;
        this.cellSize = cellSize;
        this.isVisible = isVisible;
        setFitHeight(cellSize);
        setFitWidth(cellSize);
        setImage(getImageForCell());
        setId(boardCell.getPosition().toString());
        if (!isVisible) {
            setOnMouseEntered(event -> mouseEnteredCell());
            setOnMouseExited(event -> setEffect(null));
        } else {
            setOnMouseDragEntered(event -> minePlaced());
        }
    }

    private Image getImageForCell() {
        Image imageToReturn;
        GameObject cellValue = boardCell.getCellValue();

        if (boardCell.wasAttacked()) {
            if (cellValue instanceof Water) {
                imageToReturn = MISS_IMAGE;
            } else if (cellValue instanceof Mine || cellValue instanceof AbstractShip) {
                imageToReturn = HIT_IMAGE;
            } else {
                imageToReturn = PROBLEM_IMAGE;
            }
        } else {
            if (cellValue instanceof AbstractShip) {
                imageToReturn = isVisible ? SHIP_IMAGE : WATER_IMAGE;
            } else if (cellValue instanceof Water) {
                imageToReturn = WATER_IMAGE;
            } else if (cellValue instanceof Mine) {
                imageToReturn = isVisible ? MINE_IMAGE : WATER_IMAGE;
            } else {
                imageToReturn = PROBLEM_IMAGE;
            }
        }

        return imageToReturn;
    }

    private void mouseEnteredCell() {
        ColorAdjust highlight = new ColorAdjust();
        highlight.setBrightness(0.5);
        setEffect(highlight);
    }

    private void minePlaced() {
        setImage(MINE_IMAGE);
    }

    public void updateImage(){
        setImage(getImageForCell());
    }


}
