package transportservice.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import transportservice.App;
import transportservice.models.FileTextReader;
import transportservice.models.ListGrid;

public class mainMenuController implements Initializable{
    public static boolean continueFromSave;
    
    @FXML
    public Button actionButton;
    @FXML
    private void newGameOnAction() throws IOException {
        continueFromSave=false;
        App.setRoot("levelSelectMenu");
    }
    @FXML
    void continueOnAction(ActionEvent event) throws IOException {
        if (FileTextReader.isLevelSaved()) {
            continueFromSave=true;
            gameController.goNextLevel=false;
            gameController.onReplay=false;
            App.setRoot("game");
        }else{
        
        }
    }
    @FXML
    void quitOnAction(ActionEvent event) {
        Platform.exit();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

}
