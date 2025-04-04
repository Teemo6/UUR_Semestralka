import javafx.beans.property.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.regex.Pattern;

public class TimeField extends HBox{
    private StringProperty value1 = new SimpleStringProperty();
    private StringProperty value2 = new SimpleStringProperty();
    private StringProperty value3 = new SimpleStringProperty();

    public TimeField(){
        super();

        TextField time1 = new TextField();
        TextField time2 = new TextField();
        TextField time3 = new TextField();

        time1.setPromptText("00");
        time2.setPromptText("00");
        time3.setPromptText("00");

        value1.bind(time1.textProperty());
        value2.bind(time2.textProperty());
        value3.bind(time3.textProperty());

        String regexHours = "(2?[0-3]?)|([01]?[0-9]?)";
        String regexOther = "([0-5]?[0-9]?)";
        Pattern patternHours = Pattern.compile(regexHours);
        Pattern patternOther = Pattern.compile(regexOther);

        time1.setTextFormatter(createFormatter(patternHours));
        time2.setTextFormatter(createFormatter(patternOther));
        time3.setTextFormatter(createFormatter(patternOther));

        registerListener(time1, time2);
        registerListener(time2, time3);

        this.getChildren().addAll(time1, new Label(":"), time2, new Label(":"), time3);
        this.getChildren().forEach(e -> {
            e.getStyleClass().add("time-field-text-field");
            e.setStyle("-fx-border-color:transparent;");
        });
        this.getStyleClass().add("time-field");
    }

    public void setFieldPrefWidth(){
        this.getChildren().forEach(e -> {
            if(e instanceof TextField) ((TextField) e).setPrefWidth(this.getPrefWidth() / 3);
        });
    }

    public Duration getDuration(){
        int finalDuration = getHoursInSeconds() + getMinutesInSeconds() + getSeconds();
        return Duration.seconds(finalDuration);
    }

    public int getHoursInSeconds(){
        if(!value1.get().equals("")) return Integer.parseInt(value1.get()) * 3600;
        return 0;
    }

    public int getMinutesInSeconds(){
        if(!value2.get().equals("")) return Integer.parseInt(value2.get()) * 60;
        return 0;
    }

    public int getSeconds(){
        if(!value3.get().equals("")) return Integer.parseInt(value3.get());
        return 0;
    }

    private void registerListener(TextField tf1, TextField tf2) {
        tf1.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() >= 2) {
                tf2.requestFocus();
            }
        });
    }

    private TextFormatter<?> createFormatter(Pattern pattern){
        return new TextFormatter<>(change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                return change;
            } else {
                return null;
            }
        });
    }
}
