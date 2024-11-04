package transportservice.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import transportservice.App;
import transportservice.models.FileTextReader;
import transportservice.models.ListGrid;
import transportservice.models.Node;

import javax.swing.*;

public class mainMenuController implements Initializable{
    @FXML
    Button A;
    @FXML
    private ChoiceBox<String> choiceBoxtest;
    @FXML
    private ImageView map;
    @FXML
    private Pane pane;
    @FXML
    private ChoiceBox<String> choiceBoxCondition;
    @FXML
    private ChoiceBox<String> choiceBoxDirection;
    @FXML
    private ChoiceBox<String> choiceBoxNode1;
    @FXML
    private ChoiceBox<String> choiceBoxNode2;
    List<Node> nodes = new ArrayList<>();
    Canvas canvas = new Canvas(700, 850);
    GraphicsContext gc = canvas.getGraphicsContext2D();

    @FXML
    void quitOnAction(ActionEvent event) {
        Platform.exit();
    }
    @FXML
    void onActionAddCondition(ActionEvent event) {
        if(choiceBoxNode1.getSelectionModel().isEmpty() || choiceBoxNode2.getSelectionModel().isEmpty()){
            return;
        }else{
            Node node1 = nodes.get(choiceBoxNode1.getSelectionModel().getSelectedIndex());
            Node node2 = nodes.get(choiceBoxNode2.getSelectionModel().getSelectedIndex());
            gc.setStroke(Color.RED); // Cambia el color según lo necesites
            gc.setLineWidth(3); // Opcional: cambiar el grosor de la línea
            gc.strokeLine(node1.getX(), node1.getY(), node2.getX(), node2.getY());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        A.setMaxHeight(5);
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/mapa.PNG")));
        map .setImage(image);
        PixelReader pixelReader = map.getImage().getPixelReader();
        int caracter=65;
        int cantNodos=1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = pixelReader.getArgb(x, y);
                int red = (argb >> 16) & 0xff;
                int green = (argb >> 8) & 0xff;
                int blue = argb & 0xff;

                if (red == 255 && green == 0 && blue == 0) {
                    // Crear y posicionar el botón

                    Button button = new Button(Character.toString(((char)caracter))+cantNodos);
                    button.setStyle("-fx-font-size: 9 ; -fx-background-color: black ; -fx-text-fill: white ");
                    button.setLayoutX(x-11);
                    button.setLayoutY(y-11);
                    button.setMinHeight(22);
                    button.setMinWidth(22);
                    nodes.add(new Node(button, button.getText(), x, y));
                    pane.getChildren().add(button);
                    if(cantNodos < 9){
                        cantNodos++;
                    }else{
                        caracter ++;
                        cantNodos = 1;
                    }
                }
            }
        }
        calculateEdges();
        pane.getChildren().add(canvas);
        for (Node node : nodes) {
            choiceBoxNode1.getItems().add(node.getName());
            choiceBoxNode2.getItems().add(node.getName());
        }

        this.choiceBoxtest.getItems().addAll(" ","  ","   ");
        choiceBoxtest.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int selectedIndex = newValue.intValue();
            System.out.println(selectedIndex);
            switch (selectedIndex) {
                case 0:
                    choiceBoxtest.setStyle("-fx-background-color: red;");
                    break;
                case 1:
                    choiceBoxtest.setStyle("-fx-background-color: green;");
                    break;
                case 2:
                    choiceBoxtest.setStyle("-fx-background-color: blue;");
                    break;
                default:
                    choiceBoxtest.setStyle("-fx-background-color: white;");
                    break;
            }
        });
    }

    private void calculateEdges() {
        for (int i=0; i< nodes.size(); i++) {
            for (int j=0; j< nodes.size(); j++) {
                printMid(nodes.get(i), nodes.get(j));
            }
        }
    }

    private void printMid(Node n, Node n1) {
        int midX = (n.getX() + n1.getX()) / 2;
        int midY = (n.getY() + n1.getY()) / 2;

        PixelReader pixelReader = map.getImage().getPixelReader();
        int argb = pixelReader.getArgb(midX, midY);
        int red = (argb >> 16) & 0xff;
        int green = (argb >> 8) & 0xff;
        int blue = argb & 0xff;

//        if((red == 139 && green == 165 && blue == 193) || (red == 211 && green == 220 && blue == 228)){
//            paintEdge(n,n1);
//        }
        System.out.println("Color en el punto medio (" + n.getName() + ", " + n1.getName() + "): " + "R: " + red + ", G: " + green + ", B: " + blue);

    }
    void paintEdge(Node n1, Node n2){
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(3);
        gc.strokeLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
    }

}
