package com.group5.charryt.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group5.charryt.R;
import com.group5.charryt.data.ImageCache;
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
        datePostedTv.setText("Posted by " + listing.getOwner().getName() + ", " + dateFormat.format(listing.getPostDate()));
        descriptionTv.setText(listing.getDescription());

        // Set action bar title to name of the listing
        Objects.requireNonNull(getSupportActionBar()).setTitle(listing.getTitle());

        // Set image
        // Handle getting image
        if (listing.getImagePath() != null && !listing.getImagePath().isEmpty()) {
            // If image already exists in the cache, grab the image from the cache instead of downloading it.
            Bitmap imageFromCache = ImageCache.getImage(listing.getImagePath());
            if (imageFromCache != null) {
                imageView.setImageBitmap(imageFromCache);
            }

            // If image doesn't exist in cache, download it from server.
            else {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(listing.getImagePath());

                storageReference.getBytes(2048 * 1024).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        Bitmap image = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                        imageView.setImageBitmap(image);
                        ImageCache.addImage(listing.getImagePath(), image);
                    }
                });
            }
        }
        // Otherwise, if there is no image, shrink the view so no space is wasted.
        else {
            imageView.setVisibility(View.GONE);
        }

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
