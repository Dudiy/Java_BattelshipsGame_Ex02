package javaFXUI.model;

import gameLogic.game.board.BoardCell;
import gameLogic.game.gameObjects.GameObject;
import gameLogic.game.gameObjects.Mine;
import gameLogic.game.gameObjects.Water;
import gameLogic.game.gameObjects.ship.AbstractShip;
import javaFXUI.Constants;
import javafx.animation.RotateTransition;
import javafx.geometry.Point3D;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.util.Duration;
import javafx.scene.input.TransferMode;

public class ImageViewProxy extends ImageView {
    private BoardCell boardCell;
    private int cellSize;
    // it is stronger then the rules of getImageForCell()
    private Image irregularImageCase = null;
    private boolean isVisible;
    //TODO put in manager
    private static final Image WATER_IMAGE = new Image(Constants.WATER_IMAGE_URL);
    private static final Image SHIP_IMAGE = new Image(Constants.SHIP_IMAGE_URL);
    private static final Image MINE_IMAGE = new Image(Constants.MINE_ON_WATER_IMAGE_URL);
    private static final Image HIT_IMAGE = new Image(Constants.HIT_IMAGE_URL);
    private static final Image MISS_IMAGE = new Image(Constants.MISS_IMAGE_URL);
    private static final Image PROBLEM_IMAGE = new Image(Constants.PROBLEM_IMAGE_URL);
    private static DataFormat boardCellAsImageDataFormat = new DataFormat("ImageViewProxy");

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
            setOnDragOver(event -> {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                event.consume();
            });
            setOnDragEntered(event -> {
                mouseEnteredCell();
                event.consume();
            });
            setOnDragExited(event -> {
                setEffect(null);
                event.consume();
            });
            setOnDragDropped(event -> {
                ClipboardContent content = new ClipboardContent();
                content.put(boardCellAsImageDataFormat, this);
                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
                event.consume();
            });
        }
    }

    public void setIrregularImageCase(Image irregularImageCase) {
        this.irregularImageCase = irregularImageCase;
    }

    public BoardCell getBoardCell() {
        return boardCell;
    }

    private Image getImageForCell() {
        Image imageToReturn;
        GameObject cellValue = boardCell.getCellValue();

        if(irregularImageCase == null){
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
        }else{
            imageToReturn= irregularImageCase;
        }

        return imageToReturn;
    }

    private void mouseEnteredCell() {
        ColorAdjust highlight = new ColorAdjust();
        highlight.setBrightness(0.5);
        setEffect(highlight);
    }

//    private void minePlaced() {
//        try {
//            boardCell.setCellValue(new Mine(boardCell.getPosition()));
//            setImage(MINE_IMAGE);
//        } catch (InvalidGameObjectPlacementException e) {
//            AlertHandlingUtils.showErrorMessage(e,"Error while put mine");
//        }
//    }

    public void updateImage(){
        setImage(getImageForCell());
    }

    public void updateImageWithTransition() {
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(2000), this);
        rotateTransition.setAxis(new Point3D(0, 1, 0));
        rotateTransition.setByAngle(360);
        Thread rotateThread = new Thread(() -> rotateTransition.play());
        rotateTransition.setOnFinished(event -> {
            updateImage();
            setMouseTransparent(false);
        });
        setMouseTransparent(true);
        rotateThread.start();
    }

}
