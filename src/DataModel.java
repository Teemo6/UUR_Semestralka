import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class DataModel {
    public ListProperty<String> mediaQueue = new SimpleListProperty<>(FXCollections.observableArrayList());

    public void initModel(){
        mediaQueue.clear();
        mediaQueue.add("Solar Opposites Episode 1  The Matter Transfer Array.mp4");
        mediaQueue.add("Solar Opposites Episode 2  The Unstable Grey Hole.mp4");
        mediaQueue.add("Solar Opposites Episode 3  The Quantum Ring.mp4");
        mediaQueue.add("Solar Opposites Episode 4  The Booster Manifold.mp4");
        mediaQueue.add("Solar Opposites Episode 5  The Lavatic Reactor.mp4");
    }
}
