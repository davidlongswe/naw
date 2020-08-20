package com.umu.se.dalo0013.naw;

import com.google.android.gms.maps.model.LatLngBounds;
import com.umu.se.dalo0013.naw.model.ArmwrestlingClub;
import com.umu.se.dalo0013.naw.model.UserProfile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.umu.se.dalo0013.naw.ui.CustomInfoWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import util.Config;

public class FindPartnerActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    public static final String TAG = "FindPartnerActivity";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private GoogleMap mMap;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Profile");

    private ArrayList<ArmwrestlingClub> armWrestlingClubs;
    private ArrayList<UserProfile> users;

    private boolean clubButtonPressed = false;
    private boolean clubsShowing = false;
    private boolean usersButtonPressed = false;
    private boolean usersShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_partner);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        Places.initialize(getApplicationContext(), Config.PLACES_API_KEY);
        PlacesClient placesClient = Places.createClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private String loadJSONFromRaw() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.clubs);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void getArmWrestlingClubs() {
        armWrestlingClubs = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(Objects.requireNonNull(loadJSONFromRaw()));
            JSONArray clubArray = obj.getJSONArray("clubs");
            for (int i = 0; i < clubArray.length(); i++) {
                ArmwrestlingClub awc = new ArmwrestlingClub();
                JSONObject club = clubArray.getJSONObject(i);
                awc.setClubInfoURL(club.getString("link"));
                awc.setClubName(club.getString("name"));
                awc.setClubLocLatitude(club.getString("latitude"));
                awc.setClubLocLongitude(club.getString("longitude"));
                awc.setClubAddress(club.getString("address"));
                armWrestlingClubs.add(awc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void toggleArmWrestlingClubs() {
        if(clubButtonPressed && !clubsShowing) {
            for (ArmwrestlingClub club : armWrestlingClubs) {
                double clubLat = Double.parseDouble(club.getClubLocLatitude());
                double clubLong = Double.parseDouble(club.getClubLocLongitude());
                LatLng clubLatLng = new LatLng(clubLat, clubLong);
                String clubInfo = club.getClubInfoURL();
                mMap.addMarker(new MarkerOptions()
                        .position(clubLatLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .title(club.getClubName())
                        .snippet(clubInfo));
            }
            clubsShowing = true;
        }else{
            mMap.clear();
            clubsShowing = false;
        }
    }
    private void getUsers(){
        users = new ArrayList<>();
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    UserProfile userProfile = document.toObject(UserProfile.class);
                    users.add(userProfile);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void toggleUsersShown() {
        if (usersButtonPressed && !usersShowing) {
            for (UserProfile user : users) {
                Double userLatitude = user.getUserLatLng().getLatitude();
                Double userLongitude = user.getUserLatLng().getLongitude();
                LatLng userLatLng = new LatLng(userLongitude, userLatitude);
                String userInfo = "Username: " + user.getUserName() + "\n"
                        + "Sex: " + user.getSex() + "\n"
                        + "Height: " + user.getHeight() + " cm" + "\n"
                        + "Forearm: " + user.getForearmSize() + " cm" + "\n"
                        + "Bicep: " + user.getBicepSize() + " cm" + "\n"
                        + "Weight class: " + user.getWeightClass();
                mMap.addMarker(new MarkerOptions()
                        .position(userLatLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .title(userInfo).snippet(user.getProfilePictureUrl().toString()));
            }
            usersShowing = true;
        } else {
            mMap.clear();
            usersShowing = false;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setCameraBounds();
        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void setCameraBounds(){
        LatLng southWestBound = new LatLng(52.262942, -23.030901);
        LatLng northEastBound = new LatLng(71.952943, 40.064378);
        LatLngBounds nord = new LatLngBounds(southWestBound, northEastBound);
        mMap.setLatLngBoundsForCameraTarget(nord);
    }

    @Override
    protected void onStart(){
        super.onStart();
        getUsers();
        getArmWrestlingClubs();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(marker.getSnippet() != null) {
            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(marker.getSnippet())));
            Toast.makeText(this, "user clicked" + currentUser.getPhotoUrl(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "user clicked", Toast.LENGTH_SHORT).show();
        }
    }

    private void search(){
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG);
        List<String> countries = Arrays.asList("SE", "DK", "NO", "FI", "IS");
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountries(countries)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng userSearchLatLng = place.getLatLng();
                assert userSearchLatLng != null;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userSearchLatLng, 8));

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                assert data != null;
                Status status = Autocomplete.getStatusFromIntent(data);
                assert status.getStatusMessage() != null;
                Log.i(TAG, status.getStatusMessage());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.users:
                usersButtonPressed = true;
                toggleUsersShown();
                break;
            case R.id.clubs:
                clubButtonPressed = true;
                toggleArmWrestlingClubs();
                break;
            case R.id.search:
                search();
                break;
            case R.id.action_home:
                if(currentUser != null && firebaseAuth != null){
                    startActivity(new Intent(FindPartnerActivity.this, HomePageActivity.class));
                    finish();
                }
                break;
            case R.id.action_find_partner:
                if(currentUser != null && firebaseAuth != null){
                    startActivity(new Intent(FindPartnerActivity.this, FindPartnerActivity.class));
                    finish();
                }
                break;
            case R.id.action_profile:
                if(currentUser != null && firebaseAuth != null){
                    startActivity(new Intent(FindPartnerActivity.this, UserProfileActivity.class));
                    finish();
                }
                break;
            case R.id.action_signout:
                if(currentUser != null && firebaseAuth != null) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(FindPartnerActivity.this, MainActivity.class));
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}