package com.example.navigationapp.ui.notifications;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

import com.example.navigationapp.MainActivity;
import com.example.navigationapp.PumpTimes;
import com.example.navigationapp.R;

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
        return root;
    }

    public void appendPump1(int time){
        txtHistoryP1.append(String.valueOf(time)+"\n");
    }

    public void appendPump2(int time){
        txtHistoryP1.append(String.valueOf(time)+"\n");
    }

    @SuppressLint("SetTextI18n")
    public static void updatePumpsTotal(){
        txtPump1.setText("Pump 1 Runtime: " + PumpTimes.pump1Total);
        txtPump2.setText("Pump 2 Runtime: " + PumpTimes.pump2Total);
    }
}