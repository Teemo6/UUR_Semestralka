import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controls {
    public static DataModel myMediaPlayer;
    public static boolean isPaused = true;

    public static void setMediaPlayer(DataModel newMediaPlayer){
        myMediaPlayer = newMediaPlayer;
    }

    public static void playFullscreen(Stage fullscreen, BorderPane pane){
        pane.setCursor(Cursor.NONE);
        pane.setTop(null);
        pane.setRight(null);
        pane.setBottom(null);
        fullscreen.setFullScreen(true);
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
}
