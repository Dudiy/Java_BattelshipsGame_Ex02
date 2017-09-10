package javaFXUI.model;

import gameLogic.game.board.BoardCell;
import gameLogic.game.gameObjects.GameObject;
import gameLogic.game.gameObjects.Mine;
import gameLogic.game.gameObjects.Water;
import gameLogic.game.gameObjects.ship.AbstractShip;
import javafx.event.EventHandler;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BoardCellAdapter  {
    private BoardCell boardCell;
    private boolean isVisible;
    private final String BASE_IMAGE_URL = "/resources/images";
    private final Image WATER_IMAGE = new Image(BASE_IMAGE_URL + "/Water.png");
    private final Image SHIP_IMAGE = new Image(BASE_IMAGE_URL + "/Ship.jpg");
    private final Image BOMB_IMAGE = new Image(BASE_IMAGE_URL + "/Bomb.png");
    private final Image HIT_IMAGE = new Image(BASE_IMAGE_URL + "/Hit.png");
    private final Image MISS_IMAGE = new Image(BASE_IMAGE_URL + "/Miss.png");
    private final Image PROBLEM_IMAGE = new Image(BASE_IMAGE_URL + "/Problem.png");
    private EventHandler CellClicked;

    public BoardCellAdapter(BoardCell boardCell, boolean isVisible) {
        this.boardCell = boardCell;
        this.isVisible = isVisible;
    }

    public ImageView getImageView(int cellSize) {
        ImageView imageView = new javafx.scene.image.ImageView();

        imageView.setFitHeight(cellSize);
        imageView.setFitWidth(cellSize);
        imageView.setImage(getImageForCell());
        imageView.setId(boardCell.getPosition().toString());
        if (!isVisible) {
            imageView.setOnMouseEntered(event -> mouseEnteredCell(imageView));
            imageView.setOnMouseExited(event -> imageView.setEffect(null));
            imageView.setOnMouseClicked(event -> cellClicked(imageView));
        }
        else{
            imageView.setOnMouseDragEntered(event -> minePlaced(imageView));
        }

        return imageView;
    }

    private void cellClicked(ImageView imageView) {
        CellClicked.notify();
    }

    private void minePlaced(ImageView imageView) {
        imageView.setImage(BOMB_IMAGE);
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
                imageToReturn = isVisible ? BOMB_IMAGE : WATER_IMAGE;
            } else {
                imageToReturn = PROBLEM_IMAGE;
            }
        }

        return imageToReturn;
    }

    private void mouseEnteredCell(javafx.scene.image.ImageView cell) {
        ColorAdjust highlight = new ColorAdjust();
        highlight.setBrightness(0.5);

        cell.setEffect(highlight);
    }

}
