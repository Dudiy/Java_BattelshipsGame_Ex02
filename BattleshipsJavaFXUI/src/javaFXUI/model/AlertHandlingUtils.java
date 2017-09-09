package javaFXUI.model;
import javafx.scene.control.Alert;

public class AlertHandlingUtils {
    public static void showErrorMessage(Exception exception, String headerText){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Huston...We have a problem!");
        alert.setHeaderText(headerText);
        alert.setContentText("Error Message: \n" + exception.getMessage());
        alert.showAndWait();
    }

}
