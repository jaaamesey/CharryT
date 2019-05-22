package com.group5.charryt.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group5.charryt.R;
import com.group5.charryt.Utils;
import com.group5.charryt.data.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateListingActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference mStorage;

    private static final int GALLERY_INTENT = 2;

    private Button submitButton;
    private EditText titleInput;
    private EditText descriptionInput;
    private Button uploadImageButton;

    private String imagePath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_listing);
        final Context context = this; // Stored for inner classes

        // Add back button to action bar (for some reason called the "home" button
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("Add a listing");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        titleInput = findViewById(R.id.title_input);
        descriptionInput = findViewById(R.id.description_input);
        submitButton = findViewById(R.id.submit_donation_listing_button);
        uploadImageButton = findViewById(R.id.uploadImageBtn);

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

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
                data.put("owner", User.getCurrentUser());
                data.put("type", User.getCurrentUser().getUserType());
                data.put("imagePath", imagePath);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
                Uri uri = data.getData();

                byte[] byteArray = compressImage(uri);

                String fileName = User.getCurrentUser().getId() + "_" + System.currentTimeMillis();
                final StorageReference filePath = mStorage.child("listing_images").child(fileName);

                String message = "Uploading image, this may take a while...";
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                submitButton.setEnabled(false);
                uploadImageButton.setEnabled(false);

                filePath.putBytes(byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String message = "Successfully uploaded image.";
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        submitButton.setEnabled(true);
                        uploadImageButton.setEnabled(true);

                        imagePath = taskSnapshot.getMetadata().getPath();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = "Image upload failed: " + Arrays.toString(e.getStackTrace());
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        submitButton.setEnabled(true);
                        uploadImageButton.setEnabled(true);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                        String message = "(" + progress + "%)";
                        uploadImageButton.setText("Upload Image " + message);
                    }
                });
            }
        }
        catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            submitButton.setEnabled(true);
            uploadImageButton.setEnabled(true);
        }
    }

    private byte[] compressImage(Uri uri) throws IOException {
        // Some crazy compression stuff here
        // Basically this forces the image to be no more than 800 pixels on its largest side,
        // and then compresses it
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();

        if (bitmap.getWidth() > 800) {
            float aspectRatio = (float) bitmap.getHeight() / (float) bitmap.getWidth();
            if (aspectRatio > 1)
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)(800 / aspectRatio), 800, true);
            else {
                bitmap = Bitmap.createScaledBitmap(bitmap, 800, (int)(aspectRatio * 800), true);
            }

        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 42, fileOutputStream);
        return fileOutputStream.toByteArray();
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