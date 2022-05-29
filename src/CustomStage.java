import javafx.scene.Node;
import javafx.stage.Stage;

public class CustomStage extends Stage {
    private Node rootNode;

    public CustomStage(){
        super();
    }

    public void setRootNode(Node rootNode){
        this.rootNode = rootNode;
    }

    public Node getRootNode(){
        return rootNode;
    }
}
