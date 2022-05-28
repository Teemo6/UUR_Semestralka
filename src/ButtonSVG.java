import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.shape.SVGPath;

public class ButtonSVG extends Button {
    private ObjectProperty<SVGPath> path = new SimpleObjectProperty<>();

    public ButtonSVG(SVGPath svgPath){
        super();

        setPath(svgPath);

        this.graphicProperty().bind(path);
        this.setAlignment(Pos.CENTER);
    }

    public void setPath(SVGPath svgPath) {
        path.set(svgPath);
    }

    public SVGPath getPath(){
        return path.get();
    }

    public ObjectProperty<SVGPath> pathProperty() {
        return path;
    }
}