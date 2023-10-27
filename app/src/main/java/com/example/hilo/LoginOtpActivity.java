package com.example.hilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private EditText[] inputCode = new EditText[6];
    private Button btnVerify;
    private ProgressBar pgbLogin;
    private TextView txtResendTimer, txtResend;
    private Long timeoutSeconds = 60L;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        inputCode[0] = findViewById(R.id.inputCode1);
        inputCode[1] = findViewById(R.id.inputCode2);
        inputCode[2] = findViewById(R.id.inputCode3);
        inputCode[3] = findViewById(R.id.inputCode4);
        inputCode[4] = findViewById(R.id.inputCode5);
        inputCode[5] = findViewById(R.id.inputCode6);

        setUpOtpInput();

        btnVerify = findViewById(R.id.button_login_verify);
        pgbLogin = findViewById(R.id.progress_bar_login);
        txtResend = findViewById(R.id.text_view_resend);
        txtResendTimer = findViewById(R.id.text_view_resend_timer);
        txtResend = findViewById(R.id.text_view_resend);

        phoneNumber = getIntent().getStringExtra("phone");
        AndroidUtil.showToast(getApplicationContext(), phoneNumber);

        sendOtp(phoneNumber, false);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder code = new StringBuilder();
                for (EditText editText : inputCode) {
                    code.append(editText.getText().toString().trim());
                }

                if (code.toString().length() != 6) {
                    AndroidUtil.showToast(LoginOtpActivity.this, "Please enter full code");
                } else {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId, code.toString());
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
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
        for (int i = 0; i < inputCode.length; i++) {
            final int index = i;
            inputCode[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() == 1 && index < 5) {
                        inputCode[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
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
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    AndroidUtil.showToast(getApplicationContext(), "Invalid request");
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    AndroidUtil.showToast(getApplicationContext(), "SMS quota exceeded");
                                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                                    AndroidUtil.showToast(getApplicationContext(), "reCAPTCHA verification failed");
                                }

                                AndroidUtil.showToast(getApplicationContext(), "OTP verification failed");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                mVerificationId = verificationId;
                                mResendToken = token;

                                AndroidUtil.showToast(getApplicationContext(), "OTP sent successfully");
                                setInProgress(false);
                            }
                        });

        if (isResend) {
            builder.setForceResendingToken(mResendToken);
        }

        PhoneAuthProvider.verifyPhoneNumber(builder.build());
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        setInProgress(true);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginOtpActivity.this, LoginUsernameActivity.class);
                            intent.putExtra("phone", phoneNumber);
                            startActivity(intent);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                AndroidUtil.showToast(LoginOtpActivity.this, "The verification code entered was invalid");
                            }
                            setInProgress(false);
                        }
                    }
                });
    }
}
