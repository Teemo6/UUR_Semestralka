import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class ControlsTimer {
    public static BooleanProperty timerRunning = new SimpleBooleanProperty(false);
    public static ObjectProperty<Color> col;

    public static boolean isTimerRunning() {
        return timerRunning.get();
    }

    public static BooleanProperty timerRunningProperty() {
        return timerRunning;
    }

    public static void setTimerRunning(boolean timerRunning) {
        ControlsTimer.timerRunning.set(timerRunning);
    }
}
