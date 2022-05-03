import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controls {

    public static boolean isPaused = true;
    DataModel model = new DataModel();

    public static void playPause(MediaPlayer mp){
        if(isPaused){
            mp.play();
            isPaused = false;
        } else {
            mp.pause();
            isPaused = true;
        }
    }

    public static void playPause(ActionEvent e, MediaPlayer mp){
        playPause(mp);
    }


    public static void playForward(ActionEvent e) {
        System.out.println("for");
    }

    public static void playBackward(ActionEvent e) {
        System.out.println("back");
    }

    public static void playVolumeUp(ActionEvent e) {
        System.out.println("up");
    }

    public static void playVolumeDown(ActionEvent e) {
        System.out.println("down");
    }

    public static void playNext(ActionEvent e){
        System.out.println("next");
    }
    public static void playPrevious(ActionEvent e){
        System.out.println("pev");
    }

    public static void playFullscreen(ActionEvent e, Stage fullscreen){
        fullscreen.setFullScreen(true);
    }

    public static void hideListQueue(ActionEvent e, BorderPane pane){
        pane.setRight(null);
    }

    public static void open(){
        }

    public static void onMenuClick(Menu menu) {
        menu.getItems().add(new MenuItem());
        menu.addEventHandler(Menu.ON_SHOWN, event -> menu.hide());
        menu.addEventHandler(Menu.ON_SHOWING, event -> menu.fire());
    }
}
