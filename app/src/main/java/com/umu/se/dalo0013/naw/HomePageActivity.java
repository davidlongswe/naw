package com.umu.se.dalo0013.naw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.umu.se.dalo0013.naw.ui.YouTubeVideoRecyclerAdapter;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private RecyclerView tutVidsRecyclerView, popVidsRecyclerView;
    private YouTubeVideoRecyclerAdapter youTubeVideoRecyclerAdapter;
    private String[] tutorialVideoURL = {"2Arzt_SSqKc", "LZHp8Y_LFNo", "GI5MkygVTU4", "jGILF0kkY7Y", "K-ogCBZ5jNI"};
    private String[] popularVideoURL = {"Rb2c6JmyFRU", "FNsJuoxB-TE", "IkkL-bAH8H4", "cefwt-dOLrg", "FoFCP2NxLRw", "Kb5eAt2BGkI"};
    private CircleImageView armBetsTV,
            armWrestlingTV,
            devonLarratt,
            globalArmWrestling,
            ryanBowen,
            swedenArmTV,
            voa, wal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        armBetsTV = findViewById(R.id.arm_bets_tv_channel);
        armWrestlingTV = findViewById(R.id.arm_wrestling_tv_channel);
        devonLarratt = findViewById(R.id.devon_larratt_channel);
        globalArmWrestling = findViewById(R.id.global_arm_wrestling_channel);
        ryanBowen = findViewById(R.id.ryan_bowen_channel);
        swedenArmTV = findViewById(R.id.sweden_arm_wrestling_tv_channel);
        voa = findViewById(R.id.voice_of_arm_wrestling_channel);
        wal = findViewById(R.id.wal_channel);

        armBetsTV.setOnClickListener(this);
        armWrestlingTV.setOnClickListener(this);
        devonLarratt.setOnClickListener(this);
        globalArmWrestling.setOnClickListener(this);
        ryanBowen.setOnClickListener(this);
        swedenArmTV.setOnClickListener(this);
        voa.setOnClickListener(this);
        wal.setOnClickListener(this);


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
                    finish();
                    startActivity(getIntent());
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case(R.id.arm_bets_tv_channel):
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_on_click_animation));
                startActivity(YouTubeIntents.createChannelIntent(this, "UCujb5MIcKh7KeSWQ7Uax-Wg"));
                break;
            case(R.id.arm_wrestling_tv_channel):
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_on_click_animation));
                startActivity(YouTubeIntents.createChannelIntent(this, "UC6eMRqZWwSBYS6IlVYD7dwQ"));
                break;
            case(R.id.devon_larratt_channel):
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_on_click_animation));
                startActivity(YouTubeIntents.createChannelIntent(this, "UCBcMvaSRmSh3362bzvOBerw"));
                break;
            case(R.id.global_arm_wrestling_channel):
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_on_click_animation));
                startActivity(YouTubeIntents.createChannelIntent(this, "UCPG1rPE_h_vlXRgaL1clakw"));
                break;
            case(R.id.ryan_bowen_channel):
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_on_click_animation));
                startActivity(YouTubeIntents.createChannelIntent(this, "UCIEjGMfXbN4LFYSnV8qSgAQ"));
                break;
            case(R.id.sweden_arm_wrestling_tv_channel):
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_on_click_animation));
                startActivity(YouTubeIntents.createChannelIntent(this, "UCy3YpN20uTexnE8DClxtp8A"));
                break;
            case(R.id.voice_of_arm_wrestling_channel):
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_on_click_animation));
                startActivity(YouTubeIntents.createChannelIntent(this, "UCAH2krcji9uc3gYSqa33Zyw"));
                break;
            case(R.id.wal_channel):
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_on_click_animation));
                startActivity(YouTubeIntents.createChannelIntent(this, "UC1pdbN6j_txoNhXVWVtouuQ"));
                break;
        }
    }
}