package com.example.hilo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.hilo.adapter.SearchUserGroupRecyclerAdapter;
import com.example.hilo.model.UserModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateGroupActivity extends AppCompatActivity {
    private ImageButton btnBack, btnSearch;
    private EditText txtSearch;
    private RecyclerView recyclerViewSearch;
    private SearchUserGroupRecyclerAdapter adapter;
    public static Set<String> selectedUserIds = new HashSet<>();
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        txtSearch = findViewById(R.id.txtSearch);
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
                AndroidUtil.showToast(CreateGroupActivity.this, selectedUserIds.toString());
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

        setUpRecyclerView();
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

        selectedUserIds.clear();
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

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}