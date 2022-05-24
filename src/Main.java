import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import java.io.File;

public class Main extends Application {

    final double ROOT_WINDOW_MIN_WIDTH = 700;
    final double ROOT_WINDOW_MIN_HEIGHT = 500;

    private Stage rootStage;
    private Scene rootScene;
    private BorderPane borderPane;
    private HBox controlHBox;

    // Media
    private MyMediaPlayer myMediaPlayer = new MyMediaPlayer();
    private ObjectProperty<MediaPlayer> currentMediaPlayer = new SimpleObjectProperty<>();
    private Pane mediaWrapper;
    private MediaView mediaView;

    // Control pane
    private Slider timeSlider = new Slider();
    private Label timeLabel = new Label();

    private Button btnPlay;
    private Button btnPause;
    private Button btnPrev;
    private Button btnNext;
    private Slider soundSlider = new Slider();
    private Label soundLabel = new Label();
    private Region spacer;
    private Button btnFullscreen;

    // Playlist
    private ListView<Media> playlist;

    // KeyCodeCombination
    private final KeyCombination openFileCombo = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
    private final KeyCombination openFolderCombo = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
    private final KeyCombination openURLCombo = new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN);
    private final KeyCombination playPauseCombo = new KeyCodeCombination(KeyCode.SPACE);
    private final KeyCombination playForwardCombo = new KeyCodeCombination(KeyCode.RIGHT);
    private final KeyCombination playBackwardCombo = new KeyCodeCombination(KeyCode.LEFT);
    private final KeyCombination playVolumeUpCombo = new KeyCodeCombination(KeyCode.UP);
    private final KeyCombination playVolumeDownCombo = new KeyCodeCombination(KeyCode.DOWN);
    private final KeyCombination playPreviousCombo = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN);
    private final KeyCombination playNextCombo = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);
    private final KeyCombination playFullscreenCombo = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);

    public void init(){
        currentMediaPlayer.bind(myMediaPlayer.getMediaPlayerProperty());

        // Binding prvku
        currentMediaPlayer.addListener((obs, oldVal, newVal) -> {
            // Slider videa
            InvalidationListener sliderChangeListener = o-> {
                if(currentMediaPlayer.getValue() != null)
                    currentMediaPlayer.getValue().seek(currentMediaPlayer.getValue().getMedia().getDuration().multiply(timeSlider.getValue() / 100.0));
            };

            timeSlider.valueProperty().addListener(sliderChangeListener);

            timeSlider.valueProperty().bind(currentMediaPlayer.getValue().volumeProperty());
            timeSlider.valueProperty().removeListener(sliderChangeListener);

            double value = (currentMediaPlayer.getValue().getCurrentTime().toSeconds() / currentMediaPlayer.getValue().getMedia().getDuration().toSeconds()) * 100;
            timeSlider.setValue(value);

            timeSlider.valueProperty().addListener(sliderChangeListener);

            // Label videa
            timeLabel.textProperty().bind(
                    Bindings.createStringBinding(() -> {
                                Duration current = currentMediaPlayer.getValue().getCurrentTime();
                                Duration total = currentMediaPlayer.getValue().getTotalDuration();
                                return String.format("%02d:%02d:%02d / %02d:%02d:%02d",
                                        (int) current.toHours(),
                                        (int) current.toMinutes() % 60,
                                        (int) current.toSeconds() % 60,

                                        (int) total.toHours(),
                                        (int) total.toMinutes() % 60,
                                        (int) total.toSeconds() % 60);
                            },
                            currentMediaPlayer.getValue().currentTimeProperty()
                    ));

            // Slider hlasitosti
            soundLabel.textProperty().bind(
                    Bindings.createStringBinding(() -> {
                                int volume = (int) (currentMediaPlayer.getValue().getVolume() * 100);
                                return String.format("%d%%", volume);
                            },
                            currentMediaPlayer.getValue().volumeProperty()
                    ));
        });

        Controls.setMediaPlayer(myMediaPlayer);
        myMediaPlayer.initModel();
    }

    @Override
    public void start(Stage stage){
        rootScene = new Scene(getRootPane());
        rootScene.getStylesheets().add("resources/stylesheet.css");
        rootScene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (openFileCombo.match(e)) Controls.openFile();
            if (openFolderCombo.match(e)) Controls.openFolder();
            if (openURLCombo.match(e)) Controls.openURL();
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

        mediaView = new MediaView(currentMediaPlayer.getValue());
        mediaView.setMediaPlayer(currentMediaPlayer.getValue());
        mediaView.mediaPlayerProperty().bind(currentMediaPlayer);
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

        timeControl.getChildren().addAll(timeSlider, timeLabel);
        timeControl.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));
        timeControl.getChildren().forEach(n -> ((Region)n).setPrefHeight(20));
        timeControl.setSpacing(10);

        btnPlay = new ButtonSVG(IconSVG.PLAY_RIGHT.getPath());
        btnPlay.setOnAction(e -> Controls.playPause());

        btnPause = new ButtonSVG(IconSVG.PAUSE.getPath());
        btnPause.setOnAction(e -> Controls.playPause());

        btnPrev = new ButtonSVG(IconSVG.ARROW_LEFT.getPath());
        btnPrev.setOnAction(e -> Controls.playBackward());

        btnNext = new ButtonSVG(IconSVG.ARROW_RIGHT.getPath());
        btnNext.setOnAction(e -> Controls.playForward());

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

        playlist = new ListView<>(myMediaPlayer.getFileQueue());
        playlist.setPlaceholder(new Label("Není co přehrávat\n Otevřete soubor"));

        playlist.setCellFactory(TextFieldListCell.forListView(new StringConverter<>() {
            @Override
            public String toString(Media f) {
                return f.getSource();
            }

            @Override
            public Media fromString(String string) {
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
        playlistAdd.setOnAction(e -> Controls.addToQueue());

        Button playlistRemove = new ButtonSVG(IconSVG.MINUS.getPath());
        playlistRemove.setOnAction(e -> Controls.removeFromQueue(playlist.getSelectionModel().getSelectedItem()));

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
        openMenu.getItems().addAll(openFile, openFolder, openURL);

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
        System.out.println(file.getName());
    }

    public static void main(String[] args) {
        launch();
    }
}