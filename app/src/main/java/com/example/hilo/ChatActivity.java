package com.example.hilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hilo.adapter.MessageRecyclerAdapter;
import com.example.hilo.model.ChatroomModel;
import com.example.hilo.model.MessageModel;
import com.example.hilo.model.UserModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private UserModel otherUserModel;
    private ChatroomModel chatroomModel;
    private EditText txtChat;
    private TextView txtUsername;
    private ImageButton btnSend, btnBack;
    private RecyclerView recyclerViewMessage;
    private ImageView imvAvatar;
    private String currentUserId, otherUserId, chatroomId;
    private MessageRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUserModel = AndroidUtil.getUserModel(getIntent());
        currentUserId = FirebaseUtil.getCurrentUserId();
        otherUserId = otherUserModel.getUserId();
        chatroomId = FirebaseUtil.getChatroomId(currentUserId, otherUserId);

        txtChat = findViewById(R.id.txtChat);
        txtUsername = findViewById(R.id.txtUsername);
        btnSend = findViewById(R.id.btnSend);
        recyclerViewMessage = findViewById(R.id.recyclerViewMessage);
        imvAvatar = findViewById(R.id.imvAvatar);
        btnBack = findViewById(R.id.btnBack);

        txtUsername.setText(otherUserModel.getUsername());

        FirebaseUtil.getOtherUserAvatarReference(otherUserModel.getUserId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    AndroidUtil.setUriToImageView(ChatActivity.this, uri, imvAvatar);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = txtChat.getText().toString().trim();
                if (message.isEmpty()) {
                    return;
                }
                sendMessage(message);
            }
        });

        setUpChatroomModel();
        setUpMessageRecyclerView();
    }

    private void setUpChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    chatroomModel = task.getResult().toObject(ChatroomModel.class);
                    if (chatroomModel == null) {
                        chatroomModel = new ChatroomModel(
                                chatroomId,
                                Arrays.asList(currentUserId, otherUserId),
                                Timestamp.now(),
                                ""
                        );
                        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                    }
                }
            }
        });
    }

    private void sendMessage(String message) {
        chatroomModel.setLastSentMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(currentUserId);
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        MessageModel messageModel = new MessageModel(message, currentUserId, Timestamp.now());
        FirebaseUtil.getChatroomMessageCollection(chatroomId).add(messageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    txtChat.setText("");
                    sendNotification(message);
                }
            }
        });
    }

    private void sendNotification(String message) {
        FirebaseUtil.getCurrentUserReference().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    UserModel currentUser = task.getResult().toObject(UserModel.class);
                    try {
                        JSONObject jsonObject = new JSONObject();

                        JSONObject notificationObject = new JSONObject();
                        notificationObject.put("title", currentUser.getUsername());
                        notificationObject.put("body", message);

                        JSONObject dataObject = new JSONObject();
                        dataObject.put("userId", currentUser.getUserId());

                        jsonObject.put("notification", notificationObject);
                        jsonObject.put("data", dataObject);
                        jsonObject.put("to", otherUserModel.getFCMToken());

                        callApi(jsonObject);
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();

        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAAckVxD5A:APA91bGVFVJNC-l9GsI_vrIeE6XmhT7sAsCbckxyBHjNYT2J48aXJrKfIDlnib4KLl5R7_PS5Qzwj5dAuxetdiSfRA5y036jHSa6MbX8W-GFcxJ0i9W23oOp9_1zXAigKkaKoWQoLulG")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    private void setUpMessageRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageCollection(chatroomId)
                .orderBy("sentTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(query, MessageModel.class).build();

        if (adapter == null) {
            adapter = new MessageRecyclerAdapter(options, getApplicationContext());
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setReverseLayout(true);
            recyclerViewMessage.setLayoutManager(manager);
            recyclerViewMessage.setAdapter(adapter);
        } else {
            adapter.updateOptions(options);
        }

        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerViewMessage.smoothScrollToPosition(0);
            }
        });
    }
}