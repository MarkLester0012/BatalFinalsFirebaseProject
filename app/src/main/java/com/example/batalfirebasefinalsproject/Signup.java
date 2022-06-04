package com.example.batalfirebasefinalsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


public class Signup extends AppCompatActivity {

    //Variables
    private TextView alreadyHave, backBTN;
    private Button signupBTN;
    private TextInputEditText emailEdit, passwordEdit, confirmpasswordEdit;
    private String userID, email, passcode, confirmPasscode;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);
        refs();

        alreadyHave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(Signup.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(Signup.this, Landing.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

        signupBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = String.valueOf(emailEdit.getText());
                passcode = String.valueOf(passwordEdit.getText());
                confirmPasscode = String.valueOf(confirmpasswordEdit.getText());
                progressDialog = new ProgressDialog(Signup.this);
                progressDialog.setMessage("Signing Up...");
                progressDialog.show();
                if(!email.equals("") && !passcode.equals("") && !confirmPasscode.equals("")) {
                    if (passcode.length() < 6) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Your Password must contain 6 Characters", Toast.LENGTH_SHORT).show();
                    }
                    else if (!passcode.equals(confirmPasscode)) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Please confirm your password!", Toast.LENGTH_SHORT).show();
                    } else {
                        if(isNetworkConnected()) {
                            registerUserAccount(email, passcode);
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                        }
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
    public void refs() {
        alreadyHave = findViewById(R.id.id_txtview_already);
        backBTN = findViewById(R.id.backButtonTextView);
        signupBTN = findViewById(R.id.signUpButton);

        //Edit Text
        emailEdit = findViewById(R.id.id_edittxt_email);
        passwordEdit = findViewById(R.id.id_edittxt_password);
        confirmpasswordEdit = findViewById(R.id.id_edittxt_confirm);
    }

    public void registerUserAccount(String email, String passcode) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,passcode).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(Signup.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(Signup.this, Landing.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
                } else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthUserCollisionException existEmail) {
                        progressDialog.dismiss();
                        Toast.makeText(Signup.this, "Email Exist!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.d("Signup_Page", e.getMessage());
                    }
                }
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(Signup.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }
}