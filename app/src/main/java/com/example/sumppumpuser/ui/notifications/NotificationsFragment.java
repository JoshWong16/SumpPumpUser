package com.example.sumppumpuser.ui.notifications;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.auth0.android.jwt.JWT;
import com.example.sumppumpuser.AppSettings;
import com.example.sumppumpuser.DatabaseAccess;
import com.example.sumppumpuser.MainActivity;
import com.example.sumppumpuser.PumpTimes;
import com.example.sumppumpuser.R;

import java.util.HashMap;
import java.util.List;

public class NotificationsFragment extends Fragment {

    public TextView txtHistoryP1;
    public TextView txtHistoryP2;
    public static TextView txtPump1;
    public static TextView txtPump2;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        txtPump1 = root.findViewById(R.id.text_pump1);
        txtPump2 = root.findViewById(R.id.text_pump2);
        final ScrollView scrollView = root.findViewById(R.id.scroll_historyP1);
        final LinearLayout layout1 = root.findViewById(R.id.layout_historyP1);
        final LinearLayout layout2 = root.findViewById(R.id.layout_historyP2);

        GetPumpTimesAsyncTask getPumps = new GetPumpTimesAsyncTask();

        txtHistoryP1 = new TextView(this.getContext());
        txtHistoryP1.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LinearLayout) layout1).addView(txtHistoryP1);

        txtHistoryP2 = new TextView(this.getContext());
        txtHistoryP2.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LinearLayout) layout2).addView(txtHistoryP2);

        txtPump1.setText("Pump 1 Runtime: " + PumpTimes.pump1Total);
        txtPump2.setText("Pump 2 Runtime: " + PumpTimes.pump2Total);

        txtHistoryP1.setText("");
        txtHistoryP2.setText("");
        getPumps.execute();

        return root;
    }

    public void appendPump1(String time){
        txtHistoryP1.append(time+"\n");
    }

    public void appendPump2(String time){
        txtHistoryP1.append(time+"\n");
    }

    @SuppressLint("SetTextI18n")
    public static void updatePumpsTotal(){
        txtPump1.setText("Pump 1 Runtime: " + PumpTimes.pump1Total);
        txtPump2.setText("Pump 2 Runtime: " + PumpTimes.pump2Total);
    }

    /**
     * Async Task to get and display pump times
     */
    private class GetPumpTimesAsyncTask extends AsyncTask<Void, Void, List<List<String>>> {
        List<String> pumpTimes1;
        List<String> pumpTimes2;
        List<List<String>> allPumpTimes;
        @Override
        protected List<List<String>> doInBackground(Void... voids) {
            Log.d(AppSettings.tag, "In GetPumpTimesAsyncTask DoInBackground");

            //retrieve Intent from LoginActivity create login credentials for identity pool
            String idToken = getActivity().getIntent().getStringExtra("idToken");
            HashMap<String, String> logins = new HashMap<String, String>();
            logins.put("cognito-idp.us-west-2.amazonaws.com/us-west-2_kZujWKyqd", idToken);

            //create instance of DatabaseAccess and access user idToken
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity(), logins);
            JWT jwt = new JWT(idToken);
            String subject = jwt.getSubject();
            try {
                //get pump times from dynamoDB
                pumpTimes1 = databaseAccess.getPumpTimeSet(subject, "PumpTimes1");
                pumpTimes2 = databaseAccess.getPumpTimeSet(subject,"PumpTimes2");

                allPumpTimes.add(pumpTimes1);
                allPumpTimes.add(pumpTimes2);

                Log.d(AppSettings.tag, String.valueOf(allPumpTimes));
            }catch (Exception e){
                Log.e(AppSettings.tag, "error getting pump times: " + e.getLocalizedMessage());
            }

            return allPumpTimes;
        }

        @Override
        protected void onPostExecute(List<List<String>> allPumpTimes) {
            super.onPostExecute(allPumpTimes);
            Log.d(AppSettings.tag, "In GetPumpTimesAsyncTask onPostExecute");
            //set Text views


            for(int j = 0; j<allPumpTimes.get(0).size();j++){
                appendPump1(allPumpTimes.get(0).get(j));
            }

            for(int i = 0; i<allPumpTimes.get(1).size();i++){
                appendPump2(allPumpTimes.get(1).get(i));
            }
        }
    }
}