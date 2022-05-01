import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    final double WINDOW_MIN_WIDTH = 500;
    final double WINDOW_MIN_HEIGHT = 500;

    private Stage rootStage;
    private Scene scene;
    private BorderPane borderPane;
    private HBox controlHBox;

    private Media media;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;

    private File file = new File("D:/Anime/Sabikui Bisco/Sabikui Bisco Episode 01.mp4");
    private final String MEDIA_URL = file.toURI().toString();

    private Button btnPlay;
    private Button btnPause;

    @Override
    public void start(Stage stage){
        rootStage = stage;
        rootStage.setTitle("MediaPlayer");
        rootStage.setScene(new Scene(getRootPane()));
        rootStage.setMinWidth(WINDOW_MIN_WIDTH);
        rootStage.setMinHeight(WINDOW_MIN_HEIGHT);

        rootStage.setFullScreen(true);

        rootStage.show();
    }

    private Parent getRootPane(){
        borderPane = new BorderPane();

        borderPane.setCenter(getMediaPlayerPane());
        borderPane.setTop(getTopMenuBar());
        borderPane.setBottom(getBottomControlBar());
        borderPane.setRight(getPlaylist());

        return borderPane;
    }

    private Node getMediaPlayerPane(){

        media = new Media(MEDIA_URL);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);

        mediaPlayer.setOnReady(rootStage::sizeToScene);
        mediaView = new MediaView(mediaPlayer);

        mediaView.fitWidthProperty().bind(Bindings.selectDouble(borderPane.sceneProperty(), "width"));
        mediaView.fitHeightProperty().bind(Bindings.selectDouble(borderPane.sceneProperty(), "height"));
        mediaView.setPreserveRatio(true);

        return mediaView;
    }

    private Node getBottomControlBar() {

        VBox mediaControl = new VBox();
        HBox timeControl = new HBox();
        HBox playControl = new HBox();

        btnPlay = new Button(">");
        btnPause = new Button("||");
        Button btnPrev = new Button("<-");
        Button btnNext = new Button("->");
        Slider soundSlider = new Slider();
        Button btnFullscreen = new Button("|=|");

        Slider timeSlider = new Slider();
        Label timeLabel = new Label("Time: ");

        timeControl.getChildren().addAll(timeSlider, timeLabel);
        playControl.getChildren().addAll(btnPlay, btnPause, btnPrev, btnNext, soundSlider, btnFullscreen);

        mediaControl.getChildren().addAll(timeControl, playControl);

        mediaControl.setAlignment(Pos.CENTER);
        mediaControl.setPadding(new Insets(5));
        BorderPane.setAlignment(mediaControl, Pos.BOTTOM_CENTER);

        return mediaControl;
    }

    private Node getPlaylist() {
        return controlHBox;
    }

    private Node getTopMenuBar() {
        HBox rootMenuBar = new HBox();

        MenuBar leftMenuBar = new MenuBar();
        MenuBar rightMenuBar = new MenuBar();

        Menu programMenu = new Menu("Program");
        MenuItem saveItem = new MenuItem("Save data");
        MenuItem uploadItem = new MenuItem("Upload data");
        MenuItem updateItem = new MenuItem("Update software");
        programMenu.getItems().addAll(saveItem, uploadItem, new SeparatorMenuItem(), updateItem);
        leftMenuBar.getMenus().addAll(programMenu);

        Region spacer = new Region();
        spacer.getStyleClass().add("menu-bar");
        HBox.setHgrow(spacer, Priority.SOMETIMES);

        Menu runMenu = new Menu("Run");
        Menu stopMenu = new Menu("Stop");
        rightMenuBar.getMenus().addAll(runMenu, stopMenu);

        rootMenuBar.getChildren().addAll(leftMenuBar, spacer, rightMenuBar);

        return rootMenuBar;
    }

    public static void main(String[] args) {
        launch();
    }
}