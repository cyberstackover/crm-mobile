package com.sinergiinformatika.sisicrm.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sinergiinformatika.sisicrm.BuildConfig;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.MainActivity;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.utils.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private boolean syncInProgress = true;
    private boolean pushInProgress = false;
    private Context context;
    private Button syncButton;
    private Button pushButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        syncInProgress = ((MainActivity) context).isSyncInProgress();
        pushInProgress = ((MainActivity) context).isPushInProgress();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        View infoHeader = view.findViewById(R.id.profile_head_info);
        View logoutHeader = view.findViewById(R.id.profile_head_logout);
        TextView tvInfo = (TextView) infoHeader.findViewById(R.id.txt_item_header);
        TextView tvLogout = (TextView) logoutHeader.findViewById(R.id.txt_item_header);
        TextView tvAbout = (TextView) view.findViewById(R.id.profile_about);
        TextView tvUsername = (TextView) view.findViewById(R.id.profile_username);
        TextView tvName = (TextView) view.findViewById(R.id.profile_name);
        TextView tvPosition = (TextView) view.findViewById(R.id.profile_position);
        syncButton = (Button) view.findViewById(R.id.profile_sync_btn);
        pushButton = (Button) view.findViewById(R.id.profile_push_10data_btn);
        User user = User.getInstance(context);

        String version = BuildConfig.VERSION_NAME;
        version += Constants.DEBUG ? "-dev" : "";

        tvInfo.setText(context.getString(R.string.label_user_profile));
        tvLogout.setText(context.getString(R.string.title_sync_logout));
        tvAbout.setText(context.getString(
                R.string.message_about, context.getString(R.string.app_name),
                version));
        tvUsername.setText(user.getUsername());
        tvName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));

        String role = user.getRoleName();
        if (role.equalsIgnoreCase(Constants.ROLE_NAME_AM)) {
            String area = user.getAreaName();
            if (TextUtils.isEmpty(area)) {
                area = context.getString(R.string.hypen);
            }
            tvPosition.setText(context.getString(R.string.text_position_am, area));
        } else if (role.equalsIgnoreCase(Constants.ROLE_NAME_SALES)) {
            String distributor = user.getDistributor();
            if (TextUtils.isEmpty(distributor)) {
                distributor = context.getString(R.string.hypen);
            }
            tvPosition.setText(context.getString(R.string.text_position_sales, distributor));
        } else {
            tvPosition.setText(role);
        }

        adjustSyncButton();
        adjustPushButton();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            adjustSyncButton();
            adjustPushButton();
        }

        super.setUserVisibleHint(isVisibleToUser);
    }

    public void setSyncInProgress(boolean syncInProgress) {
        this.syncInProgress = syncInProgress;
        adjustSyncButton();
    }

    private void adjustSyncButton() {
        if (syncButton != null) {
            if (syncInProgress) {
                syncButton.setEnabled(false);
                syncButton.setText(R.string.label_syncing);
            } else {
                syncButton.setEnabled(true);
                syncButton.setText(R.string.action_sync);
            }
        }
    }

    public void setPushInProgress(boolean pushInProgress) {
        this.pushInProgress = pushInProgress;
        adjustPushButton();
    }

    private void adjustPushButton() {
        if (pushButton != null) {
            if (pushInProgress) {
                pushButton.setEnabled(false);
                pushButton.setText(R.string.label_push_data);
            } else {
                pushButton.setEnabled(true);
                pushButton.setText(R.string.action_push_data);
            }
        }
    }
}
