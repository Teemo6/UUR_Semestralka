import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.File;

public class Main extends Application {

    final double ROOT_WINDOW_MIN_WIDTH = 700;
    final double ROOT_WINDOW_MIN_HEIGHT = 500;

    final double LOADER_WINDOW_MIN_WIDTH = 500;
    final double LOADER_WINDOW_MIN_HEIGHT = 400;

    final double TIMER_WINDOW_MIN_WIDTH = 350;
    final double TIMER_WINDOW_MIN_HEIGHT = 300;

    private Stage rootStage, timerStage, loaderStage;
    private Scene rootScene, timeScene, loaderScene;
    private BorderPane borderPane;
    private HBox controlHBox;

    private Media media;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;

    private File file = new File("D:/Anime/Jahy-sama Will NOT Be Defeated/Jahy-sama wa Kujikenai Episode 01.mp4");
    private final String MEDIA_URL = file.toURI().toString();

    //private String MEDIA_URL = "https://www.youtube.com/watch?v=hh1WeQxfCX0";
    //private String MEDIA_URL = "https://www.kiv.zcu.cz/~herout/vyuka/oop/video/oop-07.mp4";

    // Media view
    private Pane mediaWrapper;

    // Control pane
    private Slider timeSlider;
    private Label timeLabel;

    private Button btnPlay;
    private Button btnPause;
    private Button btnPrev;
    private Button btnNext;
    private Slider soundSlider;
    private Label soundLabel;
    private Region spacer;
    private Button btnFullscreen;

    // Playlist
    private ListView<String> playlist;
    DataModel model = new DataModel();

    public void init(){
        model.initModel();
    }

    @Override
    public void start(Stage stage){
        rootStage = stage;
        rootStage.setTitle("MediaPlayer");
        rootStage.setScene(new Scene(getRootPane()));
        rootStage.setMinWidth(ROOT_WINDOW_MIN_WIDTH);
        rootStage.setMinHeight(ROOT_WINDOW_MIN_HEIGHT);

        //rootStage.setFullScreen(true);

        rootStage.show();
    }

    public void createLoaderStage(ActionEvent a){
        if (loaderStage != null && loaderStage.isShowing()){
            loaderStage.close();
        }

        loaderStage = new Stage();
        loaderStage.setTitle("Otevřít URL");
        loaderStage.setScene(new Scene(getLoaderPane()));
        loaderStage.setMinWidth(LOADER_WINDOW_MIN_WIDTH);
        loaderStage.setMinHeight(LOADER_WINDOW_MIN_HEIGHT);
        loaderStage.show();
    }

