package javaFXUI.model;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Paths;

public class FileChooserProxy {
    public FileChooser FileChooser = new FileChooser();

    public File showOpenDialog(final Window ownerWindow){
        // set extension filter
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        FileChooser.getExtensionFilters().add(extensionFilter);
        // set initial directory
        // TODO ?
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        FileChooser.setInitialDirectory(new File(currentPath));
        // Show open file dialog
        return FileChooser.showOpenDialog(ownerWindow);
    }
}
