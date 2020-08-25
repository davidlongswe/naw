package com.umu.se.dalo0013.naw.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.umu.se.dalo0013.naw.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    private static final String TAG = "CustomInfoWindow";
    private View view;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomInfoWindow(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        view = layoutInflater.inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        CircleImageView infoWindowProfilePic = view.findViewById(R.id.info_window_profile_picture);
        Picasso.get().load(marker.getSnippet()).placeholder(R.drawable.loading).fit().into(infoWindowProfilePic,
                new MarkerCallback(marker));
        TextView infoWindowDetails = view.findViewById(R.id.info_window_user_details);
        infoWindowDetails.setText(marker.getTitle());
        return view;
    }

    static class MarkerCallback implements Callback
    {
        Marker marker = null;
        MarkerCallback(Marker marker)
        {
            this.marker = marker;
        }

        @Override
        public void onSuccess()
        {
            if (marker == null)
            {
                return;
            }

            if (!marker.isInfoWindowShown())
            {
                return;
            }
            // If Info Window is showing, then refresh it's contents:

            marker.hideInfoWindow(); // Calling only showInfoWindow() throws an error
            marker.showInfoWindow();
        }

        @Override
        public void onError(Exception e) {
            Log.d(TAG, "onError: " + e.getMessage());
        }
    }
}

