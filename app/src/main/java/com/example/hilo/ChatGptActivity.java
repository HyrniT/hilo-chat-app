package com.example.hilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.hilo.adapter.MessageAiRecyclerAdapter;
import com.example.hilo.model.ChatroomAiModel;
import com.example.hilo.model.MessageAiModel;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGptActivity extends AppCompatActivity {
    private EditText txtChat;
    private ImageButton btnSend, btnBack;
    private RecyclerView recyclerViewMessage;
    private String currentUserId, chatroomId;
    private ChatroomAiModel chatroomAiModel;
    private MessageAiRecyclerAdapter adapter;
    MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_gpt);

        txtChat = findViewById(R.id.txtChat);
        btnSend = findViewById(R.id.btnSend);
        recyclerViewMessage = findViewById(R.id.recyclerViewMessage);
        btnBack = findViewById(R.id.btnBack);

        currentUserId = FirebaseUtil.getCurrentUserId();
        chatroomId = currentUserId + "-chatgpt"; // hard assign data

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
                callApi(message);
            }
        });

        setUpChatroomModel();
        setUpMessageRecyclerView();
    }

    private void setUpChatroomModel() {
        FirebaseUtil.getChatroomAiReference(chatroomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    chatroomAiModel = task.getResult().toObject(ChatroomAiModel.class);
                }
            }
        });
    }

    private void setUpMessageRecyclerView() {
        Query query = FirebaseUtil.getChatroomAiMessageCollection(chatroomId)
                .orderBy("sentTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<MessageAiModel> options = new FirestoreRecyclerOptions.Builder<MessageAiModel>()
                .setQuery(query, MessageAiModel.class).build();

        if (adapter == null) {
            adapter = new MessageAiRecyclerAdapter(options, getApplicationContext());
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

    private void sendMessage(String message) {
        if (chatroomAiModel != null) {
            chatroomAiModel.setLastSentMessageTimestamp(Timestamp.now());
            chatroomAiModel.setLastMessageSenderId(currentUserId);
            chatroomAiModel.setLastMessage(message);
            FirebaseUtil.getChatroomAiReference(chatroomId).set(chatroomAiModel);

            MessageAiModel messageModel = new MessageAiModel(message, currentUserId, Timestamp.now());
            FirebaseUtil.getChatroomAiMessageCollection(chatroomId).add(messageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        txtChat.setText("");
                    }
                }
            });
        }
    }

    private void receiveMessage(String message) {
        if (chatroomAiModel != null) {
            chatroomAiModel.setLastSentMessageTimestamp(Timestamp.now());
            chatroomAiModel.setLastMessageSenderId("chatgpt");
            chatroomAiModel.setLastMessage(message);
            FirebaseUtil.getChatroomAiReference(chatroomId).set(chatroomAiModel);

            MessageAiModel messageModel = new MessageAiModel(message, "chatgpt", Timestamp.now());
            FirebaseUtil.getChatroomAiMessageCollection(chatroomId).add(messageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        txtChat.setText("");
                    }
                }
            });
        }
    }

    private void callApi(String question) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-3.5-turbo-instruct");
            jsonObject.put("prompt", question);
            jsonObject.put("max_tokens", 500);
            jsonObject.put("temperature", 0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        String url = "https://api.openai.com/v1/completions";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer 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")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                receiveMessage("Failed to load response" + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        receiveMessage(result.trim());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    receiveMessage("Failed to load response: " + response.body().toString());
                }
            }
        });
    }
}