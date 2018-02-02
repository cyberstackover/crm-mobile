package com.sinergiinformatika.sisicrm.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 1/4/2015.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class ActionButtonsDialog extends DialogFragment {
    private static final String ARG_ACTIONS = "actions";
    private static final String ARG_LABELS = "labels";
    private static final String ARG_OBJECT_ID = "id";
    private static final String ARG_EXTRA_ID = "extra_id";
    private static final String TAG = ActionButtonsDialog.class.getSimpleName();

    private long objectId, extraId;
    private List<String> actions, labels;
    private Context context;
    private View.OnClickListener listener;

    public static ActionButtonsDialog newInstance(long objectId,
                                                  @NonNull List<String> actions, List<String> titles,
                                                  @NonNull View.OnClickListener listener) {
        if (Constants.DEBUG) Log.d(TAG, "agenda id: " + objectId);
        ActionButtonsDialog dialog = new ActionButtonsDialog();
        Bundle args = new Bundle();

        args.putLong(ARG_OBJECT_ID, objectId);
        args.putStringArrayList(ARG_ACTIONS, new ArrayList<>(actions));
        if (titles != null)
            args.putStringArrayList(ARG_LABELS, new ArrayList<>(titles));

        dialog.setArguments(args);
        dialog.listener = listener;

        return dialog;
    }

    public static ActionButtonsDialog newInstance(long objectId, long extraId,
                                                  @NonNull List<String> actions, List<String> titles,
                                                  @NonNull View.OnClickListener listener) {
        if (Constants.DEBUG) Log.d(TAG, "agenda id: " + objectId);
        ActionButtonsDialog dialog = new ActionButtonsDialog();
        Bundle args = new Bundle();

        args.putLong(ARG_OBJECT_ID, objectId);
        args.putLong(ARG_EXTRA_ID, extraId);
        args.putStringArrayList(ARG_ACTIONS, new ArrayList<>(actions));
        if (titles != null)
            args.putStringArrayList(ARG_LABELS, new ArrayList<>(titles));

        dialog.setArguments(args);
        dialog.listener = listener;

        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;

        Bundle args = getArguments();
        objectId = args.getLong(ARG_OBJECT_ID);
        actions = args.getStringArrayList(ARG_ACTIONS);
        labels = args.getStringArrayList(ARG_LABELS);
        extraId = args.getLong(ARG_EXTRA_ID, -1);
        if (Constants.DEBUG) Log.d(TAG, "agenda id: " + objectId);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(context);
        LinearLayout rootView = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().width = LinearLayout.LayoutParams.MATCH_PARENT;

        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setLayoutParams(layoutParams);

        dialog.setContentView(rootView);

        String defTitle = "Button";
        for (int i = 0; i < actions.size(); i++) {
            Button button = new Button(context);

            button.setLayoutParams(layoutParams);
            button.setPadding(
                    context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin), 0,
                    context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin), 0);
            button.setBackgroundResource(R.drawable.button_transparent);
            button.setTag(R.string.tag_action, actions.get(i));
            button.setTag(R.string.tag_object_id, objectId);
            if (extraId >= 0) button.setTag(R.string.tag_extra_id, extraId);
            button.setOnClickListener(listener);

            if (labels != null && labels.size() > i) {
                button.setText(labels.get(i));
            } else {
                button.setText(defTitle + i);
            }

            rootView.addView(button);
        }

        return dialog;
    }
}
