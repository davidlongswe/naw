package com.umu.se.dalo0013.naw;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.UserProfileApi;
/**
 * CreateAccountActivity - creates a user account
 * @author  David Elfving Long
 * @version 1.0
 * @since   2020-08-27
 */
public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Gets users
    private CollectionReference collectionReference = db.collection("Users");

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private EditText userNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        mAuth = FirebaseAuth.getInstance();

        userNameEditText = findViewById(R.id.username_acc);
        emailEditText = findViewById(R.id.email_account);
        passwordEditText = findViewById(R.id.password_account);

        Button createAccountButton = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.progressbar_create_acc);
        progressBar.setVisibility(View.INVISIBLE);

        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();
            if(currentUser != null){
                //user is already logged in...
            }else{
                //no user yet...
            }
        };

        createAccountButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String username = userNameEditText.getText().toString().trim();

            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)){
                createUserEmailAccount(username, email, password);
            }

        });

    }

    /**
     * createUserEmailAccount - Creates a new account with email and password
     * @param username
     * @param email
     * @param password
     */
    private void createUserEmailAccount(final String username, String email, String password){
        if(!TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(username)){
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            currentUser = mAuth.getCurrentUser();
                            assert currentUser != null;
                            final String currentUserId = currentUser.getUid();

                            //Create a user map so we can create a user in the user collection
                            Map<String, String> userObj = new HashMap<>();
                            userObj.put("userId", currentUserId);
                            userObj.put("username", username);

                            //save to our FireStore database
                            collectionReference.add(userObj).addOnSuccessListener(documentReference -> documentReference.get().addOnCompleteListener(task1 -> {
                                if (Objects.requireNonNull(task1.getResult()).exists()){
                                    progressBar.setVisibility(View.INVISIBLE);
                                    String name = task1.getResult().getString("username");
                                    UserProfileApi userProfileApi = UserProfileApi.getInstance();
                                    userProfileApi.setUserId(currentUserId);
                                    userProfileApi.setUsername(name);
                                    Intent intent = new Intent(CreateAccountActivity.this, CreateUserProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            })).addOnFailureListener(e -> Log.d("CreateAccountActivity",
                                    "onFailure: " + e.getMessage() ));

                        } else {
                            try
                            {
                                throw Objects.requireNonNull(task.getException());
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthWeakPasswordException weakPassword)
                            {
                                passwordEditText.setError("Weak password!");
                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                emailEditText.setError("Invalid email address");
                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                emailEditText.setError("That email exists, try logging in!");
                            }
                            catch (Exception e)
                            {
                                Log.d("CreateAccountActivity",
                                        "onComplete: " + e.getMessage());
                            }
                        }
                    });
        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuth != null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }
}