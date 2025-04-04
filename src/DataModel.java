import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;
import java.util.ArrayList;
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

    public void setMediaPlayerBasedOnIndex() throws Exception{
        if(getMediaPlayer() != null){
            getMediaPlayer().stop();
            getMediaPlayer().dispose();
            getMediaPlayer().volumeProperty().unbind();
        }
        if(mediaQueue.isEmpty()) {
            mediaPlayer.set(null);
        } else {
            try {
                MediaPlayer mp = new MediaPlayer(mediaQueue.get(currentMediaIndex.getValue()));
                mp.setOnReady(() -> {
                    setMediaPlayer(mp);
                    forcePlay();
                });
                mp.setOnEndOfMedia(() -> {
                    try {
                        playNext();
                    } catch (Exception ignored){}
                });
            } catch (Exception e){
                throw e;
            }
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
            getMediaPlayer().volumeProperty().unbind();
            getFileQueue().remove(toRemove);
            if(!mediaQueue.isEmpty()){
                setCurrentMediaIndex(0);
                try {
                    setMediaPlayerBasedOnIndex();
                } catch (Exception e){
                    removeFromQueue(getCurrentMediaFile());
                }
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
        Media newMedia;
        try {
            newMedia = new Media(file.toURI().toString());
        } catch (Exception e){
            throw new Exception("Nepovedlo se přečíst soubor");
        }

        getFileQueue().add(position, newMedia);
    }

    public void overwriteQueueWithFile(File file) throws Exception {
        try {
            Media newMedia = new Media(file.toURI().toString());

            getFileQueue().clear();
            resetMediaIndex();

            getFileQueue().add(newMedia);
            try {
                setMediaPlayerBasedOnIndex();
            } catch (Exception e){
                removeFromQueue(getCurrentMediaFile());
            }
        } catch (Exception e){
            throw new Exception("Nepovedlo se přečíst soubor");
        }
    }

    public ArrayList<File> overwriteQueueWithFolder(File[] files){
        ArrayList<File> unloaded = new ArrayList<>();

        getFileQueue().clear();
        resetMediaIndex();

        for(File f : files){
            try {
                Media newMedia = new Media(f.toURI().toString());

                getFileQueue().add(newMedia);
            } catch (Exception ignored){
                unloaded.add(f);
            }
        }
        try {
            setMediaPlayerBasedOnIndex();
        } catch (Exception e){
            removeFromQueue(getCurrentMediaFile());
        }
        return unloaded;
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

    public void forcePlay(){
        if (getMediaPlayer() != null){
            getMediaPlayer().play();
            isPlaying.set(true);
        }
    }

    public void forcePause(){
        if (getMediaPlayer() != null){
            getMediaPlayer().pause();
            isPlaying.set(false);
        }
    }

    public void moveTime(int seekTime){
        if (getMediaPlayer() != null) {
            getMediaPlayer().seek(getMediaPlayer().getCurrentTime().add(Duration.seconds(seekTime)));
        }
    }

    public void playNext() throws Exception{
        if(getCurrentMediaIndex() + 1 <= getFileQueue().size() - 1){
            raiseCurrentMediaIndex();
            try {
                setMediaPlayerBasedOnIndex();
            } catch (Exception e){
                removeFromQueue(getCurrentMediaFile());
                throw e;
            }
        }
    }

    public void playPrevious() throws Exception{
        if(getCurrentMediaIndex() - 1 >= 0){
            lowerCurrentMediaIndex();
            try {
                setMediaPlayerBasedOnIndex();
            } catch (Exception e){
                removeFromQueue(getCurrentMediaFile());
                throw e;
            }
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