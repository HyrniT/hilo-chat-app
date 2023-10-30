package com.example.hilo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class SearchUserActivity extends AppCompatActivity {

    private ImageButton btnBack, btnSearch;
    private EditText txtSearch;
    private RecyclerView recyclerViewSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        txtSearch = findViewById(R.id.txtSearch);
        recyclerViewSearch = findViewById(R.id.recyclerViewSearch);

        txtSearch.requestFocus();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputSearch = txtSearch.getText().toString();
                if (inputSearch.isEmpty() || inputSearch.length() < 3) {
                    txtSearch.setError("Invalid Username");
                    return;
                }
                setUpRecyclerViewSearch(inputSearch);
            }
        });
    }

    private void setUpRecyclerViewSearch(String inputSearch) {

    }
}