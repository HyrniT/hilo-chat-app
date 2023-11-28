package com.example.hilo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.hilo.model.UserModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EditProfileActivity extends AppCompatActivity {
    private UserModel currentUserModel;
    private Uri selectedImageUri;
    private EditText txtUsername, txtPhone;
    private ImageView imvAvatar;
    private Button btnUpdate;
    private ImageButton btnBack;
    private ProgressBar pgbLogin;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        txtUsername = findViewById(R.id.txtUsername);
        txtPhone = findViewById(R.id.txtPhone);
        imvAvatar = findViewById(R.id.imvAvatar);
        btnUpdate = findViewById(R.id.btnUpdate);
        pgbLogin = findViewById(R.id.pgbLogin);
        btnBack = findViewById(R.id.btnBack);

        getUserData();

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                selectedImageUri = data.getData();
                                AndroidUtil.setUriToImageView(EditProfileActivity.this, selectedImageUri, imvAvatar);
                            }
                        }
                    }
                });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = txtUsername.getText().toString();

                if (newUsername.isEmpty() || newUsername.length() < 3) {
                    txtUsername.setError("Username length must be at least 3 characters");
                    return;
                }

                currentUserModel.setUsername(newUsername);

                if (selectedImageUri != null) {
                    FirebaseUtil.getCurrentUserAvatarReference().putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                updateToFirebase();
                            }
                        }
                    });
                } else {
                    updateToFirebase();
                }
            }
        });

        imvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(EditProfileActivity.this)
                        .cropSquare()	    			//Crop image(Optional), Check Customization for more option
                        .compress(512)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(512, 512)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickerLauncher.launch(intent);
                                return null;
                            }
                        });
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getUserData() {
        setInProgress(true);

        FirebaseUtil.getCurrentUserAvatarReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    AndroidUtil.setUriToImageView(EditProfileActivity.this, uri, imvAvatar);
                }
            }
        });

        FirebaseUtil.getCurrentUserReference().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                currentUserModel = task.getResult().toObject(UserModel.class);
                txtUsername.setText(currentUserModel.getUsername());
                txtPhone.setText(currentUserModel.getPhone());
            }
        });
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            pgbLogin.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.GONE);
        } else {
            pgbLogin.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);
        }
    }

    private void updateToFirebase() {
        setInProgress(true);
        FirebaseUtil.getCurrentUserReference().set(currentUserModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    setInProgress(false);
                    AndroidUtil.showToast(EditProfileActivity.this, "Updated successfully");
                } else {
                    AndroidUtil.showToast(EditProfileActivity.this, "Updated failed");
                }
            }
        });
    }
}