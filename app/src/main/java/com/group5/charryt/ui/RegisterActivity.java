package com.group5.charryt.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.group5.charryt.data.User.UserType;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private UserType userType = UserType.Donor;
    private Button registerBtn;
    private EditText firstNameEt, lastNameEt, emailEt, passwordEt, passwordConfirmEt;
    private Spinner userTypeSpinner;
    private TextView feedbackTxt, firstNameTxt, lastNameTxt;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean created;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        init();
        registerBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String email, pw, pwConf;
                    email = emailEt.getText().toString();
                    pw = passwordEt.getText().toString();
                    pwConf = passwordConfirmEt.getText().toString();
                    if (email.isEmpty() || pw.isEmpty() || pwConf.isEmpty())
                        return;

                    if(!pw.equals(pwConf)) {
                        feedbackTxt.setTextColor(Color.RED);
                        feedbackTxt.setText("Passwords do not match.");
                    } else {
                        createAccount(email, pw);
                    }
                }
            }
        );

        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Cast clicked item position to enum value
                userType = UserType.values()[position];

                if (userType == UserType.Donor) {
                    firstNameTxt.setText("First Name:");
                    lastNameTxt.setVisibility(View.VISIBLE);
                    lastNameEt.setVisibility(View.VISIBLE);
                } else if (userType == UserType.Charity) {
                    firstNameTxt.setText("Organisation Name:");
                    lastNameTxt.setVisibility(View.INVISIBLE);
                    lastNameEt.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    // returns true or false depending on if account was created successfully
    private boolean createAccount(String email, String pw) {
        mAuth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                            Map<String, Object> userData = new HashMap<>();

                            userData.put("userType", userType);

                            if (userType == UserType.Donor) {
                                userData.put("firstName", firstNameEt.getText().toString());
                                userData.put("lastName", lastNameEt.getText().toString());
                            }

                            if (userType == UserType.Charity) {
                                userData.put("name", firstNameEt.getText().toString());
                            }


                            db.collection("users").document(currentUser.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            feedbackTxt.setTextColor(Color.GREEN);
                                            feedbackTxt.setText("Account Created.");
                                            created = true;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            feedbackTxt.setTextColor(Color.RED);
                                            feedbackTxt.setText("Data Not Created.");
                                            created = false;
                                        }
                                    });
                        } else {
                            feedbackTxt.setTextColor(Color.RED);
                            feedbackTxt.setText("User Not Created.");
                            created = false;
                        }
                    }
                });
        return created;
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
        firstNameTxt = findViewById(R.id.firstNameTxt);
        lastNameTxt = findViewById(R.id.lastNameTxt);
        userTypeSpinner = findViewById(R.id.userTypeSpinner);
    }


}
