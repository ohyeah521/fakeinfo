package com.sch.fakecontacts.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.sch.fakecontacts.R;

public class InputDialogFragment extends BaseDialogFragment {
    private static final String ARG_TITLE = "title";

    private EditText editView;
    private OnPositiveButtonClickListener onPositiveButtonClickListener;
    private OnNegativeButtonClickListener onNegativeButtonClickListener;

    public static InputDialogFragment newInstance(Context context, @StringRes int titleId) {
        return newInstance(context.getString(titleId));
    }

    public static InputDialogFragment newInstance(String title) {
        final Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);

        final InputDialogFragment fragment = new InputDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnPositiveButtonClickListener) {
            onPositiveButtonClickListener = (OnPositiveButtonClickListener) getParentFragment();
        } else if (getContext() instanceof OnPositiveButtonClickListener) {
            onPositiveButtonClickListener = (OnPositiveButtonClickListener) getContext();
        }
        if (getParentFragment() instanceof OnNegativeButtonClickListener) {
            onNegativeButtonClickListener = (OnNegativeButtonClickListener) getParentFragment();
        } else if (getContext() instanceof OnNegativeButtonClickListener) {
            onNegativeButtonClickListener = (OnNegativeButtonClickListener) getContext();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onPositiveButtonClickListener = null;
        onNegativeButtonClickListener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final DialogInterface.OnClickListener listener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    onPositiveButtonClick();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    onNegativeButtonClick();
                    break;
            }
        };
        final String title = getArguments().getString(ARG_TITLE);
        return new AlertDialog.Builder(getContext())
                .setView(createView())
                .setTitle(title)
                .setPositiveButton(R.string.button_ok, listener)
                .setNegativeButton(R.string.button_cancel, listener)
                .create();
    }

    public String getInputText() {
        return editView.getText().toString();
    }

    protected void onPositiveButtonClick() {
        if (onPositiveButtonClickListener != null) {
            onPositiveButtonClickListener.onPositiveButtonClick(this);
        }
    }

    protected void onNegativeButtonClick() {
        if (onNegativeButtonClickListener != null) {
            onNegativeButtonClickListener.onNegativeButtonClick(this);
        }
    }

    private View createView() {
        final View view = View.inflate(getContext(), R.layout.dialog_input, null);
        editView = (EditText) view.findViewById(R.id.edit);
        return view;
    }
}
