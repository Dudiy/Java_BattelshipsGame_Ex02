package javaFXUI;

import gameLogic.GamesManager;
import gameLogic.IGamesLogic;
import gameLogic.game.Game;
import javaFXUI.view.MainWindowController;
import javaFXUI.view.PauseWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Scanner;

public class JavaFXManager extends Application {
    private IGamesLogic gamesManager = new GamesManager();
    // JavaFX application may have only 1 game
    private Game activeGame;
    private boolean exitGameSelected = false;
    private int computerPlayerIndex = 0;

    private Stage primaryStage;
    private Stage secondaryStage = new Stage();
    private BorderPane mainWindowLayout;
    private VBox pauseWindowLayout;

    public static void Run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Battleships by Or and Dudi");
        this.primaryStage.getIcons().add(new Image("/resources/images/gameIcon.ico"));
        initMainWindow();
        initPauseWindow();
    }

    private void initMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/MainWindow.fxml"));
            mainWindowLayout = loader.load();

            Scene scene = new Scene(mainWindowLayout);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image(JavaFXManager.class.getResourceAsStream("/resources/images/gameIcon.png")));
            MainWindowController controller = loader.getController();
            controller.setProgram(this);

            primaryStage.show();
        } catch (IOException e) {
            // TODO handle exception
            e.printStackTrace();
        }
    }

    private void initPauseWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(JavaFXManager.class.getResource("/javaFXUI/view/PauseWindow.fxml"));
            pauseWindowLayout = loader.load();

            Scene scene = new Scene(pauseWindowLayout);
            secondaryStage.setScene(scene);
            secondaryStage.initOwner(primaryStage);
            secondaryStage.initModality(Modality.WINDOW_MODAL);
            PauseWindowController controller = loader.getController();
            controller.setProgram(this);
            secondaryStage.setTitle("Game Paused");
            secondaryStage.getIcons().add(new Image(JavaFXManager.class.getResourceAsStream("/resources/images/gameIcon.png")));
            secondaryStage.showAndWait();
        } catch (IOException e) {
            // TODO handle exception
            e.printStackTrace();
        }

    }

    public Stage getSecondaryStage() {
        return secondaryStage;
    }

    public IGamesLogic getGamesManager() {
        return gamesManager;
    }
}
