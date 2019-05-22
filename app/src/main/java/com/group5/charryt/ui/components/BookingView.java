package com.group5.charryt.ui.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group5.charryt.R;
import com.group5.charryt.data.Booking;
import com.group5.charryt.data.Listing;
import com.group5.charryt.data.User;
import com.group5.charryt.ui.DetailedListingActivity;
import com.group5.charryt.ui.MainActivity;
import com.group5.charryt.ui.ViewBookingsFragment;

import org.parceler.Parcels;

import java.util.Arrays;

@SuppressLint("ViewConstructor")
public class BookingView extends View {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private LinearLayout linearLayout;

    private Booking booking;

    public BookingView(final Context context, ViewGroup parent, final Booking booking) {
        super(context);
        final View view = inflate(context, R.layout.booking_view, parent);
        linearLayout = view.findViewById(R.id.linear_layout);
        titleTextView = view.findViewById(R.id.title_text);
        descriptionTextView = view.findViewById(R.id.description_text);

        this.booking = booking;
        setTag(booking);
        updateView(booking);

    }

    @UiThread
    public void updateView(Booking booking) {
        // Bug fix for stupid fudging ID thing that doesn't make sense but shut up this was
        // the only way I could fix it, fight me.
        // This generates a unique ID for each item in the layout. Because of this, all UI
        // references need to be obtained BEFORE this loop.

        linearLayout.setId(generateViewId());

        int childCount = linearLayout.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View v = linearLayout.getChildAt(i);
            v.setId(generateViewId());
        }

        String userString = "INVALID USER";
        for (User user : booking.getInvolvedUsers()) {
            if (!user.getId().equals(User.getCurrentUser().getId())) {
                userString = user.getName();
                break;
            }
        }
        descriptionTextView.setText(userString + "\n\n" + booking.getDate().toString() + "\n\n" + booking.getDescription());
        titleTextView.setText(booking.getListing().getTitle());


        ((CardView)linearLayout.getParent()).setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onBookingLongClicked();
                return false;
            }
        });

        ((CardView)linearLayout.getParent()).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Long press a booking to cancel it.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onBookingLongClicked() {
        // Create a YES/NO dialog for booking cancellation/deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        builder.setTitle("Confirm cancellation");
        builder.setMessage("Cancel this booking?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Delete the booking
                DocumentReference docRef = db.collection("bookings").document(booking.getId());
                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Booking successfully cancelled.", Toast.LENGTH_SHORT).show();
                        // Refresh parent to remove from list
                        refreshParent();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error cancelling booking.", Toast.LENGTH_SHORT).show();
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
            ViewBookingsFragment bookingsFragment = (ViewBookingsFragment) ((MainActivity) getActivity()).getCurrentFragment();
            bookingsFragment.refreshBookings();
        }
        catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

}
