package transportservice.Controllers;

import java.net.URL;
import java.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import transportservice.models.Node;
import transportservice.models.data;
import transportservice.util.*;

public class mainMenuController implements Initializable{
    @FXML
    private ImageView map;
    @FXML
    private Pane buttonsPane;
    @FXML
    private Pane rutePane;
    @FXML
    private Pane conditionsPane;
    @FXML
    private Pane roadsPane;
    @FXML
    private ChoiceBox<String> choiceBoxCondition;
    @FXML
    private ChoiceBox<String> choiceBoxTraficLevel;
    @FXML
    private ChoiceBox<String> choiceBoxNode1;
    @FXML
    private ChoiceBox<String> choiceBoxNode2;
    @FXML
    private ChoiceBox<String> choiceBoxAlgorithms;
    @FXML
    private Label labelNodeEnd;
    @FXML
    private Label labelNodeStart;
    @FXML
    private Label labelStartRute;
    @FXML
    private Label labelFinalRute;
    @FXML
    private Label labelStartRuteWithConditions;

    List<Node> nodes = new ArrayList<>();
    Canvas canvas = new Canvas(700, 850);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    Canvas canvasFinalWay = new Canvas(700, 850);
    GraphicsContext gcFinalWay = canvasFinalWay.getGraphicsContext2D();
    Canvas canvasConditions = new Canvas(700, 850);
    GraphicsContext gcConditions = canvasConditions.getGraphicsContext2D();
    Canvas canvasRoads = new Canvas(700, 850);
    GraphicsContext gcRoads = canvasRoads.getGraphicsContext2D();
    private double[][] weights;
    private int[][] conditions;//1 Calle cerrada, 2 Accidentes, 3 trafico nivel 1, 4 trafico nivel 2, 5 trafico nivel 3
    private int[][] startWay;
    private List<Node> startPath;

