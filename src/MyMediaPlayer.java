import static javafx.beans.binding.Bindings.*;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class MyMediaPlayer{
    private ObservableList<File> fileQueue = FXCollections.observableArrayList();
    private ObjectProperty<MediaPlayer> mediaPlayer = new SimpleObjectProperty<>();
    private IntegerProperty currentFileIndex = new SimpleIntegerProperty();

    public MyMediaPlayer(){
        resetFileIndex();

        fileQueue.addListener((ListChangeListener.Change<? extends File> c) -> mediaPlayer.set(new MediaPlayer(new Media(fileQueue.get(currentFileIndex.getValue()).toURI().toString()))));

        currentFileIndex.addListener((obs, oldVal, newVal) -> mediaPlayer.set(new MediaPlayer(new Media(fileQueue.get(currentFileIndex.getValue()).toURI().toString()))));
    }

    public Media getCurrentMediaFile(){
        return new Media(fileQueue.get(currentFileIndex.get()).toURI().toString());
    }

    public int getCurrentFileIndex(){
        return currentFileIndex.get();
    }

    public IntegerProperty getCurrentMediaProperty(){
        return currentFileIndex;
    }

    public ObservableList<File> getFileQueue(){
        return fileQueue;
    }

    public void setCurrentMediaProperty(int currentMedia){
        this.currentFileIndex.set(currentMedia);
    }

    public void resetFileIndex(){
        this.currentFileIndex.set(0);
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
