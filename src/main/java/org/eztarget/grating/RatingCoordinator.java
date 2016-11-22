package org.eztarget.grating;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import java.util.Date;

public class RatingCoordinator {

    private static final String TAG = "rate/" + RatingCoordinator.class.getSimpleName();

    private static final boolean VERBOSE = false;

    private static RatingCoordinator sInstance;

    private long mInstallDaysThreshold = 10;

    private int mNumberOfLaunchesThreshold = 10;

    private int mDaysTillReminder = 1;

    private int mNumberOfEventThreshold = 4;

    private CountDownTimer mSessionTimer;

    private long mSessionMillis = 30L * 1000L;

    private boolean mPaused = true;

    private OnClickButtonListener mListener;

    private RatingCoordinator() {
    }

    public static RatingCoordinator getInstance() {
        if (sInstance == null) {
            synchronized (RatingCoordinator.class) {
                if (sInstance == null) {
                    sInstance = new RatingCoordinator();
                }
            }
        }
        return sInstance;
    }

    public RatingCoordinator setNumberOfLaunchesThreshold(int numberOfLaunches) {
        mNumberOfLaunchesThreshold = numberOfLaunches;
        return this;
    }

    public RatingCoordinator setSessionLength(final int lengthMillis) {
        mSessionMillis = lengthMillis;
        return this;
    }

    public RatingCoordinator setInstallDaysThresholds(final int daysThreshold) {
        mInstallDaysThreshold = daysThreshold;
        return this;
    }

    public RatingCoordinator setDaysTillReminder(final int days) {
        mDaysTillReminder = days;
        return this;
    }

    public RatingCoordinator setNumberOfEventsThreshold(final int numberOfEvents) {
        mNumberOfEventThreshold = numberOfEvents;
        return this;
    }

    public RatingCoordinator setOnClickButtonListener(OnClickButtonListener listener) {
        mListener = listener;
        return this;
    }

    public void handleLaunch(final Context context) {
        if (PreferenceHelper.isFirstLaunch(context)) {
            PreferenceHelper.setInstallDate(context);
        }
        PreferenceHelper.from(context).increaseNumberOfLaunches();
    }

    public void onPause() {
        mPaused = true;
        mSessionTimer.cancel();
    }

    public void onResume(final Activity activity) {
        if (mPaused) {
            mSessionTimer = new CountDownTimer(mSessionMillis, 1000L) {
                @Override
                public void onTick(long l) {
//                    mSessionMillis = l;
                }

                @Override
                public void onFinish() {
                    handleEvent(activity);
                }
            }.start();

            mPaused = false;
            showRateDialogIfMeetsConditions(activity);
        }
    }

    public void handleEvent(final Activity activity) {
        PreferenceHelper.from(activity).increaseNumberOfRatingEvents();
        showRateDialogIfMeetsConditions(activity);
    }

    private void showRateDialogIfMeetsConditions(final Activity activity) {
        if (shouldShowRateDialog(activity.getApplicationContext())) {
            showRateDialog(activity);
        }
    }

    private void showRateDialog(Activity activity) {
        if (!activity.isFinishing()) {
            resetConditions(activity);
            DialogBuilder.create(activity, mListener).show();
        }
    }

    void resetConditions(final Context context) {
        PreferenceHelper.from(context).resetNumberOfLaunches();
        PreferenceHelper.from(context).resetNumberOfRatingEvents();
    }

    private boolean shouldShowRateDialog(final Context context) {
        final boolean ratingAgreed = PreferenceHelper.isRatingEnabled(context);
        if (VERBOSE) {
            Log.d(TAG, "Agreed to rating: " + ratingAgreed);
        }

        return ratingAgreed
                && didReachNumberOfLaunches(context)
                && didReachInstallationAge(context)
                && didReachNumberOfEvents(context)
                && didReachReminderAge(context);
    }

    private boolean didReachNumberOfLaunches(final Context context) {
        final int numberOfLaunches = PreferenceHelper.from(context).getNumberOfLaunches();

        if (VERBOSE) {
            Log.d(TAG, "Number of launches: " + numberOfLaunches + "/" + mNumberOfLaunchesThreshold);
        }
        return numberOfLaunches >= mNumberOfLaunchesThreshold;
    }

    private boolean didReachInstallationAge(final Context context) {
        final long installDate = PreferenceHelper.getInstallDate(context);
        if (VERBOSE) {
            Log.d(TAG, "Installation Date: " + new Date(installDate));
        }
        return isOverDate(installDate, mInstallDaysThreshold);
    }

    private boolean didReachNumberOfEvents(final Context context) {
        final int numberOfEvents = PreferenceHelper.from(context).getNumberOfRatingEvents();
        if (VERBOSE) {
            Log.d(TAG, "Number of events: " + numberOfEvents + "/" + mNumberOfEventThreshold);
        }
        return numberOfEvents > mNumberOfEventThreshold;
    }

    private boolean didReachReminderAge(final Context context) {
        final long remindDate = PreferenceHelper.getRemindSelectedDate(context);
        if (VERBOSE) {
            Log.d(TAG, "Reminder Date: " + new Date(remindDate));
        }
        return isOverDate(remindDate, mDaysTillReminder);
    }

    private boolean isOverDate(final long targetMillis, final long thresholdMillis) {
        return System.currentTimeMillis() - targetMillis
                >= thresholdMillis * 24L * 60L * 60L * 1000L;
    }

}