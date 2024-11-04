package transportservice.models;

public class ListGrid {
    private int numbRows;
    private int numbColumns;
    private NodeGrid firstElement;

    public ListGrid(int numbRows,int numbColumns,char elements[]) {
        this.numbRows = numbRows;
        this.numbColumns = numbColumns;
        this.addItems(elements);
    }
    private void addItems(char elements[]){
        NodeGrid nodeGridAux = new NodeGrid(elements[0]);
        int cont=1;
        NodeGrid nodeGridAux2;
        this.firstElement=nodeGridAux;
        for (int i = 1; i < numbColumns; i++) {
            nodeGridAux2 = new NodeGrid(elements[cont]);
            nodeGridAux.setRight(nodeGridAux2);
            nodeGridAux2.setLeft(nodeGridAux);
            nodeGridAux=nodeGridAux2;
            cont++;
        }
        for (int i = 1; i < numbRows; i++) {
            NodeGrid nodeGridTop= this.firstElement;
            for (int j = 0; j < numbColumns; j++) {
                if(j==0){
                    for (int k = 0; k < i-1; k++){
                        nodeGridTop=nodeGridTop.getDown();
                    }
                    nodeGridAux =new NodeGrid(elements[cont]);
                    nodeGridAux.setUp(nodeGridTop);
                    nodeGridTop.setDown(nodeGridAux);
                }else{
                    nodeGridTop=nodeGridTop.getRight();
                    nodeGridAux2=nodeGridAux;
                    nodeGridAux=new NodeGrid(elements[cont]);
                    
                    nodeGridAux.setLeft(nodeGridAux2);
                    nodeGridAux.setUp(nodeGridTop);
                        
                    nodeGridTop.setDown(nodeGridAux);
                    nodeGridAux2.setRight(nodeGridAux);
                }
                cont++;
            }
        }
    }

    public NodeGrid getFirstElement() {
        return firstElement;
    }

    public NodeGrid getElement(int row,int column){
        NodeGrid auxNode= this.firstElement;
        for (int i = 0; i < row; i++) {
            auxNode=auxNode.getDown();
        }
        for (int i = 0; i < column; i++) {
            auxNode=auxNode.getRight();
        }
        return auxNode;
    }
}
