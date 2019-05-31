package com.group5.charryt.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group5.charryt.R;

import java.util.Arrays;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailEt, passwordEt;
    private TextView firstNameTxt, lastNameTxt;
    private Button loginBtn, registerBtn;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        init();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                if (email.isEmpty() || password.isEmpty())
                    return;
                Toast.makeText(getBaseContext(), "Validating credentials...", Toast.LENGTH_SHORT).show();
                getUser(email, password);

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(register);
            }
        });
    }

    private void init() {
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.pwEt);
        firstNameTxt = findViewById(R.id.firstNameTxt);
        lastNameTxt = findViewById(R.id.lastNameTxt);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
    }

    private void getUser(String emailParam, String passwordParam) {
        mAuth.signInWithEmailAndPassword(emailParam, passwordParam)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            updateDetails();
                            return;
                        } else {
                            firstNameTxt.setTextColor(Color.RED);
                            firstNameTxt.setText("Incorrect password/email.");
                        }
                    }
                });
    }

    private void updateDetails() {
        try {
            DocumentReference docRef = db.collection("users").document(uid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        if (document.exists()) {
                            Intent main = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(main);
                            finish();
                        }
                    }
                }
            });
        } catch (Exception e){
            firstNameTxt.setText(Arrays.toString(e.getStackTrace()));
        }
    }
}
