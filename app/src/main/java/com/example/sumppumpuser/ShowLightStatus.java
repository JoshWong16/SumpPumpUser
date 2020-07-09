package com.example.sumppumpuser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.auth0.android.jwt.JWT;

import java.util.HashMap;
import java.util.List;

public class ShowLightStatus extends AppCompatActivity {

    TextView Light1, Light2, Light3, Light4, Light5, Light6;

    TextView[] textViewArr = new TextView[]{Light1, Light2, Light3, Light4, Light5, Light6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_light_status);



        //cast textView objects to actual textViews
        textViewArr[0] = findViewById(R.id.light1);
        textViewArr[1] = findViewById(R.id.light2);
        textViewArr[2] = findViewById(R.id.light3);
        textViewArr[3] = findViewById(R.id.light4);
        textViewArr[4] = findViewById(R.id.light5);
        textViewArr[5] = findViewById(R.id.light6);

        final GetAllAsyncTask getAllAsyncTask = new GetAllAsyncTask();
        getAllAsyncTask.execute();
    }

    /**
     * Async Task to get all light statuses
     */
    private class GetAllAsyncTask extends AsyncTask<Void, Void, String[]>{
        Document userItem;
        String[] lightStatuses = new String[6];

        @Override
        protected String[] doInBackground(Void... voids) {
            Log.d(AppSettings.tag, "In GetAllAsyncTask DoInBackground");

            String idToken = getIntent().getStringExtra("idToken");
            HashMap<String, String> logins = new HashMap<String, String>();
            logins.put("cognito-idp.us-west-2.amazonaws.com/us-west-2_kZujWKyqd", idToken);

            //create instance of DatabaseAccess and access user idToken
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ShowLightStatus.this, logins);
            JWT jwt = new JWT(idToken);
            String subject = jwt.getSubject();
            Log.d(AppSettings.tag, "user sub: " + subject);
            try {
                //call getUserItem method
                userItem = databaseAccess.getUserItem(subject);

                //retrieve light statuses from dynamoDB
                for(int i = 1; i<7; i++){
                    String lightID = "LightStatus" + String.valueOf(i);
                    lightStatuses[i-1] = databaseAccess.getLightStatus(lightID, userItem);
                }

            }catch (Exception e){
                Log.e(AppSettings.tag, "error getting light statuses: " + e.getLocalizedMessage());
            }

            return lightStatuses;
        }

        @Override
        protected void onPostExecute(String[] lightStatuses) {
            super.onPostExecute(lightStatuses);
            Log.d(AppSettings.tag, "In GetAllAsyncTask onPostExecute");
            //set Text views
            for(int lightNum = 0; lightNum<6; lightNum++){
                textViewArr[lightNum].setText(lightStatuses[lightNum]);
            }

        }


    }


    /**
     * Refresh method
     */
    private void refresh(int milliseconds){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                postRefresh();
            }
        };
    }

    public void postRefresh(){
        //Get all LightStatuses for each LightID and return as a list of Document objects
        final GetAllAsyncTask getAllAsyncTask = new GetAllAsyncTask();
        getAllAsyncTask.execute();

        refresh(1000);
    }
}