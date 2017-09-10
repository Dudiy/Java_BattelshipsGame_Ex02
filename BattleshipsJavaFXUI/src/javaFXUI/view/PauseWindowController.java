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
            Game loadedGame = null;
            try {
                loadedGame = program.getGamesManager().loadGameFile(file.getPath());
                program.setActiveGame(loadedGame);
            } catch (LoadException e) {
                AlertHandlingUtils.showErrorMessage(e, "Error while loading xml file");
            } finally {
                updateButtonState(
                        loadedGame != null ? loadedGame.getGameState() : eGameState.INVALID);
            }
        }
    }

    @FXML
    void buttonStartGame_Clicked(ActionEvent event) {
        program.startGame();
    }

    @FXML
    void buttonContinueGame_Clicked(ActionEvent event) {
    }

    @FXML
    void buttonEndGame_Clicked(ActionEvent event) {

    }

    @FXML
    public void buttonExitApplication_Clicked(ActionEvent actionEvent) {
        exitApplication();
    }

    private void exitApplication() {
        program.exitGame();
    }

    public void setProgram(JavaFXManager program) {
        this.program = program;
    }

    public void updateButtonState(eGameState gameState) {
        buttonLoadGameFromXML.setDisable(!eButtonOption.LOAD_GAME.isVisibleAtGameState(gameState));
        buttonStartGame.setDisable(!eButtonOption.START_GAME.isVisibleAtGameState(gameState));
        buttonEndGame.setDisable(!eButtonOption.END_GAME.isVisibleAtGameState(gameState));
        buttonContinueGame.setDisable(!eButtonOption.CONTINUE_GAME.isVisibleAtGameState(gameState));
        buttonExitApplication.setDisable(!eButtonOption.EXIT.isVisibleAtGameState(gameState));
    }
}
