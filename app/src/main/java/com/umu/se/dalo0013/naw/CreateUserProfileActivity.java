package com.umu.se.dalo0013.naw;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import util.Config;
import util.LatLng;
import util.UserProfileApi;
/**
 * CreateUserProfileActivity - creates a user profile and stores it in a cloud database
 *
 * @author  David Elfving Long
 * @version 1.0
 * @since   2020-08-27
 */
public class CreateUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateUserProfileActivity";
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public static final int GALLERY_CODE = 1;
    private TextView addProfilePhotoTextView;
    private LatLng userLocation;
    private EditText heightEditText,
        forearmEditText,
        bicepEditText,
        userBioEditText;
    private String homeTown;
    private ImageButton profilePictureButton;
    private Spinner userSexSpinner;
    private Spinner weightClassSpinner;
    private Spinner userHandSpinner;
    private Spinner userClubSpinner;
    private ProgressBar profileCreationProgressBar;
    private SwitchCompat ghostSwitch;

    ArrayAdapter<CharSequence> sexSpinnerAdapter;
    ArrayAdapter<CharSequence> weightClassSpinnerAdapter;
    ArrayAdapter<CharSequence> handSpinnerAdapter;
    ArrayAdapter<CharSequence> clubSpinnerAdapter;


    private Uri imageUri;

    private String currentUserId;
    private String currentUserName;
    private boolean cameraPicked = false;
    private boolean galleryPicked = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to FireStore
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
        Places.initialize(getApplicationContext(), Config.PLACES_API_KEY);

        userBioEditText = findViewById(R.id.user_bio_profile_create);
        //view elements
        TextView userName = findViewById(R.id.create_profile_username);
        addProfilePhotoTextView = findViewById(R.id.add_photo_text_view);
        heightEditText = findViewById(R.id.height_text_view_profile_creation);
        forearmEditText = findViewById(R.id.forearm_size_edit_text);
        bicepEditText = findViewById(R.id.bicep_size_edit_text);
        profileCreationProgressBar = findViewById(R.id.profile_creation_progress_bar);
        Button saveButton = findViewById(R.id.create_acc_save_button);
        profilePictureButton = findViewById(R.id.profile_picture_button);
        ghostSwitch = findViewById(R.id.ghost_switch);

        profilePictureButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        if(UserProfileApi.getInstance() != null){
            currentUserId = UserProfileApi.getInstance().getUserId();
            currentUserName = UserProfileApi.getInstance().getUsername();
            userName.setText(currentUserName);
        }

        authStateListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if(user != null){

            }else{

            }
        };

        userSexSpinner = findViewById(R.id.user_sex_spinner);
        userHandSpinner = findViewById(R.id.user_hand_spinner);
        userClubSpinner = findViewById(R.id.user_club_spinner);
        weightClassSpinner = findViewById(R.id.weight_class_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        sexSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sexes, android.R.layout.simple_spinner_item);
        weightClassSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.weight_classes, android.R.layout.simple_spinner_item);
        handSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.hands, android.R.layout.simple_spinner_item);
        clubSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.clubs, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sexSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightClassSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        handSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        userSexSpinner.setAdapter(sexSpinnerAdapter);
        weightClassSpinner.setAdapter(weightClassSpinnerAdapter);
        userHandSpinner.setAdapter(handSpinnerAdapter);
        userClubSpinner.setAdapter(clubSpinnerAdapter);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                userLocation = new LatLng(Objects.requireNonNull(place.getLatLng()).latitude,
                        place.getLatLng().longitude);
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
                AlertDialog.Builder cameraAlert = new AlertDialog.Builder(this);
                cameraAlert.setTitle("Pick a profile photo");
                cameraAlert.setIcon(R.drawable.camera);
                cameraAlert.setPositiveButton("CAMERA",
                        (dialog, which) -> {
                    dispatchTakePictureIntent();
                    cameraPicked = true;
                });
                cameraAlert.setNegativeButton("GALLERY",
                        (dialog, which) -> {
                    dispatchGalleryIntent();
                    galleryPicked = true;
                    dialog.cancel();
                });
                cameraAlert.show();
                break;
            case R.id.create_acc_save_button:
                try {
                    saveProfile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * Dispatches a camera intent, retrieves image from users phones camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Dispatches a gallery intent, retrieves image from users phones gallery
     */
    private void dispatchGalleryIntent(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_CODE);
    }

    /**
     * saveProfile - saves the user profile in the firebase database
     * This method also compresses the profile picture size before upload
     * @throws IOException exception to be thrown if bitmap retrieval fails
     */
    private void saveProfile() throws IOException {
        final String userBio = userBioEditText.getText().toString().trim();
        final String userSex = userSexSpinner.getSelectedItem().toString().trim();
        final String userHeight = heightEditText.getText().toString().trim();
        final String userForearmSize = forearmEditText.getText().toString().trim();
        final String userBicepSize = bicepEditText.getText().toString().trim();
        final String userWeightClass = weightClassSpinner.getSelectedItem().toString().trim();
        final String userHand = userHandSpinner.getSelectedItem().toString().trim();
        final String userClub = userClubSpinner.getSelectedItem().toString().trim();
        profileCreationProgressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(userBio)
                && !TextUtils.isEmpty(userSex)
                && !TextUtils.isEmpty(userHeight)
                && !TextUtils.isEmpty(userForearmSize)
                && !TextUtils.isEmpty(userBicepSize)
                && !TextUtils.isEmpty(userWeightClass)
                && !TextUtils.isEmpty(userHand)
                && !TextUtils.isEmpty(userClub)
                && homeTown != null
                && imageUri != null){

            final StorageReference filepath = storageReference
                    .child("profile_picture_" + Timestamp.now().getSeconds());
            //compress image give uri
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            final UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                // Continue with the task to get the download URL
                return filepath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String imageUrl = Objects.requireNonNull(task.getResult()).toString();
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
                    userProfile.setHand(userHand);
                    userProfile.setClub(userClub);
                    if(ghostSwitch.isChecked()){
                        userProfile.setGhost(true);
                    }else{
                        userProfile.setGhost(false);
                    }

                    collectionReference.document(
                            user.getUid())
                            .set(userProfile)
                            .addOnSuccessListener(aVoid -> {
                                prolongProgressbarAnimation();
                                startActivity(
                                        new Intent(
                                                CreateUserProfileActivity.this,
                                                HomePageActivity.class));
                                finish();
                            }).addOnFailureListener(e ->
                            Log.d("TAG", "onFailure: " + e.getMessage()));

                }
            })).addOnFailureListener(e -> Toast.makeText(CreateUserProfileActivity.this,
                    "Upload Failed -> " + e, Toast.LENGTH_LONG).show());
        }else{
            warnUserOfEmptyFields(userBio, userSex,
                    userHeight, userForearmSize,
                    userBicepSize, userWeightClass,
                    homeTown, imageUri,
                    userHand, userClub);
            profileCreationProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * warnUserOfEmptyFields - warns the user whenever they have left a required field empty.
     * @param imageUri user profile picture, required to make profile
     * @param userBio user bio, required to make profile
     * @param userSex user sex, required to make profile
     * @param userHeight user height, required to make profile
     * @param userForearmSize user forearm size, required to make profile
     * @param userBicepSize user bicep size, required to make profile
     * @param userWeightClass weight class, required to make profile
     * @param homeTown user hometown, required to make profile
     * @param userHand user hands used, required to make profile
     * @param userClub user club, required to make profile (none is ok)
     */
    private void warnUserOfEmptyFields(String userBio, String userSex, String userHeight,
                                       String userForearmSize, String userBicepSize,
                                       String userWeightClass, String homeTown, Uri imageUri,
                                       String userHand, String userClub){
        String required = "Required field";
        if(TextUtils.isEmpty(userBio)){
            userBioEditText.setError(required);
        }
        if(TextUtils.isEmpty(userSex)){
            TextView sexErrorText = (TextView)userSexSpinner.getSelectedView();
            sexErrorText.setError("");
            sexErrorText.setTextColor(Color.RED);
            sexErrorText.setText(required);
        }
        if(TextUtils.isEmpty(userHeight)){
            heightEditText.setError(required);
        }
        if(TextUtils.isEmpty(userForearmSize)){
            forearmEditText.setError(required);
        }
        if(TextUtils.isEmpty(userBicepSize)){
            bicepEditText.setError(required);
        }
        if(TextUtils.isEmpty(userWeightClass)){
            TextView weightErrorText = (TextView)weightClassSpinner.getSelectedView();
            weightErrorText.setError("");
            weightErrorText.setTextColor(Color.RED);
            weightErrorText.setText(required);
        }
        if(homeTown == null){
            Toast.makeText(this, "Please enter your hometown!", Toast.LENGTH_SHORT).show();
        }
        if(imageUri == null){
            addProfilePhotoTextView.setTextColor(Color.RED);
        }
        if(TextUtils.isEmpty(userHand)){
            TextView handErrorText = (TextView)userHandSpinner.getSelectedView();
            handErrorText.setError("");
            handErrorText.setTextColor(Color.RED);
            handErrorText.setText(required);
        }
        if(TextUtils.isEmpty(userClub)){
            TextView clubErrorText = (TextView)userClubSpinner.getSelectedView();
            clubErrorText.setError("");
            clubErrorText.setTextColor(Color.RED);
            clubErrorText.setText(required);
        }
    }

    /**
     * Starts an animation of the progress bar so that it takes a few seconds before
     * changing activities, this to prevent activities changing before everything has
     * successfully uploaded to the database. (profile photo etc)
     */
    private void prolongProgressbarAnimation(){
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

    /**
     *  onActivityResult - Retrieve photo taken by user if user picks to take profile
     *  photo with camera, otherwise retrieve the photo chosen from gallery
     * @param requestCode code returned specifically for chosen intent
     * @param resultCode 1 if successful
     * @param data the data retrieved from the chosen intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(cameraPicked){
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                assert data != null;
                Bundle extras = data.getExtras();
                assert extras != null;
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                assert imageBitmap != null;
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "Title", null);
                imageUri = Uri.parse(path);
                profilePictureButton.setBackground(null);
                addProfilePhotoTextView.setText(null);
                profilePictureButton.setImageURI(imageUri);
                cameraPicked = false;
            }
        }else if(galleryPicked){
            if(requestCode == GALLERY_CODE && resultCode == RESULT_OK && data != null){
                imageUri = data.getData();
                profilePictureButton.setBackground(null);
                addProfilePhotoTextView.setText(null);
                profilePictureButton.setImageURI(imageUri);
                galleryPicked = false;
            }
        }
    }

    /**
     * Check to see if the user is logged in
     */
    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    /**
     * Remove authentication listener to not drain battery
     */
    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}