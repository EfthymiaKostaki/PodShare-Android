package com.aueb.podshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aueb.podshare.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.aueb.podshare.view.InputLayoutWithEditTextView;
import com.aueb.podshare.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Christina Chaniotaki (christinachaniotaki96@gmail.com) on 08,April,2019
 */
public class RegisterActivity extends AppCompatActivity {

    private String TAG = "TAG_Register";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button registerButton;
    private InputLayoutWithEditTextView email;
    private InputLayoutWithEditTextView password;
    private InputLayoutWithEditTextView username;
    private ProgressDialog progressDialog;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        initUIComponents();
    }

    private void initUIComponents() {
        registerButton = findViewById(R.id.submitButton);

        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        username = findViewById(R.id.usernameInput);
        loginBtn = findViewById(R.id.hasAccountBtn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                registerUser(email.getEditTextValue(), password.getEditTextValue(), username.getEditTextValue());
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });
    }

    private void goToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void registerUser(final String email, final String password, final String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("username", username).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() == null || task.getResult().size() != 0) {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        dismissLoading();
                        Toast.makeText(RegisterActivity.this, "Username already exists in database.", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        //https://firebase.google.com/docs/auth/android/start/
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            writeNewUser(email, username); //Realtime Database
                                            writeUserToFirestore(email, username); // Firestore
                                            createUserStorage(email, username);
                                            Log.d(TAG, "createUserWithEmail:success");
                                            dismissLoading();
                                            updateUI();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            dismissLoading();
                                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createUserStorage(String email, String username) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        String userId = firebaseUser.getUid();
        StorageReference storageRef = storage.getReference();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user);
        StorageReference riversRef = storageRef.child("users/"+userId+"/user.png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = riversRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w(TAG, "Error uploading to storage default user image.");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Log.w(TAG, "Successfully uploaded to storage default user image.");
            }
        });
    }

    // Register observers to listen for when the download is done or if it fails


    private void writeNewUser(String email, String username) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        String userId = firebaseUser.getUid();
        DatabaseReference mRef = database.getReference().child("Users").child(userId);
        mRef.child("username").setValue(username);
        mRef.child("email").setValue(email);
    }

    private void writeUserToFirestore(String email, String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        User user = new User(email, username);
        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void updateUI() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showLoading() {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(this);

        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissLoading() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}