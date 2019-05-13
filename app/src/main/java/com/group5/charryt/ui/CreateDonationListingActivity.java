package com.group5.charryt.ui;

import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group5.charryt.R;
import com.group5.charryt.Utils;

import java.util.HashMap;
import java.util.Map;

public class CreateDonationListingActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Button submitButton;
    private EditText titleInput;
    private EditText descriptionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_donation_listing);
        final Context context = this; // Stored for inner classes

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        titleInput = findViewById(R.id.title_input);
        descriptionInput = findViewById(R.id.description_input);
        submitButton = findViewById(R.id.submit_donation_listing_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) {
                    Utils.showDialog("Error: User not logged in", context);
                    return;
                }
                String title = titleInput.getText().toString();
                String description = descriptionInput.getText().toString();
                if (title.isEmpty() || description.isEmpty()) {
                    Utils.showDialog("Fields must not be empty.", context);
                    return;
                }

                Map<String, Object> data = new HashMap<>();
                data.put("title", title);
                data.put("description", description);
                data.put("postDate", Calendar.getInstance().getTime());
                data.put("owner", user);

                submitButton.setEnabled(false);
                Task<DocumentReference> postListingTask = db.collection("listings").add(data);
                postListingTask.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful())
                            finish();
                        else {
                            submitButton.setEnabled(true);
                            String err = String.valueOf(task.getException());
                            Utils.showDialog("Submission error: " + err, context);
                        }
                    }
                });
            }
        });
    }



}