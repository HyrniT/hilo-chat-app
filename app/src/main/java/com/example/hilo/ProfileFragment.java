package com.example.hilo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hilo.model.UserModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileFragment extends Fragment {

    private ImageView imvAvatar;
    private EditText txtUsername, txtPhone;
    private TextView txtLogout;
    private Button btnUpdate;
    private ProgressBar pgbLogin;
    private UserModel currentUserModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imvAvatar = view.findViewById(R.id.imvAvatar);
        txtUsername = view.findViewById(R.id.txtUsername);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtLogout = view.findViewById(R.id.txtLogout);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        pgbLogin = view.findViewById(R.id.pgbLogin);

        getUserData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = txtUsername.getText().toString();

                if (newUsername.isEmpty() || newUsername.length() < 3) {
                    txtUsername.setError("Username length must be at least 3 characters");
                    return;
                }

                currentUserModel.setUsername(newUsername);
                updateToFirebase();
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.logOut();
                Intent intent = new Intent(getContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }

    private void getUserData() {
        setInProgress(true);
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
                    AndroidUtil.showToast(getContext(), "Updated successfully");
                } else {
                    AndroidUtil.showToast(getContext(), "Updated failed");
                }
            }
        });
    }
}