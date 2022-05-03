import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
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

public class RecentStage {
    private static final double RECENT_WINDOW_MIN_WIDTH = 350;
    private static final double RECENT_WINDOW_MIN_HEIGHT = 300;

    private static Stage recentStage;
    private static Scene recentScene;

    public static void createRecentStage(ActionEvent a){
        if (recentStage != null && recentStage.isShowing()){
            recentStage.close();
        }
        recentScene = new Scene(getRecentPane());
        recentScene.getStylesheets().add("resources/stylesheet.css");

        recentStage = new Stage();
        recentStage.setTitle("Nedávná média");
        recentStage.setScene(recentScene);
        recentStage.setMinWidth(RECENT_WINDOW_MIN_WIDTH);
        recentStage.setMinHeight(RECENT_WINDOW_MIN_HEIGHT);
        recentStage.getIcons().add(new Image("/resources/icon.png"));
        recentStage.setResizable(false);
        recentStage.show();
    }

    private static Parent getRecentPane() {
        VBox timerWrapper = new VBox();

        HBox timerState = new HBox();
        CheckBox timerStateCheckbox = new CheckBox();
        Label timerStateLabel = new Label("Zapnout");
        timerStateLabel.setFont(Font.font(13));
        timerState.getChildren().addAll(timerStateCheckbox, timerStateLabel);

        GridPane timerGrid = new GridPane();

        // Co se udělá
        RadioButton timerWhat1Radio = new RadioButton();
        Label timerWhat1Label = new Label("Zapnout přehrávání");
        timerWhat1Label.setFont(Font.font(13));
        timerWhat1Radio.setSelected(true);

        RadioButton timerWhat2Radio = new RadioButton();
        Label timerWhat2Label = new Label("Zastavit přehrávání");
        timerWhat2Label.setFont(Font.font(13));

        RadioButton timerWhat3Radio = new RadioButton();
        Label timerWhat3Label = new Label("Zastavit přehrávání");
        timerWhat3Label.setFont(Font.font(13));

        // Kdy se to udělá
        RadioButton timerWhen1Radio = new RadioButton();
        Label timerWhen1Label = new Label("Po uplynutí zadaného času");
        timerWhen1Label.setFont(Font.font(13));
        TextField timerWhen1Textfield = new TextField("00:00:00");
        timerWhen1Textfield.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        timerWhen1Textfield.setPrefWidth(60);
        timerWhen1Radio.setSelected(true);

        RadioButton timerWhen2Radio = new RadioButton();
        Label timerWhen2Label = new Label("V zadanám čase");
        timerWhen2Label.setFont(Font.font(13));
        TextField timerWhen2Textfield = new TextField("00:00:00");
        timerWhen2Textfield.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        timerWhen2Textfield.setPrefWidth(60);

        HBox buttonWrapper = new HBox();
        Button btnConfirm = new Button("Ok");
        Button btnCancel = new Button("Zrušit");

        timerGrid.add(timerWhat1Radio, 0, 0);
        timerGrid.add(timerWhat1Label, 1, 0);
        timerGrid.add(timerWhat2Radio, 0, 1);
        timerGrid.add(timerWhat2Label, 1, 1);
        timerGrid.add(timerWhat3Radio, 0, 2);
        timerGrid.add(timerWhat3Label, 1, 2);

        timerGrid.add(new Separator(), 0, 3, GridPane.REMAINING, 1);

        timerGrid.add(timerWhen1Radio, 0, 4);
        timerGrid.add(timerWhen1Label, 1, 4);
        timerGrid.add(timerWhen1Textfield, 2, 4);
        timerGrid.add(timerWhen2Radio, 0, 5);
        timerGrid.add(timerWhen2Label, 1, 5);
        timerGrid.add(timerWhen2Textfield, 2, 5);

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
}
