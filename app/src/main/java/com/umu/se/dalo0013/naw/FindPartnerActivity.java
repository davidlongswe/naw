package com.umu.se.dalo0013.naw;

import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
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
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.umu.se.dalo0013.naw.model.ArmwrestlingClub;
import com.umu.se.dalo0013.naw.model.UserProfile;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import util.Config;
import util.UserProfileApi;


public class FindPartnerActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    public static final String TAG = "FindPartnerActivity";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Profile");

    private ArrayList<ArmwrestlingClub> armwrestlingClubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_partner);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        armwrestlingClubs = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(Objects.requireNonNull(loadJSONFromRaw()));
            JSONArray clubArray = obj.getJSONArray("clubs");
            for (int i = 0; i < clubArray.length() ; i++) {
                ArmwrestlingClub awc = new ArmwrestlingClub();
                JSONObject club = clubArray.getJSONObject(i);
                awc.setClubInfoURL(club.getString("link"));
                awc.setClubName(club.getString("name"));
                awc.setClubLocLatitude(club.getString("latitude"));
                awc.setClubLocLongitude(club.getString("longitude"));
                awc.setClubAddress(club.getString("address"));
                armwrestlingClubs.add(awc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Initialize the SDK
        Places.initialize(getApplicationContext(), Config.PLACES_API_KEY);
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    private String loadJSONFromRaw(){
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
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                // we have permission!
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                zoomInOnUser(location);
                addArmwrestlingClubs();
            }
        }
    }

    private void addArmwrestlingClubs(){
        for(ArmwrestlingClub club : armwrestlingClubs){
            double clubLat = Double.parseDouble(club.getClubLocLatitude());
            double clubLong = Double.parseDouble(club.getClubLocLongitude());
            LatLng clubLatLng = new LatLng(clubLat, clubLong);
            mMap.addMarker(new MarkerOptions()
                    .position(clubLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title(club.getClubName()));
        }
    }

    private void zoomInOnUser(Location location){
        if(location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .title("Me"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    UserProfile userProfile = document.toObject(UserProfile.class);
                    Double userLatitude = userProfile.getUserLatLng().getLatitude();
                    Double userLongitude = userProfile.getUserLatLng().getLongitude();
                    LatLng userLatLng = new LatLng(userLongitude, userLatitude);
                    mMap.addMarker(new MarkerOptions()
                            .position(userLatLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .title(userProfile.getUserName()));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

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