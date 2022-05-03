import javafx.beans.InvalidationListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;

public class MyMediaPlayer {
    public static Media media;
    public static MediaPlayer mediaPlayer;
    public static MediaView mediaView;
    public static Pane mediaWrapper;

    private static File file = new File("D:/Anime/Jahy-sama Will NOT Be Defeated/Jahy-sama wa Kujikenai Episode 01.mp4");
    private static final String MEDIA_URL = file.toURI().toString();

    //private String MEDIA_URL = "https://www.youtube.com/watch?v=hh1WeQxfCX0";
    //private String MEDIA_URL = "https://www.kiv.zcu.cz/~herout/vyuka/oop/video/oop-07.mp4";


    public static Node getMediaPlayerPane(){
        mediaWrapper = new Pane();

        media = new Media(MEDIA_URL);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        //mediaPlayer.setOnReady(rootStage::sizeToScene);
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


}
