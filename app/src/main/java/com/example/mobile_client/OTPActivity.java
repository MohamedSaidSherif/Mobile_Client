package com.example.mobile_client;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.SignInResult;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoServiceConstants;
import com.amplifyframework.core.Amplify;

import java.util.HashMap;
import java.util.Map;

public class OTPActivity extends AppCompatActivity {
    EditText otp;
    Button submitOTP;
    Util util;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);

        otp = findViewById(R.id.otp);
        submitOTP = findViewById(R.id.submit_otp);
        util = new Util(this);
        submitOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOTP();
            }
        });
    }

    private void submitOTP() {
        show();
        Map<String, String> res = new HashMap<String, String>();
        res.put(CognitoServiceConstants.CHLG_RESP_ANSWER, otp.getText().toString());
        AWSMobileClient.getInstance().confirmSignIn(res, new Callback<SignInResult>() {
            @Override
            public void onResult(final SignInResult signInResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Sign-in callback state: " + signInResult.getSignInState());
                        switch (signInResult.getSignInState()) {
                            case DONE:
                                Log.d(TAG, "Sign-in done.");
                                hide();
                                Intent homeIntent = new Intent(OTPActivity.this, HomeActivity.class);
                                OTPActivity.this.startActivity(homeIntent);
                                break;
                            case SMS_MFA:
                                Log.d(TAG, "Please confirm sign-in with SMS.");
                                util.showAlert("Please confirm sign-in with SMS.");
                                hide();
                                break;
                            case NEW_PASSWORD_REQUIRED:
                                Log.d(TAG, "Please confirm sign-in with new password.");
                                util.showAlert("Please confirm sign-in with new password.");
                                hide();
                                break;
                            default:
                                Log.d(TAG, "Unsupported sign-in confirmation: " + signInResult.getSignInState());
                                util.showAlert("Unsupported sign-in confirmation");
                                hide();
                                break;
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Confirm Custom auth Sign-in error", e);
                util.showAlert("Confirm Custom auth Sign-in error");
                hide();
            }
        });

    }

    public void show() {

        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void hide() {

        if (null != this.mProgressDialog && this.mProgressDialog.isShowing())
            this.mProgressDialog.dismiss();
    }
}