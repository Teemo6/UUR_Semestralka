import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import java.io.File;

public class Main extends Application {

    final double ROOT_WINDOW_MIN_WIDTH = 700;
    final double ROOT_WINDOW_MIN_HEIGHT = 500;

    private Stage rootStage;
    private Scene rootScene;
    private BorderPane borderPane;
    private HBox controlHBox;

    private Media media;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;

    private File file = new File("D:/Anime/Solar Opposites/Solar Opposites Episode 2  The Unstable Grey Hole.mp4");
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
        rootScene = new Scene(getRootPane());
        rootScene.getStylesheets().add("resources/stylesheet.css");

        KeyCombination cntrlZ = new KeyCodeCombination(KeyCode.SPACE);
        rootScene.setOnKeyPressed(e -> {
            if(cntrlZ.match(e)){
                Controls.playPause(mediaPlayer);
            }
        });

        rootStage = stage;
        rootStage.setTitle("MediaPlayer - Štěpán Faragula, A21B0119P");
        rootStage.setScene(rootScene);
        rootStage.setMinWidth(ROOT_WINDOW_MIN_WIDTH);
        rootStage.setMinHeight(ROOT_WINDOW_MIN_HEIGHT);
        rootStage.getIcons().add(new Image("/resources/icon.png"));
        rootStage.setOnCloseRequest(e -> Platform.exit());
        //rootStage.setFullScreen(true);

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
        mediaWrapper = new Pane();

        media = new Media(MEDIA_URL);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
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

