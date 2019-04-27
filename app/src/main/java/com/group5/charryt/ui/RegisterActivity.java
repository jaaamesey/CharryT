package com.group5.charryt.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group5.charryt.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private Button registerBtn;
    private EditText firstNameEt, lastNameEt, emailEt, passwordEt, passwordConfirmEt;
    private TextView feedbackTxt;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        mAuth = FirebaseAuth.getInstance();

        init();
        registerBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String email, pw, pwConf;
                    email = emailEt.getText().toString();
                    pw = passwordEt.getText().toString();
                    pwConf = passwordConfirmEt.getText().toString();

                    if(!pw.equals(pwConf)) {
                        feedbackTxt.setTextColor(Color.RED);
                        feedbackTxt.setText("Passwords do not match.");
                    }

                    if(createAccount(email, pw)) {
                        feedbackTxt.setTextColor(Color.GREEN);
                        feedbackTxt.setText("Account created successfully!");
                    }
                }
            }
        );
    }

    // returns true or false depending on if account was created successfully
    private boolean createAccount(String email, String pw) {
        mAuth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("firstName", firstNameEt.getText());
                            userData.put("lastName", lastNameEt.getText());

                            db.collection("users").document(currentUser.getEmail())
                                    .set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            success = true;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            success = false;
                                        }
                                    });
                        } else {
                            success = false;
                        }
                    }
                });
        return success;
    }

    //Private procedure to initialise all textfields and button
    private void init() {
        registerBtn = findViewById(R.id.registerBtn);
        firstNameEt = findViewById(R.id.firstNameEt);
        lastNameEt = findViewById(R.id.lastNameEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        passwordConfirmEt = findViewById(R.id.passwordConfirmEt);
        feedbackTxt = findViewById(R.id.feedbackTxt);
    };


}
