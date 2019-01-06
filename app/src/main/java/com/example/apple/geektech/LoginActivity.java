package com.example.apple.geektech;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

public class LoginActivity extends AppCompatActivity {

    ImageButton login_forward, getVerCode;
    EditText phone_number, verificationCodeInput;
    public FirebaseAuth mAuth;
    public String mVerificationId;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public PhoneAuthProvider.ForceResendingToken mResendToken;

    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);



        userIsLoggedIn();

        getVerCode = findViewById(R.id.login_forward2);
        login_forward = findViewById(R.id.login_forward);
        phone_number = findViewById(R.id.phone_number_field);
        verificationCodeInput = findViewById(R.id.verifaciton_code);


        login_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!TextUtils.isEmpty(phone_number.getText())) {


                    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                            // out.println(phoneAuthCredential);
                            // App.telegram("Verification completed");
                            signInWithPhoneAuthCredential(phoneAuthCredential);

//                            Toast.makeText(LoginActivity.this, "Verification Done" + phoneAuthCredential, Toast.LENGTH_LONG).show();
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


                            login_forward.setVisibility(View.INVISIBLE);
                            phone_number.setVisibility(View.INVISIBLE);

                            getVerCode.setVisibility(View.VISIBLE);
                            verificationCodeInput.setVisibility(View.VISIBLE);

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


        getVerCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence code = verificationCodeInput.getText();

                if (!TextUtils.isEmpty(code)) {

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code.toString());
                    signInWithPhoneAuthCredential(credential);

                } else {
                    verificationCodeInput.setError("Code is required");
                }
            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user != null){
                                final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                                mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()){
                                            Map<String,Object> userMap = new HashMap<>();
                                            userMap.put("phone",user.getPhoneNumber());
                                            userMap.put("name",user.getPhoneNumber());
                                            mUserDB.updateChildren(userMap);
                                        }
                                        userIsLoggedIn();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                verificationCodeInput.setError("The verification code entered was invalid");
                            }
                        }
                    }
                });


    }

    public void verifyPhone() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_number.getText().toString(),
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void userIsLoggedIn() {



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }


}
