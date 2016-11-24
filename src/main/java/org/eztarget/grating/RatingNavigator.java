package org.eztarget.grating;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

public final class RatingNavigator {

    public static void startGooglePlayActivity(final Activity activity) {
        final String packageName = activity.getPackageName();
        activity.startActivity(
                new Intent(Intent.ACTION_VIEW, UriHelper.getGooglePlay(packageName))
        );

        PreferenceHelper.from(activity).disableRating();
    }

    static Dialog create(final Activity activity, final OnClickButtonListener listener) {

        final String appName = activity.getString(activity.getApplicationInfo().labelRes);
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(
                String.format(activity.getString(R.string.rate_dialog_title), appName)
        );
        builder.setMessage(
                String.format(activity.getString(R.string.rate_dialog_message), appName)
        );

//        if (view != null) {
//            builder.setView(view);
//        }

        builder.setPositiveButton(
                R.string.rate_dialog_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startGooglePlayActivity(activity);

                        if (listener != null) {
                            listener.onClickButton(OnClickButtonListener.RateButton.RATE);
                        }
                    }
                }
        );

        builder.setNeutralButton(
                R.string.rate_dialog_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceHelper.setRemindSelectedDate(activity);

                        if (listener != null)
                            listener.onClickButton(OnClickButtonListener.RateButton.REMIND);
                    }
                }
        );

        builder.setNegativeButton(
                R.string.rate_dialog_no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceHelper.from(activity).disableRating();

                        if (listener != null)
                            listener.onClickButton(OnClickButtonListener.RateButton.DECLINE);
                    }
                }
        );

        return builder.create();
    }


}