    @FXML
    void quitOnAction(ActionEvent event) {
        Platform.exit();
    }
    @FXML
    void onActionAddCondition(ActionEvent event) {
        if(choiceBoxNode1.getSelectionModel().isEmpty() || choiceBoxNode2.getSelectionModel().isEmpty()){
            new Message().showModal(Alert.AlertType.ERROR,"Error creando condición",labelNodeEnd.getScene().getWindow(),"Debe elegir nodo de inicio y de final para agregar la condición");
        } else if (this.choiceBoxCondition.getSelectionModel().isEmpty()) {
            new Message().showModal(Alert.AlertType.ERROR,"Error creando condición",labelNodeEnd.getScene().getWindow(),"Debe elegir nodo debe de elegir una condición");
        } else if (this.choiceBoxCondition.getSelectionModel().getSelectedIndex()==2 && this.choiceBoxTraficLevel.getSelectionModel().isEmpty()) {
            new Message().showModal(Alert.AlertType.ERROR,"Error creando condición",labelNodeEnd.getScene().getWindow(),"Para agregar una condición de trafico debe elegir un nivel para el mismo");
        } else{
            Node node1 = null; Node node2 = null;
            int posNode1 = -1;
            int posNode2 = -1;
            for (int i = 0 ; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                if (node.getName().equals(choiceBoxNode1.getSelectionModel().getSelectedItem())) {
                    node1 = node;
                    posNode1 = i;
                }
                if (node.getName().equals(choiceBoxNode2.getSelectionModel().getSelectedItem())) {
                    node2 = node;
                    posNode2 = i;
                }
            }
            int condition = -1;
            switch(this.choiceBoxCondition.getSelectionModel().getSelectedIndex()){
                case 0:
                    condition = 1;
                    break;
                case 1:
                    condition = 2;
                    break;
                case 2:
                    switch (this.choiceBoxTraficLevel.getSelectionModel().getSelectedIndex()){
                        case 0:
                            condition = 3;
                            break;
                        case 1:
                            condition = 4;
                            break;
                        case 2:
                            condition = 5;
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            if (condition != -1 && posNode1 != -1 && posNode2 != -1) {
                System.out.println("Se asigna " + condition);
                conditions[posNode1][posNode2] = condition;
            }
            drawConditions();
        }
    }

    @FXML
    void onActionDeleteNodeEnd(ActionEvent event) {
        int indexOfSelected = -1;
        for (int i =0 ; i < nodes.size() ; i++){
            if (nodes.get(i).getName().equals(this.labelNodeEnd.getText())){
                indexOfSelected = i;
                break;
            }
        }
        if (indexOfSelected != -1){
            nodes.get(indexOfSelected).getButton().setStyle("-fx-font-size: 9 ; -fx-background-color: black ; -fx-text-fill: white ");
            this.labelNodeEnd.setText("");
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            this.labelStartRute.setText("");
            this.labelStartRuteWithConditions.setText("");
        }
    }

    @FXML
    void onActionDeleteNodeStart(ActionEvent event) {
        int indexOfSelected = -1;
        for (int i =0 ; i < nodes.size() ; i++){
            if (nodes.get(i).getName().equals(this.labelNodeStart.getText())){
                indexOfSelected = i;
                break;
            }
        }
        if (indexOfSelected != -1) {
            nodes.get(indexOfSelected).getButton().setStyle("-fx-font-size: 9 ; -fx-background-color: black ; -fx-text-fill: white ");
            this.labelNodeStart.setText("");
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            this.labelStartRute.setText("");
            this.labelStartRuteWithConditions.setText("");
        }
    }
//  En revisión
    @FXML
    void onActionStartRoute(ActionEvent event) {
        // Obtener los nodos seleccionados y el algoritmo de choiceBox
        String startNodeName = labelNodeStart.getText();
        String endNodeName = labelNodeEnd.getText();
        String algorithm = choiceBoxAlgorithms.getSelectionModel().getSelectedItem();

        if (startNodeName == null || endNodeName == null || algorithm == null) {
            new Message().showModal(Alert.AlertType.ERROR,"Error Iniciando la Ruta",labelNodeEnd.getScene().getWindow(),"Selecciona los nodos de inicio, fin y el algoritmo.");
            return;
        }

        // Encontrar nodos de inicio y fin
        Node startNode = findNodeByName(startNodeName);
        Node endNode = findNodeByName(endNodeName);

        if (startNode == null || endNode == null) {
            new Message().showModal(Alert.AlertType.ERROR,"Error Iniciando la Ruta",labelNodeEnd.getScene().getWindow(),"Selecciona los nodos de inicio, fin.");
            return;
        }

        // Obtener la ruta más corta según el algoritmo elegido
        List<Node> route = findShortestPath(startNode, endNode, algorithm);

        if (route == null || route.isEmpty()) {
            System.out.println("No se encontró una ruta válida.");
            return;
        }

        // Inicializar peso total y limpiar canvas
        double totalWeight = 0.0;
        gcFinalWay.clearRect(0, 0, canvasRoads.getWidth(), canvasRoads.getHeight());

        // Coordenadas iniciales del vehículo
        double vehicleX = startNode.getX();
        double vehicleY = startNode.getY();
        drawVehicle(vehicleX, vehicleY);

        // Recorrer la ruta y aplicar condiciones de peso
        for (int i = 0; i < route.size() - 1; i++) {
            Node currentNode = route.get(i);
            Node nextNode = route.get(i + 1);

            int fromIndex = nodes.indexOf(currentNode);
            int toIndex = nodes.indexOf(nextNode);

            double edgeWeight = weights[fromIndex][toIndex];
            int condition = conditions[fromIndex][toIndex];

            switch (condition) {
                case 1:
                case 2:
                    System.out.println("La calle está cerrada. No se puede continuar.");
                    return;
                case 3:
                    break;
                case 4:
                    edgeWeight *= 2;
                    break;
                case 5:
                    edgeWeight *= 3;
                    break;
            }
            totalWeight += edgeWeight;
        }
        animateVehicle(route);
        this.labelFinalRute.setText(String.valueOf((int)totalWeight));
        this.calculateStartRuteWithConditions();
    }

    // Método para elegir el algoritmo según la selección del usuario
    private List<Node> findShortestPath(Node start, Node end, String algorithm) {
        if ("Dijkstra".equals(algorithm)) {
            return dijkstra(start, end);
        } else if ("Floyd".equals(algorithm)) {
            return floydWarshall(start, end);
        } else {
            return null;
        }
    }

    // Implementación del algoritmo de Dijkstra
    private List<Node> dijkstra(Node start, Node end) {
        int n = nodes.size();
        double[] distances = new double[n];
        Node[] previous = new Node[n];
        Arrays.fill(distances, Double.POSITIVE_INFINITY);
        distances[nodes.indexOf(start)] = 0;

        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(node -> distances[nodes.indexOf(node)]));
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.equals(end)) break;

            int currentIndex = nodes.indexOf(current);

            for (String adj : current.getAdjacencies()) {
                Node neighbor = findNodeByName(adj);
                int neighborIndex = nodes.indexOf(neighbor);
                double weight = weights[currentIndex][neighborIndex];
                int condition = conditions[currentIndex][neighborIndex];

                if (condition == 1 || condition == 2) continue; // Calle cerrada o accidente
                if (condition == 4) weight *= 2;
                if (condition == 5) weight *= 3;

                double newDist = distances[currentIndex] + weight;
                if (newDist < distances[neighborIndex]) {
                    distances[neighborIndex] = newDist;
                    previous[neighborIndex] = current;
                    queue.add(neighbor);
                }
            }
        }

