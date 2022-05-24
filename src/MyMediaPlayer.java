import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class MyMediaPlayer{
    private ObservableList<Media> mediaQueue = FXCollections.observableArrayList();
    private ObjectProperty<MediaPlayer> mediaPlayer = new SimpleObjectProperty<>();
    private IntegerProperty currentMediaIndex = new SimpleIntegerProperty();

    public MyMediaPlayer(){
        resetMediaIndex();

        mediaQueue.addListener((ListChangeListener.Change<? extends Media> c) -> mediaPlayer.set(new MediaPlayer(mediaQueue.get(currentMediaIndex.getValue()))));

        currentMediaIndex.addListener((obs, oldVal, newVal) -> mediaPlayer.set(new MediaPlayer(mediaQueue.get(currentMediaIndex.getValue()))));
    }

    public Media getCurrentMediaFile(){
        return mediaQueue.get(currentMediaIndex.get());
    }

    public int getCurrentMediaIndex(){
        return currentMediaIndex.get();
    }

    public void setCurrentMediaIndex(int value){
        currentMediaIndex.set(value);}

    public void raiseCurrentFileIndex(){
        currentMediaIndex.set(getCurrentMediaIndex() + 1);}

    public void lowerCurrentMediaIndex(){
        currentMediaIndex.set(getCurrentMediaIndex() - 1);}

    public void resetMediaIndex(){
        this.currentMediaIndex.set(0);
    }

    public IntegerProperty getCurrentMediaProperty(){
        return currentMediaIndex;
    }

    public ObservableList<Media> getFileQueue(){
        return mediaQueue;
    }

    public void setCurrentMediaProperty(int currentMedia){
        this.currentMediaIndex.set(currentMedia);
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
        mediaQueue.add(new Media(new File("D:/Anime/Jahy-sama Will NOT Be Defeated/Jahy-sama wa Kujikenai Episode 01.mp4").toURI().toString()));
        mediaQueue.add(new Media(new File("D:/Anime/Jahy-sama Will NOT Be Defeated/Jahy-sama wa Kujikenai Episode 02.mp4").toURI().toString()));
        mediaQueue.add(new Media(new File("D:/Anime/Jahy-sama Will NOT Be Defeated/Jahy-sama wa Kujikenai Episode 03.mp4").toURI().toString()));
    }
}
