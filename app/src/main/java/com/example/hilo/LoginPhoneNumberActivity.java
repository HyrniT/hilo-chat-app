package com.example.hilo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    private CountryCodePicker pickerCountryCode;
    private EditText txtLoginPhoneNumber;
    private Button btnSendOtp;
    private ProgressBar pgbLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        pickerCountryCode = findViewById(R.id.picker_country_code);
        txtLoginPhoneNumber = findViewById(R.id.edittext_login_phone_number);
        btnSendOtp = findViewById(R.id.button_send_otp);
        pgbLogin = findViewById(R.id.progress_bar_login);

        pgbLogin.setVisibility(View.GONE); // hide pgbLogin

        pickerCountryCode.registerCarrierNumberEditText(txtLoginPhoneNumber);
        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pickerCountryCode.isValidFullNumber()) {
                    txtLoginPhoneNumber.setError("Phone number not valid");
                    return;
                }
                Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOtpActivity.class);
                intent.putExtra("phone", pickerCountryCode.getFullNumberWithPlus());
                startActivity(intent);
            }
        });
    }
}