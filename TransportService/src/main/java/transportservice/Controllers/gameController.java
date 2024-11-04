package transportservice.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import transportservice.App;
import transportservice.models.FileTextReader;
import transportservice.models.GameEngine;

public class gameController implements Initializable{

   
    GameEngine gameEngine;
    static boolean goNextLevel;
    public static boolean onReplay;
    @FXML
    private Label replayLabel;

    @FXML
    private GridPane gameGrid;
    
    @FXML
    void keyPressed(KeyEvent event) throws IOException {
        gameEngine.keyPressed(event.getText());
    }

    @FXML
    void saveLevel(ActionEvent event) {
        if(!onReplay){
            FileTextReader.saveLevel(gameEngine.getCurrentLevel(), gameEngine.getCurrentLevelID(),gameEngine.getSteps());
        }
    }
    @FXML
    void quitToMenu(ActionEvent event) throws IOException {
        if(gameEngine.getTimer()!=null){gameEngine.getTimer().cancel();}
        App.setRoot("mainMenu");
    }
    @FXML
    void quitOnAction(ActionEvent event) {
        Platform.exit();
    }
    @FXML
    void restartOnAction(ActionEvent event) {
        if(!onReplay){
            gameEngine.restartLevel();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.replayLabel.setVisible(onReplay);
        this.gameEngine= new GameEngine();
        this.gameEngine.initializeGameGrid(gameGrid);
        if(goNextLevel){
        FileTextReader.deleteWinnedLevel();
        gameEngine.loadNextLevel(victoryController.winnedLevel+1);
        }else if(onReplay){
                gameEngine.reproduceReplay();
            }else {
//                FileTextReader.deleteWinnedLevel();
//                if(mainMenuController.continueFromSave){
//                    gameEngine.loadSavedLevel();
//                }else{
//                    gameEngine.loadSelectedLevel(levelSelectMenuController.lvlSelected);
//                }
            }
        }
}