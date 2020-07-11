package com.example.sumppumpuser.ui.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.auth0.android.jwt.JWT;
import com.example.sumppumpuser.AppSettings;
import com.example.sumppumpuser.DatabaseAccess;
import com.example.sumppumpuser.MainActivity;
import com.example.sumppumpuser.PumpTimes;
import com.example.sumppumpuser.R;
import com.example.sumppumpuser.ui.notifications.NotificationsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardFragment extends Fragment {

    Button resetP1, resetP2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);

        resetP1 = root.findViewById(R.id.button_reset_pump1);
        resetP2 = root.findViewById(R.id.button_reset_pump2);

        resetP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setCancelable(true);
                builder.setTitle("Reset Alert!");
                builder.setMessage("You are about to reset the stored time information of pump 1.");

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PumpTimes.pump1Total = 0;
                        NotificationsFragment.updatePumpsTotal();
                    }
                });
                builder.show();
            }
        });

        resetP2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setCancelable(true);
                builder.setTitle("Reset Alert!");
                builder.setMessage("You are about to reset the stored time information of pump 2.");

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PumpTimes.pump2Total = 0;
                        NotificationsFragment.updatePumpsTotal();
                    }
                });
                builder.show();
            }
        });

        return root;
    }
    /**
     * Async Task to reset pump times
     */
    private class ResetPumpTimesAsyncTask extends AsyncTask<String, Void, Boolean> {
        Boolean updateResult;

        @Override
        protected Boolean doInBackground(String... strings) {
            Log.d(AppSettings.tag, "In ResetPumpTimesAsyncTask DoInBackground");

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
                 updateResult = databaseAccess.resetPumpTimes(subject, strings[0]);

            }catch (Exception e){
                Log.e(AppSettings.tag, "error getting pump times: " + e.getLocalizedMessage());
            }

            return updateResult;
        }

        @Override
        protected void onPostExecute(Boolean updateResult) {
            super.onPostExecute(updateResult);
            Log.d(AppSettings.tag, "In ResetPumpTimesAsyncTask onPostExecute: " + updateResult);
        }
    }
}