package com.example.sumppumpuser.ui.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.auth0.android.jwt.JWT;
import com.example.sumppumpuser.AppSettings;
import com.example.sumppumpuser.DatabaseAccess;
import com.example.sumppumpuser.MainActivity;
import com.example.sumppumpuser.R;
import com.example.sumppumpuser.ShowLightStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    TextView Light1, Light2, Light3, Light4, Light5, Light6;
    Button btnRefresh;
    TextView[] textViewArr = new TextView[]{Light1, Light2, Light3, Light4, Light5, Light6};
    Timer timer;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //cast textView objects to actual textViews
        textViewArr[0] = root.findViewById(R.id.light1);
        textViewArr[1] = root.findViewById(R.id.light2);
        textViewArr[2] = root.findViewById(R.id.light3);
        textViewArr[3] = root.findViewById(R.id.light4);
        textViewArr[4] = root.findViewById(R.id.light5);
        textViewArr[5] = root.findViewById(R.id.light6);

        btnRefresh = root.findViewById(R.id.refresh);
        btnRefresh.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        }));

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //call getAllAsyncTask and execute
                final GetAllAsyncTask getAllAsyncTask = new GetAllAsyncTask();
                getAllAsyncTask.execute();
            }
        };

        timer = new Timer("MyTimer");//create a new timer
        timer.scheduleAtFixedRate(timerTask, 1000, 3000);
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer.cancel();
    }

    private void refresh() {
        timer.cancel();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(HomeFragment.this).attach(HomeFragment.this).commit();
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

            //retrieve Intent from LoginActivity create login credentials for identity pool
            String idToken = getActivity().getIntent().getStringExtra("idToken");
            HashMap<String, String> logins = new HashMap<String, String>();
            logins.put("cognito-idp.us-west-2.amazonaws.com/us-west-2_kZujWKyqd", idToken);

            //create instance of DatabaseAccess and access user idToken
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity(), logins);
            JWT jwt = new JWT(idToken);
            String subject = jwt.getSubject();
            try {
                //call getUserItem method
                userItem = databaseAccess.getUserItem(subject);

                //retrieve light statuses from dynamoDB
                for(int i = 1; i<7; i++){
                    String lightID = "LightStatus" + i;
                    Log.d(AppSettings.tag, databaseAccess.getLightStatus(lightID, userItem));
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

}