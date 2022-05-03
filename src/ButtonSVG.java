import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.shape.SVGPath;

public class ButtonSVG extends Button {
    public ButtonSVG(String svg){
        super();

        SVGPath path = new SVGPath();
        path.setContent(svg);
        path.getStyleClass().add("button-raised");
        path.setStyle("-fx-fill: ORANGE;");

        this.setGraphic(path);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #303030; -fx-border-color: ORANGE; -fx-border-width: 1;");
    }
}