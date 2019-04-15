package com.group5.charryt.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.group5.charryt.R;

public class RegisterActivity extends AppCompatActivity {
    private Button registerBtn;
    private EditText firstNameEt, lastNameEt, emailEt, passwordEt, passwordConfirmEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        init();
        registerBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String fName, lName, email, pw, pwConf;
                    fName = firstNameEt.getText().toString();
                    lName = lastNameEt.getText().toString();
                    email = emailEt.getText().toString();
                    pw = passwordEt.getText().toString();
                    pwConf = passwordConfirmEt.getText().toString();
                }
            }
        );
    }

    //Private procedure to initialise all textfields and button
    private void init() {
        registerBtn = findViewById(R.id.registerBtn);
        firstNameEt = findViewById(R.id.firstNameEt);
        lastNameEt = findViewById(R.id.lastNameEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        passwordConfirmEt = findViewById(R.id.passwordConfirmEt);
    };


}
