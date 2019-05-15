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
import com.group5.charryt.data.Listing;
import com.group5.charryt.ui.DetailedListingActivity;
import com.group5.charryt.ui.MainActivity;
import com.group5.charryt.ui.ViewListingsFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("ViewConstructor")
public class ListingView extends View {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView imageView;
    private LinearLayout linearLayout;
    private Fragment currentFragment;

//    private Listing listing;

    public ListingView(final Context context, ViewGroup parent, final Listing listing) {

        super(context);
        final View view = inflate(context, R.layout.listing_view, parent);
        linearLayout = view.findViewById(R.id.linear_layout);
        titleTextView = view.findViewById(R.id.title_text);
        descriptionTextView = view.findViewById(R.id.description_text);
        imageView = view.findViewById(R.id.item_image_view);

//        this.listing = listing;
        setTag(listing);

        updateView(listing);

        linearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date datePosted = listing.getPostDate();
                    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
                    DetailedListingActivity item = new DetailedListingActivity();
                    Intent viewItem = new Intent(getActivity(), item.getClass());
                    viewItem.putExtra("name", listing.getTitle());
                    viewItem.putExtra("date", "Posted: " + format.format(datePosted));
                    viewItem.putExtra("description", listing.getDescription());
                    viewItem.putExtra("location", "6.9, 6.9");
                    context.startActivity(viewItem);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @UiThread
    public void updateView(Listing listing) {
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

        // Test image
        Drawable testImage = ContextCompat.getDrawable(getContext(), R.drawable.test);
        imageView.setImageDrawable(testImage);
        descriptionTextView.setText(listing.getDescription());
        titleTextView.setText(listing.getTitle());
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
