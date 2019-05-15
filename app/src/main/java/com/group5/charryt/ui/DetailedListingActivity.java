package com.group5.charryt.ui;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.group5.charryt.R;
import com.group5.charryt.data.Listing;

public class DetailedListingActivity extends AppCompatActivity {
    private TextView listingNameTv, datePostedTv, descriptionTv, locationPlaceholder;
    private ImageView imageView;
    private Button makeBookingBtn;
    private String title, datePosted, description, location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_listing_activity);
        init();


        imageView.setImageResource(R.drawable.test);

        Intent intent = getIntent();
        listingNameTv.setText(intent.getStringExtra("name"));
        descriptionTv.setText(intent.getStringExtra("description"));
        datePostedTv.setText(intent.getStringExtra("date"));
        locationPlaceholder.setText(intent.getStringExtra("location"));
    }

    private void init() {
        listingNameTv = findViewById(R.id.listingNameTv);
        datePostedTv = findViewById(R.id.datePostedTv);
        descriptionTv = findViewById(R.id.descriptionTv);
        makeBookingBtn = findViewById(R.id.makeBookingBtn);
        locationPlaceholder = findViewById(R.id.locationPlaceholder);
        imageView = findViewById(R.id.imageView);
    }
}
