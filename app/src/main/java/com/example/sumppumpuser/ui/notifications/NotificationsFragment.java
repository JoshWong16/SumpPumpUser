package com.example.sumppumpuser.ui.notifications;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.auth0.android.jwt.JWT;
import com.example.sumppumpuser.AppSettings;
import com.example.sumppumpuser.DatabaseAccess;
import com.example.sumppumpuser.MainActivity;
import com.example.sumppumpuser.PumpTimes;
import com.example.sumppumpuser.R;
import com.example.sumppumpuser.ui.home.HomeFragment;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationsFragment extends Fragment {

    public TextView txtHistoryP1;
    public TextView txtHistoryP2;
    public static TextView txtPump1;
    public static TextView txtPump2;
    private Timer timer;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        txtPump1 = root.findViewById(R.id.text_pump1);
        txtPump2 = root.findViewById(R.id.text_pump2);
        final ScrollView scrollView = root.findViewById(R.id.scroll_historyP1);
        final LinearLayout layout1 = root.findViewById(R.id.layout_historyP1);
        final LinearLayout layout2 = root.findViewById(R.id.layout_historyP2);

        txtHistoryP1 = new TextView(this.getContext());
        txtHistoryP1.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LinearLayout) layout2).addView(txtHistoryP1);

        txtHistoryP2 = new TextView(this.getContext());
        txtHistoryP2.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LinearLayout) layout1).addView(txtHistoryP2);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final GetPumpTimesAsyncTask getPumps = new GetPumpTimesAsyncTask();
                        txtHistoryP1.setText("");
                        txtHistoryP2.setText("");
                        getPumps.execute();
                        txtPump1.setText("Pump 1 Total Runtime: " + PumpTimes.pump1Total);
                        txtPump2.setText("Pump 2 Total Runtime: " + PumpTimes.pump2Total);
                    }
                });
            }
        };

        timer = new Timer("MyTimer");//create a new timer
        timer.scheduleAtFixedRate(timerTask, 1000, 10000);

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

    public void appendPump1(String time){

        txtHistoryP1.append(time + "\n"+ "\n");
    }

    public void appendPump2(String time){
        txtHistoryP2.append(time + "\n"+ "\n");
    }

    @SuppressLint("SetTextI18n")
    public static void updatePumpsTotal(){
        txtPump1.setText("Pump 1 Runtime: " + PumpTimes.pump1Total);
        txtPump2.setText("Pump 2 Runtime: " + PumpTimes.pump2Total);
    }



    /**
     *
     *
     * Sorts the dates in ascending order and prints them
     *
     * returns the total time
     */
    public int sortDateAscending(int pumpSize, List<List<String>> allPumpTimes, int index) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");

        int total = 0;

        String[] times1Arr = new String[pumpSize];

        for (int i = 1; i < pumpSize; i++) {
            times1Arr[i] = allPumpTimes.get(index).get(i);
        }

        Date date1 = null;
        Date date2 = null;
        String temp;
        int hoursPrev = 0;
        int minutesPrev = 0;
        int secondsPrev = 0;
        int hoursDiff = 0;
        int minutesDiff = 0;
        int secondsDiff = 0;


        for (int k = 1; k < pumpSize; k++) {

            String[] date1Split = times1Arr[k].split(",", -1);
            try {
                date1 = sdf.parse(date1Split[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }




            for (int h = k + 1; h < pumpSize; h++) {

                String[] date2Split = times1Arr[h].split(",", -1);
                try {
                    date2 = sdf.parse(date2Split[0]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                try {
                    date2 = sdf.parse(date1Split[0]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if ( date1 != null && date2 != null && date1.compareTo(date2) > 0) {
                    temp = times1Arr[k];
                    times1Arr[k] = times1Arr[h];
                    times1Arr[h] = temp;
                }
            }
        }

        for (int m = 1; m < pumpSize; m++) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM d yyyy HH:mm:ss");
            String[] arrSplit = times1Arr[m].split(",", -1);
            Date tempDate = sdf.parse(arrSplit[0]);
            String strDate = formatter.format(tempDate);

            //getting between runtime times
            String[] spcSplit = arrSplit[0].split(" ", -1);
            String[] hms = spcSplit[3].split(":", -1);
            String hours= hms[0];
            String minutes = hms[1];
            String seconds = hms[2];
            String time = arrSplit[1];

            if(m > 1){
                hoursDiff = Integer.parseInt(hours) - hoursPrev;
                minutesDiff = Integer.parseInt(minutes) - minutesPrev;
                secondsDiff = Integer.parseInt(seconds) - secondsPrev;

            }
            hoursPrev = Integer.parseInt(hours);
            minutesPrev = Integer.parseInt(minutes);
            secondsPrev = Integer.parseInt(seconds) + Integer.parseInt(time);




            if(index == 0) {
                if(m>1){
                    appendPump1("Inactive time: "+hoursDiff+":"+minutesDiff+":"+secondsDiff);
                }
                appendPump1("Active time: "+ strDate + " ---" + arrSplit[1]);
            }else {
                if(m>1){
                    appendPump2("Inactive time: "+hoursDiff+":"+minutesDiff+":"+secondsDiff);
                }
                appendPump2("Active time: "+ strDate + " ---" + arrSplit[1]);
            }
            total = total + Integer.parseInt(arrSplit[1]);
        }
        return total;
    }







    /**
     * Async Task to get and display pump times
     */
    private class GetPumpTimesAsyncTask extends AsyncTask<Void, Void, List<List<String>>> {
        List<String> pumpTimes1;
        List<String> pumpTimes2;
        List<List<String>> allPumpTimes = new ArrayList<>();
        @Override
        protected List<List<String>> doInBackground(Void... voids) {
            Log.d(AppSettings.tag, "In GetPumpTimesAsyncTask DoInBackground");

            //retrieve Intent from LoginActivity create login credentials for identity pool
            String idToken = getActivity().getIntent().getStringExtra("idToken");
            HashMap<String, String> logins = new HashMap<String, String>();
            logins.put(AppSettings.cognitoPoolURL, idToken);

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

            int pump1Size = (allPumpTimes.get(0).size());
            int pump2Size = (allPumpTimes.get(1).size());


            try {
                PumpTimes.pump1Total = sortDateAscending(pump1Size, allPumpTimes, 0);
                PumpTimes.pump2Total = sortDateAscending(pump2Size, allPumpTimes, 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}