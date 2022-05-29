import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.stage.WindowEvent;

public class TimerStage {
    private static final TimerStage INSTANCE = new TimerStage();

    private static final double TIMER_WINDOW_MIN_WIDTH = 350;
    private static final double TIMER_WINDOW_MIN_HEIGHT = 300;

    private static Stage timerStage;
    private static Scene timerScene;

    GridPane timerGrid;

    private BooleanProperty timerRunning = new SimpleBooleanProperty();

    public static TimerStage getInstance(){
        return INSTANCE;
    }

    public void createTimerStage(){
        if (timerStage != null && timerStage.isShowing()){
            timerStage.close();
        }
        Parent parentTimer = getTimerPane();
        parentTimer.getStylesheets().addAll("resources/stylesheet.css");
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

        timerRunning.set(ControlsTimer.isTimerRunning());
    }

    private Parent getTimerPane() {
        VBox timerWrapper = new VBox();

        HBox timerState = new HBox();
        Label timerStateLabel = new Label("Zapnout");
        timerStateLabel.setFont(Font.font(13));
        CheckBox timerStateCheckbox = new CheckBox();
        timerStateCheckbox.selectedProperty().bindBidirectional(timerRunning);
        timerRunning.addListener((obs, oldVal, newVal) -> {
            if(isTimerRunning()) timerGrid.setDisable(false);
            else timerGrid.setDisable(true);

        });

        timerState.getChildren().addAll(timerStateCheckbox, timerStateLabel);

        timerGrid = new GridPane();
        if(!isTimerRunning()) timerGrid.setDisable(true);

        // Co se udělá
        RadioButton timerWhat1Radio = new RadioButton();
        Label timerWhat1Label = new Label("Zapnout přehrávání");
        timerWhat1Label.setFont(Font.font(13));
        timerWhat1Radio.setSelected(true);

        RadioButton timerWhat2Radio = new RadioButton();
        Label timerWhat2Label = new Label("Zastavit přehrávání");
        timerWhat2Label.setFont(Font.font(13));

        RadioButton timerWhat3Radio = new RadioButton();
        Label timerWhat3Label = new Label("Zavřít program");
        timerWhat3Label.setFont(Font.font(13));

        // Kdy se to udělá
        RadioButton timerWhen1Radio = new RadioButton();
        Label timerWhen1Label = new Label("Po uplynutí času");
        timerWhen1Label.setFont(Font.font(13));
        TimeField timerWhen1Timefield = new TimeField();
        timerWhen1Timefield.setPrefWidth(84);
        timerWhen1Timefield.setFieldPrefWidth();
        timerWhen1Radio.setSelected(true);

        RadioButton timerWhen2Radio = new RadioButton();
        Label timerWhen2Label = new Label("V zadanám čase");
        timerWhen2Label.setFont(Font.font(13));
        TimeField timerWhen2Timefield = new TimeField();
        timerWhen2Timefield.setPrefWidth(84);
        timerWhen2Timefield.setFieldPrefWidth();

        HBox buttonWrapper = new HBox();
        Button btnConfirm = new Button("Ok");
        Button btnCancel = new Button("Zrušit");
        btnCancel.setOnAction(e -> timerStage.close());

        timerGrid.add(timerWhat1Radio, 0, 0);
        timerGrid.add(timerWhat1Label, 1, 0);
        timerGrid.add(timerWhat2Radio, 0, 1);
        timerGrid.add(timerWhat2Label, 1, 1);
        timerGrid.add(timerWhat3Radio, 0, 2);
        timerGrid.add(timerWhat3Label, 1, 2);

        timerGrid.add(new Separator(), 0, 3, GridPane.REMAINING, 1);

        timerGrid.add(timerWhen1Radio, 0, 4);
        timerGrid.add(timerWhen1Label, 1, 4);
        timerGrid.add(timerWhen1Timefield, 2, 4);
        timerGrid.add(timerWhen2Radio, 0, 5);
        timerGrid.add(timerWhen2Label, 1, 5);
        timerGrid.add(timerWhen2Timefield, 2, 5);

        ToggleGroup groupWhat = new ToggleGroup();
        groupWhat.getToggles().addAll(timerWhat1Radio, timerWhat2Radio, timerWhat3Radio);

        ToggleGroup groupWhen = new ToggleGroup();
        groupWhen.getToggles().addAll(timerWhen1Radio, timerWhen2Radio);

        timerState.setPadding(new Insets(10, 0, 0, 10));
        timerState.setSpacing(5);

        buttonWrapper.getChildren().addAll(btnConfirm, btnCancel);
        buttonWrapper.getChildren().forEach(n -> ((Region)n).setPrefWidth(100));
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
