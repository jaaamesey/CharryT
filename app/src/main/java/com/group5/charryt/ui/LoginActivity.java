package com.group5.charryt.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.group5.charryt.R;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        System.out.println("OPENED LOGIN");
    }
}
