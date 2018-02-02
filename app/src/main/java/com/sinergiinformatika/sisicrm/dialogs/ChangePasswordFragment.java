package com.sinergiinformatika.sisicrm.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinergiinformatika.sisicrm.Constants;
import com.sinergiinformatika.sisicrm.R;
import com.sinergiinformatika.sisicrm.conn.RestClient;
import com.sinergiinformatika.sisicrm.conn.RestResponseHandler;
import com.sinergiinformatika.sisicrm.exceptions.HashGenerationException;
import com.sinergiinformatika.sisicrm.utils.Hasher;
import com.sinergiinformatika.sisicrm.utils.User;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "ChangePassDialog";

    private Context context;
    private EditText passwordOldEdit;
    private EditText passwordNewEdit;
    private EditText passwordConfirmEdit;
    private Button sendButton;
    private ProgressBar progressBar;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_change_password);
        dialog.getWindow().getAttributes().width = LinearLayout.LayoutParams.MATCH_PARENT;
        dialog.getWindow().getAttributes().height = LinearLayout.LayoutParams.WRAP_CONTENT;

        passwordOldEdit = (EditText) dialog.findViewById(R.id.password_old_edit);
        passwordNewEdit = (EditText) dialog.findViewById(R.id.password_new_edit);
        passwordConfirmEdit = (EditText) dialog.findViewById(R.id.password_confirm_edit);
        progressBar = (ProgressBar) dialog.findViewById(R.id.password_progress);

        sendButton = (Button) dialog.findViewById(R.id.password_send_btn);
        sendButton.setOnClickListener(this);

        return dialog;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(sendButton)) {
            attemptSend();
        }
    }

    private void attemptSend() {
        View focusView = null;
        String passwordOld = passwordOldEdit.getText().toString();
        final String passwordNew = passwordNewEdit.getText().toString().trim();
        String passwordConfirm = passwordConfirmEdit.getText().toString().trim();

        if (passwordConfirm.isEmpty()) {
            passwordConfirmEdit.setError(context.getString(R.string.error_field_required));
            focusView = passwordConfirmEdit;
        } else if (!passwordConfirm.equals(passwordNew)) {
            passwordConfirmEdit
                    .setError(context.getString(R.string.error_incorrect_password_confirm));
            focusView = passwordConfirmEdit;
        }

        if (passwordNew.isEmpty()) {
            passwordNewEdit.setError(context.getString(R.string.error_field_required));
            focusView = passwordNewEdit;
        } else if (passwordNew.length() < 5) {
            passwordNewEdit.setError(
                    context.getString(R.string.error_field_length_must_equal_to,
                                      "Password baru", "5"));
            focusView = passwordNewEdit;
        }

        if (passwordOld.isEmpty()) {
            passwordOldEdit.setError(context.getString(R.string.error_field_required));
            focusView = passwordOldEdit;
        }

        if (focusView != null) {
            focusView.requestFocus();
        } else {
            JsonHttpResponseHandler responseHandler = new RestResponseHandler(context) {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (Constants.DEBUG) Log.d(TAG, response.toString());

                    try {
                        if (isSuccess(response)) {
                            String hashedPassword = null;
                            try {
                                hashedPassword = Hasher.generateMD5(passwordNew);
                            } catch (HashGenerationException e) {
                                e.printStackTrace();
                                Log.e(TAG, getString(R.string.error_hash_failed) + e.getMessage(),
                                      e);
                            }

                            if (hashedPassword != null) {
                                User.getInstance(context).setKey(hashedPassword);
                            } else {
                                User.getInstance(context).setKey("");
                            }

                            Toast.makeText(context, "Password berhasil diubah", Toast.LENGTH_SHORT)
                                 .show();
                            dismiss();
                        } else {
                            int errorCode = getErrorCode(response);
                            if(errorCode == Constants.ERROR_CODE_PASSWORD_NO_MATCH){
                                Toast.makeText(context, getString(R.string.error_old_password_no_match), Toast.LENGTH_SHORT).show();
                            }else{
                                throw new Exception(getString(R.string.error_change_password_failed));
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage(), e);
                        Toast.makeText(context, getString(R.string.error_change_password_failed), Toast.LENGTH_SHORT).show();
                    }catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                        Toast.makeText(context, getString(R.string.error_change_password_failed), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onStart() {
                    sendButton.setEnabled(false);
                    passwordOldEdit.setEnabled(false);
                    passwordNewEdit.setEnabled(false);
                    passwordConfirmEdit.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    sendButton.setEnabled(true);
                    passwordOldEdit.setEnabled(true);
                    passwordNewEdit.setEnabled(true);
                    passwordConfirmEdit.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                }
            };

            RestClient.getInstance(context, responseHandler)
                      .postPasswordChange(User.getInstance(context).getToken(), passwordOld,
                                          passwordNew, passwordConfirm, true);
        }
    }
}
