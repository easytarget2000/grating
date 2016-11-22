package org.eztarget.grating;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by michelsievers on 22/11/2016.
 *
 */

class PreferenceHelper {

    private static final String TAG = "rate/" + PreferenceHelper.class.getSimpleName();

    private static PreferenceHelper instance = null;

    private SharedPreferences mPreferences;

    protected PreferenceHelper(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    static PreferenceHelper from(Context context) {
        if ((instance == null || instance.mPreferences == null) && context != null) {
            instance = new PreferenceHelper(context);
        }
        return instance;
    }

    private static final String PREF_FILE_NAME = "android_rate_pref_file";

    private static final String PREF_KEY_IS_AGREE_SHOW_DIALOG = "android_rate_is_agree_show_dialog";

    private static final String PREF_KEY_REMIND_INTERVAL = "android_rate_remind_interval";

    private static final String PREF_KEY_EVENT_TIMES = "android_rate_event_times";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getPreferencesEditor(Context context) {
        return getPreferences(context).edit();
    }

    void disableRating() {
        edit(PREF_KEY_IS_AGREE_SHOW_DIALOG, false);
    }

    static boolean isRatingEnabled(Context context) {
        return getPreferences(context).getBoolean(PREF_KEY_IS_AGREE_SHOW_DIALOG, true);
    }

    static void setRemindSelectedDate(Context context) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.remove(PREF_KEY_REMIND_INTERVAL);
        editor.putLong(PREF_KEY_REMIND_INTERVAL, System.currentTimeMillis());
        commitOrApply(editor);
    }

    static long getRemindSelectedDate(final Context context) {
        return getPreferences(context).getLong(PREF_KEY_REMIND_INTERVAL, 0);
    }

    private static final String PREF_KEY_INSTALL_DATE = "android_rate_install_date";

    static void setInstallDate(final Context context) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putLong(PREF_KEY_INSTALL_DATE, System.currentTimeMillis());
        commitOrApply(editor);
    }

    static long getInstallDate(Context context) {
        return getPreferences(context).getLong(PREF_KEY_INSTALL_DATE, 0);
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

    static boolean isFirstLaunch(Context context) {
        return getPreferences(context).getLong(PREF_KEY_INSTALL_DATE, 0) == 0L;
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

    private void edit(final String key, final boolean value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void edit(final String key, final int value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private void edit(final String key, final String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void remove(final String key) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(key);
        editor.apply();
    }


    private static void commitOrApply(SharedPreferences.Editor editor) {
        editor.apply();
    }
}
