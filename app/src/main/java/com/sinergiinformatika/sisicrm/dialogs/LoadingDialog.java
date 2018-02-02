package com.sinergiinformatika.sisicrm.dialogs;


import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Window;

import com.sinergiinformatika.sisicrm.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoadingDialog extends DialogFragment {


    public LoadingDialog() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading);

        return dialog;
    }
}
