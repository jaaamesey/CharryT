package com.group5.charryt.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group5.charryt.R;
import com.group5.charryt.data.ImageCache;
import com.group5.charryt.data.Listing;
import com.group5.charryt.data.User;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class DetailedListingActivity extends AppCompatActivity {
    private TextView listingNameTv, datePostedTv, descriptionTv;
    private ImageView imageView;
    private Button makeBookingBtn;
    private MapView mapView;

    private Listing listing;
    private GoogleMap map;

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
        imageView = findViewById(R.id.imageView);
        mapView = findViewById(R.id.mapView);

        // Get listing from parcel in extra data
        listing = Parcels.unwrap(getIntent().getParcelableExtra("listing"));

        // Feed text views data from the listing
        listingNameTv.setText(listing.getTitle());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        datePostedTv.setText("Posted by " + listing.getOwner().getName() + ", " + dateFormat.format(listing.getPostDate()));
        String descriptionText = listing.getDescription();
        if (listing.isLocationProvided())
            descriptionText += "\n\nLocation: " + listing.getLocationString();
        descriptionTv.setText(descriptionText);

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
                imageView.setVisibility(View.INVISIBLE);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(listing.getImagePath());

                storageReference.getBytes(2048 * 1024).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        Bitmap image = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                        imageView.setImageBitmap(image);
                        imageView.setVisibility(View.VISIBLE);
                        ImageCache.addImage(listing.getImagePath(), image);
                    }
                });
            }
        }
        // Otherwise, if there is no image, shrink the view so no space is wasted.
        else {
            imageView.setVisibility(View.GONE);
        }

        final DetailedListingActivity self = this;
        // Set mapView stuff if location is given
        if (listing.isLocationProvided()) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    try {
                        MapsInitializer.initialize(getBaseContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Try to set starting location to wherever user is
                    try {
                        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    } catch (SecurityException ignored) {
                        ActivityCompat.requestPermissions(self, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(listing.getLatitude(), listing.getLongitude()), 10);
                    map.animateCamera(cameraUpdate);

                    MarkerOptions marker = new MarkerOptions();
                    marker.position(new LatLng(listing.getLatitude(), listing.getLongitude()));
                    googleMap.addMarker(marker);

                    // Disable all gestures on the map
                    map.getUiSettings().setAllGesturesEnabled(false);
                }
            });
        } else {
            mapView.setVisibility(View.GONE);
        }

        // The make booking button changes to a delete button if the listing is owned by the user.
        if (User.getCurrentUser().getId().equals(listing.getOwner().getId()))
            makeBookingBtn.setText("Delete listing");


        makeBookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If the make booking button is a delete button, open the deletion dialog.
                if (User.getCurrentUser().getId().equals(listing.getOwner().getId())) {
                    onDeletePressed();
                    return;
                }

                CreateBookingActivity item = new CreateBookingActivity();
                Intent intent = new Intent(DetailedListingActivity.this, item.getClass());
                intent.putExtra("listing", Parcels.wrap(listing));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        if (listing.isLocationProvided()) {
            try {
                mapView.onResume();
            } catch (Exception ignored) {

            }
        }


        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listing.isLocationProvided()) {
            try {
                mapView.onDestroy();
            } catch (Exception ignored) {

            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (listing.isLocationProvided()) {
            try {
                mapView.onLowMemory();
            } catch (Exception ignored) {

            }
        }
    }

    private void onDeletePressed() {
        // Create a YES/NO dialog for listing deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm deletion");
        builder.setMessage("Delete this listing?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Delete the listing
                DocumentReference docRef = db.collection("listings").document(listing.getId());
                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), "Listing successfully deleted.", Toast.LENGTH_SHORT).show();
                        refreshParent();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "Error deleting listing.", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void refreshParent() {
        try {
            ViewListingsFragment listingsFragment = (ViewListingsFragment) MainActivity.mainActivity.getCurrentFragment();
            listingsFragment.refreshListings();
        }
        catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
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
