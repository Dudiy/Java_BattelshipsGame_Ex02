package javaFXUI.view;

import gameLogic.game.eGameState;
import javaFXUI.JavaFXManager;
import javaFXUI.eButtonOption;
import javaFXUI.model.AlertHandlingUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.LoadException;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;

public class PauseWindowController {
    private JavaFXManager program;

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
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);

        // Show open file dialog
        File file = fileChooser.showOpenDialog(program.getSecondaryStage());

        if (file != null) {
            try {
                program.setActiveGame(program.getGamesManager().loadGameFile(file.getPath()));

            } catch (LoadException e) {
                AlertHandlingUtils.showErrorMessage(e,"Error while loading xml file");
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
    }

    @FXML
    public void buttonExitApplication_Clicked(ActionEvent actionEvent) {
    }

    public void setProgram(JavaFXManager program) {
        this.program = program;
    }

    public void updateButtonState(eGameState gameState){
        buttonLoadGameFromXML.setDisable(!eButtonOption.LOAD_GAME.isVisibleAtGameState(gameState));
        buttonStartGame.setDisable(!eButtonOption.START_GAME.isVisibleAtGameState(gameState));
        buttonEndGame.setDisable(!eButtonOption.END_GAME.isVisibleAtGameState(gameState));
        buttonContinueGame.setDisable(!eButtonOption.CONTINUE_GAME.isVisibleAtGameState(gameState));
        buttonExitApplication.setDisable(!eButtonOption.EXIT.isVisibleAtGameState(gameState));
    }
}
