package com.group5.charryt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.group5.charryt.R;
import com.group5.charryt.data.User;

import org.parceler.Parcels;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {
    private TextView nameTv, descriptionTv;

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);

        // Add back button to action bar (for some reason called the "home" button
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Initialise view fields
        nameTv = findViewById(R.id.listingNameTv);
        descriptionTv = findViewById(R.id.descriptionTv);


        // Get user from parcel in extra data
        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        // Feed text views data from the user
        nameTv.setText(user.getName());

        String descriptionText = "";

        descriptionTv.setText(descriptionText);

        // Set action bar title to name of the user
        Objects.requireNonNull(getSupportActionBar()).setTitle(user.getName());

        Button messageUserBtn = findViewById(R.id.messageUserBtn);
        messageUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageActivity item = new MessageActivity();
                Intent intent = new Intent(UserProfileActivity.this, item.getClass());
                intent.putExtra("user", Parcels.wrap(user));
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
