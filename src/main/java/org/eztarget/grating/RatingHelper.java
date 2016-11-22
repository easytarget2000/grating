package org.eztarget.grating;

import android.app.Activity;
import android.content.Context;

/**
 * Created by ronny on 18.12.14.
 */
public class RatingHelper {

    private static RatingHelper mInstance;



    private RatingHelper(final Activity activity) {

        final Context context = activity.getApplicationContext();


    }

    public static RatingHelper with(Activity activity) {
        if (mInstance == null) {
            mInstance = new RatingHelper(activity);
        }

        return mInstance;
    }


}
