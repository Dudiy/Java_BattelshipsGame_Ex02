package javaFXUI.view;

import gameLogic.game.Game;
import gameLogic.game.eGameState;
import javaFXUI.JavaFXManager;
import javaFXUI.model.FileChooserProxy;
import javaFXUI.model.eButtonOption;
import javaFXUI.model.AlertHandlingUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.LoadException;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.File;

public class PauseWindowController {
    private JavaFXManager program;
    private Stage pauseWindowStage = new Stage();

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
    void buttonLoadGameFromXML_Clicked(ActionEvent event) {
        File file = new FileChooserProxy().showOpenDialog(program.getSecondaryStage());

        if (file != null) {
            try {
                program.loadGame(file.getPath());
            } catch (LoadException e) {
                AlertHandlingUtils.showErrorMessage(e, "Error while loading xml file");
            }
        }
    }

    @FXML
    void buttonContinueGame_Clicked(ActionEvent event) {
    }

    @FXML
    void buttonEndGame_Clicked(ActionEvent event) {

    }

    @FXML
    void buttonStartGame_Clicked(ActionEvent event) {
        program.startGame();
    }

    @FXML
    public void buttonExitApplication_Clicked(ActionEvent actionEvent) {
        program.exitGame();
    }

    public void setProgram(JavaFXManager program) {
        this.program = program;
        program.getGameStateProperty().addListener((observable, oldValue, newValue) -> updateButtonState(newValue));
    }

    public void updateButtonState(eGameState gameState) {
        buttonLoadGameFromXML.setDisable(!eButtonOption.LOAD_GAME.isVisibleAtGameState(gameState));
        buttonStartGame.setDisable(!eButtonOption.START_GAME.isVisibleAtGameState(gameState));
        buttonEndGame.setDisable(!eButtonOption.END_GAME.isVisibleAtGameState(gameState));
        buttonContinueGame.setDisable(!eButtonOption.CONTINUE_GAME.isVisibleAtGameState(gameState));
        buttonExitApplication.setDisable(!eButtonOption.EXIT.isVisibleAtGameState(gameState));
    }
}
