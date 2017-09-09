package javaFXUI;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Program extends Application{
    private Stage primaryStage;
    private BorderPane rootLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Battleships by Or and Dudi");
        this.primaryStage.getIcons().add(new Image("/resources/images/gameIcon.ico"));
        initRootLayout();
    }

    private void initRootLayout() {

    }
}