        return mediaWrapper;
    }

    private Node getBottomControlBar() {
        VBox mediaControl = new VBox();
        HBox timeControl = new HBox();
        HBox playControl = new HBox();

        timeSlider = new Slider();
        timeLabel = new Label("00:00:07 / 00:23:42");

        timeControl.getChildren().addAll(timeSlider, timeLabel);
        timeControl.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));
        timeControl.getChildren().forEach(n -> ((Region)n).setPrefHeight(20));
        timeControl.setSpacing(10);

        btnPlay = new ButtonSVG(IconSVG.PLAY_RIGHT.getPath());
        btnPause = new ButtonSVG(IconSVG.PAUSE.getPath());
        btnPrev = new ButtonSVG(IconSVG.ARROW_LEFT.getPath());
        btnNext = new ButtonSVG(IconSVG.ARROW_RIGHT.getPath());
        soundSlider = new Slider();
        soundLabel = new Label("100%");
        soundLabel.setStyle("-fx-font-size: 12.0 pt;");
        spacer = new Region();
        btnFullscreen = new ButtonSVG(IconSVG.FULLSCREEN.getPath());

        playControl.getChildren().addAll(btnPlay, btnPause, btnPrev, btnNext, soundSlider, soundLabel, spacer, btnFullscreen);
        playControl.getChildren().forEach(n -> ((Region)n).setPrefWidth(30));
        playControl.getChildren().forEach(n -> ((Region)n).setPrefHeight(30));
        soundSlider.setPrefWidth(100);
        playControl.setSpacing(5);
        HBox.setHgrow(spacer, Priority.SOMETIMES);

        mediaControl.getChildren().addAll(timeControl, playControl);

        mediaControl.setAlignment(Pos.CENTER);
        mediaControl.setSpacing(5);
        mediaControl.setPadding(new Insets(5));

        return mediaControl;
    }

    private Node getPlaylist() {
        VBox playlistWrapper = new VBox();

        playlist = new ListView<>(model.mediaQueue.get());
        /*
        playlist.setCellFactory(e -> {
            ListCell<String> cell = new ListCell<>();
            cell.setPrefHeight(20);
            return cell;
        });
*/
        HBox playlistButtonWrapper = new HBox();

        HBox playlistMoveWrapper = new HBox();
        Button playlistMoveStart = new ButtonSVG(IconSVG.FORWARD_UP.getPath());
        Button playlistMoveUp = new ButtonSVG(IconSVG.PLAY_UP.getPath());
        Button playlistMoveDown = new ButtonSVG(IconSVG.PLAY_DOWN.getPath());
        Button playlistMoveEnd = new ButtonSVG(IconSVG.FORWARD_DOWN.getPath());

        HBox playlistManageWrapper = new HBox();
        Button playlistAdd = new ButtonSVG(IconSVG.PLUS.getPath());
        Button playlistRemove = new ButtonSVG(IconSVG.MINUS.getPath());

        HBox playlistOrderWrapper = new HBox();
        Button playlistShuffle = new ButtonSVG(IconSVG.SHUFFLE.getPath());
        Button playlistSort = new ButtonSVG(IconSVG.SORT.getPath());

        playlistMoveWrapper.getChildren().addAll(playlistMoveStart, playlistMoveUp, playlistMoveDown, playlistMoveEnd);
        playlistManageWrapper.getChildren().addAll(playlistAdd, playlistRemove);
        playlistOrderWrapper.getChildren().addAll(playlistShuffle, playlistSort);

        playlistButtonWrapper.getChildren().addAll(playlistMoveWrapper, playlistManageWrapper, playlistOrderWrapper);

        playlistButtonWrapper.getChildren().forEach(n -> ((HBox)n).setSpacing(5));
        playlistButtonWrapper.getChildren().forEach(n -> ((HBox)n).setPadding(new Insets(5)));
        playlistButtonWrapper.getChildren().forEach(n -> ((HBox)n).getChildren().forEach(h -> ((Region)h).setPrefHeight(25)));
        playlistButtonWrapper.getChildren().forEach(n -> ((HBox)n).getChildren().forEach(h -> ((Region)h).setPrefWidth(25)));
        playlistButtonWrapper.setSpacing(10);

        playlistWrapper.getChildren().addAll(playlist, playlistButtonWrapper);
        VBox.setVgrow(playlist, Priority.ALWAYS);

        return playlistWrapper;
    }

    private Node getTopMenuBar() {
        HBox menuBarWrapper = new HBox();

        MenuBar menuBar = new MenuBar();

        Menu openMenu = new Menu("Otevřít");
        MenuItem openFile = new MenuItem("Otevřít soubor");
        KeyCombination openFileCombo = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        openFile.setAccelerator(openFileCombo);
        //openFile.setOnAction(LoaderStage::createLoaderStage);     <---

        MenuItem openFolder= new MenuItem("Otevřít složku");
        MenuItem openURL = new MenuItem("Otevřít URL");
        KeyCombination openURLCombo = new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN);
        openURL.setAccelerator(openURLCombo);
        openURL.setOnAction(LoaderStage::createLoaderStage);

        MenuItem openRecent = new MenuItem("Otevřít nedávné");
        KeyCombination openRecentCombo = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        openRecent.setAccelerator(openRecentCombo);
        openRecent.setOnAction(RecentStage::createRecentStage);
        openMenu.getItems().addAll(openFile, openFolder, openURL, openRecent);

        Menu playMenu = new Menu("Přehrávání");
        MenuItem playPause = new MenuItem("Pustit / Zastavit");
        KeyCombination playPauseCombo = new KeyCodeCombination(KeyCode.SPACE);
        playPause.setAccelerator(playPauseCombo);

        MenuItem playForward = new MenuItem("Posun dopředu");
        KeyCombination playForwardCombo = new KeyCodeCombination(KeyCode.RIGHT);
        playForward.setAccelerator(playForwardCombo);
        playForward.setOnAction(Controls::playForward);

        MenuItem playBackward = new MenuItem("Posun dozadu");
        KeyCombination playBackwardCombo = new KeyCodeCombination(KeyCode.LEFT);
        playBackward.setAccelerator(playBackwardCombo);
        playBackward.setOnAction(Controls::playBackward);

        MenuItem playVolumeUp = new MenuItem("Zvýšit hlasitost");
        KeyCombination playVolumeUpCombo = new KeyCodeCombination(KeyCode.UP);
        playVolumeUp.setAccelerator(playVolumeUpCombo);
        playVolumeUp.setOnAction(Controls::playVolumeUp);

        MenuItem playVolumeDown = new MenuItem("Snížit hlasitost");
        KeyCombination playVolumeDownCombo = new KeyCodeCombination(KeyCode.DOWN);
        playVolumeDown.setAccelerator(playVolumeDownCombo);
        playVolumeDown.setOnAction(Controls::playVolumeDown);

        MenuItem playPrevious = new MenuItem("Předchozí stopa");
        KeyCombination playPreviousCombo = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN);
        playPrevious.setAccelerator(playPreviousCombo);
        playPrevious.setOnAction(Controls::playPrevious);

        MenuItem playNext = new MenuItem("Následující stopa");
        KeyCombination playNextCombo = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);
        playNext.setAccelerator(playNextCombo);
        playNext.setOnAction(Controls::playNext);

        MenuItem playFullscreen = new MenuItem("Celá obrazovka");
        KeyCombination playFullscreenCombo = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        playFullscreen.setAccelerator(playFullscreenCombo);
        playFullscreen.setOnAction(e -> Controls.playFullscreen(e, rootStage));

        playMenu.getItems().addAll(
                playPause, playForward, playBackward, new SeparatorMenuItem(),
                playVolumeUp ,playVolumeDown, new SeparatorMenuItem(),
                playPrevious, playNext, new SeparatorMenuItem(),
                playFullscreen
        );

        Menu timerMenu = new Menu("Časovač");
        Menu application = new Menu("O aplikaci");
        Region spacer = new Region();
        Menu hideQueue = new Menu("✎");

        openFile.setOnAction(AboutStage::createAboutStage);
        openRecent.setOnAction(RecentStage::createRecentStage);
        openURL.setOnAction(LoaderStage::createLoaderStage);
        openFolder.setOnAction(TimerStage::createTimerStage);
        Controls.onMenuClick(hideQueue);

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