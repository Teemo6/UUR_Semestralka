import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.util.Duration;

public class ControlsTimer {
    private static DataModel dataModel;

    private static final TimerAction DEFAULT_TIMER_ACTION = TimerAction.START_PLAYING;

    public static BooleanProperty timerRunning = new SimpleBooleanProperty(false);
    public static TimerAction timerAction = DEFAULT_TIMER_ACTION;

    private static Timeline timer;

    public static void setTimer(Duration duration){
        clearTimer();
        setTimerRunning(true);

        timer = new Timeline(new KeyFrame(duration, event -> {
            switch(timerAction){
                case START_PLAYING -> dataModel.forcePlay();
                case STOP_PLAYING -> dataModel.forcePause();
                case CLOSE_PROGRAM -> Platform.exit();
            }
            setTimerRunning(false);
        }));
        timer.play();
    }

    public static void clearTimer(){
        if(timer != null) {
            timer.getKeyFrames().clear();
        }
        setTimerRunning(false);
    }

    public static void initTimer(DataModel model){
        dataModel = model;
    }

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
