package com.group5.charryt.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group5.charryt.R;
import com.group5.charryt.Utils;
import com.group5.charryt.data.Listing;

import org.parceler.Parcels;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateBookingActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Listing listing;

    private DatePicker datePicker;
    private EditText descriptionEt, timeEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_booking_activity);

        // Get view refs
        datePicker = findViewById(R.id.datePicker);
        descriptionEt = findViewById(R.id.descriptionEt);
        timeEt = findViewById(R.id.timeEt);


        // Get listing from parcel in extra data
        listing = Parcels.unwrap(getIntent().getParcelableExtra("listing"));

        // Add back button to action bar (for some reason called the "home" button
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("Create Booking");

        mAuth = FirebaseAuth.getInstance();

    }
    public void Booking(View v){
        Map<String, Object> bookingInformation = new HashMap<>();

        String timeString = timeEt.getText().toString();
        String[] timeArr = timeString.split(":");

        try {
            if (timeArr.length != 2)
                throw new NumberFormatException();

            int hours = Integer.parseInt(timeArr[0]);
            int minutes = Integer.parseInt(timeArr[1]);
            if (hours < 0 || hours > 23)
                throw new NumberFormatException();
            if (minutes < 0 || minutes > 59)
                throw new NumberFormatException();
        }
        catch (NumberFormatException exception) {
            Toast.makeText(getBaseContext(), "Please provide a valid time.", Toast.LENGTH_SHORT).show();
            return;
        }
        bookingInformation.put("creator", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        bookingInformation.put("listing", listing.getId());
        bookingInformation.put("time", timeString);
        bookingInformation.put("date", datePicker.getDayOfMonth() + "/" + datePicker.getMonth() + "/" + datePicker.getYear());
        bookingInformation.put("description", descriptionEt.getText().toString());

        final DocumentReference newDocument = db.collection("bookings").document();

        newDocument.set(bookingInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), "Successfully created booking.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "Error creating booking.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If back button pressed, close this activity.
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
