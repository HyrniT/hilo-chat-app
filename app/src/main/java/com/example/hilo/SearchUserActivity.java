package com.example.hilo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.example.hilo.adapter.SearchUserRecyclerAdapter;
import com.example.hilo.model.UserModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {

    private ImageButton btnBack, btnSearch;
    private EditText txtSearch;
    private RecyclerView recyclerViewSearch;
    private SearchUserRecyclerAdapter adapter;
    private String[] bannedWords = { "chatgpt" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        initializeViews();

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
            }
        });
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        txtSearch = findViewById(R.id.txtSearch);
        recyclerViewSearch = findViewById(R.id.recyclerViewSearch);
        txtSearch.requestFocus();
    }

    private void performSearch() {
        String inputSearch = txtSearch.getText().toString();
        if (inputSearch.isEmpty() || inputSearch.length() < 3) {
            txtSearch.setError("Invalid Username");
            return;
        }

        for (String bannedWord : bannedWords) {
            if (inputSearch.contains(bannedWord)) {
                return;
            }
        }
        setUpRecyclerViewSearch(inputSearch);
    }

    private void setUpRecyclerViewSearch(String inputSearch) {
        Query query = FirebaseUtil.getUsersCollection()
                .whereGreaterThanOrEqualTo("username", inputSearch)
                .whereLessThanOrEqualTo("username", inputSearch + '\uf8ff');

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        if (adapter != null) {
            adapter.startListening();
        }
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSearch.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
