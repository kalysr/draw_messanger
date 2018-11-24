package com.example.apple.geektech;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.apple.geektech.Library.App;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    ImageButton login_forward;
    EditText phone_number;

    public String mVerificationId;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public PhoneAuthProvider.ForceResendingToken mResendToken;

    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_forward = findViewById(R.id.login_forward);
        phone_number = findViewById(R.id.phone_number_field);
        Button main_activity_btn = findViewById(R.id.main_activity_btn);

        main_activity_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });


        login_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(phone_number.getText())) {

                    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                            App.telegram("Verification completed");
                            Toast.makeText(LoginActivity.this, "Verification Done" + phoneAuthCredential, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                            Toast.makeText(LoginActivity.this, "Verification Fail: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof FirebaseTooManyRequestsException) {
                                Toast.makeText(LoginActivity.this, "Too many Request", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            mVerificationId = s;
                            mResendToken = forceResendingToken;
                            Toast.makeText(LoginActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
                            App.telegram("Code Sent");
                        }
                    };

                    verifyPhone();

//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else
                    phone_number.setError("Phone is required");
            }
        });
    }

    public void verifyPhone() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone_number.getText().toString(), 60, TimeUnit.SECONDS, this, mCallbacks);
    }


}
