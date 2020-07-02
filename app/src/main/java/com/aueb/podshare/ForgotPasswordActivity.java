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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.aueb.podshare.view.InputLayoutWithEditTextView;

/**
 * Created by Efthymia Kostaki (kostakiefthymia@gmail.com) on 22/06/2020.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private String TAG = "TAG_forgot_password";
    private Button loginButton;
    private Button signUpBtn;
    private Button forgotPass;
    private Button sendButton;
    private InputLayoutWithEditTextView email;
    private InputLayoutWithEditTextView password;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        mAuth = FirebaseAuth.getInstance();
        initUIComponents();
    }

    private void initUIComponents() {
        loginButton = findViewById(R.id.hasAccountBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });
        sendButton = findViewById(R.id.sendButton);
        email = findViewById(R.id.emailInput);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getEditTextValue())) {
                    email.setLayoutError("Email cannot be null or empty");
                } else {
                    showLoading();
                    sendEmail(email.getEditTextValue());
                }
            }
        });
    }

    private void sendEmail(String editTextValue) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = editTextValue;

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            goToLoginActivity();
                        }
                    }
                });
    }

    private void showLoading() {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(this);

        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void goToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}