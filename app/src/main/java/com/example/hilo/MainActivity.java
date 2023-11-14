package com.example.hilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.example.hilo.model.UserModel;
import com.example.hilo.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageButton btnSearch;
    private ChatFragment chatFragment;
    private ProfileFragment profileFragment;
    private ChatAiFragment chatAiFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        btnSearch = findViewById(R.id.btnSearch);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();
        chatAiFragment = new ChatAiFragment();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_chat) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_frame, chatFragment)
                            .commit();
                }
                if (item.getItemId() == R.id.menu_profile) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_frame, profileFragment)
                            .commit();
                }
                if (item.getItemId() == R.id.menu_chatgpt) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_frame, chatAiFragment)
                            .commit();
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchUserActivity.class);
                startActivity(intent);
            }
        });

        getFCMToken();
        startZegoCloudService();
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    FirebaseUtil.getCurrentUserReference().update("fcmtoken", token);
                }
            }
        });
    }

    private void startZegoCloudService() {
        FirebaseUtil.getCurrentUserReference().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                UserModel currentUserModel = task.getResult().toObject(UserModel.class);
//                Application application = getApplication();
                long appID = BuildConfig.ZEGO_APP_ID;
                String appSign = BuildConfig.ZEGO_APP_SIGN;
                String userID = currentUserModel.getUserId();
                String userName = currentUserModel.getUsername();

                ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
                callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;
                ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
                notificationConfig.sound = "zego_uikit_sound_call";
                notificationConfig.channelID = "CallInvitation";
                notificationConfig.channelName = "CallInvitation";
                ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }
}