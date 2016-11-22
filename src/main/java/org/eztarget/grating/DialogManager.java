package org.eztarget.grating;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

import thingsilike.MONOQI.R;

final class DialogManager {
    private DialogManager() {
    }

    static Dialog create(
            final Activity activity,
            final boolean isShowNeutralButton,
            final OnClickButtonListener listener
    ) {

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

                        final String packageName = activity.getPackageName();
                        final Intent intent = new Intent(
                                Intent.ACTION_VIEW,
                                UriHelper.getGooglePlay(packageName)
                        );

                        activity.startActivity(intent);

                        PreferenceHelper.from(activity).disableRating();

                        if (listener != null) {
                            listener.onClickButton(OnClickButtonListener.RateButton.RATE);
                        }
                    }
                }
        );

        if (isShowNeutralButton) {
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
        }

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