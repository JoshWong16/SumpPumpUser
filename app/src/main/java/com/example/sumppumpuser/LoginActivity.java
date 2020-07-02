package com.example.sumppumpuser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText editTextUsername = findViewById(R.id.username);
        final EditText editTextPassword = findViewById(R.id.password);

        final AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                Log.d(AppSettings.tag, "Login successful, can get tokens here");
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                Log.d(AppSettings.tag, "in getAuthenticationDetails");

                //need to get userID and password to continue
                AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, String.valueOf(editTextPassword.getText()), null);

                //Pass user sign-in credentials to the continuation
                authenticationContinuation.setAuthenticationDetails(authenticationDetails);

                //Allow sign in to continue
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                //not using and MFA
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                //not using
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(AppSettings.tag, "Login failed: " + exception.getLocalizedMessage());
            }
        };

        Button btnlogin = findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CognitoSettings cognitoSettings = new CognitoSettings(LoginActivity.this);
                //retrieve given cognito user from the aws user pool
                CognitoUser thisUser = cognitoSettings.getUserPool().getUser(String.valueOf(editTextUsername.getText()));

                Log.d(AppSettings.tag, "Login button clicked");

                thisUser.getSessionInBackground(authenticationHandler);
            }
        });
    }
}