        List<Node> path = new ArrayList<>();
        for (Node at = end; at != null; at = previous[nodes.indexOf(at)]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    // Implementación del algoritmo de Floyd-Warshall
    private List<Node> floydWarshall(Node start, Node end) {
        int n = nodes.size();
        double[][] dist = new double[n][n];
        Node[][] next = new Node[n][n];

        // Inicializar matrices de distancias y caminos
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], Double.POSITIVE_INFINITY);
            dist[i][i] = 0;
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (weights[i][j] != 0) {
                    double weight = weights[i][j];
                    int condition = conditions[i][j];
                    if (condition == 1 || condition == 2) continue;
                    if (condition == 4) weight *= 2;
                    if (condition == 5) weight *= 3;
                    dist[i][j] = weight;
                    next[i][j] = nodes.get(j);
                }
            }
        }

        // Aplicar Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }

        // Construir la ruta desde el nodo start al nodo end
        List<Node> path = new ArrayList<>();
        Node at = start;
        while (at != null && !at.equals(end)) {
            path.add(at);
            at = next[nodes.indexOf(at)][nodes.indexOf(end)];
        }
        if (at == null) return null;
        path.add(end);
        return path;
    }

    // Método auxiliar para encontrar el nodo por nombre
    private Node findNodeByName(String name) {
        return nodes.stream().filter(node -> node.getName().equals(name)).findFirst().orElse(null);
    }

    // Métodos auxiliares de dibujo y animación de vehículo
    private void drawVehicle(double x, double y) {
        gcFinalWay.setFill(Color.LIME);
        gcFinalWay.fillOval(x - 5, y - 5, 10, 10);
    }

    public void animateVehicle(List<Node> rute) {
        if (rute == null || rute.isEmpty()) {
            System.out.println("La lista de nodos está vacía.");
            return;
        }

        Timeline timeline = new Timeline();
        for (int i = 0; i < rute.size() - 1; i++) {
            Node startNode = rute.get(i);
            Node endNode = rute.get(i + 1);
            int xN1 = startNode.getX();
            int yN1 = startNode.getY();
            int xN2 = endNode.getX();
            int yN2 = endNode.getY();
            int gap = 5;
            if (startNode.getX() < endNode.getX() && startNode.getY() < endNode.getY()) {
                xN1 -= gap;
                xN2 -= gap;
                yN1 += gap;
                yN2 += gap;
            } else if (startNode.getX() > endNode.getX() && startNode.getY() < endNode.getY()) {
                xN1 -= gap;
                xN2 -= gap;
                yN1 -= gap;
                yN2 -= gap;
            } else if (startNode.getX() > endNode.getX() && startNode.getY() > endNode.getY()) {
                xN1 += gap;
                xN2 += gap;
                yN1 -= gap;
                yN2 -= gap;
            } else {
                xN1 += gap;
                xN2 += gap;
                yN1 += gap;
                yN2 += gap;
            }
            double startX = xN1;
            double startY = yN1;
            double endX = xN2;
            double endY = yN2;

            int steps = 25; // Número de pasos para que el movimiento sea más suave
            System.out.println("steps " + steps);
            double stepX = (endX - startX) / steps;
            double stepY = (endY - startY) / steps;

            for (int j = 0; j <= steps; j++) {
                final double x = startX + stepX * j;
                final double y = startY + stepY * j;
                KeyFrame keyFrame = new KeyFrame(Duration.millis(i * steps * 50 + j * 50), event -> {
                    //gcFinalWay.clearRect(startX - 5, startY - 5, 10, 10); // Limpiar el vehículo anterior
                    drawVehicle(x, y);
                });
                timeline.getKeyFrames().add(keyFrame);
            }
        }
        timeline.play();
    }

    // Final de revisión
    void handleChoiceBoxNode1Selection(ActionEvent event) {
        String selected = this.choiceBoxNode1.getSelectionModel().getSelectedItem();
        Node node1Selected = null;
        if(!selected.isEmpty()){
            System.out.println("Elemento seleccionado: " + selected);
            for (Node node : nodes){
                if (node.getName().equals(selected)){
                    node1Selected = node;
                }
            }
            if (node1Selected != null){
                this.choiceBoxNode2.getItems().clear();
                node1Selected.getAdjacencies().stream().forEach(s->this.choiceBoxNode2.getItems().add(s));
            }
        }else {

        }
    }

    void assignStartAndFinish(String name){
        int indexOfSelected = -1;
        for (int i =0 ; i < nodes.size() ; i++){
            if (nodes.get(i).getName().equals(name)){
                indexOfSelected = i;
                break;
            }
        }
        if (labelNodeStart.getText().isEmpty()){
            labelNodeStart.setText(name);
            nodes.get(indexOfSelected).getButton().setStyle("-fx-font-size: 9 ; -fx-background-color:#006f73 ; -fx-text-fill: #000000");
        } else if (labelNodeEnd.getText().isEmpty()) {
            labelNodeEnd.setText(name);
            nodes.get(indexOfSelected).getButton().setStyle("-fx-font-size: 9 ; -fx-background-color:#15c618 ; -fx-text-fill: #000000");
        } else {
            new Message().showModal(Alert.AlertType.ERROR,"Error Asignando Nodo",labelNodeEnd.getScene().getWindow(),"Debe eliminar un nodo antes de asignar otro");
        }
        if(!this.labelNodeStart.getText().isEmpty() && !this.labelNodeEnd.getText().isEmpty()){
            if(this.choiceBoxAlgorithms.getSelectionModel().getSelectedItem().equals("Dijkstra")){
                cleanStartWay();
                double value = calculateAndDrawDijkstra(labelNodeStart.getText(), labelNodeEnd.getText());
                this.labelStartRute.setText(String.valueOf((int) value));
                drawStartWay();
            }else{
                cleanStartWay();
                double value = calculateAndDrawFloydWarshall(labelNodeStart.getText(), labelNodeEnd.getText());
                this.labelStartRute.setText(String.valueOf((int) value));
                drawStartWay();
            }
        }
    }

    private double calculateAndDrawDijkstra(String node1, String node2) {
        int indexNode1 = -1;
        int indexNode2 = -1;

        // Obtener los índices de los nodos en la lista
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getName().equals(node1)) {
                indexNode1 = i;
            }
            if (nodes.get(i).getName().equals(node2)) {
                indexNode2 = i;
            }
        }

        if (indexNode1 == -1 || indexNode2 == -1) {
            System.out.println("Nodos no encontrados.");
            return -1;
        }

        int n = nodes.size();
        double[] dist = new double[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, Double.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[indexNode1] = 0;

        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingDouble(i -> dist[i]));
        pq.add(indexNode1);

        while (!pq.isEmpty()) {
            int u = pq.poll();
            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (data.adjacent[u][v] == 1 && !visited[v]) {
                    double alt = dist[u] + weights[u][v];
                    if (alt < dist[v]) {
                        dist[v] = alt;
                        prev[v] = u;
                        pq.add(v);
                    }
                }
            }
        }

        // Reconstruir la ruta más corta
        List<Node> path = new ArrayList<>();
        for (int at = indexNode2; at != -1; at = prev[at]) {
            path.add(nodes.get(at));
        }
        Collections.reverse(path);
        this.startPath = path;
        // Dibujar la ruta usando paintEdge y calcular el peso total
        double totalWeight = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node n1 = path.get(i);
            Node n2 = path.get(i + 1);
            startWay[nodes.indexOf(n1)][nodes.indexOf(n2)] = 1;
            totalWeight += weights[nodes.indexOf(n1)][nodes.indexOf(n2)];
            //paintEdge(n1, n2, Color.BLUE, 2); // Cambia el color y grosor según sea necesario
        }
        return totalWeight;
    }

    private double calculateAndDrawFloydWarshall(String node1, String node2) {
        int indexNode1 = -1;
        int indexNode2 = -1;

        // Obtener los índices de los nodos en la lista
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getName().equals(node1)) {
                indexNode1 = i;
            }
            if (nodes.get(i).getName().equals(node2)) {
                indexNode2 = i;
            }
        }

        if (indexNode1 == -1 || indexNode2 == -1) {
            System.out.println("Nodos no encontrados.");
            return -1;
        }

        int n = nodes.size();
        double[][] dist = new double[n][n];
        int[][] next = new int[n][n];

        // Inicialización de distancias y predecesores
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                } else if (data.adjacent[i][j] == 1) {
                    dist[i][j] = weights[i][j];
                    next[i][j] = j;
                } else {
                    dist[i][j] = Double.MAX_VALUE;
                    next[i][j] = -1;
                }
            }
        }

        // Aplicación del algoritmo de Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] != Double.MAX_VALUE && dist[k][j] != Double.MAX_VALUE && dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }

        // Reconstrucción de la ruta más corta entre indexNode1 y indexNode2
        List<Node> path = new ArrayList<>();
        int at = indexNode1;
        while (at != -1 && at != indexNode2) {
            path.add(nodes.get(at));
            at = next[at][indexNode2];
        }
        if (at != -1) {
            path.add(nodes.get(indexNode2));
        }
        this.startPath = path;
        // Dibujar la ruta y calcular el peso total
        double totalWeight = dist[indexNode1][indexNode2];
        for (int i = 0; i < path.size() - 1; i++) {
            Node n1 = path.get(i);
            Node n2 = path.get(i + 1);
            startWay[nodes.indexOf(n1)][nodes.indexOf(n2)] = 1;
//            paintEdge(path.get(i), path.get(i + 1), Color.BLUE, 2); // Cambia el color y grosor según sea necesario
        }
        return totalWeight;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
                    button.setOnAction(event -> assignStartAndFinish(button.getText()));
                    buttonsPane.getChildren().add(button);
                    if(cantNodos < 9){
                        cantNodos++;
                    }else{
                        caracter ++;
                        cantNodos = 1;
                    }
                }
            }
        }
        for (int i = 0 ; i < nodes.size() ; i++) {
            for (int j = 0 ; j < nodes.size() ; j++) {
                if(data.adjacent[i][j] == 1){
                    nodes.get(i).getAdjacencies().add(nodes.get(j).getName());
                }
            }
        }
        for (int i = 0; i < nodes.size(); i++) {
            System.out.print(nodes.get(i).getName());
            nodes.get(i).getAdjacencies().stream().forEach(ad-> System.out.print(" "+ad + " "));
            System.out.println("\n");
        }
        calculateEdges();
        rutePane.getChildren().add(canvasFinalWay);
        rutePane.getChildren().add(canvas);
        conditionsPane.getChildren().add(canvasConditions);
        roadsPane.getChildren().add(canvasRoads);
        for (Node node : nodes) {
            choiceBoxNode1.getItems().add(node.getName());
        }
        calculateWeights();
        choiceBoxAlgorithms.getItems().addAll("Dijkstra","Floyd");
        choiceBoxAlgorithms.getSelectionModel().select(0);
        this.conditions = new int[nodes.size()][nodes.size()];
        this.startWay = new int[nodes.size()][nodes.size()];
        this.choiceBoxNode1.setOnAction(this::handleChoiceBoxNode1Selection);
        this.choiceBoxCondition.getItems().addAll("Cerrada por manteniomiento(COSEVI)","Cerrada por accidente","Trafico");
        this.choiceBoxCondition.setOnAction(this::handleChoiceBoxCondition);
        this.choiceBoxTraficLevel.getItems().addAll("1","2","3");
        this.choiceBoxTraficLevel.setDisable(true);
        this.drawRoads();
    }

    private void handleChoiceBoxCondition(ActionEvent actionEvent) {
        int indeXSelected = this.choiceBoxCondition.getSelectionModel().getSelectedIndex();
        if ( indeXSelected != -1) {
            this.choiceBoxTraficLevel.setDisable(indeXSelected != 2);
        }
    }

    private void calculateWeights() {
        this.weights = new double[nodes.size()][nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if(data.adjacent[i][j] == 1 ){
                    double x1 = nodes.get(i).getX(), y1 = nodes.get(i).getY();
                    double x2 = nodes.get(j).getX(), y2 = nodes.get(j).getY();
                    double value = (double) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                    this.weights[i][j] = value;
                    //System.out.println("Distancia entre : "+ nodes.get(i).getName() + " y "+ nodes.get(j).getName()+ " = " + value);
                }
            }
        }
    }

    private void calculateEdges() {
    Color c = Color.RED;
    int size = nodes.size();
//        for (int i=0; i< size; i++) {
//            for (int j=i; j< size; j++) {
//                printMid(nodes.get(i), nodes.get(j));
//                if(data.adjacent[i][j] == 1){
//                    paintEdge(nodes.get(i), nodes.get(j),c,5);
//                }
//            }
//        }
//        c= Color.BLUE;
//        for (int i=0; i< size; i++) {
//            for (int j=i; j< size; j++) {
//                printMid(nodes.get(i), nodes.get(j));
//                if(data.adjacent[j][i] == 1){
//                    paintEdge(nodes.get(i), nodes.get(j),c,3);
//                }
//            }
//        }
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
//        System.out.println("Color en el punto medio (" + n.getName() + ", " + n1.getName() + "): " + "R: " + red + ", G: " + green + ", B: " + blue);
    }
    void paintEdge(Node n1, Node n2,Color c, int lane){
        gc.setStroke(c);
        gc.setLineWidth(lane);
        gc.strokeLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
    }
    //StartWay Functions
    private void updateStartWay(){
        this.cleanStartWay();
        this.drawStartWay();
    }
    private void cleanStartWay(){
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                this.startWay[i][j] = 0;
            }
        }
    }
    private void drawStartWay(){
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if(this.startWay[i][j] == 1){
                    Node n1 = nodes.get(i);
                    Node n2 = nodes.get(j);
                    int xN1 = n1.getX();
                    int yN1 = n1.getY();
                    int xN2 = n2.getX();
                    int yN2 = n2.getY();
                    int gap = 5;
                    if(n1.getX()<n2.getX() && n1.getY()<n2.getY()){
                        xN1-=gap;
                        xN2-=gap;
                        yN1+=gap;
                        yN2+=gap;
                    }else if (n1.getX()>n2.getX() && n1.getY()<n2.getY()){
                        xN1-=gap;
                        xN2-=gap;
                        yN1-=gap;
                        yN2-=gap;
                    } else if (n1.getX()>n2.getX() && n1.getY()>n2.getY()) {
                        xN1+=gap;
                        xN2+=gap;
                        yN1-=gap;
                        yN2-=gap;
                    }else {
                        xN1+=gap;
                        xN2+=gap;
                        yN1+=gap;
                        yN2+=gap;
                    }
                    gc.setStroke(Color.BLUE);
                    gc.setLineWidth(3);
                    gc.strokeLine(xN1,yN1,xN2,yN2);
                }
            }
        }
    }

    private void drawRoads(){
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i; j < nodes.size(); j++) {
                if(data.adjacent[i][j] == 1){
                    Node n1 = nodes.get(i);
                    Node n2 = nodes.get(j);
                    int xN1 = n1.getX();
                    int yN1 = n1.getY();
                    int xN2 = n2.getX();
                    int yN2 = n2.getY();
                    int gap = 5;
                    if(n1.getX()<n2.getX() && n1.getY()<n2.getY()){
                        xN1-=gap;
                        xN2-=gap;
                        yN1+=gap;
                        yN2+=gap;
                    }else if (n1.getX()>n2.getX() && n1.getY()<n2.getY()){
                        xN1-=gap;
                        xN2-=gap;
                        yN1-=gap;
                        yN2-=gap;
                    } else if (n1.getX()>n2.getX() && n1.getY()>n2.getY()) {
                        xN1+=gap;
                        xN2+=gap;
                        yN1-=gap;
                        yN2-=gap;
                    }else {
                        xN1+=gap;
                        xN2+=gap;
                        yN1+=gap;
                        yN2+=gap;
                    }
                    gcRoads.setStroke(Color.GRAY);
                    gcRoads.setLineWidth(3);
                    gcRoads.strokeLine(xN1,yN1,xN2,yN2);
                }
            }
        }
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < i; j++) {
                if(data.adjacent[i][j] == 1){
                    Node n1 = nodes.get(i);
                    Node n2 = nodes.get(j);
                    int xN1 = n1.getX();
                    int yN1 = n1.getY();
                    int xN2 = n2.getX();
                    int yN2 = n2.getY();
                    int gap = 5;
                    if(n1.getX()<n2.getX() && n1.getY()<n2.getY()){
                        xN1-=gap;
                        xN2-=gap;
                        yN1+=gap;
                        yN2+=gap;
                    }else if (n1.getX()>n2.getX() && n1.getY()<n2.getY()){
                        xN1-=gap;
                        xN2-=gap;
                        yN1-=gap;
                        yN2-=gap;
                    } else if (n1.getX()>n2.getX() && n1.getY()>n2.getY()) {
                        xN1+=gap;
                        xN2+=gap;
                        yN1-=gap;
                        yN2-=gap;
                    }else {
                        xN1+=gap;
                        xN2+=gap;
                        yN1+=gap;
                        yN2+=gap;
                    }
                    gcRoads.setStroke(Color.GRAY);
                    gcRoads.setLineWidth(3);
                    gcRoads.strokeLine(xN1,yN1,xN2,yN2);
                }
            }
        }
    }

    private void drawConditions(){
        gcConditions.clearRect(0,0,canvasConditions.getWidth(),canvasConditions.getHeight());

        Color c = Color.WHITE;
        int lineWidth = 0;
        int gap = 5;
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i; j < nodes.size(); j++) {
                if(conditions[i][j] != 0){
                    switch(conditions[i][j]){
                        case 1:
                            c = Color.BLACK;
                            break;
                        case 2:
                            c = Color.CYAN;
                            break;
                        case 3:
                            c = Color.DARKGREEN;
                            break;
                        case 4:
                            c = Color.DARKORANGE;
                            break;
                        case 5:
                            c = Color.DARKRED;
                            break;
                        default:
                            break;
                    }
                    Node n1 = nodes.get(i);
                    Node n2 = nodes.get(j);
                    int xN1 = n1.getX();
                    int yN1 = n1.getY();
                    int xN2 = n2.getX();
                    int yN2 = n2.getY();
                    if(n1.getX()<n2.getX() && n1.getY()<n2.getY()){
                        xN1-=gap;
                        xN2-=gap;
                        yN1+=gap;
                        yN2+=gap;
                    }else if (n1.getX()>n2.getX() && n1.getY()<n2.getY()){
                        xN1-=gap;
                        xN2-=gap;
                        yN1-=gap;
                        yN2-=gap;
                    } else if (n1.getX()>n2.getX() && n1.getY()>n2.getY()) {
                        xN1+=gap;
                        xN2+=gap;
                        yN1-=gap;
                        yN2-=gap;
                    }else {
                        xN1+=gap;
                        xN2+=gap;
                        yN1+=gap;
                        yN2+=gap;
                    }
                    gcConditions.setStroke(c);
                    gcConditions.setLineWidth(6);
                    gcConditions.strokeLine(xN1,yN1,xN2,yN2);
                }
            }
        }
    }
    private void calculateStartRuteWithConditions(){
        double totalWeight=0;
        this.labelStartRuteWithConditions.setText("");
        for (int i = 0; i < this.startPath.size() - 1; i++) {
            Node currentNode = this.startPath.get(i);
            Node nextNode = this.startPath.get(i + 1);

            int fromIndex = nodes.indexOf(currentNode);
            int toIndex = nodes.indexOf(nextNode);

            double edgeWeight = weights[fromIndex][toIndex];
            int condition = conditions[fromIndex][toIndex];

            switch (condition) {
                case 1:
                    this.labelStartRuteWithConditions.setText("Cerrada por mantenimiento del COSEVI");
                    return;
                case 2:
                    this.labelStartRuteWithConditions.setText("Cerrada por accidente");
                    return;
                case 3:
                    break;
                case 4:
                    edgeWeight *= 2;
                    break;
                case 5:
                    edgeWeight *= 3;
                    break;
            }
            totalWeight += edgeWeight;
        }this.labelStartRuteWithConditions.setText(String.valueOf((int)totalWeight));
    }
}
