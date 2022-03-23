package com.example.mobile_client;

import static android.content.ContentValues.TAG;

import static com.amazonaws.mobile.client.UserState.SIGNED_OUT_USER_POOLS_TOKENS_INVALID;
import static com.amplifyframework.auth.AuthChannelEventName.SIGNED_IN;
import static com.amplifyframework.auth.AuthChannelEventName.SIGNED_OUT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.results.SignInResult;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthSession;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText mobileNumber;
    Button signIn, goToSignUp;
    Util util;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        show();
        AWSMobileClient.getInstance().initialize(this, new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
                switch (result.getUserState()) {
                    case SIGNED_IN:
                        Log.d(TAG, "SIGNED_IN");
                        hide();
                        Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                        LoginActivity.this.startActivity(homeIntent);
                        break;
                    case SIGNED_OUT:
                        Log.d(TAG, "SIGNED_OUT");
                        hide();
                        break;
                    case SIGNED_OUT_USER_POOLS_TOKENS_INVALID:
                        Log.d(TAG, "SIGNED_OUT_USER_POOLS_TOKENS_INVALID");
                        hide();
                        break;
                    default:
                        Log.d(TAG, "DEFAULT");
                        hide();
                        AWSMobileClient.getInstance().signOut();
                        break;
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "DEFAULT");
                hide();
            }
        });

        signIn = findViewById(R.id.signin);
        goToSignUp = findViewById(R.id.go_to_signup);
        mobileNumber = findViewById(R.id.mobile_number);

        util = new Util(this);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(myIntent);
                finish();
            }
        });
    }

    private void signIn() {
        show();
        AWSMobileClient.getInstance().signIn(mobileNumber.getText().toString(), "", null, new Callback<SignInResult>() {
            @Override
            public void onResult(final SignInResult signInResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("APP", "Sign-in callback state: " + signInResult.getSignInState());
                        switch (signInResult.getSignInState()) {
                            case DONE:
                                Log.d(TAG, "Sign-in done.");
                                hide();
                                Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                LoginActivity.this.startActivity(homeIntent);
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
                            case CUSTOM_CHALLENGE:
                                hide();
                                Intent myIntent = new Intent(LoginActivity.this, OTPActivity.class);
                                LoginActivity.this.startActivity(myIntent);
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
                Log.e(TAG, "Sign-in error", e);
                util.showAlert("Sign-in error");
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