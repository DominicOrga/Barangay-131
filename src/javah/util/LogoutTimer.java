package javah.util;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javah.Main;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A class timer running on a background thread that log-outs the application once
 * the given time duration is reached. The MAX_IDLE_DURATION stored at the preference
 * determines the time duration. It can be started or stopped at any given moment.
 */
public class LogoutTimer {

    /**
     * A listener used for this LogoutTimer.
     *
     * @see LogoutTimer
     */
    public interface OnLogoutTimerListener {
        /**
         * Inform the Main Control when the max idle duration is reached.
         *
         * @see javah.controller.MainControl
         */
        void onMaxIdleDurationReached();
    }

    /**
     * An event handler that restarts the seconds back to 0 every time the user
     * interacts with the application.
     */
    private EventHandler mAppInUseListener = event -> mSec = 0;

    /* Represents the idle duration of the application. */
    private int mSec;

    /**
     * A timer that calls a task to increment the mSec every second and checks if the
     * max idle duration is reached.
     */
    private Timer mTimer;

    private OnLogoutTimerListener mListener;

    /**
     * Set the listener for this timer.
     *
     * @param listener
     *        The listener for this timer.
     */
    public void setListener(OnLogoutTimerListener listener) {
        mListener = listener;
    }

    /**
     * Reset and start the timer.
     *
     * Note: Called during application log-in and if the MAX_IDLE_DURATION is changed.
     *
     * @param idleDuration
     *        The total time in seconds which determines how long the application can be left
     *        idling before being log-out automatically.
     *
     * @see javah.model.PreferenceModel
     */
    public void start(int idleDuration) {
        // Reset the time back to 0 every time the timer is started.
        mSec = 0;

        // If the application is not idle, then reset the mSec back to 0.
        Main.getPrimaryStage().addEventFilter(EventType.ROOT, mAppInUseListener);

        // Create a timer task that increments the seconds variable.
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                mSec++;

                if (mSec >= idleDuration && mListener != null) {
                    mListener.onMaxIdleDurationReached();
                    stop();
                }
            }
        };

        // The timer task must always be executed every second.
        mTimer = new Timer(true);
        mTimer.schedule(task, 0, 1000);
    }

    /**
     * Stop the timer.
     *
     * Note: Called during application log-out and when Idle duration is reached.
     */
    public void stop() {
        Main.getPrimaryStage().removeEventFilter(EventType.ROOT, mAppInUseListener);

        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }
}