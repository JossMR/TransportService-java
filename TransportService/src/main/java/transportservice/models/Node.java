package transportservice.models;

import javafx.scene.control.Button;

import java.util.List;

public class Node {
    private String name;
    private Button button;
    private int x;
    private int y;

    public Node(Button button, String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.button = button;
        List<String> adjacencies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
