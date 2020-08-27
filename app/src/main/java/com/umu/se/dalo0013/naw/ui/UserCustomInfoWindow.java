package com.umu.se.dalo0013.naw.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.umu.se.dalo0013.naw.R;
import com.umu.se.dalo0013.naw.model.UserProfile;

import java.text.MessageFormat;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * UserCustomInfoWindow - a custom info window for when a user is pressed on the map, for use with
 * google maps api
 * @author  David Elfving Long
 * @version 1.0
 * @since   2020-08-27
 */
public class UserCustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    private static final String TAG = "CustomInfoWindow";
    private View view;
    private Context context;

    public UserCustomInfoWindow(Context context) {
        this.context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        view = layoutInflater.inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        UserProfile user = (UserProfile)marker.getTag();
        CircleImageView infoWindowProfilePic = view.findViewById(R.id.info_window_profile_picture);
        Picasso.get().load(marker.getSnippet()).placeholder(R.drawable.loading).fit().into(infoWindowProfilePic,
                new MarkerCallback(marker));
        TextView username = view.findViewById(R.id.iw_username);
        ImageView userGenderIcon= view.findViewById(R.id.iw_gender_icon);
        TextView userGender = view.findViewById(R.id.iw_gender_text);
        TextView userHeight = view.findViewById(R.id.iw_height);
        TextView userForearm = view.findViewById(R.id.iw_forearm);
        TextView userBicep = view.findViewById(R.id.iw_bicep);
        TextView userWeight = view.findViewById(R.id.iw_weight);
        TextView userHometown= view.findViewById(R.id.iw_hometown);
        TextView userHand = view.findViewById(R.id.iw_hand);
        TextView userClub= view.findViewById(R.id.iw_club);
        TextView userBio = view.findViewById(R.id.iw_bio);

        assert user != null;
        username.setText(user.getUserName());
        if(user.getSex().equals("Female")){
            userGenderIcon.setBackground(ResourcesCompat.getDrawable(context.getResources(),
                    R.drawable.female, null));
        }else{
            userGenderIcon.setBackground(ResourcesCompat.getDrawable(context.getResources(),
                    R.drawable.male, null));
        }
        userGender.setText(user.getUserName());
        userHeight.setText(MessageFormat.format("{0} cm", user.getHeight()));
        userForearm.setText(MessageFormat.format("{0} cm", user.getForearmSize()));
        userBicep.setText(MessageFormat.format("{0} cm", user.getBicepSize()));
        userWeight.setText(user.getWeightClass());
        userHometown.setText(user.getHomeTown());
        userHand.setText(user.getHand());
        userClub.setText(user.getClub());
        userBio.setText(user.getBio());
        return view;
    }

    @Override
    public View getInfoContents(final Marker marker) {

        return view;
    }

    /**
     * Hide and show info window incase of populated images not having
     * loaded in time.
     */
    static class MarkerCallback implements Callback
    {
        Marker marker;
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

