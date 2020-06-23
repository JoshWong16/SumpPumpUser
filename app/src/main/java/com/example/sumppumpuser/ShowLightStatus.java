package com.example.sumppumpuser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import java.util.List;

public class ShowLightStatus extends AppCompatActivity {

    private List<Document> lightItems;
    static TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_light_status);

        //Get all LightStatuses for each LightID and return as a list of Document objects
        GetAllAsyncTask getAllAsyncTask = new GetAllAsyncTask();
        lightItems = (List<Document>) getAllAsyncTask.execute();

        Document light1 = lightItems.get(0);

        String light1Status = light1.get("LightStatus").asString();

        textView.findViewById(R.id.light1);
        textView.setText(light1Status);
    }

    /**
     * Async Task to get all light statuses
     */
    private class GetAllAsyncTask extends AsyncTask<Void, Void, List<Document>>{
        List<Document> Result;
        @Override
        protected List<Document> doInBackground(Void... voids) {
            Log.d(AppSettings.tag, "In GetAllAsyncTask");
            //create instance of DatabaseAccess
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ShowLightStatus.this);

            try {
                //call getAllLightStatus method
                Result = databaseAccess.getAllLightStatus();

            }catch (Exception e){
                Log.e(AppSettings.tag, "error getting light statuses");
            }

            return Result;
        }
    }
}