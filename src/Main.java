import com.sun.javafx.charts.Legend;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.File;

public class Main extends Application {

    final double ROOT_WINDOW_MIN_WIDTH = 700;
    final double ROOT_WINDOW_MIN_HEIGHT = 500;

    private Stage rootStage;
    private Scene rootScene;
    private BorderPane borderPane;
    private HBox controlHBox;

    // Media
    private MyMediaPlayer mediaPlayer = new MyMediaPlayer();
    private Pane mediaWrapper;
    private MediaView mediaView;

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
    private ListView<File> playlist;

    // KeyCodeCombination
    private final KeyCombination openFileCombo = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
    private final KeyCombination openFolderCombo = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
    private final KeyCombination openURLCombo = new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN);
    private final KeyCombination openRecentCombo = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
    private final KeyCombination playPauseCombo = new KeyCodeCombination(KeyCode.SPACE);
    private final KeyCombination playForwardCombo = new KeyCodeCombination(KeyCode.RIGHT);
    private final KeyCombination playBackwardCombo = new KeyCodeCombination(KeyCode.LEFT);
    private final KeyCombination playVolumeUpCombo = new KeyCodeCombination(KeyCode.UP);
    private final KeyCombination playVolumeDownCombo = new KeyCodeCombination(KeyCode.DOWN);
    private final KeyCombination playPreviousCombo = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN);
    private final KeyCombination playNextCombo = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);
    private final KeyCombination playFullscreenCombo = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);

    public void init(){
        Controls.setMediaPlayer(mediaPlayer);
        //mediaPlayer.initModel();
    }

    @Override
    public void start(Stage stage){
        rootScene = new Scene(getRootPane());
        rootScene.getStylesheets().add("resources/stylesheet.css");
        rootScene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (openFileCombo.match(e)) Controls.openFile();
            if (openFolderCombo.match(e)) Controls.openFolder();
            if (openURLCombo.match(e)) Controls.openURL();
            if (openRecentCombo.match(e)) Controls.openRecent();
            if (playPauseCombo.match(e)) Controls.playPause();
            if (playForwardCombo.match(e)) Controls.playForward();
            if (playBackwardCombo.match(e)) Controls.playBackward();
            if (playVolumeUpCombo.match(e)) Controls.playVolumeUp();
            if (playVolumeDownCombo.match(e)) Controls.playVolumeDown();
            if (playPreviousCombo.match(e)) Controls.playPrevious();
            if (playNextCombo.match(e)) Controls.playNext();
            if (playFullscreenCombo.match(e)) Controls.playFullscreen(rootStage, borderPane);
            e.consume();
        });

        rootStage = stage;
        rootStage.setTitle("MediaPlayer - Štěpán Faragula, A21B0119P");
        rootStage.setScene(rootScene);
        rootStage.setMinWidth(ROOT_WINDOW_MIN_WIDTH);
        rootStage.setMinHeight(ROOT_WINDOW_MIN_HEIGHT);
        rootStage.getIcons().add(new Image("/resources/icon.png"));
        rootStage.setOnCloseRequest(e -> Platform.exit());
        rootStage.show();
    }

    private Parent getRootPane(){
        borderPane = new BorderPane();

        borderPane.setCenter(getMediaPlayerPane());
        borderPane.setTop(getTopMenuBar());
        borderPane.setBottom(getBottomControlBar());
        borderPane.setRight(getPlaylist());
        borderPane.requestFocus();

        borderPane.setOnDragOver(this::handleDragOver);
        borderPane.setOnDragDropped(this::handleDragDropped);

        return borderPane;
    }

    private Node getMediaPlayerPane(){
        mediaWrapper = new Pane();
/*
        if(mediaPlayer.getMediaPlayer() == null){
            Label mediaErrorLabel = new Label("Není co přehrávat\n Otevřete soubor");

            return mediaErrorLabel;
        }
*/
        mediaView = new MediaView(mediaPlayer.getMediaPlayer());
        mediaView.setMediaPlayer(mediaPlayer.getMediaPlayer());
        mediaView.mediaPlayerProperty().bind(mediaPlayer.getMediaPlayerProperty());
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

        playlist = new ListView<>(mediaPlayer.getFileQueue());
        playlist.setPlaceholder(new Label("Není co přehrávat\n Otevřete soubor"));

        playlist.setCellFactory(TextFieldListCell.forListView(new StringConverter<>() {
            @Override
            public String toString(File f) {
                return f.getName();
            }

            @Override
            public File fromString(String string) {
                return null;
            }
        }));


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
        openFile.setAccelerator(openFileCombo);
        openFile.setOnAction(e -> Controls.openFile());

        MenuItem openFolder= new MenuItem("Otevřít složku");
        openFolder.setAccelerator(openFolderCombo);
        openFolder.setOnAction(e -> Controls.openFolder());

        MenuItem openURL = new MenuItem("Otevřít URL");
        openURL.setAccelerator(openURLCombo);
        openURL.setOnAction(e -> Controls.openURL());

        MenuItem openRecent = new MenuItem("Otevřít nedávné");
        openRecent.setAccelerator(openRecentCombo);
        openRecent.setOnAction(e -> Controls.openRecent());

        openMenu.getItems().addAll(openFile, openFolder, openURL, openRecent);

        Menu playMenu = new Menu("Přehrávání");
        MenuItem playPause = new MenuItem("Pustit / Zastavit");
        playPause.setAccelerator(playPauseCombo);
        playPause.setOnAction(e -> Controls.playPause());

        MenuItem playForward = new MenuItem("Posun dopředu");
        playForward.setAccelerator(playForwardCombo);
        playForward.setOnAction(e -> Controls.playForward());

        MenuItem playBackward = new MenuItem("Posun dozadu");
        playBackward.setAccelerator(playBackwardCombo);
        playBackward.setOnAction(e -> Controls.playBackward());

        MenuItem playVolumeUp = new MenuItem("Zvýšit hlasitost");
        playVolumeUp.setAccelerator(playVolumeUpCombo);
        playVolumeUp.setOnAction(e -> Controls.playVolumeUp());

        MenuItem playVolumeDown = new MenuItem("Snížit hlasitost");
        playVolumeDown.setAccelerator(playVolumeDownCombo);
        playVolumeDown.setOnAction(e -> Controls.playVolumeDown());

        MenuItem playPrevious = new MenuItem("Předchozí stopa");
        playPrevious.setAccelerator(playPreviousCombo);
        playPrevious.setOnAction(e -> Controls.playPrevious());

        MenuItem playNext = new MenuItem("Následující stopa");
        playNext.setAccelerator(playNextCombo);
        playNext.setOnAction(e -> Controls.playNext());

        MenuItem playFullscreen = new MenuItem("Celá obrazovka");
        playFullscreen.setAccelerator(playFullscreenCombo);
        playFullscreen.setOnAction(e -> Controls.playFullscreen(rootStage, borderPane));

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

        spacer.getStyleClass().add("menu-bar");
        HBox.setHgrow(spacer, Priority.ALWAYS);
        menuBar.getMenus().addAll(openMenu, playMenu, timerMenu, application);

        menuBarWrapper.getChildren().addAll(menuBar, spacer, new MenuBar(hideQueue));

        return menuBarWrapper;
    }

    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != borderPane && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event){
        Dragboard db = event.getDragboard();
        File file = db.getFiles().get(0);
        System.out.println("file");
    }

    public static void main(String[] args) {
        launch();
    }
}