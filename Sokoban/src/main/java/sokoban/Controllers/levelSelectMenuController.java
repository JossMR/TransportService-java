package sokoban.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import sokoban.App;

public class levelSelectMenuController implements Initializable{
    public static int lvlSelected=0;
    @FXML
    private Button level1Button;
    
    @FXML
    private Button level2Button;
    
    @FXML
    private Button level3Button;

    @FXML
    private Button level4Button;

    @FXML
    private Button level5Button;

    @FXML
    private Button quitButton;

    @FXML
    private Button returnMenuButton;

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("mainMenu");
    }
    @FXML
    void pressLvl1Button(ActionEvent event) throws IOException {
        lvlSelected=1;
        App.setRoot("game");
    }

    @FXML
    void pressLvl2Button(ActionEvent event) throws IOException {
        lvlSelected=2;
        App.setRoot("game");
    }

    @FXML
    void pressLvl3Button(ActionEvent event) throws IOException {
        lvlSelected=3;
        App.setRoot("game");
    }

    @FXML
    void pressLvl4Button(ActionEvent event) throws IOException {
        lvlSelected=4;
        App.setRoot("game");
    }

    @FXML
    void pressLvl5Button(ActionEvent event) throws IOException {
        lvlSelected=5;
        App.setRoot("game");
    }
    @FXML
    void onActionQuit(ActionEvent event) {
         Platform.exit();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gameController.goNextLevel=false;
        gameController.onReplay=false;
    }

}