    private Parent getLoaderPane() {
        BorderPane loaderWrapper = new BorderPane();

        Label tutorLabel = new Label("Zadejte libovolný počet URL oddělené klávesou ENTER");
        tutorLabel.setFont(Font.font(13));
        tutorLabel.setPadding(new Insets(5));

        TextArea loadArea = new TextArea();
        loadArea.setPrefRowCount(16);
        loadArea.setPadding(new Insets(0, 5, 0, 5));
        loadArea.setStyle("-fx-focus-color: transparent;");
        loadArea.setStyle("-fx-faint-focus-color: transparent;");

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

    public void createTimerStage(ActionEvent a){
        if (timerStage != null && timerStage.isShowing()){
            timerStage.close();
        }

        timerStage = new Stage();
        timerStage.setTitle("Časovač");
        timerStage.setScene(new Scene(getTimerPane()));
        timerStage.setMinWidth(TIMER_WINDOW_MIN_WIDTH);
        timerStage.setMinHeight(TIMER_WINDOW_MIN_HEIGHT);
        timerStage.setResizable(false);
        timerStage.show();
    }

    private Parent getTimerPane() {
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

    private Parent getRootPane(){
        borderPane = new BorderPane();

        borderPane.setCenter(getMediaPlayerPane());
        borderPane.setTop(getTopMenuBar());
        borderPane.setBottom(getBottomControlBar());
        borderPane.setRight(getPlaylist());

        return borderPane;
    }

    private Node getMediaPlayerPane(){
        mediaWrapper = new Pane();

        media = new Media(MEDIA_URL);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setOnReady(rootStage::sizeToScene);
        mediaPlayer.pause();

        mediaView = new MediaView(mediaPlayer);
        mediaView.fitWidthProperty().bind(mediaWrapper.widthProperty());
        mediaView.fitHeightProperty().bind(mediaWrapper.heightProperty());
        mediaView.setPreserveRatio(true);

        InvalidationListener centerMediaView = o -> {
            Bounds mediaBounds = mediaView.getLayoutBounds();
            mediaView.setX((mediaWrapper.getWidth() - mediaBounds.getWidth()) / 2);
            mediaView.setY((mediaWrapper.getHeight() - mediaBounds.getHeight()) / 2);
        };
        mediaWrapper.heightProperty().addListener(centerMediaView);
        mediaWrapper.widthProperty().addListener(centerMediaView);

        mediaWrapper.getChildren().add(mediaView);
        mediaWrapper.setStyle("-fx-background-color: #1c1c1c;");

        return mediaWrapper;
    }

    private Node getBottomControlBar() {
        VBox mediaControl = new VBox();
        HBox timeControl = new HBox();
        HBox playControl = new HBox();

        timeSlider = new Slider();
        timeLabel = new Label("00:00:00 / 12:34:54");

        timeControl.getChildren().addAll(timeSlider, timeLabel);
        timeControl.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));
        timeControl.getChildren().forEach(n -> ((Region)n).setPrefHeight(20));
        timeControl.setSpacing(10);

        btnPlay = new Button(">");
        btnPause = new Button("||");
        btnPrev = new Button("<-");
        btnNext = new Button("->");
        soundSlider = new Slider();
        soundLabel = new Label("100%");
        spacer = new Region();
        btnFullscreen = new Button("|=|");

        playControl.getChildren().addAll(btnPlay, btnPause, btnPrev, btnNext, soundSlider, soundLabel, spacer, btnFullscreen);
        playControl.getChildren().forEach(n -> ((Region)n).setPrefWidth(30));
        playControl.getChildren().forEach(n -> ((Region)n).setPrefHeight(30));
        soundSlider.setPrefWidth(110);
        playControl.setSpacing(5);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        mediaControl.getChildren().addAll(timeControl, playControl);

        mediaControl.setAlignment(Pos.CENTER);
        mediaControl.setSpacing(5);
        mediaControl.setPadding(new Insets(5));

        return mediaControl;
    }
    private Node getPlaylist() {
        playlist = new ListView<>(model.mediaQueue.get());

        return playlist;
    }

    private Node getTopMenuBar() {
        HBox menuBarWrapper = new HBox();

        MenuBar menuBar = new MenuBar();

        Menu openMenu = new Menu("Otevřít");
        MenuItem openFile = new MenuItem("Otevřít soubor");
        MenuItem openFolder= new MenuItem("Otevřít složku");
        MenuItem openURL = new MenuItem("Otevřít URL");
        openMenu.getItems().addAll(openFile, openFolder, openURL);

        Menu playMenu = new Menu("Přehrávání");
        MenuItem playPlay = new MenuItem("Pustit");
        MenuItem playPause = new MenuItem("Zastavit");
        MenuItem playPrevious = new MenuItem("Předchozí stopa");
        MenuItem playNext = new MenuItem("Následující stopa");
        MenuItem playFullscreen = new MenuItem("Režim celé obrazovky");
        playMenu.getItems().addAll(playPlay, playPause, new SeparatorMenuItem(), playPrevious, playNext, new SeparatorMenuItem(), playFullscreen);

        Menu timerMenu = new Menu("Časovač");
        Menu application = new Menu("O aplikaci");
        Region spacer = new Region();
        Menu hideQueue = new Menu("\\");

        openURL.setOnAction(this::createLoaderStage);
        openFolder.setOnAction(this::createTimerStage);

        spacer.getStyleClass().add("menu-bar");
        HBox.setHgrow(spacer, Priority.ALWAYS);
        menuBar.getMenus().addAll(openMenu, playMenu, timerMenu, application);

        menuBarWrapper.getChildren().addAll(menuBar, spacer, new MenuBar(hideQueue));

        return menuBarWrapper;
    }

    public static void main(String[] args) {
        launch();
    }
}