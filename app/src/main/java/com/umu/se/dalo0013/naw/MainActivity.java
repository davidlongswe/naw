package com.umu.se.dalo0013.naw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import util.UserProfileApi;

public class MainActivity extends AppCompatActivity {

    private VideoView bicepVideoView;
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
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null){
                    currentUser = firebaseAuth.getCurrentUser();
                    String currentUserId = currentUser.getUid();
                    collectionReference.whereEqualTo("userId", currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
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
                        }
                    });
                }
            }
        };

        //Source: https://www.pexels.com/video/a-man-building-his-muscle-by-exercising-with-dumbbells-4115126/
        bicepVideoView = findViewById(R.id.bicep_video_view);
        Uri bicepVideoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bicep_curl);
        bicepVideoView.setVideoURI(bicepVideoUri);
        bicepVideoView.start();

        Button joinButton = findViewById(R.id.join_button);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bicepVideoView.stopPlayback();
                //startActivity(new Intent(MainActivity.this, LogInActivity.class));
                startActivity(new Intent(MainActivity.this, LogInActivity.class));
                finish();
            }
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