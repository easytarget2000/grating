package org.eztarget.grating;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by michelsievers on 22/11/2016.
 *
 */

class PersistenceHelper {

    private static final String TAG = "rate/" + PersistenceHelper.class.getSimpleName();

    private SharedPreferences mPreferences;

    protected PersistenceHelper(@NonNull final Context context) {
        mPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    static PersistenceHelper from(@NonNull final Context context) {
        return new PersistenceHelper(context);
    }

    private static final String PREF_FILE_NAME = "android_rate_pref_file";

    private static final String PREF_KEY_IS_AGREE_SHOW_DIALOG = "android_rate_is_agree_show_dialog";

    private static final String PREF_KEY_REMIND_INTERVAL = "android_rate_remind_interval";

    private static final String PREF_KEY_EVENT_TIMES = "android_rate_event_times";

    void disableRating() {
        edit(PREF_KEY_IS_AGREE_SHOW_DIALOG, false);
    }

    boolean isRatingEnabled() {
        return mPreferences.getBoolean(PREF_KEY_IS_AGREE_SHOW_DIALOG, true);
    }

    void setRemindSelectedDate() {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(PREF_KEY_REMIND_INTERVAL);
        editor.apply();

        edit(PREF_KEY_REMIND_INTERVAL, System.currentTimeMillis());
    }

    long getRemindSelectedDate() {
        return mPreferences.getLong(PREF_KEY_REMIND_INTERVAL, 0);
    }

    private static final String PREF_KEY_INSTALL_DATE = "android_rate_install_date";

    void setInstallDate() {
        edit(PREF_KEY_INSTALL_DATE, System.currentTimeMillis());
    }

    long getInstallDate() {
        return mPreferences.getLong(PREF_KEY_INSTALL_DATE, 0);
    }

    private static final String PREF_KEY_LAUNCH_TIMES = "android_rate_launch_times";

    void increaseNumberOfLaunches() {
        int numberOfLaunches = getNumberOfLaunches();

        Log.d(TAG, "Number of launches: " + numberOfLaunches + "++");

        edit(PREF_KEY_LAUNCH_TIMES, numberOfLaunches + 1);
    }

    void resetNumberOfLaunches() {
        edit(PREF_KEY_LAUNCH_TIMES, 0);
    }

    int getNumberOfLaunches() {
        return mPreferences.getInt(PREF_KEY_LAUNCH_TIMES, 0);
    }

    boolean isFirstLaunch() {
        return mPreferences.getLong(PREF_KEY_INSTALL_DATE, 0) == 0L;
    }

    int getNumberOfRatingEvents() {
        return mPreferences.getInt(PREF_KEY_EVENT_TIMES, 0);
    }

    void increaseNumberOfRatingEvents() {
        int numberOfRatingEvents = getNumberOfRatingEvents();

        if (numberOfRatingEvents > 5000) {
            return;
        }

        edit(PREF_KEY_EVENT_TIMES, numberOfRatingEvents + 1);
    }

    void resetNumberOfRatingEvents() {
        edit(PREF_KEY_EVENT_TIMES, 0);
    }

    /*
    Accessories
     */

    private void edit(@NonNull final String key, final boolean value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void edit(@NonNull final String key, final int value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private void edit(@NonNull final String key, final long value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    private void edit(@NonNull final String key, final String value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void remove(@NonNull final String key) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

}
