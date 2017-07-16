package com.sch.fakecontacts.ui.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
    private static final String ARG_MESSAGE = "message";

    public static ProgressDialogFragment newInstance(Context context, @StringRes int messageId) {
        return newInstance(context.getString(messageId));
    }

    public static ProgressDialogFragment newInstance(String message) {
        final Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);

        final ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.setArguments(args);
        fragment.setCancelable(false);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage(getArguments().getString(ARG_MESSAGE));
        return dialog;
    }
}
