package com.group5.charryt.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group5.charryt.R;

import java.util.HashMap;
import java.util.Map;

public class CreateBookingActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText firstNameEt, lastNameEt ,emailEt, phoneEt, dateEt, timeEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_booking_activity);
        mAuth = FirebaseAuth.getInstance();

        firstNameEt = (EditText) findViewById(R.id.firstNameEt);
        lastNameEt = (EditText) findViewById(R.id.lastNameEt);
        emailEt = (EditText) findViewById(R.id.emailEt);
        phoneEt = (EditText)findViewById(R.id.phoneEt);
        dateEt = (EditText)findViewById(R.id.dateEt);
        timeEt = (EditText)findViewById(R.id.timeEt);
    }
    public void Booking(View v){
        String firstname = firstNameEt.getText().toString();
        String lastname = lastNameEt.getText().toString();
        String email = emailEt.getText().toString();
        String phone = phoneEt.getText().toString();
        String date = dateEt.getText().toString();
        String time = timeEt.getText().toString();


        Map<String, Object> BookingInformation = new HashMap<>();
        BookingInformation.put(KEY_FIRSTNAME, firstname);
        BookingInformation.put(KEY_LASTNAME, lastname);
        BookingInformation.put(KEY_EMAIL, email);
        BookingInformation.put(KEY_PHONE, phone);
        BookingInformation.put(KEY_DATE, date);
        BookingInformation.put(KEY_TIME, time);

        db.collection("bookings").document().set(BookingInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), "Book Successful", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

    }
}
