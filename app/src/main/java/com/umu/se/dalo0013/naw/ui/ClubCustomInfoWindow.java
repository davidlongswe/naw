package com.umu.se.dalo0013.naw.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.umu.se.dalo0013.naw.R;

public class ClubCustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    private static final String TAG = "CustomInfoWindow";
    private View view;
    private LayoutInflater layoutInflater;
    private Context context;
    private Button learnMore;

    public ClubCustomInfoWindow(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        view = layoutInflater.inflate(R.layout.custom_club_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView clubName = view.findViewById(R.id.club_name_text_view);
        clubName.setText(marker.getTitle());
        return view;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        return view;
    }

}

