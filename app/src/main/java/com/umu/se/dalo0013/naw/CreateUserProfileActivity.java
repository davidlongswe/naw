package com.umu.se.dalo0013.naw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.umu.se.dalo0013.naw.model.UserProfile;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import util.LatLng;
import util.UserProfileApi;

public class CreateUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateUserProfileActivity";
    //view elements
    private TextView userName;
    private LatLng userLocation;
    private EditText heightEditText,
        forearmEditText,
        bicepEditText,
        userBioEditText;
    private String homeTown;
    private Button saveButton;
    private ImageButton profilePictureButton;
    private Spinner userSexSpinner;
    private Spinner weightClassSpinner;
    private ProgressBar profileCreationProgressBar;

    ArrayAdapter<CharSequence> sexSpinnerAdapter;
    ArrayAdapter<CharSequence> weightClassSpinnerAdapter;

    public static final int GALLERY_CODE = 1;
    private Uri imageUri;

    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Profile");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user_profile);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyBWbCiPv4T6jCxqnAS5VAqPjfZ8XyVsn3I");

        userBioEditText = findViewById(R.id.user_bio_profile_create);
        userName = findViewById(R.id.create_profile_username);
        heightEditText = findViewById(R.id.height_text_view_profile_creation);
        forearmEditText = findViewById(R.id.forearm_size_edit_text);
        bicepEditText = findViewById(R.id.bicep_size_edit_text);
        //hometownEditText = findViewById(R.id.hometown_edit_text);
        profileCreationProgressBar = findViewById(R.id.profile_creation_progress_bar);
        saveButton = findViewById(R.id.create_acc_save_button);
        profilePictureButton = findViewById(R.id.profile_picture_button);

        profilePictureButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        if(UserProfileApi.getInstance() != null){
            currentUserId = UserProfileApi.getInstance().getUserId();
            currentUserName = UserProfileApi.getInstance().getUsername();
            userName.setText(currentUserName);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){

                }else{

                }
            }
        };

        userSexSpinner = findViewById(R.id.user_sex_spinner);
        weightClassSpinner = findViewById(R.id.weight_class_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        sexSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sexes, android.R.layout.simple_spinner_item);
        weightClassSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.weight_classes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sexSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightClassSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        userSexSpinner.setAdapter(sexSpinnerAdapter);
        weightClassSpinner.setAdapter(weightClassSpinnerAdapter);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                userLocation = new LatLng(Objects.requireNonNull(place.getLatLng()).latitude, place.getLatLng().longitude);
                Log.d(TAG, "onPlaceSelected: " + userLocation);
                homeTown = place.getName();
            }

            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        profileCreationProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.profile_picture_button:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;
            case R.id.create_acc_save_button:
                saveProfile();
                startActivity(new Intent(CreateUserProfileActivity.this, HomePageActivity.class));
                finish();
                break;
        }
    }

    private void saveProfile(){
        final String userBio = userBioEditText.getText().toString().trim();
        final String userSex = userSexSpinner.getSelectedItem().toString().trim();
        final String userHeight = heightEditText.getText().toString().trim();
        final String userForearmSize = forearmEditText.getText().toString().trim();
        final String userBicepSize = bicepEditText.getText().toString().trim();
        final String userWeightClass = weightClassSpinner.getSelectedItem().toString().trim();
        profileCreationProgressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(userBio)
                && !TextUtils.isEmpty(userSex)
                && !TextUtils.isEmpty(userHeight)
                && !TextUtils.isEmpty(userForearmSize)
                && !TextUtils.isEmpty(userBicepSize)
                && !TextUtils.isEmpty(userWeightClass)
                && homeTown != null
                && imageUri != null){
            final StorageReference filepath = storageReference
                    .child("profile_picture_" + Timestamp.now().getSeconds());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            UserProfile userProfile = new UserProfile();
                            userProfile.setBio(userBio);
                            userProfile.setProfilePictureUrl(imageUrl);
                            userProfile.setSex(userSex);
                            userProfile.setHeight(userHeight);
                            userProfile.setForearmSize(userForearmSize);
                            userProfile.setLastUpdated(new Timestamp(new Date()));
                            userProfile.setWeightClass(userWeightClass);
                            userProfile.setBicepSize(userBicepSize);
                            userProfile.setUserLatLng(userLocation);
                            userProfile.setHomeTown(homeTown);
                            userProfile.setUserName(currentUserName);
                            userProfile.setUserId(currentUserId);

                            collectionReference.document(user.getUid()).set(userProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    ObjectAnimator animation = ObjectAnimator.ofInt(profileCreationProgressBar,
                                            "progress",
                                            0, 100);
                                    animation.setDuration(5000);
                                    animation.setInterpolator(new DecelerateInterpolator());
                                    animation.addListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animator) { }

                                        @Override
                                        public void onAnimationEnd(Animator animator) {
                                            profileCreationProgressBar.setVisibility(View.INVISIBLE);
                                        }
                                        @Override
                                        public void onAnimationCancel(Animator animator) { }
                                        @Override
                                        public void onAnimationRepeat(Animator animator) { }
                                    });
                                    animation.start();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "onFailure: " + e.getMessage());
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    profileCreationProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }else{
            profileCreationProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if(data != null){
                imageUri = data.getData();
                profilePictureButton.setBackground(null);
                profilePictureButton.setImageURI(imageUri);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}