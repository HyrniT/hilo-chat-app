package com.example.hilo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hilo.adapter.SearchUserGroupRecyclerAdapter;
import com.example.hilo.model.GroupModel;
import com.example.hilo.model.UserModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class CreateGroupActivity extends AppCompatActivity {
    private ImageButton btnBack, btnSearch, btnCreate;
    private EditText txtSearch, txtGroupName;
    private ImageView imvAvatar;
    public static TextView txtCountSelected;
    private RecyclerView recyclerViewSearch;
    private SearchUserGroupRecyclerAdapter adapter;
    public static Set<String> selectedUserIds = new HashSet<>();
    private String currentUserId;
    private GroupModel groupModel;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        txtSearch = findViewById(R.id.txtSearch);
        txtGroupName = findViewById(R.id.txtGroupName);
        imvAvatar = findViewById(R.id.imvAvatar);
        btnCreate = findViewById(R.id.btnCreate);
        txtCountSelected = findViewById(R.id.txtCountSelected);
        recyclerViewSearch = findViewById(R.id.recyclerViewSearch);

        Intent intent = getIntent();
        if (intent != null) {
            currentUserId = intent.getStringExtra("currentUserId");
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
                hideKeyboard();
            }
        });

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (txtSearch.getText().toString().trim().isEmpty()) {
                    renewRecyclerViewSearch();
                }
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performCreateGroup();
            }
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        handleImagePickerResult(result);
                    }
                });

        imvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchImagePicker();
            }
        });

        setUpRecyclerView();
    }

    private void handleImagePickerResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                selectedImageUri = data.getData();
                AndroidUtil.setUriToImageView(CreateGroupActivity.this, selectedImageUri, imvAvatar);
                imvAvatar.setPadding(10,10,10,10);
            }
        } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
            AndroidUtil.showToast(CreateGroupActivity.this, ImagePicker.getError(result.getData()));
        } else {
            AndroidUtil.showToast(CreateGroupActivity.this, "Task Cancelled");
        }
    }

    private void launchImagePicker() {
        ImagePicker.with(CreateGroupActivity.this)
                .cropSquare()
                .compress(512)
                .maxResultSize(512, 512)
                .createIntent(new Function1<Intent, Unit>() {
                    @Override
                    public Unit invoke(Intent intent) {
                        imagePickerLauncher.launch(intent);
                        return null;
                    }
                });
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);
        txtSearch.clearFocus();
    }

    private void performSearch() {
        String inputSearch = txtSearch.getText().toString().trim();
        if (inputSearch.isEmpty() || inputSearch.length() < 3) {
            txtSearch.setError("Invalid Username");
            return;
        }

        setUpRecyclerViewSearch(inputSearch);
    }

    private void performCreateGroup() {
        String groupName = txtGroupName.getText().toString().trim();
        String groupId = String.valueOf(System.currentTimeMillis());
        Integer countUsers = selectedUserIds.size();
        String userCreatedId = FirebaseUtil.getCurrentUserId();

        if (groupName.isEmpty()) {
            txtGroupName.setError("Please input group name");
            return;
        }
        if (countUsers < 2) {
            AndroidUtil.showToast(CreateGroupActivity.this, "Group must be at least 3 members");
            return;
        }

        selectedUserIds.add(userCreatedId);

        FirebaseUtil.getGroupReference(groupId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    groupModel = task.getResult().toObject(GroupModel.class);
                    if (groupModel == null) {
                        groupModel = new GroupModel(
                                groupId,
                                groupName,
                                userCreatedId,
                                new ArrayList<>(selectedUserIds),
                                Timestamp.now()
                        );
                        updateToFirebase(groupId);
                        if (selectedImageUri != null) {
                            FirebaseUtil.getGroupAvatarReference(groupId).putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        AndroidUtil.showToast(CreateGroupActivity.this, "Upload avatar group failed");
                                    }
                                }
                            });
                        }
                        selectedUserIds.clear();
                    }
                }
            }
        });

        Intent intent = new Intent(CreateGroupActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void updateToFirebase(String groupId) {
        FirebaseUtil.getGroupReference(groupId).set(groupModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    AndroidUtil.showToast(CreateGroupActivity.this, "Created group successfully");
                } else {
                    AndroidUtil.showToast(CreateGroupActivity.this, "Created group failed");
                }
            }
        });
    }

    private void setUpRecyclerView() {
        Query query = FirebaseUtil.getUsersCollection().whereNotEqualTo("userId", currentUserId);

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        if (adapter == null) {
            adapter = new SearchUserGroupRecyclerAdapter(options, getApplicationContext());
            recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewSearch.setAdapter(adapter);
            adapter.startListening();
        }
    }

    private void renewRecyclerViewSearch() {
        Query query = FirebaseUtil.getUsersCollection().whereNotEqualTo("userId", currentUserId);

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        adapter.updateOptions(options);
        adapter.notifyDataSetChanged();
    }

    private void setUpRecyclerViewSearch(String inputSearch) {
        Query query = FirebaseUtil.getUsersCollection()
                .whereGreaterThanOrEqualTo("username", inputSearch)
                .whereLessThanOrEqualTo("username", inputSearch + '\uf8ff')
                .whereNotEqualTo("userId", currentUserId);

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        adapter.updateOptions(options);
        adapter.notifyDataSetChanged();
    }
}