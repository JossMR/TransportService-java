package transportservice.models;

public class NodeGrid {
    private NodeGrid up;
    private NodeGrid down;
    private NodeGrid left;
    private NodeGrid right;
    private char character;

    public void setCharacter(char character) {
        this.character = character;
    }
    NodeGrid(char character) {
        this.up=null;
        this.down=null;
        this.left=null;
        this.right=null;
        this.character=character;
    }

    public char getCharacter() {
        return character;
    }

    public NodeGrid getUp() {
        return up;
    }

    public void setUp(NodeGrid up) {
        this.up = up;
    }

    public NodeGrid getDown() {
        return down;
    }

    public void setDown(NodeGrid down) {
        this.down = down;
    }

    public NodeGrid getLeft() {
        return left;
    }

    public void setLeft(NodeGrid left) {
        this.left = left;
    }

    public NodeGrid getRight() {
        return right;
    }

    public void setRight(NodeGrid right) {
        this.right = right;
    }
    
}
