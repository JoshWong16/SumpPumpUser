package com.example.sumppumpuser.ui.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.sumppumpuser.MainActivity;
import com.example.sumppumpuser.R;

import java.util.List;

public class HomeFragment extends Fragment {

    TextView Light1, Light2, Light3, Light4, Light5, Light6;

    Button btnRefresh;
    TextView[] textViewArr = new TextView[]{Light2, Light3, Light4, Light5, Light6, Light1};

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        /*homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        //cast textView objects to actual textViews
        textViewArr[0] = root.findViewById(R.id.light2);
        textViewArr[1] = root.findViewById(R.id.light3);
        textViewArr[2] = root.findViewById(R.id.light4);
        textViewArr[3] = root.findViewById(R.id.light5);
        textViewArr[4] = root.findViewById(R.id.light6);
        textViewArr[5] = root.findViewById(R.id.light1);

        //Get all LightStatuses for each LightID and return as a list of Document objects
 //       final HomeFragment.GetAllAsyncTask getAllAsyncTask = new HomeFragment.GetAllAsyncTask();
  //      getAllAsyncTask.execute();

        /*btnRefresh = root.findViewById(R.id.refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });*/


        return root;
    }

    /**
     * Async Task to get all light statuses
     */
    /*private class GetAllAsyncTask extends AsyncTask<Void, Void, List<Document>> {
        List<Document> documents;

        @Override
        protected List<Document> doInBackground(Void... voids) {
            Log.d("MainActivity", "In GetAllAsyncTask DoInBackground");
            //create instance of DatabaseAccess
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(HomeFragment.this);

            try {
                //call getAllLightStatus method
                documents = databaseAccess.getAllLightStatus();
                System.out.println(documents);

            }catch (Exception e){
                Log.e("MainActivity", "error getting light statuses");
            }

            return documents;
        }

        @Override
        protected void onPostExecute(List<Document> documents) {
            super.onPostExecute(documents);
            Log.d("MainActivity", "In GetAllAsyncTask onPostExecute");

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
    }*/

}