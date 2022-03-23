package com.example.mobile_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText firstName, lastName, mobileNumber;
    Button signUp, goToSignIn;
    Util util;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);

        util = new Util(this);

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        mobileNumber = findViewById(R.id.mobile_number);
        signUp = findViewById(R.id.signup);
        goToSignIn = findViewById(R.id.go_to_signin);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        goToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);
                finish();
            }
        });
    }

    private void signUp() {
        show();
        List<AuthUserAttribute> userAttributes = new ArrayList<>();
        userAttributes.add(new AuthUserAttribute(AuthUserAttributeKey.givenName(), firstName.getText().toString()));
        userAttributes.add(new AuthUserAttribute(AuthUserAttributeKey.middleName(), lastName.getText().toString()));
        AuthSignUpOptions options = AuthSignUpOptions.builder()
                .userAttributes(userAttributes)
                .build();
        Amplify.Auth.signUp(mobileNumber.getText().toString(), "Password123", options, new Consumer<AuthSignUpResult>() {
                    @Override
                    public void accept(@NonNull AuthSignUpResult value) {
                        hide();
                        Log.i("AuthQuickStart", "Result: " + value.toString());
                        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                        MainActivity.this.startActivity(myIntent);
                    }
                }, new Consumer<AuthException>() {
                    @Override
                    public void accept(@NonNull AuthException value) {
                        hide();

                        Log.e("AuthQuickStart", "Sign up failed", value);
//                        util.showAlert("sign up failed");
                    }
                }

        );
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