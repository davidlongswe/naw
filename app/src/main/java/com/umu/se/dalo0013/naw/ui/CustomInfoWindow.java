package com.umu.se.dalo0013.naw.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.umu.se.dalo0013.naw.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
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
    public View getInfoContents(Marker marker) {
        CircleImageView infoWindowProfilePic = view.findViewById(R.id.info_window_profile_picture);
        Picasso.get().load(marker.getSnippet()).fit().into(infoWindowProfilePic);
        Button connectButton = view.findViewById(R.id.info_window_send_message_button);
        TextView infoWindowDetails = view.findViewById(R.id.info_window_user_details);
        infoWindowDetails.setText(marker.getTitle());
        return view;
    }

}

