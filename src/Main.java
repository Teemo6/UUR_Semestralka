import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
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
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main extends Application {

    final double ROOT_WINDOW_MIN_WIDTH = 700;
    final double ROOT_WINDOW_MIN_HEIGHT = 500;

    final int SEEK_TIME = 5;
    final int VOLUME_CHANGE = 5;
    final boolean START_WITH_PLAYLIST_SHOWN = true;

    IntegerProperty playerVolume = new SimpleIntegerProperty(100);

    private Stage rootStage;
    private Scene rootScene;
    private BorderPane borderPane;

    // Media
    private DataModel dataModel = new DataModel();
    private ObjectProperty<MediaPlayer> currentMediaPlayer = new SimpleObjectProperty<>();
    private Pane mediaWrapper;
    private MediaView mediaView;
    private Node mediaPlayerPane;

    // Control pane
    private Slider timeSlider = new Slider();
    private Label timeLabel = new Label();

    private ButtonSVG btnPlay;
    private ButtonSVG btnPrev;
    private ButtonSVG btnNext;
    private ButtonSVG btnPrevFile;
    private ButtonSVG btnNextFile;
    private Slider soundSlider = new Slider();
    private Label soundLabel = new Label();
    private Region spacer;
    private Button btnFullscreen;

    // Top menu
    private ButtonMenu hideQueue;
    private BooleanProperty listVisiblePreference = new SimpleBooleanProperty(START_WITH_PLAYLIST_SHOWN);

    // Playlist
    private ListView<Media> playlist;
    private IntegerProperty playlistIndexPlaying = new SimpleIntegerProperty();
    private IntegerProperty playlistIndexSelected = new SimpleIntegerProperty();

    // Stages
    private AppearanceStage appearanceStage = AppearanceStage.getInstance();
    private TimerStage timerStage = TimerStage.getInstance();
    private AboutStage aboutStage = AboutStage.getInstance();

    // KeyCodeCombination
    private final KeyCombination openFileCombo = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
    private final KeyCombination openFolderCombo = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
    private final KeyCombination playPauseCombo = new KeyCodeCombination(KeyCode.SPACE);
    private final KeyCombination playForwardCombo = new KeyCodeCombination(KeyCode.RIGHT);
    private final KeyCombination playBackwardCombo = new KeyCodeCombination(KeyCode.LEFT);
    private final KeyCombination playVolumeUpCombo = new KeyCodeCombination(KeyCode.UP);
    private final KeyCombination playVolumeDownCombo = new KeyCodeCombination(KeyCode.DOWN);
    private final KeyCombination playPreviousCombo = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN);
    private final KeyCombination playNextCombo = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);
    private final KeyCombination playFullscreenCombo = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
    // tohle omylem nikdo nenakliká ...
    // když jo, rozbije si fullscreen :)
    private final KeyCombination unrealCombo = new KeyCodeCombination(KeyCode.UNDERSCORE, KeyCombination.CONTROL_DOWN, KeyCombination.META_DOWN, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN, KeyCombination.SHORTCUT_DOWN);

    public void init() {
        ControlsTimer.initTimer(dataModel);

        currentMediaPlayer.bind(dataModel.mediaPlayerProperty());

        soundSlider.maxProperty().set(100);
        soundSlider.valueProperty().bindBidirectional(playerVolume);
        soundLabel.textProperty().bind(Bindings.createStringBinding(() -> String.format("%d%%", playerVolume.get()), playerVolume));
        soundLabel.setStyle("-fx-font-size: 12.0 pt;");
        // Binding MediaPlayer
        currentMediaPlayer.addListener((obs, oldVal, newVal) -> updateBinding(newVal));
    }


    public void updateBinding(MediaPlayer newPlayer) {
        timeSlider.maxProperty().unbind();
        timeLabel.textProperty().unbind();

        timeSlider.setValue(0);
        timeLabel.setText("00:00:00/00:00:00");

        if (newPlayer == null) {
            return;
        }

        // Slider videa
        timeSlider.maxProperty().bind(Bindings.createDoubleBinding(() -> newPlayer.getTotalDuration().toSeconds(), newPlayer.totalDurationProperty()));

        InvalidationListener timeSliderChangeListener = o -> {
            try {
                Duration seekTo = Duration.seconds(timeSlider.getValue());
                newPlayer.seek(seekTo);
            } catch (Exception ignored) {
            }
        };

        timeSlider.valueProperty().addListener(timeSliderChangeListener);

        newPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            try {
                timeSlider.valueProperty().removeListener(timeSliderChangeListener);
                timeSlider.setValue(newPlayer.currentTimeProperty().get().toSeconds());
                timeSlider.valueProperty().addListener(timeSliderChangeListener);
            } catch (Exception ignored) {
            }
        });

        // Label videa
        timeLabel.textProperty().bind(
                Bindings.createStringBinding(() -> {
                            Duration current = newPlayer.getCurrentTime();
                            Duration total = newPlayer.getTotalDuration();
                            return String.format("%02d:%02d:%02d / %02d:%02d:%02d",
                                    (int) current.toHours(),
                                    (int) current.toMinutes() % 60,
                                    (int) current.toSeconds() % 60,

                                    (int) total.toHours(),
                                    (int) total.toMinutes() % 60,
                                    (int) total.toSeconds() % 60);
                        },
                        newPlayer.currentTimeProperty()
                ));

        // Binding hlasitosti videa na hlasitost prehravace
        newPlayer.volumeProperty().bind(Bindings.createDoubleBinding(() -> playerVolume.get() / 100.0, playerVolume));
    }

    @Override
    public void start(Stage stage) {
        Parent parentMain = getRootPane();
        parentMain.getStylesheets().add("resources/stylesheet.css");

        try {
            String programPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            Path parent = Paths.get(programPath).getParent();
            String colorCSS = "file:/" + parent.toString().replace("\\", "/") + "/customColor.css";

            parentMain.getStylesheets().add(colorCSS);
            ControlsCSS.setParentMain(parentMain);
            ControlsCSS.parseCSSFile();
            ControlsCSS.refreshCSS();
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Chybí CSS");
            alert.setHeaderText("Nepodařilo se načíst CSS soubor");
            alert.setContentText(
                    "Přehrávač funguje, jenom má defaultní barvu rozhraní.\n" +
                    "ALE!!! můžete vytvořit nový soubor!!!\n" +
                    "(Vzhled -> Vytvořit)\n" +
                    "Poté restartujte program, měli byste vidět zvolenou barvu.");
            alert.showAndWait();
            parentMain.getStylesheets().add("resources/customColor.css");
        }

        rootScene = new Scene(parentMain);
        rootScene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (openFileCombo.match(e)) overwriteQueueWithFile();
            if (openFolderCombo.match(e)) overwriteQueueWithFolder();
            if (playPauseCombo.match(e)) dataModel.playOrPause();
            if (playForwardCombo.match(e)) dataModel.moveTime(SEEK_TIME);
            if (playBackwardCombo.match(e)) dataModel.moveTime(-SEEK_TIME);
            if (playVolumeUpCombo.match(e)) movePlayerVolume(VOLUME_CHANGE);
            if (playVolumeDownCombo.match(e)) movePlayerVolume(-VOLUME_CHANGE);
            if (playPreviousCombo.match(e)) dataModel.playPrevious();
            if (playNextCombo.match(e)) dataModel.playNext();
            if (playFullscreenCombo.match(e)) switchFullscreen();
            e.consume();
        });

        rootStage = stage;
        rootStage.setTitle("MediaPlayer - Štěpán Faragula, A21B0119P");
        rootStage.setScene(rootScene);
        rootStage.setMinWidth(ROOT_WINDOW_MIN_WIDTH);
        rootStage.setMinHeight(ROOT_WINDOW_MIN_HEIGHT);
        rootStage.getIcons().add(new Image("/resources/icon.png"));
        rootStage.setOnCloseRequest(e -> Platform.exit());
        rootStage.setFullScreenExitHint("[Ctrl + F] ukončí režim celé obrazovky.");
        rootStage.setFullScreenExitKeyCombination(unrealCombo);
        rootStage.show();
    }

    private Parent getRootPane() {
        borderPane = new BorderPane();

        mediaPlayerPane = getMediaPlayerPane();

        borderPane.setCenter(mediaPlayerPane);
        borderPane.setTop(getTopMenuBar());
        borderPane.setBottom(getBottomControlBar());
        borderPane.setRight(getPlaylist());
        borderPane.requestFocus();

        borderPane.setOnDragOver(this::handleDragOver);
        borderPane.setOnDragDropped(this::handleDragDropped);

        return borderPane;
    }

    private Node getMediaPlayerPane() {
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
        timeControl.getChildren().forEach(n -> ((Region) n).setPrefHeight(20));
        timeControl.setSpacing(10);

        btnPlay = new ButtonSVG(new SVGPath());
        btnPlay.setOnAction(e -> dataModel.playOrPause());
        btnPlay.pathProperty().bind(Bindings.createObjectBinding(() -> {
                    if (dataModel.isPlayingProperty().get()) return IconSVG.PAUSE.getSVGPath();
                    else return IconSVG.PLAY_RIGHT.getSVGPath();
                }, dataModel.isPlayingProperty()
        ));

        btnPrev = new ButtonSVG(IconSVG.ARROW_LEFT.getSVGPath());
        btnPrev.setOnAction(e -> dataModel.moveTime(-SEEK_TIME));

        btnNext = new ButtonSVG(IconSVG.ARROW_RIGHT.getSVGPath());
        btnNext.setOnAction(e -> dataModel.moveTime(SEEK_TIME));

        btnPrevFile = new ButtonSVG(IconSVG.FORWARD_LEFT.getSVGPath());
        btnPrevFile.setOnAction(e -> dataModel.playPrevious());

        btnNextFile = new ButtonSVG(IconSVG.FORWARD_RIGHT.getSVGPath());
        btnNextFile.setOnAction(e -> dataModel.playNext());

        soundLabel.setStyle("-fx-font-size: 12.0 pt;");
        spacer = new Region();


        ButtonSVG btnTimer = new ButtonSVG(IconSVG.CLOCK.getSVGPath());
        btnTimer.getStyleClass().add("disButton");
        btnTimer.disableProperty().bind(ControlsTimer.timerRunning.not());


        btnFullscreen = new ButtonSVG(IconSVG.FULLSCREEN.getSVGPath());
        btnFullscreen.setOnAction(e -> switchFullscreen());

        playControl.getChildren().addAll(btnPlay, btnPrev, btnNext, btnPrevFile, btnNextFile, soundSlider, soundLabel, spacer, btnTimer, btnFullscreen);
        playControl.getChildren().forEach(n -> ((Region) n).setPrefWidth(30));
        playControl.getChildren().forEach(n -> ((Region) n).setPrefHeight(30));
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

        playlist = new ListView<>(dataModel.getFileQueue());
        playlist.setPlaceholder(new Label("Není co přehrávat\n Otevřete soubor"));
        playlist.setCellFactory(TextFieldListCell.forListView(new StringConverter<>() {
            @Override
            public String toString(Media f) {
                String fileString = f.getSource();
                fileString = fileString.substring(fileString.lastIndexOf('/') + 1);
                fileString = fileString.replace("%20", " ");
                return fileString;
            }

            @Override
            public Media fromString(String string) {
                return null;
            }
        }));

        playlistIndexPlaying.addListener((b, o, n) -> {
            playlist.getFocusModel().focus(playlistIndexPlaying.get());
            playlist.scrollTo(playlistIndexPlaying.get());
        });
        playlistIndexPlaying.bind(dataModel.currentMediaProperty());

        HBox playlistButtonWrapper = new HBox();

        HBox playlistMoveWrapper = new HBox();
        Button playlistMoveStart = new ButtonSVG(IconSVG.FORWARD_UP.getSVGPath());
        playlistMoveStart.setOnAction(e -> moveFileAllWayUp());

        Button playlistMoveUp = new ButtonSVG(IconSVG.PLAY_UP.getSVGPath());
        playlistMoveUp.setOnAction(e -> moveFileUp());

        Button playlistMoveDown = new ButtonSVG(IconSVG.PLAY_DOWN.getSVGPath());
        playlistMoveDown.setOnAction(e -> moveFileDown());

        Button playlistMoveEnd = new ButtonSVG(IconSVG.FORWARD_DOWN.getSVGPath());
        playlistMoveEnd.setOnAction(e -> moveFileAllWayDown());


        HBox playlistManageWrapper = new HBox();
        Button playlistAdd = new ButtonSVG(IconSVG.PLUS.getSVGPath());
        playlistAdd.setOnAction(e -> addFileToQueue());

        Button playlistRemove = new ButtonSVG(IconSVG.MINUS.getSVGPath());
        playlistRemove.setOnAction(e -> dataModel.removeFromQueue(playlist.getSelectionModel().getSelectedItem()));

        HBox playlistOrderWrapper = new HBox();
        Button playlistShuffle = new ButtonSVG(IconSVG.SHUFFLE.getSVGPath());
        playlistShuffle.setOnAction(e -> playlistShuffle());

        Button playlistSort = new ButtonSVG(IconSVG.SORT.getSVGPath());
        playlistSort.setOnAction(e -> playlistSort());

        playlistMoveWrapper.getChildren().addAll(playlistMoveStart, playlistMoveUp, playlistMoveDown, playlistMoveEnd);
        playlistManageWrapper.getChildren().addAll(playlistAdd, playlistRemove);
        playlistOrderWrapper.getChildren().addAll(playlistShuffle, playlistSort);

        playlistButtonWrapper.getChildren().addAll(playlistMoveWrapper, playlistManageWrapper, playlistOrderWrapper);

        playlistButtonWrapper.getChildren().forEach(n -> ((HBox) n).setSpacing(5));
        playlistButtonWrapper.getChildren().forEach(n -> ((HBox) n).setPadding(new Insets(5)));
        playlistButtonWrapper.getChildren().forEach(n -> ((HBox) n).getChildren().forEach(h -> ((Region) h).setPrefHeight(25)));
        playlistButtonWrapper.getChildren().forEach(n -> ((HBox) n).getChildren().forEach(h -> ((Region) h).setPrefWidth(25)));
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
        openFile.setOnAction(e -> overwriteQueueWithFile());

        MenuItem openFolder = new MenuItem("Otevřít složku");
        openFolder.setAccelerator(openFolderCombo);
        openFolder.setOnAction(e -> overwriteQueueWithFolder());

        openMenu.getItems().addAll(openFile, openFolder);

        Menu playMenu = new Menu("Přehrávání");
        MenuItem playPause = new MenuItem("Pustit / Zastavit");
        playPause.setAccelerator(playPauseCombo);
        playPause.setOnAction(e -> dataModel.playOrPause());

        MenuItem playForward = new MenuItem("Posun dopředu");
        playForward.setAccelerator(playForwardCombo);
        playForward.setOnAction(e -> dataModel.moveTime(SEEK_TIME));

        MenuItem playBackward = new MenuItem("Posun dozadu");
        playBackward.setAccelerator(playBackwardCombo);
        playBackward.setOnAction(e -> dataModel.moveTime(-SEEK_TIME));

        MenuItem playVolumeUp = new MenuItem("Zvýšit hlasitost");
        playVolumeUp.setAccelerator(playVolumeUpCombo);
        playVolumeUp.setOnAction(e -> movePlayerVolume(VOLUME_CHANGE));

        MenuItem playVolumeDown = new MenuItem("Snížit hlasitost");
        playVolumeDown.setAccelerator(playVolumeDownCombo);
        playVolumeDown.setOnAction(e -> movePlayerVolume(-VOLUME_CHANGE));

        MenuItem playFullscreen = new MenuItem("Celá obrazovka");
        playFullscreen.setAccelerator(playFullscreenCombo);
        playFullscreen.setOnAction(e -> switchFullscreen());

        playMenu.getItems().addAll(
                playPause, playForward, playBackward, new SeparatorMenuItem(),
                playVolumeUp, playVolumeDown, new SeparatorMenuItem(),
                playFullscreen
        );

        Menu playlistMenu = new Menu("Seznam");
        MenuItem playlistAdd = new MenuItem("Přidat do seznamu");
        playlistAdd.setOnAction(e -> addFileToQueue());

        MenuItem playlistRemove = new MenuItem("Odebrat ze seznamu");
        playlistRemove.setOnAction(e -> dataModel.removeFromQueue(playlist.getSelectionModel().getSelectedItem()));

        MenuItem playlistPrevious = new MenuItem("Předchozí stopa");
        playlistPrevious.setAccelerator(playPreviousCombo);
        playlistPrevious.setOnAction(e -> dataModel.playPrevious());

        MenuItem playlistNext = new MenuItem("Následující stopa");
        playlistNext.setAccelerator(playNextCombo);
        playlistNext.setOnAction(e -> dataModel.playNext());

        MenuItem playlistSort = new MenuItem("Seřadit");
        playlistSort.setOnAction(e -> dataModel.sortQueue());

        MenuItem playlistShuffle = new MenuItem("Zamíchat");
        playlistShuffle.setOnAction(e -> playlistShuffle());

        playlistMenu.getItems().addAll(
                playlistAdd, playlistRemove, new SeparatorMenuItem(),
                playlistPrevious, playlistNext, new SeparatorMenuItem(),
                playlistSort, playlistShuffle
        );

        ButtonMenu timerMenu = new ButtonMenu("Časovač");
        timerMenu.setOnAction(e -> timerStage.createTimerStage());

        ButtonMenu appearanceMenu = new ButtonMenu("Vzhled");
        appearanceMenu.setOnAction(e -> appearanceStage.createAppearanceStage());

        ButtonMenu aboutApp = new ButtonMenu("O aplikaci");
        aboutApp.setOnAction(e -> aboutStage.createAboutStage());

        Region spacer = new Region();
        hideQueue = new ButtonMenu("");
        hideQueue.setOnAction(e -> hideQueue());
        hideQueue.textLabelProperty().bind(Bindings.createStringBinding(() -> {
                    if (listVisiblePreference.get()) return "Skrýt";
                    else return "Zobrazit";
                }, listVisiblePreference
        ));

        spacer.getStyleClass().add("menu-bar");
        HBox.setHgrow(spacer, Priority.ALWAYS);
        menuBar.getMenus().addAll(openMenu, playMenu, playlistMenu, timerMenu, appearanceMenu, aboutApp);

        menuBarWrapper.getChildren().addAll(menuBar, spacer, new MenuBar(hideQueue));

        return menuBarWrapper;
    }

    private void handleDragOver(DragEvent event) {
        if (event.getGestureSource() != borderPane && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        File file = db.getFiles().get(0);
        System.out.println(file.getName());
    }

    public void overwriteQueueWithFile() {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        if (file != null) {
            try {
                dataModel.overwriteQueueWithFile(file);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Otevřít soubor");
                alert.setHeaderText("Nepodporovaný formát");
                alert.setContentText("Zvolte jiný typ souboru.");
                alert.showAndWait();
            }
        }
    }

    public void overwriteQueueWithFolder() {
        DirectoryChooser dc = new DirectoryChooser();
        File directory = dc.showDialog(null);

        if (directory != null) {
            ArrayList<File> unloaded = dataModel.overwriteQueueWithFolder(directory.listFiles());

            if (unloaded.size() > 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Otevřít složku");
                alert.setHeaderText("Nepodporovaný formát");

                StringBuilder content = new StringBuilder("Nepovedlo se načíst tyto soubory:");
                for (File f : unloaded) {
                    content.append("\n").append(f.getName());
                }
                alert.setContentText(content.toString());
                alert.showAndWait();
            }
        }
    }

    public void addFileToQueue() {
        if(playlist.getItems().size() == 0){
            overwriteQueueWithFile();
            return;
        }

        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        if (file != null) {
            try {
                int selected = playlist.getSelectionModel().getSelectedIndex() + 1;
                if (selected == -1) {
                    selected = 0;
                }
                dataModel.addFileToQueue(file, selected);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Otevřít soubor");
                alert.setHeaderText("Nepodporovaný formát");
                alert.setContentText("Zvolte jiný typ souboru.");
                alert.showAndWait();
            }
        }
    }

    public void moveFileUp() {
        int selected = playlist.getSelectionModel().getSelectedIndex();
        if (selected != -1) {
            dataModel.moveFileLowerOnce(selected);
            playlist.getSelectionModel().select(dataModel.getCurrentMediaIndex());
            playlist.scrollTo(dataModel.getCurrentMediaIndex());
        }
    }

    public void moveFileDown() {
        int selected = playlist.getSelectionModel().getSelectedIndex();
        if (selected != -1) {
            dataModel.moveFileHigherOnce(selected);
            playlist.getSelectionModel().select(dataModel.getCurrentMediaIndex());
            playlist.scrollTo(dataModel.getCurrentMediaIndex());
        }
    }

    public void moveFileAllWayUp() {
        int selected = playlist.getSelectionModel().getSelectedIndex();
        if (selected != -1) {
            dataModel.moveFileToFirst(selected);
            playlist.getSelectionModel().select(dataModel.getCurrentMediaIndex());
            playlist.scrollTo(dataModel.getCurrentMediaIndex());
        }
    }

    public void moveFileAllWayDown() {
        int selected = playlist.getSelectionModel().getSelectedIndex();
        if (selected != -1) {
            dataModel.moveFileToLast(selected);
            playlist.getSelectionModel().select(dataModel.getCurrentMediaIndex());
            playlist.scrollTo(dataModel.getCurrentMediaIndex());
        }
    }

    public void switchFullscreen() {
        if (rootStage.isFullScreen()) {
            mediaWrapper.getStyleClass().remove("mediaBackground");

            borderPane.setBottom(getBottomControlBar());
            borderPane.setTop(getTopMenuBar());
            if (listVisiblePreference.get()) {
                borderPane.setRight(getPlaylist());
            }
            rootStage.setFullScreen(false);
        } else {
            mediaWrapper.getStyleClass().add("mediaBackground");

            borderPane.setBottom(null);
            borderPane.setTop(null);
            borderPane.setRight(null);

            rootStage.setFullScreen(true);
        }
    }

    public void hideQueue() {
        if (borderPane.getRight() == null) {
            borderPane.setRight(getPlaylist());
            listVisiblePreference.set(true);
        } else {
            borderPane.setRight(null);
            listVisiblePreference.set(false);
        }
    }

    public void movePlayerVolume(int moveVolume) {
        int currentVolume = playerVolume.get();
        if (currentVolume + moveVolume > 100) {
            playerVolume.set(100);
        } else {
            playerVolume.set(Math.max(currentVolume + moveVolume, 0));
        }
    }

    public void playlistSort() {
        dataModel.sortQueue();
    }

    public void playlistShuffle() {
        dataModel.shuffleQueue();
    }

    public static void main(String[] args) {
        launch();
    }
}