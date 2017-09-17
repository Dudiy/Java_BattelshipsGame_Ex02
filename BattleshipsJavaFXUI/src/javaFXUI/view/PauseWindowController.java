package javaFXUI.view;

import gameLogic.game.eGameState;
import javaFXUI.JavaFXManager;
import javaFXUI.model.FileChooserProxy;
import javaFXUI.model.eButtonOption;
import javaFXUI.model.AlertHandlingUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.LoadException;
import javafx.scene.control.Button;

import java.io.File;

public class PauseWindowController {
    private JavaFXManager javaFXManager;

    @FXML
    private Button buttonLoadGameFromXML;

    @FXML
    private Button buttonStartGame;

    @FXML
    private Button buttonEndGame;

    @FXML
    private Button buttonContinueGame;

    @FXML
    private Button buttonExitApplication;

    @FXML
    private void initialize(){
        if (javaFXManager == null || javaFXManager.gameStateProperty().getValue() == null){
            updateButtonState(eGameState.INVALID);
        }
        else{
            updateButtonState(javaFXManager.gameStateProperty().getValue());
        }
    }

    @FXML
    void buttonLoadGameFromXML_Clicked(ActionEvent event) {
        File file = new FileChooserProxy().showOpenDialog(javaFXManager.getSecondaryStage());

        if (file != null) {
            try {
                javaFXManager.loadGame(file.getPath());
            } catch (LoadException e) {
                AlertHandlingUtils.showErrorMessage(e, "Error while loading xml file");
            }
        }
    }

    @FXML
    void buttonStartGame_Clicked(ActionEvent event) {
        javaFXManager.startGame();
    }

    @FXML
    void buttonContinueGame_Clicked(ActionEvent event) {
        javaFXManager.hidePauseMenu();
    }

    @FXML
    void buttonEndGame_Clicked(ActionEvent event) {
        javaFXManager.endGame(false);
    }

    @FXML
    public void buttonExitApplication_Clicked(ActionEvent actionEvent) {
        javaFXManager.exitGame();
    }

    public void setJavaFXManager(JavaFXManager javaFXManager) {
        this.javaFXManager = javaFXManager;
        javaFXManager.gameStateProperty().addListener((observable, oldValue, newValue) -> updateButtonState(newValue));
    }

    public void updateButtonState(eGameState gameState) {
        buttonLoadGameFromXML.setDisable(!eButtonOption.LOAD_GAME.isVisibleAtGameState(gameState));
        buttonStartGame.setDisable(!eButtonOption.START_GAME.isVisibleAtGameState(gameState));
        buttonEndGame.setDisable(!eButtonOption.END_GAME.isVisibleAtGameState(gameState));
        buttonContinueGame.setDisable(!eButtonOption.CONTINUE_GAME.isVisibleAtGameState(gameState));
        buttonExitApplication.setDisable(!eButtonOption.EXIT.isVisibleAtGameState(gameState));
    }
}
