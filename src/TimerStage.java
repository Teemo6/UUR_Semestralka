import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Calendar;

public class TimerStage {
    private static final TimerStage INSTANCE = new TimerStage();

    private final double TIMER_WINDOW_MIN_WIDTH = 350;
    private final double TIMER_WINDOW_MIN_HEIGHT = 300;

    private final String RADIO_WHAT_1 = "Zapnout přehrávání";
    private final String RADIO_WHAT_2 = "Zastavit přehrávání";
    private final String RADIO_WHAT_3 = "Zavřít program";
    private final String RADIO_WHEN_1 = "Po uplynutí času";
    private final String RADIO_WHEN_2 = "V zadanám čase";

    private Stage timerStage;
    private Scene timerScene;

    private TimeField timerWhen1Timefield;
    private TimeField timerWhen2Timefield;

    private GridPane timerGrid;

    private BooleanProperty timerRunning = new SimpleBooleanProperty();
    private TimerAction timerAction;
    private boolean timerCountdown;

    public static TimerStage getInstance(){
        return INSTANCE;
    }

    public void createTimerStage(){
        if (timerStage != null && timerStage.isShowing()){
            timerStage.close();
        }

        Parent parentTimer = getTimerPane();
        parentTimer.getStylesheets().add("resources/stylesheet.css");

        if(!ControlsCSS.isBroken){
            try {
                timerScene.getStylesheets().add(ControlsCSS.pathToCSS);
            } catch (Exception ignored){}
        } else {
            parentTimer.getStylesheets().add("resources/customColor.css");
        }

        ControlsCSS.setParentTimer(parentTimer);
        ControlsCSS.refreshCSS();

        timerScene = new Scene(parentTimer);

        timerStage = new Stage();
        timerStage.setTitle("Časovač");
        timerStage.setScene(timerScene);
        timerStage.setMinWidth(TIMER_WINDOW_MIN_WIDTH);
        timerStage.setMinHeight(TIMER_WINDOW_MIN_HEIGHT);
        timerStage.getIcons().add(new Image("/resources/icon.png"));
        timerStage.setResizable(false);
        timerStage.show();

        setTimerRunning(ControlsTimer.isTimerRunning());
        timerAction = ControlsTimer.timerAction;
        timerCountdown = true;
    }

