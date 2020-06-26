package com.example.sumppumpuser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import java.util.List;

public class ShowLightStatus extends AppCompatActivity {

    TextView Light1, Light2, Light3, Light4, Light5, Light6;

    Button btnRefresh;
    TextView[] textViewArr = new TextView[]{Light2, Light3, Light4, Light5, Light6, Light1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_light_status);



        //cast textView objects to actual textViews
        textViewArr[0] = findViewById(R.id.light2);
        textViewArr[1] = findViewById(R.id.light3);
        textViewArr[2] = findViewById(R.id.light4);
        textViewArr[3] = findViewById(R.id.light5);
        textViewArr[4] = findViewById(R.id.light6);
        textViewArr[5] = findViewById(R.id.light1);

        //Get all LightStatuses for each LightID and return as a list of Document objects
        final GetAllAsyncTask getAllAsyncTask = new GetAllAsyncTask();
        getAllAsyncTask.execute();

        btnRefresh = findViewById(R.id.refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

    }

    /**
     * Async Task to get all light statuses
     */
    private class GetAllAsyncTask extends AsyncTask<Void, Void, List<Document>>{
        List<Document> documents;

        @Override
        protected List<Document> doInBackground(Void... voids) {
            Log.d(AppSettings.tag, "In GetAllAsyncTask DoInBackground");
            //create instance of DatabaseAccess
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ShowLightStatus.this);

            try {
                //call getAllLightStatus method
                documents = databaseAccess.getAllLightStatus();
                System.out.println(documents);

            }catch (Exception e){
                Log.e(AppSettings.tag, "error getting light statuses");
            }

            return documents;
        }

        @Override
        protected void onPostExecute(List<Document> documents) {
            super.onPostExecute(documents);
            Log.d(AppSettings.tag, "In GetAllAsyncTask onPostExecute");

            for(int lightNum = 0; lightNum<documents.size(); lightNum++){
                Document lightDoc = documents.get(lightNum);
                String lightStatus = String.valueOf(lightDoc.get("LightStatus").asBoolean());
                textViewArr[lightNum].setText(lightStatus);
            }
//            Document light1 = documents.get(1);
//            boolean light1Status = light1.get("LightStatus").asBoolean();
//            String string = String.valueOf(light1Status);
//            textView.setText(string);

        }
    }
}