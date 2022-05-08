import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class MyMediaPlayer{
    private ObservableList<File> fileQueue = FXCollections.observableArrayList();
    private ObjectProperty<MediaPlayer> mediaPlayer = new SimpleObjectProperty<>();
    private IntegerProperty currentMedia = new SimpleIntegerProperty();

    public MyMediaPlayer(){
        setCurrentMediaProperty(0);
    }

    public Media getCurrentMediaFile(){
        return new Media(fileQueue.get(currentMedia.get()).toURI().toString());
    }

    public int getCurrentMedia(){
        return currentMedia.get();
    }

    public IntegerProperty getCurrentMediaProperty(){
        return currentMedia;
    }

    public ObservableList<File> getFileQueue(){
        return fileQueue;
    }

    public void setCurrentMediaProperty(int currentMedia){
        this.currentMedia.set(currentMedia);
    }

    public ObjectProperty<MediaPlayer> getMediaPlayerProperty(){
        return mediaPlayer;
    }

    public MediaPlayer getMediaPlayer(){
        return mediaPlayer.get();
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer.set(mediaPlayer);
    }

    public void initModel(){
        fileQueue.add(new File("D:/Anime/Jahy-sama Will NOT Be Defeated/Jahy-sama wa Kujikenai Episode 01.mp4"));
        fileQueue.add(new File("D:/Anime/Jahy-sama Will NOT Be Defeated/Jahy-sama wa Kujikenai Episode 02.mp4"));
        fileQueue.add(new File("D:/Anime/Jahy-sama Will NOT Be Defeated/Jahy-sama wa Kujikenai Episode 03.mp4"));
    }
}
