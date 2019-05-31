package com.group5.charryt.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.group5.charryt.R;
import com.group5.charryt.data.User;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.annotation.Nullable;

public class MessageActivity extends AppCompatActivity {

    private DocumentReference msgReference;
    private String msgId;
    private FirebaseFirestore db;
    private Button sendBtn;
    private TextView textView;
    private EditText textEdit;
    private ScrollView scrollView;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);

        // Add back button to action bar (for some reason called the "home" button
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();

        sendBtn = findViewById(R.id.sendBtn);
        textEdit = findViewById(R.id.sendText);
        textView = findViewById(R.id.msgTextView);
        scrollView = findViewById(R.id.scroll);

        sendBtn.setEnabled(false);


        // Get user from parcel in extra data
        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        actionBar.setTitle("Message " + user.getName());

        if (user.getId().compareTo(User.getCurrentUser().getId()) > 0) {
            msgId = user.getId() + "_" + User.getCurrentUser().getId();
        } else {
            msgId = User.getCurrentUser().getId() + "_" + user.getId();
        }

        msgReference = db.collection("messages").document(msgId);


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgReference.update("messages", FieldValue.arrayUnion(User.getCurrentUser().getName() + ": " + textEdit.getText()));
                refreshMessages();
                textEdit.setText("");
            }
        });

        msgReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                refreshMessages();
            }
        });

        refreshMessages();
    }

    public void refreshMessages() {
        msgReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {
                    if (Objects.requireNonNull(task.getResult()).exists()) {
                        ArrayList<String> msgArray = (ArrayList<String>) task.getResult().get("messages");
                        StringBuilder output = new StringBuilder();
                        assert msgArray != null;
                        for (String msg : msgArray) {
                            output.append(msg + "\n\n");
                        }
                        textView.setText(output);
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                        sendBtn.setEnabled(true);

                        // Update each others' list of messaged users to have each other at the top
                        db.collection("users").document(user.getId()).update("messagedUsers", FieldValue.arrayRemove(User.getCurrentUser())).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                db.collection("users").document(user.getId()).update("messagedUsers", FieldValue.arrayUnion(User.getCurrentUser()));
                            }
                        });
                        db.collection("users").document(User.getCurrentUser().getId()).update("messagedUsers", FieldValue.arrayRemove(user)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                db.collection("users").document(User.getCurrentUser().getId()).update("messagedUsers", FieldValue.arrayUnion(user));
                            }
                        });
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    // Create empty message data
                    HashMap<String, ArrayList<String>> messagesData = new HashMap<>();
                    messagesData.put("messages", new ArrayList<String>());
                    db.collection("messages").document(msgId)
                            .set(messagesData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            db.collection("users").document(user.getId()).update("messagedUsers", FieldValue.arrayUnion(User.getCurrentUser()));
                            db.collection("users").document(User.getCurrentUser().getId()).update("messagedUsers", FieldValue.arrayUnion(user));
                        }
                    });
                }

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
