package com.example.mobile_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.Tokens;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthSession;
import com.amplifyframework.core.Action;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;

public class HomeActivity extends AppCompatActivity {

    TextView token;
    Button signout;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        show();

        signout = findViewById(R.id.signout);
        token = findViewById(R.id.token);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
                Amplify.Auth.signOut(new Action() {
                    @Override
                    public void call() {
                        hide();
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        HomeActivity.this.startActivity(intent);
                    }
                }, new Consumer<AuthException>() {
                    @Override
                    public void accept(@NonNull AuthException value) {
                        Log.d("signout error",value.toString());
                        hide();
                    }
                });
            }
        });

        AWSMobileClient.getInstance().getTokens(new Callback<Tokens>() {
            @Override
            public void onResult(Tokens result) {
                hide();
                Log.d("token",result.getAccessToken().getTokenString());
                token.setText(result.getAccessToken().getTokenString());
            }

            @Override
            public void onError(Exception e) {
                hide();
                Log.d("token error",e.toString());
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