import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoaderStage {
    private final static double LOADER_WINDOW_MIN_WIDTH = 500;
    private final static double LOADER_WINDOW_MIN_HEIGHT = 400;

    private static Stage loaderStage;
    private static Scene loaderScene;

    public static void createLoaderStage(ActionEvent a){
        if (loaderStage != null && loaderStage.isShowing()){
            loaderStage.close();
        }
        loaderScene = new Scene(getLoaderPane());
        loaderScene.getStylesheets().add("resources/stylesheet.css");

        loaderStage = new Stage();
        loaderStage.setTitle("Otevřít URL");
        loaderStage.setScene(loaderScene);
        loaderStage.setMinWidth(LOADER_WINDOW_MIN_WIDTH);
        loaderStage.setMinHeight(LOADER_WINDOW_MIN_HEIGHT);
        loaderStage.getIcons().add(new Image("/resources/icon.png"));
        loaderStage.show();
    }

    private static Parent getLoaderPane() {
        BorderPane loaderWrapper = new BorderPane();

        Label tutorLabel = new Label("Zadejte libovolný počet URL oddělené klávesou ENTER");
        tutorLabel.setFont(Font.font(13));
        tutorLabel.setPadding(new Insets(5));

        TextArea loadArea = new TextArea();
        loadArea.setPrefRowCount(16);
        loadArea.setPadding(new Insets(0, 5, 0, 5));

        HBox buttonWrapper = new HBox();
        Button btnConfirm = new Button("Ok");
        Button btnCancel = new Button("Zrušit");

        buttonWrapper.getChildren().addAll(btnConfirm, btnCancel);
        buttonWrapper.getChildren().forEach(n -> ((Region)n).setPrefWidth(100));
        buttonWrapper.getChildren().forEach(n -> ((Region)n).setPrefHeight(20));
        buttonWrapper.setAlignment(Pos.CENTER);
        buttonWrapper.setSpacing(10);
        buttonWrapper.setPadding(new Insets(10));

        loaderWrapper.setTop(tutorLabel);
        loaderWrapper.setCenter(loadArea);
        loaderWrapper.setBottom(buttonWrapper);

        BorderPane.setAlignment(tutorLabel, Pos.CENTER);

        return loaderWrapper;
    }

}
