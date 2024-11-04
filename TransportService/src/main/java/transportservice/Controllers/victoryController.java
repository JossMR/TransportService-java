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
import transportservice.models.GameEngine;

public class victoryController implements Initializable{
    public static int winnedLevel=-1;
    @FXML
    private Button levelSelectionButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button quitButton;

    @FXML
    private Button repetitionButton;

    @FXML
    private Button returnMenuButton;
    @FXML
    void onActionNextLevel(ActionEvent event) throws IOException {
        gameController.goNextLevel=true;
        gameController.onReplay=false;
        winnedLevel=GameEngine.currentLevelID;
        App.setRoot("game");
    }
    @FXML
    private void onActionSelectLevel() throws IOException {
        App.setRoot("levelSelectMenu");
    }
    @FXML
    private void onActionReplay() throws IOException {
        gameController.goNextLevel=false;
        gameController.onReplay=true;
        App.setRoot("game");
    }
    @FXML
    void onActionQuit(ActionEvent event) {
         Platform.exit();
    }
    @FXML
    void onActionReturnMenu(ActionEvent event) throws IOException {
        App.setRoot("mainMenu");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.nextButton.setVisible(GameEngine.currentLevelID!=4);
    }
}
