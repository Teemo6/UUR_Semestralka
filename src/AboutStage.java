import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileInputStream;

public class AboutStage {
    private static final double ABOUT_WINDOW_MIN_WIDTH = 350;
    private static final double ABOUT_WINDOW_MIN_HEIGHT = 200;

    private static Stage aboutStage;
    private static Scene aboutScene;

    public static void createAboutStage(ActionEvent a){
        if (aboutStage != null && aboutStage.isShowing()){
            aboutStage.close();
        }
        aboutScene = new Scene(getAboutPane());
        aboutScene.getStylesheets().add("resources/stylesheet.css");

        aboutStage = new Stage();
        aboutStage.setTitle("O aplikaci");
        aboutStage.setScene(aboutScene);
        aboutStage.setMinWidth(ABOUT_WINDOW_MIN_WIDTH);
        aboutStage.setMinHeight(ABOUT_WINDOW_MIN_HEIGHT);
        aboutStage.getIcons().add(new Image("/resources/icon.png"));
        aboutStage.setResizable(false);
        aboutStage.show();
    }

    private static Parent getAboutPane() {
        VBox aboutWrapper = new VBox();

        GridPane aboutGrid = new GridPane();

        Image appIcon;
        ImageView appIconView = new ImageView();

        try {
            appIcon = new Image(new FileInputStream("src/resources/icon.png"));
            appIconView = new ImageView(appIcon);
        } catch(Exception e) {
            System.out.println(e);
        }

        Label aboutName = new Label("MediaPlayer - Semestrální práce UUR");
        Label aboutMe = new Label("Štěpán Faragula, 2022");
        Button btnExit = new Button("Zavřít");

        aboutGrid.add(appIconView, 0,0,2,2);
        aboutGrid.add(aboutName, 2, 0);
        aboutGrid.add(aboutMe, 2, 1);

        aboutGrid.setHgap(10);
        aboutGrid.setVgap(5);
        aboutGrid.setAlignment(Pos.TOP_CENTER);
        aboutGrid.setPadding(new Insets(10, 0, 30, 0));

        btnExit.setPrefWidth(100);
        btnExit.setPrefHeight(20);

        aboutWrapper.getChildren().addAll(aboutGrid, btnExit);
        aboutWrapper.setAlignment(Pos.CENTER);

        return aboutWrapper;
    }
}
