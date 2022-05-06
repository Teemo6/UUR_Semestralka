import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.shape.SVGPath;

public class ButtonSVG extends Button {
    public ButtonSVG(String svg){
        super();

        SVGPath path = new SVGPath();
        path.setContent(svg);
        path.getStyleClass().add("customButton");;
        path.setScaleX(2);
        path.setScaleY(2);

        this.setGraphic(path);
        this.setAlignment(Pos.CENTER);
    }
}