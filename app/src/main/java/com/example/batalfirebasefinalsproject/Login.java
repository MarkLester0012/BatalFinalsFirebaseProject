package com.example.batalfirebasefinalsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    //Variables
    private TextView backBTN;
    private TextInputEditText emailEdit, passwordEdit;
    private Button loginButton;
    private String email, passcode;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        refs();

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(Login.this, Landing.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = String.valueOf(emailEdit.getText());
                passcode = String.valueOf(passwordEdit.getText());
                progressDialog = new ProgressDialog(Login.this);
                progressDialog.setMessage("Logging in...");
                progressDialog.show();
                if (!(email.equals("")) && !(passcode.equals(""))) {
                    if (isNetworkConnected()) {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, passcode).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(new Intent(Login.this, Signup.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                }
                                else {
                                    try {
                                        throw task.getException();
                                    } catch (Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Login.this, "Email or Password is Incorrect! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Please complete all fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Variable Assignments
    private void refs(){
        backBTN = findViewById(R.id.backButtonTextView);
        loginButton = findViewById(R.id.loginButton);
        emailEdit = findViewById(R.id.textInputEditText_Email);
        passwordEdit = findViewById(R.id.textInputEditText_Password);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(Login.this, Landing.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }
}