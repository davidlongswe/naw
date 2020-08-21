package com.umu.se.dalo0013.naw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.umu.se.dalo0013.naw.model.UserProfile;

import java.text.MessageFormat;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import util.UserProfileApi;

public class UserProfileActivity extends AppCompatActivity {

    //View elements
    private CircleImageView profilePic;
    private TextView userName,
            profileSex,
            profileHeight,
            profileForearmSize,
            profileBicepSize,
            profileWeightClass,
            profileHomeTown,
            userBio;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Profile");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        profilePic = findViewById(R.id.profile_picture);
        userBio = findViewById(R.id.user_profile_bio);
        userName = findViewById(R.id.profile_username);
        profileSex = findViewById(R.id.profile_sex_text_view);
        profileHeight = findViewById(R.id.profile_height_text_view);
        profileForearmSize = findViewById(R.id.profile_forearm_text_view);
        profileBicepSize = findViewById(R.id.profile_bicep_text_view);
        profileWeightClass = findViewById(R.id.profile_weight_class_text_view);
        profileHomeTown = findViewById(R.id.profile_hometown_text_view);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
    }

   @Override
    protected void onStart() {
        super.onStart();
        collectionReference.whereEqualTo("userId", UserProfileApi.getInstance().getUserId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    UserProfile userProfile = document.toObject(UserProfile.class);
                    Picasso.get().load(userProfile.getProfilePictureUrl()).placeholder(R.drawable.sunset).fit().into(profilePic);
                    userBio.setText(userProfile.getBio());
                    userName.setText(userProfile.getUserName());
                    profileSex.setText(userProfile.getSex());
                    profileHeight.setText(MessageFormat.format("{0} cm", userProfile.getHeight()));
                    profileForearmSize.setText(MessageFormat.format("{0} cm", userProfile.getForearmSize()));
                    profileBicepSize.setText(MessageFormat.format("{0} cm", userProfile.getBicepSize()));
                    profileWeightClass.setText(userProfile.getWeightClass());
                    profileHomeTown.setText(userProfile.getHomeTown());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_home:
                if(currentUser != null && firebaseAuth != null){
                    startActivity(new Intent(UserProfileActivity.this, HomePageActivity.class));
                    finish();
                }
                break;
            case R.id.action_find_partner:
                if(currentUser != null && firebaseAuth != null){
                    startActivity(new Intent(UserProfileActivity.this, FindPartnerActivity.class));
                    finish();
                }
                break;
            case R.id.action_edit_profile:
                if(currentUser != null && firebaseAuth != null){
                    startActivity(new Intent(UserProfileActivity.this, CreateUserProfileActivity.class));
                    finish();
                }
                break;
            case R.id.action_signout:
                if(currentUser != null && firebaseAuth != null) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}