    private Parent getTimerPane() {
        VBox timerWrapper = new VBox();

        HBox timerState = new HBox();
        Label timerStateLabel = new Label("Zapnout");
        timerStateLabel.setFont(Font.font(13));
        CheckBox timerStateCheckbox = new CheckBox();
        timerStateCheckbox.selectedProperty().bindBidirectional(timerRunning);
        timerRunning.addListener((obs, oldVal, newVal) -> timerGrid.setDisable(!isTimerRunning()));

        timerState.getChildren().addAll(timerStateCheckbox, timerStateLabel);

        timerGrid = new GridPane();
        if(!isTimerRunning()) timerGrid.setDisable(true);

        // Co se udělá
        RadioButton timerWhat1Radio = new RadioButton();
        timerWhat1Radio.setText(RADIO_WHAT_1);
        timerWhat1Radio.setSelected(true);

        RadioButton timerWhat2Radio = new RadioButton();
        timerWhat2Radio.setText(RADIO_WHAT_2);

        RadioButton timerWhat3Radio = new RadioButton();
        timerWhat3Radio.setText(RADIO_WHAT_3);

        // Kdy se to udělá
        RadioButton timerWhen1Radio = new RadioButton();
        timerWhen1Radio.setText(RADIO_WHEN_1);
        timerWhen1Radio.setSelected(true);
        timerWhen1Timefield = new TimeField();
        timerWhen1Timefield.setPrefWidth(84);
        timerWhen1Timefield.setFieldPrefWidth();
        timerWhen1Timefield.disableProperty().bind(timerWhen1Radio.selectedProperty().not());

        RadioButton timerWhen2Radio = new RadioButton();
        timerWhen2Radio.setText(RADIO_WHEN_2);
        timerWhen2Timefield = new TimeField();
        timerWhen2Timefield.setPrefWidth(84);
        timerWhen2Timefield.setFieldPrefWidth();
        timerWhen2Timefield.disableProperty().bind(timerWhen2Radio.selectedProperty().not());

        HBox buttonWrapper = new HBox();
        Button btnConfirm = new Button("Nastavit");
        btnConfirm.setOnAction(e -> setTimerAction());
        Button btnExit = new Button("Odejít");
        btnExit.setOnAction(e -> timerStage.close());

        timerGrid.add(timerWhat1Radio, 0, 0);
        timerGrid.add(timerWhat2Radio, 0, 1);
        timerGrid.add(timerWhat3Radio, 0, 2);

        timerGrid.add(new Separator(), 0, 3, GridPane.REMAINING, 1);

        timerGrid.add(timerWhen1Radio, 0, 4);
        timerGrid.add(timerWhen1Timefield, 2, 4);
        timerGrid.add(timerWhen2Radio, 0, 5);
        timerGrid.add(timerWhen2Timefield, 2, 5);

        ToggleGroup groupWhat = new ToggleGroup();
        groupWhat.getToggles().addAll(timerWhat1Radio, timerWhat2Radio, timerWhat3Radio);
        groupWhat.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            RadioButton selectedButton = (RadioButton) groupWhat.getSelectedToggle();
            switch (selectedButton.getText()) {
                case RADIO_WHAT_1 -> timerAction = TimerAction.START_PLAYING;
                case RADIO_WHAT_2 -> timerAction = TimerAction.STOP_PLAYING;
                case RADIO_WHAT_3 -> timerAction = TimerAction.CLOSE_PROGRAM;
            }
        });

        ToggleGroup groupWhen = new ToggleGroup();
        groupWhen.getToggles().addAll(timerWhen1Radio, timerWhen2Radio);
        groupWhen.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            RadioButton selectedButton = (RadioButton) groupWhen.getSelectedToggle();
            switch (selectedButton.getText()) {
                case RADIO_WHEN_1 -> timerCountdown = true;
                case RADIO_WHEN_2 -> timerCountdown = false;
            }
        });

        timerState.setPadding(new Insets(10, 0, 0, 10));
        timerState.setSpacing(5);

        buttonWrapper.getChildren().addAll(btnConfirm, btnExit);
        buttonWrapper.getChildren().forEach(n -> ((Region)n).setPrefWidth(90));
        buttonWrapper.getChildren().forEach(n -> ((Region)n).setPrefHeight(20));
        buttonWrapper.setSpacing(10);
        buttonWrapper.setPadding(new Insets(10, 0, 10, 0));
        buttonWrapper.setAlignment(Pos.TOP_CENTER);

        timerGrid.setHgap(5);
        timerGrid.setVgap(10);
        timerGrid.setAlignment(Pos.TOP_CENTER);

        timerWrapper.getChildren().addAll(timerState, timerGrid, buttonWrapper);
        timerWrapper.setSpacing(10);

        return timerWrapper;
    }

    public void setTimerAction(){
        if(isTimerRunning()) {
            Duration calculated = calculateDuration();

            ControlsTimer.timerAction = timerAction;
            ControlsTimer.setTimer(calculated);
        } else {
            ControlsTimer.clearTimer();
        }
    }

    private Duration calculateDuration(){
        Duration outputTime;
        if(timerCountdown){
            outputTime = timerWhen1Timefield.getDuration();
            if(outputTime.toSeconds() == 0) outputTime = new Duration(1);
        }else{
            Calendar calendar = Calendar.getInstance();
            double hours = calendar.get(Calendar.HOUR_OF_DAY) * 3600;
            double minutes = calendar.get(Calendar.MINUTE) * 60;
            double seconds = calendar.get(Calendar.SECOND);
            double currentTime = hours + minutes + seconds;

            if(timerWhen2Timefield.getDuration().toSeconds() < currentTime){
                double newTime = 86400 + timerWhen2Timefield.getDuration().toSeconds() - currentTime;
                outputTime = Duration.seconds(newTime);
            } else {
                double newTime = timerWhen2Timefield.getDuration().toSeconds() - currentTime;
                outputTime = Duration.seconds(newTime);
            }
        }
        return outputTime;
    }

    public boolean isTimerRunning() {
        return timerRunning.get();
    }

    public BooleanProperty timerRunningProperty() {
        return timerRunning;
    }

    public void setTimerRunning(boolean timerRunning) {
        this.timerRunning.set(timerRunning);
    }
}
