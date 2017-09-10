package javaFXUI.view;

import javaFXUI.JavaFXManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController{
    @FXML
    public GridPane gridPaneMyBoard;
    @FXML
    public TableView tablViewTest;

    @FXML
    private GridPane gridPaneOpponentsBoard;

    private JavaFXManager program;

    @FXML
    private void initialize() {
//        // TODO this doesnt work...add columns and rows
//        for (int i = 0; i < program.getActiveGame().getActivePlayer().getMyBoard().getBoardSize() - 5; i++) {
//            TableColumn column = new TableColumn("Test");
//            tablViewTest.setEditable(true);
//            tablViewTest.getColumns().add(column);
//        }
    }

    public void setProgram(JavaFXManager mainApp) {
        this.program = mainApp;
    }
}
