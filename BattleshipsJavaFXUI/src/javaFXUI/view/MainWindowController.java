package javaFXUI.view;

import javaFXUI.JavaFXManager;
import javaFXUI.model.AlertHandlingUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainWindowController {
    private JavaFXManager program;

    @FXML
    private ScrollPane rightPane;

    public ScrollPane getRightPane() {
        if (rightPane == null){
            initRightPane();
        }

        return rightPane;
    }

    public void initRightPane(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/LayoutCurrentTurnInfo.fxml"));
            rightPane = loader.load();
        } catch (IOException e) {
            AlertHandlingUtils.showErrorMessage(e,"Error while loading right pane of main window");
        }
    }

    public void setProgram(JavaFXManager mainApp) {
        this.program = mainApp;
    }
}
