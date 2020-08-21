package com.umu.se.dalo0013.naw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import util.UserProfileApi;

public class LogInActivity extends AppCompatActivity {

    private Button logInButton;
    private Button createAccountButton;
    private AutoCompleteTextView emailAdress;
    private EditText password;
    private ProgressBar progressbarLogIn;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        logInButton = findViewById(R.id.email_sign_in_button);
        createAccountButton = findViewById(R.id.create_account_button_login);
        emailAdress = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressbarLogIn = findViewById(R.id.progressbar_login);
        progressbarLogIn.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, CreateAccountActivity.class));
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmailPasswordUser(emailAdress.getText().toString().trim(),
                        password.getText().toString().trim());
            }
        });
    }

    private void loginEmailPasswordUser(String email, String password){
        progressbarLogIn.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        assert user != null;
                        final String currentUserId = user.getUid();

                        collectionReference
                                .whereEqualTo("userId", currentUserId)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                        }
                                        assert queryDocumentSnapshots != null;
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            progressbarLogIn.setVisibility(View.INVISIBLE);
                                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                UserProfileApi userProfileApi = UserProfileApi.getInstance();
                                                userProfileApi.setUsername(snapshot.getString("username"));
                                                userProfileApi.setUserId(snapshot.getString("userId"));

                                                //Go to ListActivity
                                                startActivity(new Intent(LogInActivity.this,
                                                        HomePageActivity.class));
                                            }
                                        }
                                    }
                                });
                    }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressbarLogIn.setVisibility(View.INVISIBLE);
                }
            });
        }else{
            progressbarLogIn.setVisibility(View.INVISIBLE);
            Toast.makeText(LogInActivity.this,
                    "Please enter email and password", Toast.LENGTH_LONG).show();
        }
    }
}