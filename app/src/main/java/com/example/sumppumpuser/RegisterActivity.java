package com.example.sumppumpuser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerUser();
    }

    private void registerUser(){
        final EditText inputEmail = findViewById(R.id.email);
        final EditText inputPassword = findViewById(R.id.password);
        final EditText inputUsername = findViewById(R.id.username);
        final EditText inputName = findViewById(R.id.name);

        //Create a CognitoUserAttributes object and add user attributes
        final CognitoUserAttributes userAttributes = new CognitoUserAttributes();

        //object that executes onSuccess or onFailure methods based on if registration by user is successful
        final SignUpHandler signupCallback = new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser user, boolean signUpConfirmationState, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                //sign up successful
                Log.d(AppSettings.tag, "sign up success...is confirmed: " + signUpConfirmationState);
                //check if this user (cognitoUser) needs to be confirmed
                if (!signUpConfirmationState){
                    Log.d(AppSettings.tag, "sign up success...not confirmed, verification code sent to: " + cognitoUserCodeDeliveryDetails.getDestination());
                }
                else{
                    //user already confirmed
                    Log.d(AppSettings.tag, "sign up success...confirmed");
                }
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(AppSettings.tag, "sign up failure: " + exception.getLocalizedMessage());
            }
        };

        Button buttonRegister = findViewById(R.id.register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add user attributes
                userAttributes.addAttribute("given_name", String.valueOf(inputName.getText()));
                userAttributes.addAttribute("email", String.valueOf(inputEmail.getText()));

                //access custom cognitoSettings class and call getUserPool() to sign up user
                CognitoSettings cognitoSettings = new CognitoSettings(RegisterActivity.this);
                cognitoSettings.getUserPool().signUpInBackground(String.valueOf(inputUsername.getText()),
                        String.valueOf(inputPassword.getText()), userAttributes, null, signupCallback);
            }
        });
    }
}