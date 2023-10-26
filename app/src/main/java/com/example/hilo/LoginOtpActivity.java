package com.example.hilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hilo.utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginOtpActivity extends AppCompatActivity {
    private String phoneNumber;
    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private Button btnVerify;
    private ProgressBar pgbLogin;
    private TextView txtResendTimer, txtResend;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Long timeoutSeconds = 60L;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        setUpOtpInput();

        btnVerify = findViewById(R.id.button_login_verify);
        pgbLogin = findViewById(R.id.progress_bar_login);
        txtResend = findViewById(R.id.text_view_resend);
        txtResendTimer = findViewById(R.id.text_view_resend_timer);
        txtResend = findViewById(R.id.text_view_resend);

        phoneNumber = getIntent().getExtras().getString("phone");
        AndroidUtil.showToast(getApplicationContext(), phoneNumber);

        sendOtp(phoneNumber, false);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code1 = inputCode1.getText().toString().trim();
                String code2 = inputCode2.getText().toString().trim();
                String code3 = inputCode3.getText().toString().trim();
                String code4 = inputCode4.getText().toString().trim();
                String code5 = inputCode5.getText().toString().trim();
                String code6 = inputCode6.getText().toString().trim();
                if (code1.isEmpty() || code2.isEmpty() || code3.isEmpty()
                 || code5.isEmpty() || code5.isEmpty() || code6.isEmpty()) {
                    AndroidUtil.showToast(LoginOtpActivity.this, "Please enter full code");
                    return;
                }
                String code = code1 + code2 + code3 + code4 + code5 + code6;
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
        });

        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOtp(phoneNumber, true);
            }
        });
    }

    private void setUpOtpInput() {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            pgbLogin.setVisibility(View.VISIBLE);
            btnVerify.setVisibility(View.GONE);
        } else {
            pgbLogin.setVisibility(View.GONE);
            btnVerify.setVisibility(View.VISIBLE);
        }
    }

    private void startResendTimer() {
        txtResendTimer.setVisibility(View.VISIBLE);
        txtResend.setVisibility(View.GONE);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                txtResendTimer.setText("Resend OTP in " + timeoutSeconds + " seconds");
                if (timeoutSeconds <= 0) {
                    timer.cancel();
                    timeoutSeconds = 60L;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtResendTimer.setVisibility(View.GONE);
                            txtResend.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        }, 0, 1000);
    }

    // https://firebase.google.com/docs/auth/android/phone-auth
    private void sendOtp(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(true);

        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder()
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                // This callback will be invoked in two situations:
                                // 1 - Instant verification. In some cases the phone number can be instantly
                                //     verified without needing to send or enter a verification code.
                                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                                //     detect the incoming verification SMS and perform verification without
                                //     user action.

                                // Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);

                                signInWithPhoneAuthCredential(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                // This callback is invoked in an invalid request for verification is made,
                                // for instance if the the phone number format is not valid.
                                //  Log.w(TAG, "onVerificationFailed", e);

                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    // Invalid request
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    // The SMS quota for the project has been exceeded
                                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                                    // reCAPTCHA verification attempted with null Activity
                                }

                                AndroidUtil.showToast(getApplicationContext(), "OTP verification failed");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                // The SMS verification code has been sent to the provided phone number, we
                                // now need to ask the user to enter the code and then construct a credential
                                // by combining the code with a verification ID.
                                // Log.d(TAG, "onCodeSent:" + verificationId);
                                super.onCodeSent(verificationId, token);

                                // Save verification ID and resending token so we can use them later
                                mVerificationId = verificationId;
                                mResendToken = token;

                                AndroidUtil.showToast(getApplicationContext(), "OTP sent successfully");
                                setInProgress(false);
                            }
                        });          // OnVerificationStateChangedCallbacks

        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(mResendToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        setInProgress(true);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");

                            // FirebaseUser user = task.getResult().getUser();

                            Intent intent = new Intent(LoginOtpActivity.this, LoginUsernameActivity.class);
                            intent.putExtra("phone", phoneNumber);
                            startActivity(intent);
                        } else {
                            // Sign in failed, display a message and update the UI
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                AndroidUtil.showToast(LoginOtpActivity.this, "The verification code entered was invalid");
                                setInProgress(false);
                            }
                        }
                    }
                });
    }
}