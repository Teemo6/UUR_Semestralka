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
        this.setAlignment(Pos.CENTER);
    }

    public SVGPath getPath(){
        return path.get();
    }

    public ObjectProperty<SVGPath> pathProperty() {
        return path;
    }

    public void setPath(SVGPath path) {
        this.path.set(path);
        getPath().getStyleClass().add("customButton");;
        getPath().setScaleX(2);
        getPath().setScaleY(2);

        this.setGraphic(getPath());
    }
}