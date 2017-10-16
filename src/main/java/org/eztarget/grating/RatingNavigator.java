package org.eztarget.grating;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

public final class RatingNavigator {

    private static final String TAG = RatingNavigator.class.getSimpleName();

    private static final String STORE_BASE_URL = "https://play.google.com/store/apps/details?id=";

    private Activity mActivity;

    public RatingNavigator(@NonNull final Activity activity) {
        mActivity = activity;
    }

    public void startGooglePlayActivity() {
        final String packageName = mActivity.getPackageName();
        mActivity.startActivity(
                new Intent(Intent.ACTION_VIEW, Uri.parse(STORE_BASE_URL + packageName))
        );

        PreferenceHelper.from(mActivity).disableRating();
    }

    RatingDialog showDialog(final boolean showRatingBar) {
        final RatingDialog ratingDialog = RatingDialog.newInstance(showRatingBar);
        ratingDialog.show(mActivity.getFragmentManager(), TAG);
        return ratingDialog;
    }

}