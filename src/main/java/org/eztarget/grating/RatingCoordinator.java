package org.eztarget.grating;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;

public class RatingCoordinator {

    private static final String TAG = "rate/" + RatingCoordinator.class.getSimpleName();

    private static final long MILLIS_PER_DAY = 24L * 60L * 60L * 1000L;

    private boolean mVerbose = false;

    private static RatingCoordinator sInstance;

    private long mInstallDaysThreshold = 10;

    private int mNumberOfLaunchesThreshold = 10;

    private int mNumberOfLaunchesReminderIncrease = 0;

    private int mDaysTillReminder = 1;

    private int mNumberOfEventThreshold = 4;

    private CountDownTimer mSessionTimer;

    private long mAfterResumeDelay = 10L * 1000L;

    private boolean mPaused = true;

    private OnClickButtonListener mListener;

    private boolean mIgnoreUsage = false;

    private String mSupportEmail;

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

    public RatingCoordinator setVerbosityEnabled(final boolean verbose) {
        mVerbose = verbose;
        return this;
    }

    public RatingCoordinator setNumberOfLaunchesThreshold(int numberOfLaunches) {
        mNumberOfLaunchesThreshold = numberOfLaunches;
        return this;
    }

    public RatingCoordinator setAfterResumeDelay(final int lengthMillis) {
        mAfterResumeDelay = lengthMillis;
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

    public RatingCoordinator setNumberOfLaunchesReminderIncrease(
            final int numberOfLaunchesReminderIncrease
    ) {
        if (numberOfLaunchesReminderIncrease < 0) {
            return this;
        }

        mNumberOfLaunchesReminderIncrease = numberOfLaunchesReminderIncrease;
        return this;
    }

    public RatingCoordinator setOnClickButtonListener(OnClickButtonListener listener) {
        mListener = listener;
        return this;
    }

    OnClickButtonListener getOnClickButtonListener() {
        return mListener;
    }

    public RatingCoordinator enableRatingBar(final String supportEmailAddress) {
        mSupportEmail = supportEmailAddress;
        return this;
    }

    public RatingCoordinator disableRatingBar() {
        mSupportEmail = null;
        return this;
    }

    String getSupportEmailAddress() {
        return mSupportEmail;
    }

    public void handleLaunch(@NonNull final Context context) {
        if (PersistenceHelper.isFirstLaunch(context)) {
            PersistenceHelper.setInstallDate(context);
        }
        PersistenceHelper.from(context).increaseNumberOfLaunches();
    }

    public void onPause() {
        mPaused = true;

        if (mSessionTimer != null) {
            mSessionTimer.cancel();
        }
    }

    public void ignoreConditionsOnce() {
        if (mVerbose) {
            Log.d(TAG, "Will ignore usage counters once.");
        }
        mIgnoreUsage = true;
    }

    public void onResume(@NonNull final Activity activity) {
        final boolean ratingAgreed = PersistenceHelper.isRatingEnabled(activity);
        if (mVerbose) {
            Log.d(TAG, "Agreed to rating: " + ratingAgreed);
        }

        if (mPaused && ratingAgreed) {
            mSessionTimer = new CountDownTimer(mAfterResumeDelay, 1000L) {
                @Override
                public void onTick(long l) {
//                    mAfterResumeDelay = l;
                }

                @Override
                public void onFinish() {
                    showRateDialogIfMeetsConditions(activity);
                }
            }.start();

            mPaused = false;
        }
    }

    public void handleEvent(final Activity activity) {
        PersistenceHelper.from(activity).increaseNumberOfRatingEvents();
    }

    private void showRateDialogIfMeetsConditions(@NonNull final Activity activity) {
        if (shouldShowRateDialog(activity.getApplicationContext())) {
            showRateDialog(activity);
        }
    }

    public void showRateDialog(@NonNull Activity activity) {
        if (!activity.isFinishing()) {
            resetConditions(activity);
            new RatingNavigator(activity).showDialog(!TextUtils.isEmpty(mSupportEmail));
        }
    }

    private void resetConditions(final Context context) {
        PersistenceHelper.from(context).resetNumberOfRatingEvents();
        mIgnoreUsage = false;
    }

    public boolean isEnabled(@NonNull final Context context) {
        return PersistenceHelper.isRatingEnabled(context);
    }

    public boolean shouldShowRateDialog(@NonNull final Context context) {

        if (!isEnabled()) {
            return false;
        }

        if (!didReachReminderAge(context)) {
            if (mVerbose) {
                Log.d(TAG, "Before Reminder date.");
            }
            return false;
        }

        if (mIgnoreUsage) {
            if (mVerbose) {
                Log.d(TAG, "Ignoring usage.");
            }
            return true;
        }

        return didReachNumberOfLaunches(context)
                && didReachInstallationAge(context)
                && didReachNumberOfEvents(context);
    }

    private boolean didReachNumberOfLaunches(final Context context) {
        final int numberOfLaunches = PersistenceHelper.from(context).getNumberOfLaunches();

        if (mVerbose) {
            Log.d(TAG, "Number of launches: " + numberOfLaunches + "/" + mNumberOfLaunchesThreshold);
        }
        return numberOfLaunches >= mNumberOfLaunchesThreshold;
    }

    private boolean didReachInstallationAge(final Context context) {
        final long installDate = PersistenceHelper.getInstallDate(context);
        if (mVerbose) {
            Log.d(TAG, "Installation Date: " + new Date(installDate));
        }
        return isOverDate(installDate, mInstallDaysThreshold);
    }

    private boolean didReachNumberOfEvents(final Context context) {
        final int numberOfEvents = PersistenceHelper.from(context).getNumberOfRatingEvents();
        if (mVerbose) {
            Log.d(TAG, "Number of events: " + numberOfEvents + "/" + mNumberOfEventThreshold);
        }
        return numberOfEvents > mNumberOfEventThreshold;
    }

    private boolean didReachReminderAge(final Context context) {
        final long remindDate = PersistenceHelper.getRemindSelectedDate(context);
        if (mVerbose) {
            Log.d(TAG, "'Reminder Selected' date: " + new Date(remindDate));
            Log.d(TAG, "Reminder after " + mDaysTillReminder + " days.");
        }
        return isOverDate(remindDate, mDaysTillReminder);
    }

    private boolean isOverDate(final long targetMillis, final long thresholdMillis) {
        final float now = System.currentTimeMillis();
        return now - targetMillis >= thresholdMillis * MILLIS_PER_DAY;
    }


    void didSelectRateNow(@NonNull final Activity activity) {
        new RatingNavigator(activity).startGooglePlayActivity();
    }

    void didSelectRemindLater(@NonNull final Context context) {
        PersistenceHelper.setRemindSelectedDate(context);
        mNumberOfLaunchesThreshold += mNumberOfLaunchesReminderIncrease;
    }

    void didSelectDisable(@NonNull final Context context) {
        PersistenceHelper.from(context).disableRating();

    }
}