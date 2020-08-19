package com.umu.se.dalo0013.naw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.umu.se.dalo0013.naw.ui.YouTubeVideoRecyclerAdapter;

import java.util.Objects;

public class HomePageActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private RecyclerView tutVidsRecyclerView, popVidsRecyclerView;
    private YouTubeVideoRecyclerAdapter youTubeVideoRecyclerAdapter;
    private String[] tutorialVideoURL = {"LZHp8Y_LFNo", "GI5MkygVTU4", "jGILF0kkY7Y","LZHp8Y_LFNo", "GI5MkygVTU4", "jGILF0kkY7Y"};
    private String[] popularVideoURL = {"FNsJuoxB-TE", "IkkL-bAH8H4", "cefwt-dOLrg", "FoFCP2NxLRw", "Kb5eAt2BGkI"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        tutVidsRecyclerView = findViewById(R.id.tutorial_videos_recycler_view);
        tutVidsRecyclerView.setHasFixedSize(true);
        tutVidsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        youTubeVideoRecyclerAdapter = new YouTubeVideoRecyclerAdapter(this, tutorialVideoURL);
        tutVidsRecyclerView.setAdapter(youTubeVideoRecyclerAdapter);

        popVidsRecyclerView = findViewById(R.id.popular_videos_recycler_view);
        popVidsRecyclerView.setHasFixedSize(true);
        popVidsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        youTubeVideoRecyclerAdapter = new YouTubeVideoRecyclerAdapter(this, popularVideoURL);
        popVidsRecyclerView.setAdapter(youTubeVideoRecyclerAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_home:
                if(currentUser != null && firebaseAuth != null){
                    startActivity(new Intent(HomePageActivity.this, HomePageActivity.class));
                    finish();
                }
                break;
            case R.id.action_find_partner:
                if(currentUser != null && firebaseAuth != null){
                    startActivity(new Intent(HomePageActivity.this, FindPartnerActivity.class));
                    finish();
                }
                break;
            case R.id.action_profile:
                if(currentUser != null && firebaseAuth != null){
                    startActivity(new Intent(HomePageActivity.this, UserProfileActivity.class));
                    finish();
                }
                break;
            case R.id.action_signout:
                if(currentUser != null && firebaseAuth != null) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(HomePageActivity.this, MainActivity.class));
                    finish();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}