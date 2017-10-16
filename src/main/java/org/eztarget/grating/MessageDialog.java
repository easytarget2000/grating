package org.eztarget.grating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by michelsievers on 29/11/2016.
 */

public class MessageDialog extends DialogFragment {

    public static final String TAG = MessageDialog.class.getSimpleName();

    private static final String ARG_IS_SUPPORT_MSG = "SUPPORT_MSG";

    private Purpose mPurpose;

    static MessageDialog newInstance(final Purpose purpose) {
        final MessageDialog dialog = new MessageDialog();

        final Bundle args = new Bundle();
        args.putInt(ARG_IS_SUPPORT_MSG, purpose == Purpose.SUPPORT ? 1 : 2);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);

        final Bundle args = getArguments();
        mPurpose = (args.getInt(ARG_IS_SUPPORT_MSG) == 1) ? Purpose.SUPPORT : Purpose.THANK_YOU;

        if (mPurpose == Purpose.SUPPORT) {
            builder.setMessage(R.string.rate_dialog_support_message);
            builder.setPositiveButton(
                    android.R.string.yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startSupportEmailIntent();
                        }
                    }
            );
            builder.setNegativeButton(R.string.rate_dialog_no, null);
        } else {
            builder.setMessage(R.string.rate_dialog_thank_message);
            builder.setNegativeButton(android.R.string.ok, null);
        }

        return builder.create();
    }

    private void startSupportEmailIntent() {

        final String address = RatingCoordinator.getInstance().getSupportEmailAddress();

        final Intent emailIntent = new Intent(
                Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", address, null)
        );

        final String appName = getString(getActivity().getApplicationInfo().labelRes);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, appName + " App");
        startActivity(Intent.createChooser(emailIntent, address));
    }

    public enum Purpose {
        SUPPORT, THANK_YOU
    }
}
