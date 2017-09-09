package javaFXUI.view;

import javaFXUI.JavaFXManager;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class MainWindowController {
    @FXML
    public GridPane gridPaneMyBoard;

    @FXML
    private GridPane gridPaneOpponentsBoard;

    private JavaFXManager program;

    @FXML
    private void initialize() {
        // TODO this doesnt work...add columns and rows
        for (int i = 0; i < program.getActiveGame().getActivePlayer().getMyBoard().getBoardSize() - 5; i++) {
            gridPaneMyBoard.addColumn(i + 5);
            gridPaneMyBoard.addRow(i + 5);
        }
    }

    public void setProgram(JavaFXManager mainApp) {
        this.program = mainApp;
    }


}
