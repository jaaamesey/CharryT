package com.group5.charryt.ui.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.group5.charryt.R;
import com.group5.charryt.data.Booking;
import com.group5.charryt.data.Listing;
import com.group5.charryt.data.User;
import com.group5.charryt.ui.DetailedListingActivity;

import org.parceler.Parcels;

@SuppressLint("ViewConstructor")
public class BookingView extends View {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private LinearLayout linearLayout;
    private Fragment currentFragment;

    public BookingView(final Context context, ViewGroup parent, final Booking booking) {

        super(context);
        final View view = inflate(context, R.layout.booking_view, parent);
        linearLayout = view.findViewById(R.id.linear_layout);
        titleTextView = view.findViewById(R.id.title_text);
        descriptionTextView = view.findViewById(R.id.description_text);

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
