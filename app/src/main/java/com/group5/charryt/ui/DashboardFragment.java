package com.group5.charryt.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.group5.charryt.R;
import com.group5.charryt.Utils;
import com.group5.charryt.data.User;

public class DashboardFragment extends Fragment {
    private TextView textView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dashboard_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        MainActivity main = Utils.getMainActivity(this);
        main.setToolbarText("Dashboard");
        textView = view.findViewById(R.id.textView);
        if (User.getCurrentUser().getUserType() == User.UserType.Donor)
            textView.setText("Welcome, " + User.getCurrentUser().getFirstName() + ".");
        else
            textView.setText("Welcome, " + User.getCurrentUser().getName() + ".");
    }


}
