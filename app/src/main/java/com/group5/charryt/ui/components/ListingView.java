package com.group5.charryt.ui.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group5.charryt.R;
import com.group5.charryt.Utils;
import com.group5.charryt.data.ImageCache;
import com.group5.charryt.data.Listing;
import com.group5.charryt.ui.DetailedListingActivity;

import org.parceler.Parcels;

import java.util.Objects;

@SuppressLint("ViewConstructor")
public class ListingView extends View {
    private View cardView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView imageView;
    private LinearLayout linearLayout;

    public ListingView(final Context context, ViewGroup parent, final Listing listing) {
        super(context);
        final View view = inflate(context, R.layout.listing_view, parent);
        linearLayout = view.findViewById(R.id.linear_layout);
        titleTextView = view.findViewById(R.id.title_text);
        descriptionTextView = view.findViewById(R.id.description_text);
        imageView = view.findViewById(R.id.item_image_view);
        cardView = view.findViewById(R.id.cardView);


        setTag(listing);
        updateView(listing);

        // Handle getting image
        if (listing.getImagePath() != null && !listing.getImagePath().isEmpty()) {

            Bitmap imageFromCache = ImageCache.getImage(listing.getImagePath());
            if (imageFromCache != null) {
                setImage(imageFromCache);
            }

            // If image doesn't exist in cache, download it from server.
            else {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(listing.getImagePath());
                // Don't download anything larger than 2MB.
                storageReference.getBytes(2048 * 1024).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        Bitmap image = BitmapFactory.decodeByteArray(task.getResult(), 0, Objects.requireNonNull(task.getResult()).length);
                        setImage(image);
                        // Add image to the cache
                        ImageCache.addImage(listing.getImagePath(), image);
                    }
                });
            }
        }

        // Otherwise, if there is no image, shrink the view so no space is wasted.
        else {
            imageView.setVisibility(GONE);
        }

        cardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailedListingActivity item = new DetailedListingActivity();
                Intent viewItem = new Intent(getActivity(), item.getClass());
                viewItem.putExtra("listing", Parcels.wrap(listing));
                context.startActivity(viewItem);
            }
        });
    }

    private void setImage(Bitmap imageFromCache) {
        imageView.setImageBitmap(imageFromCache);
        // If image is horizontal, don't extend the image downwards.
        float aspectRatio = (float) imageFromCache.getWidth() / (float) imageFromCache.getHeight();
        if (aspectRatio > 0.9) {
            imageView.setCropToPadding(true);
        }
    }

    @UiThread
    public void updateView(Listing listing) {
        // Bug fix for stupid fudging ID thing that doesn't make sense but shut up this was
        // the only way I could fix it, fight me.
        // This generates a unique ID for each item in the layout. Because of this, all UI
        // references need to be obtained BEFORE this loop.
        linearLayout.setId(generateViewId());
        int childCount = linearLayout.getChildCount();
        cardView.setId(generateViewId());
        for (int i = 0; i < childCount; i++) {
            View v = linearLayout.getChildAt(i);
            v.setId(generateViewId());
        }
        imageView.setId(generateViewId());
        descriptionTextView.setId(generateViewId());
        titleTextView.setId(generateViewId());


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
