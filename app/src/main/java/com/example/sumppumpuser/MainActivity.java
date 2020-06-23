package com.example.sumppumpuser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static Button btnShowLightStatus;
    private static final String tag = "sumpUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind btnShowLightStatus to actual button
        btnShowLightStatus = findViewById(R.id.lightStatus);

        //Set onClickListener to execute code when button is clicked
        btnShowLightStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowLightStatusClicked();
            }
        });

    }

    /**
     * Creates intent to start new activity that displays light status
     */
    private void onShowLightStatusClicked(){
        Log.d(AppSettings.tag, "onShowLightStatusClicked");
        Intent intent = new Intent("android.intent.action.ShowLightStatus");
        startActivity(intent);
    }
}