package com.group5.charryt.ui;

import android.icu.util.Calendar;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
                if (textEdit.getText().toString().length() < 2) {
                    if (textEdit.getText().toString().length() == 0) {
                        Toast.makeText(getBaseContext(), "You can't just send an empty message, silly.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getBaseContext(), "Your message is too short.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String messageText = User.getCurrentUser().getName() + " at " + Calendar.getInstance().getTime().toString() + "\n" + textEdit.getText().toString();

                msgReference.update("messages", FieldValue.arrayUnion(messageText));
                // Update each others' list of messaged users
                final HashMap<String, Object> currentUserData = new HashMap<>();
                currentUserData.put("date", Calendar.getInstance().getTime());
                currentUserData.put("text", messageText);
                currentUserData.put("name", user.getName());

                final HashMap<String, Object> otherUserData = new HashMap<>();
                otherUserData.put("date", Calendar.getInstance().getTime());
                otherUserData.put("text", messageText);
                otherUserData.put("name", User.getCurrentUser().getName());

                // Update current list if exists, or create new list if doesn't exist.
                db.collection("messages").document(user.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                            db.collection("messages").document(user.getId()).update(User.getCurrentUser().getId(), otherUserData);
                        else {
                            HashMap<String, Object> userMessageData = new HashMap<>();
                            userMessageData.put(User.getCurrentUser().getId(), otherUserData);
                            db.collection("messages").document(user.getId()).set(userMessageData);
                        }
                    }
                });

                db.collection("messages").document(User.getCurrentUser().getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                            db.collection("messages").document(User.getCurrentUser().getId()).update(user.getId(), currentUserData);
                        else {
                            HashMap<String, Object> userMessageData = new HashMap<>();
                            userMessageData.put(user.getId(), currentUserData);
                            db.collection("messages").document(User.getCurrentUser().getId()).set(userMessageData);
                        }
                    }
                });

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
