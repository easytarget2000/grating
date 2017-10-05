package org.eztarget.grating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

/**
 * Created by michelsievers on 29/11/2016.
 */

public class RatingDialog extends DialogFragment {

    private static final String TAG = "rate/" + RatingDialog.class.getSimpleName();

    private static final String ARG_SHOW_BAR = "ARG_SHOW_BAR";

    private float mPlayStoreMinRating = 4f;

    private float mSupportMaxRating = 3f;

    public static RatingDialog newInstance(final boolean showRatingBar) {
        final RatingDialog ratingDialog = new RatingDialog();

        final Bundle args = new Bundle();
        args.putBoolean(ARG_SHOW_BAR, showRatingBar);
        ratingDialog.setArguments(args);

        return ratingDialog;
    }

    public RatingDialog() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AppCompatActivity activity = (AppCompatActivity) getActivity();

        final String appName = getString(activity.getApplicationInfo().labelRes);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setTitle(
                String.format(getString(R.string.rate_dialog_title), appName)
        );
        builder.setMessage(
                String.format(getString(R.string.rate_dialog_message), appName)
        );

        final Bundle args = getArguments();
        if (args.getBoolean(ARG_SHOW_BAR, true)) {
            final View ratingGroup;
            ratingGroup = LayoutInflater.from(activity).inflate(R.layout.view_rating_bar, null);

            final RatingBar ratingBar = (RatingBar) ratingGroup.findViewById(R.id.rating_bar);
            ratingBar.setOnRatingBarChangeListener(
                    new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                            handleRating(v);
                        }
                    }
            );

            builder.setView(ratingGroup);

        } else {
            builder.setPositiveButton(
                    R.string.rate_dialog_ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final AppCompatActivity activity = (AppCompatActivity) getActivity();
                            RatingCoordinator.getInstance().didSelectRateNow(activity);
                        }
                    }
            );
        }

        builder.setNeutralButton(
                R.string.rate_dialog_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RatingCoordinator.getInstance().didSelectRemindLater(getContext());
                    }
                }
        );

        builder.setNegativeButton(
                R.string.rate_dialog_no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RatingCoordinator.getInstance().didSelectDisable(getContext());
                    }
                }
        );

        return builder.create();
    }

    private void handleRating(final float value) {

        if (value <= mSupportMaxRating) {
            showSupportDialog();
        } else if (value >= mPlayStoreMinRating) {
            new RatingNavigator((AppCompatActivity) getActivity()).startGooglePlayActivity();
        } else {
            showThankYouDialog();
        }

        dismiss();
    }

    private void showSupportDialog() {
        MessageDialog
                .newInstance(MessageDialog.Purpose.SUPPORT)
                .show(getFragmentManager(), MessageDialog.TAG);
    }

    private void showThankYouDialog() {
        MessageDialog
                .newInstance(MessageDialog.Purpose.THANK_YOU)
                .show(getFragmentManager(), MessageDialog.TAG);
    }
}
