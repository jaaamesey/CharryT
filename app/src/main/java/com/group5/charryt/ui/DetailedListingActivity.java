package com.group5.charryt.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.group5.charryt.R;
import com.group5.charryt.data.Listing;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class DetailedListingActivity extends AppCompatActivity {
    private TextView listingNameTv, datePostedTv, descriptionTv, locationPlaceholder;
    private ImageView imageView;
    private Button makeBookingBtn;
    private Listing listing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_listing_activity);

        // Add back button to action bar (for some reason called the "home" button
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Initialise view fields
        listingNameTv = findViewById(R.id.listingNameTv);
        datePostedTv = findViewById(R.id.datePostedTv);
        descriptionTv = findViewById(R.id.descriptionTv);
        makeBookingBtn = findViewById(R.id.makeBookingBtn);
        locationPlaceholder = findViewById(R.id.locationPlaceholder);
        imageView = findViewById(R.id.imageView);

        // Get listing from parcel in extra data
        listing = Parcels.unwrap(getIntent().getParcelableExtra("listing"));

        // Feed text views data from the listing
        listingNameTv.setText(listing.getTitle());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        datePostedTv.setText("Date posted: " + dateFormat.format(listing.getPostDate()));
        descriptionTv.setText(listing.getDescription());

        // Set action bar title to name of the listing
        Objects.requireNonNull(getSupportActionBar()).setTitle(listing.getTitle());

        // Set image
        imageView.setImageResource(R.drawable.test);

        makeBookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateBookingActivity item = new CreateBookingActivity();
                Intent intent = new Intent(DetailedListingActivity.this, item.getClass());
                intent.putExtra("listing", Parcels.wrap(listing));
                startActivity(intent);
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
