package com.example.hilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageButton btnSearch;
    private Fragment chatFragment, profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        btnSearch = findViewById(R.id.btnSearch);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        bottomNavigationView.setSelectedItemId(R.id.menu_chat);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_chat) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, chatFragment).commit();
                }
                if (item.getItemId() == R.id.menu_profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, profileFragment).commit();
                }
                return true;
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchUserActivity.class);
                startActivity(intent);
            }
        });
    }
}