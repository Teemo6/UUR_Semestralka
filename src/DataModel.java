import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

public class DataModel {
    private ObservableList<Media> mediaQueue = FXCollections.observableArrayList();
    private ObjectProperty<MediaPlayer> mediaPlayer = new SimpleObjectProperty<>();
    private IntegerProperty currentMediaIndex = new SimpleIntegerProperty();
    private BooleanProperty isPlaying = new SimpleBooleanProperty();

    public DataModel(){
        mediaPlayer.set(null);
        isPlaying.set(false);
        resetMediaIndex();

    }

    public void setMediaPlayerBasedOnIndex(){
        if(getMediaPlayer() != null){
            getMediaPlayer().stop();
            getMediaPlayer().dispose();
            getMediaPlayer().volumeProperty().unbind();
        }
        if(mediaQueue.isEmpty()) {
            mediaPlayer.set(null);
        } else {
            mediaPlayer.set(new MediaPlayer(mediaQueue.get(currentMediaIndex.getValue())));
        }
    }

    public void resetMediaIndex(){
        this.currentMediaIndex.set(0);
    }

    public void raiseCurrentMediaIndex(){
        currentMediaIndex.set(getCurrentMediaIndex() + 1);}

    public void lowerCurrentMediaIndex(){
        currentMediaIndex.set(getCurrentMediaIndex() - 1);}

    public void removeFromQueue(Media toRemove){
        if(getCurrentMediaFile().equals(toRemove)){
            resetMediaIndex();
            getMediaPlayer().stop();
            getMediaPlayer().dispose();
            getFileQueue().remove(toRemove);
            if(!mediaQueue.isEmpty()){
                setCurrentMediaIndex(0);
                setMediaPlayerBasedOnIndex();
                getMediaPlayer().setOnReady(() -> getMediaPlayer().play());
            } else {
                setIsPlaying(false);
                setCurrentMediaIndex(-1);
                setMediaPlayer(null);
            }
        } else {
            getFileQueue().remove(toRemove);
        }
    }

    public void addFileToQueue(File file, int position) throws Exception {
        Media newMedia = new Media(file.toURI().toString());

        getFileQueue().add(position, newMedia);
    }

    public void overwriteQueueWithFile(File file) throws Exception {
        Media newMedia = new Media(file.toURI().toString());

        getFileQueue().clear();
        resetMediaIndex();

        getFileQueue().add(newMedia);
        setMediaPlayerBasedOnIndex();
        getMediaPlayer().setOnReady(() -> getMediaPlayer().play());
        isPlaying.set(true);
    }

    public void playOrPause() {
        if (getMediaPlayer() != null){
            if (isPlaying.get()) {
                getMediaPlayer().pause();
                isPlaying.set(false);
            } else {
                getMediaPlayer().play();
                isPlaying.set(true);
            }
        }
    }

    public void moveTime(int seekTime){
        if (getMediaPlayer() != null) {
            getMediaPlayer().seek(getMediaPlayer().getCurrentTime().add(Duration.seconds(seekTime)));
        }
    }

    public void playNext(){
        if(getCurrentMediaIndex() + 1 <= getFileQueue().size() - 1){
            raiseCurrentMediaIndex();
            setMediaPlayerBasedOnIndex();
            getMediaPlayer().setOnReady(() -> getMediaPlayer().play());
            isPlaying.set(true);
        }
    }

    public void playPrevious(){
        if(getCurrentMediaIndex() - 1 >= 0){
            lowerCurrentMediaIndex();
            setMediaPlayerBasedOnIndex();
            getMediaPlayer().setOnReady(() -> getMediaPlayer().play());
            isPlaying.set(true);
        }
    }

    public void moveFileLowerOnce(int selected){
        if(selected < 1) return;
        Collections.swap(mediaQueue, selected, selected - 1);
        setCurrentMediaIndex(selected - 1);
    }

    public void moveFileHigherOnce(int selected){
        if(selected > mediaQueue.size() - 2) return;
        Collections.swap(mediaQueue, selected, selected + 1);
        setCurrentMediaIndex(selected + 1);
    }

    public void moveFileToFirst(int selected){
        Collections.swap(mediaQueue, selected, 0);
        setCurrentMediaIndex(0);
    }

    public void moveFileToLast(int selected){
        Collections.swap(mediaQueue, selected, mediaQueue.size() - 1);
        setCurrentMediaIndex(mediaQueue.size() - 1);
    }

    public void shuffleQueue(){
        Media oldMedia = getCurrentMediaFile();
        FXCollections.shuffle(mediaQueue);
        setCurrentMediaIndex(mediaQueue.indexOf(oldMedia));
    }

    public void sortQueue(){
        Media oldMedia = getCurrentMediaFile();
        FXCollections.sort(mediaQueue, Comparator.comparing(Media::getSource));
        setCurrentMediaIndex(mediaQueue.indexOf(oldMedia));
    }

    public void initModel(){
        mediaQueue.add(new Media(new File("D:/Anime/Komi Cant Communicate/Komi Cant Communicate Episode 02.mp4").toURI().toString()));
        mediaQueue.add(new Media(new File("D:/Anime/Jahy-sama Will NOT Be Defeated/Jahy-sama wa Kujikenai Episode 01.mp4").toURI().toString()));
        mediaQueue.add(new Media("https://www.kiv.zcu.cz/~herout/vyuka/oop/video/oop-04.mp4"));
        mediaQueue.add(new Media(new File("D:/Anime/Odd Taxi/Odd Taxi Episode 01.mp4").toURI().toString()));

        resetMediaIndex();
        setMediaPlayerBasedOnIndex();
    }

    public Media getCurrentMediaFile(){
        return mediaQueue.get(currentMediaIndex.get());
    }

    public int getCurrentMediaIndex(){
        return currentMediaIndex.get();
    }

    public void setCurrentMediaIndex(int value){
        currentMediaIndex.set(value);}

    public IntegerProperty currentMediaProperty(){
        return currentMediaIndex;
    }

    public ObservableList<Media> getFileQueue(){
        return mediaQueue;
    }

    public void setCurrentMediaProperty(int currentMedia){
        this.currentMediaIndex.set(currentMedia);
    }

    public ObjectProperty<MediaPlayer> mediaPlayerProperty(){
        return mediaPlayer;
    }

    public MediaPlayer getMediaPlayer(){
        return mediaPlayer.get();
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer.set(mediaPlayer);
    }

    public boolean getIsPlaying() {
        return isPlaying.get();
    }

    public BooleanProperty isPlayingProperty() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying.set(isPlaying);
    }
}
