package com.example.hilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hilo.adapter.MessageRecyclerAdapter;
import com.example.hilo.model.ChatroomAiModel;
import com.example.hilo.model.MessageModel;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class ChatGptActivity extends AppCompatActivity {
    private EditText txtChat;
    private TextView txtWelcome;
    private ImageButton btnSend, btnBack;
    private RecyclerView recyclerViewMessage;
    private String currentUserId, chatroomId;
    private ChatroomAiModel chatroomAiModel;
    private MessageRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_gpt);

        txtChat = findViewById(R.id.txtChat);
        txtWelcome = findViewById(R.id.txtWelcome);
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

        FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(query, MessageModel.class).build();

        if (adapter == null) {
            adapter = new MessageRecyclerAdapter(options);
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

            MessageModel messageModel = new MessageModel(message, currentUserId, Timestamp.now());
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
}