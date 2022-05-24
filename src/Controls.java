import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class Controls {
    public static MyMediaPlayer myMediaPlayer;
    public static boolean isPaused = true;

    public static void setMediaPlayer(MyMediaPlayer newMediaPlayer){
        myMediaPlayer = newMediaPlayer;
    }

    public static void playPause() {
        if (myMediaPlayer.getMediaPlayer() != null){
            if (isPaused) {
                myMediaPlayer.getMediaPlayer().play();
                isPaused = false;
            } else {
                myMediaPlayer.getMediaPlayer().pause();
                isPaused = true;
            }
        }
    }

    public static void playForward() {
        if (myMediaPlayer.getMediaPlayer() != null) {
            myMediaPlayer.getMediaPlayer().seek(myMediaPlayer.getMediaPlayer().getCurrentTime().add(Duration.seconds(5)));
        }
    }

    public static void playBackward() {
        if (myMediaPlayer.getMediaPlayer() != null) {
            myMediaPlayer.getMediaPlayer().seek(myMediaPlayer.getMediaPlayer().getCurrentTime().add(Duration.seconds(-5)));
        }
    }

    public static void playVolumeUp() {
        if (myMediaPlayer.getMediaPlayer() != null) {
            double currentVolume = myMediaPlayer.getMediaPlayer().getVolume();
            if(currentVolume + 0.05 > 1) {
                myMediaPlayer.getMediaPlayer().setVolume(1);
            } else {
                myMediaPlayer.getMediaPlayer().setVolume(currentVolume + 0.05);
            }
        }
    }

    public static void playVolumeDown() {
        if (myMediaPlayer.getMediaPlayer() != null) {
            double currentVolume = myMediaPlayer.getMediaPlayer().getVolume();
            if(currentVolume - 0.05 < 0) {
                myMediaPlayer.getMediaPlayer().setVolume(0);
            } else {
                myMediaPlayer.getMediaPlayer().setVolume(currentVolume - 0.05);
            }
        }
    }

    public static void playNext(){
        if(myMediaPlayer.getCurrentMediaIndex() + 1 <= myMediaPlayer.getFileQueue().size() - 1){
            if(myMediaPlayer.getMediaPlayer() != null){
                myMediaPlayer.getMediaPlayer().stop();
                myMediaPlayer.getMediaPlayer().dispose();
            }
            myMediaPlayer.raiseCurrentFileIndex();
            myMediaPlayer.getMediaPlayer().setOnReady(() -> myMediaPlayer.getMediaPlayer().play());
        }
    }

    public static void playPrevious(){
        if(myMediaPlayer.getCurrentMediaIndex() - 1 >= 0){
            if(myMediaPlayer.getMediaPlayer() != null){
                myMediaPlayer.getMediaPlayer().stop();
                myMediaPlayer.getMediaPlayer().dispose();
            }
            myMediaPlayer.lowerCurrentMediaIndex();
            myMediaPlayer.getMediaPlayer().setOnReady(() -> myMediaPlayer.getMediaPlayer().play());
        }
    }

    public static void playFullscreen(Stage fullscreen, BorderPane pane){
        pane.setCursor(Cursor.NONE);
        pane.setTop(null);
        pane.setRight(null);
        pane.setBottom(null);
        fullscreen.setFullScreen(true);
    }

    public static void hideListQueue(BorderPane pane){
        pane.setRight(null);
    }

    public static void open(){}

    public static void closeWindow(Stage s){
        s.close();
    }

    public static void openFile(){
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        if(file != null){
            try{
                new MediaPlayer(new Media(file.toURI().toString()));

                myMediaPlayer.getFileQueue().clear();
                myMediaPlayer.getFileQueue().add(new Media(file.toURI().toString()));
                myMediaPlayer.resetMediaIndex();
                myMediaPlayer.getMediaPlayer().setOnReady(() -> myMediaPlayer.getMediaPlayer().play());
            } catch(Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Otevřít soubor");
                alert.setHeaderText("Nepodporovaný formát");
                alert.setContentText("Zvolte jiný typ souboru.");
                alert.showAndWait();
            }
        }
    }

    public static void openFolder() {
        DirectoryChooser dc = new DirectoryChooser();
        File file = dc.showDialog(null);

        System.out.println(file);

        if(file != null){
            try{
                new MediaPlayer(new Media(file.toURI().toString()));

                myMediaPlayer.getFileQueue().clear();
                myMediaPlayer.getFileQueue().add(new Media(file.toURI().toString()));
                myMediaPlayer.resetMediaIndex();
                myMediaPlayer.getMediaPlayer().setOnReady(() -> myMediaPlayer.getMediaPlayer().play());
            } catch(Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Otevřít soubor");
                alert.setHeaderText("Nepodporovaný formát");
                alert.setContentText("Zvolte jiný typ souboru.");
                alert.showAndWait();
            }
        }
    }


    public static void openURL() {
        LoaderStage.createLoaderStage();
    }

    public static void addToQueue(){
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        if(file != null){
            try{
                new MediaPlayer(new Media(file.toURI().toString()));

                myMediaPlayer.getFileQueue().add(new Media(file.toURI().toString()));
            } catch(Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Otevřít soubor");
                alert.setHeaderText("Nepodporovaný formát");
                alert.setContentText("Zvolte jiný typ souboru.");
                alert.showAndWait();
            }
        }
    }

    public static void removeFromQueue(Media toRemove){
            if(myMediaPlayer.getCurrentMediaFile().equals(toRemove)){
                myMediaPlayer.resetMediaIndex();
            }
        myMediaPlayer.getFileQueue().remove(toRemove);
    }
}
