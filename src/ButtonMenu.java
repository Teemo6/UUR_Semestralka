import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class ButtonMenu extends Menu {
    private StringProperty textLabel = new SimpleStringProperty();

    public ButtonMenu(String text) {
        super();
        setTextLabel(text);

        MenuItem dummyItem = new MenuItem();
        dummyItem.setVisible(false);
        getItems().add(dummyItem);

        Label label = new Label();
        label.textProperty().bind(textLabel);
        label.setOnMouseClicked(evt -> dummyItem.fire());
        setGraphic(label);
    }

    public void setTextLabel(String text){
        textLabel.set(text);
    }

    public String getTextLabel() {
        return textLabel.get();
    }

    public StringProperty textLabelProperty() {
        return textLabel;
    }
}