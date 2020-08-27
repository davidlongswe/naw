package com.umu.se.dalo0013.naw;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import util.UserProfileApi;
/**
 *
 *
 *
 * @author  David Elfving Long
 * @version 1.0
 * @since   2020-08-27
 */
public class LogInActivity extends AppCompatActivity {

    private AutoCompleteTextView emailAddress;
    private EditText passwordEditText;
    private ProgressBar progressbarLogIn;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        Button logInButton = findViewById(R.id.email_sign_in_button);
        Button createAccountButton = findViewById(R.id.create_account_button_login);
        emailAddress = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        progressbarLogIn = findViewById(R.id.progressbar_login);
        progressbarLogIn.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();

        createAccountButton.setOnClickListener(v ->
                startActivity(new Intent(LogInActivity.this,
                        CreateAccountActivity.class)));

        logInButton.setOnClickListener(v ->
                loginEmailPasswordUser(emailAddress.getText().toString().trim(),
                passwordEditText.getText().toString().trim()));
    }

    /**
     *
     * @param email
     * @param password
     */
    private void loginEmailPasswordUser(final String email, final String password) {
        progressbarLogIn.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            final String currentUserId = user.getUid();

                            collectionReference
                                    .whereEqualTo("userId", currentUserId)
                                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                                        if (e != null) {
                                            Log.d("LogInActivity", "onEvent: "
                                                    + e.getLocalizedMessage());
                                        }
                                        assert queryDocumentSnapshots != null;
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            progressbarLogIn.setVisibility(View.INVISIBLE);
                                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                UserProfileApi userProfileApi = UserProfileApi.getInstance();
                                                userProfileApi.setUsername(snapshot.getString("username"));
                                                userProfileApi.setUserId(snapshot.getString("userId"));
                                            }
                                            //Go to ListActivity
                                            startActivity(new Intent(LogInActivity.this,
                                                    HomePageActivity.class));
                                            finish();
                                        }
                                    });
                        } else {
                            progressbarLogIn.setVisibility(View.INVISIBLE);
                            try
                            {
                                throw Objects.requireNonNull(task.getException());
                            }
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                passwordEditText.setError("Invalid password!");
                            }
                            catch (FirebaseAuthInvalidUserException existEmail)
                            {
                                emailAddress.setError("Invalid email address!");
                            }
                            catch (Exception e)
                            {
                                Log.d("CreateAccountActivity",
                                        "onComplete: " + e.getMessage());
                            }
                        }
                    }).addOnFailureListener(e -> {
                        progressbarLogIn.setVisibility(View.INVISIBLE);
                        Toast.makeText(LogInActivity.this,
                                e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }else{
            progressbarLogIn.setVisibility(View.INVISIBLE);
            Toast.makeText(LogInActivity.this,
                    "Please enter email and password", Toast.LENGTH_LONG).show();
        }
    }
}