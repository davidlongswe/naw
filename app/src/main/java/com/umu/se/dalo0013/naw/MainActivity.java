package com.umu.se.dalo0013.naw;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import util.UserProfileApi;
/**
 * MainActivity - A welcome screen for new users, which is bypassed by logging in on a device
 * @author  David Elfving Long
 * @version 1.0
 * @since   2020-08-27
 */
public class MainActivity extends AppCompatActivity {

    private VideoView armWrestlingVideoView;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();
            if(currentUser != null){
                currentUser = firebaseAuth.getCurrentUser();
                String currentUserId = currentUser.getUid();
                /*
                 * Go to HomePageActivity directly if user has previously logged in on device.
                 */
                collectionReference.whereEqualTo("userId", currentUserId).addSnapshotListener((value, error) -> {
                    if(error != null){
                        return;
                    }
                    assert value != null;
                    if(!value.isEmpty()){
                        for(QueryDocumentSnapshot snapshot : value){
                            UserProfileApi userProfileApi = UserProfileApi.getInstance();
                            userProfileApi.setUserId(snapshot.getString("userId"));
                            userProfileApi.setUsername(snapshot.getString("username"));
                            startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                            finish();
                        }
                    }
                });
            }
        };

        armWrestlingVideoView = findViewById(R.id.armWrestling_video_view);
        Uri bicepVideoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.armwrestle_dev);
        armWrestlingVideoView.setVideoURI(bicepVideoUri);
        armWrestlingVideoView.start();
        armWrestlingVideoView.setOnCompletionListener (mediaPlayer -> armWrestlingVideoView.start());

        Button joinButton = findViewById(R.id.join_button);
        //Go to LogInActivity if user hasn't already logged in on device
        joinButton.setOnClickListener(v -> {
            armWrestlingVideoView.stopPlayback();
            startActivity(new Intent(MainActivity.this, LogInActivity.class));
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}