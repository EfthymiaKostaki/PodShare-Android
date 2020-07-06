package com.aueb.podshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aueb.podshare.Sessions.SessionManagement;
import com.aueb.podshare.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.aueb.podshare.view.InputLayoutWithEditTextView;

/**
 * Created by Christina Chaniotaki (christinachaniotaki96@gmail.com) on 08,April,2019
 */
public class LoginActivity extends AppCompatActivity {
    private String TAG = "TAG_Login";
    private Button loginButton;
    private Button signUpBtn;
    private Button forgotPass;
    private InputLayoutWithEditTextView email;
    private InputLayoutWithEditTextView password;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        initUIComponents();
    }

    private void initUIComponents() {
        loginButton = findViewById(R.id.logInButton);
        signUpBtn = findViewById(R.id.noAccountBtn);
        forgotPass = findViewById(R.id.forgotPass);
        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getEditTextValue()))
                    email.setLayoutError("Email cannot be null or empty");
                else if (TextUtils.isEmpty(password.getEditTextValue()))
                    password.setLayoutError("Password cannot be null or empty");
                else {
                    showLoading();
                    loginUser(email.getEditTextValue(), password.getEditTextValue());
                }
            }
        });
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToForgotPasswordActivity();
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity();
            }
        });
    }

    private void goToForgotPasswordActivity() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    private void loginUser(final String email, String password) {
        //https://firebase.google.com/docs/auth/android/start/
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            dismissLoading();
                            FirebaseUser firebaseuser = mAuth.getCurrentUser();
                            User user = new User(email, firebaseuser.getDisplayName());
                            SessionManagement sm = new SessionManagement(LoginActivity.this);
                            sm.saveSession(email);
                            goToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            dismissLoading();
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void goToRegisterActivity() {
        startActivity(new Intent(this, RegisterActivity.class));
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
