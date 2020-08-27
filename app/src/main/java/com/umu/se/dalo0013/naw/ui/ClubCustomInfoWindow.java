package com.umu.se.dalo0013.naw.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.umu.se.dalo0013.naw.R;
/**
 * ClubCustomInfoWindow - custom info window for when an arm wrestling club is pressed,
 * for use with google maps api
 * @author  David Elfving Long
 * @version 1.0
 * @since   2020-08-27
 */
public class ClubCustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    private static final String TAG = "CustomInfoWindow";
    private View view;

    public ClubCustomInfoWindow(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

