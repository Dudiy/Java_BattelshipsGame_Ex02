package javaFXUI.view;

import gameLogic.users.Player;
import javaFXUI.JavaFXManager;
import javaFXUI.model.PlayerAdapter;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static javaFXUI.Constants.DEFAULT_PLAYER_PROFILE_URL;

public class PlayerInitializerController {
    @FXML
    private ImageView imageViewProfilePic;
    @FXML
    private Button buttonChangePicture;
    @FXML
    private Label labelPlayerNumber;
    @FXML
    private TextField textFieldPlayerName;
    @FXML
    private Button buttonOK;

    private Window ownerWindow;
    private JavaFXManager javaFXManager;
    private PlayerAdapter player;
    private Image playerImage;
    @FXML
    void buttonChangePicture_Clicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter("image files (*.jpg,*png)", "*.jpg", "*png")
        );
        File imageFile = fileChooser.showOpenDialog(ownerWindow);
        try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            playerImage = SwingFXUtils.toFXImage(bufferedImage, null);
            imageViewProfilePic.setImage(playerImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void buttonOK_Clicked(ActionEvent event) {
        updatePlayerInfo();
        javaFXManager.playerWindowOkClicked();
    }

    public void updatePlayerInfo() {
        player.setName(textFieldPlayerName.getText());
        player.setPlayerImage(playerImage);
    }

    public void setOwnerWindow(Window ownerWindow, JavaFXManager javaFXManager) {
        this.ownerWindow = ownerWindow;
        this.javaFXManager = javaFXManager;
    }

    public void setPlayer(PlayerAdapter player, int playerNumber) {
        this.player = player;
        labelPlayerNumber.setText("Player " + playerNumber);
        textFieldPlayerName.setText(player.getName());

        // TODO uncomment
//        if (player.getPlayerImage() != null) {
//            imageViewProfilePic.setImage(player.getPlayerImage());
//        }
//        else{
//            imageViewProfilePic.setImage(new Image(DEFAULT_PLAYER_PROFILE_URL));
//        }
    }
}
