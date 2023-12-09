package com.example.hilo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hilo.adapter.MessageGroupRecyclerAdapter;
import com.example.hilo.model.GroupModel;
import com.example.hilo.model.MessageModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.EncryptionUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGroupActivity extends AppCompatActivity {

    private GroupModel groupModel;
    private EditText txtChat;
    private TextView txtUsername;
    private ImageButton btnSend, btnBack, btnChooseImage, btnClosePreview;
    private RecyclerView recyclerViewMessage;
    private ImageView imvAvatar, imvPreviewImage;
    private String groupId, currentUserId, currentUsername;
    private List<String> otherUserIds;
    private MessageGroupRecyclerAdapter adapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private ZegoSendCallInvitationButton btnPhoneCall, btnVideoCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);

        txtChat = findViewById(R.id.txtChat);
        txtUsername = findViewById(R.id.txtUsername);
        btnSend = findViewById(R.id.btnSend);
        recyclerViewMessage = findViewById(R.id.recyclerViewMessage);
        imvAvatar = findViewById(R.id.imvAvatar);
        btnBack = findViewById(R.id.btnBack);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnVideoCall = findViewById(R.id.btnVideoCall);
        btnPhoneCall = findViewById(R.id.btnPhoneCall);
        imvPreviewImage = findViewById(R.id.imvPreviewImage);
        btnClosePreview = findViewById(R.id.btnClosePreview);

        imvPreviewImage.setVisibility(View.GONE);
        btnClosePreview.setVisibility(View.GONE);

        Intent intent = getIntent();
        if (intent != null) {
            groupId = intent.getStringExtra("groupId");
            currentUserId = intent.getStringExtra("currentUserId");
        }

        FirebaseUtil.getCurrentUserReference().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    currentUsername = task.getResult().getString("username");
                }
            }
        });

        FirebaseUtil.getGroupReference(groupId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    groupModel = task.getResult().toObject(GroupModel.class);
                    txtUsername.setText(groupModel.getGroupName());
                    List<String> userIds = new ArrayList<>(groupModel.getUserIds());
                    userIds.remove(currentUserId);
                    otherUserIds = userIds;
                    setUpMessageGroupRecyclerView();
                }
            }
        });

        FirebaseUtil.getGroupAvatarReference(groupId).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    AndroidUtil.setUriToImageView(ChatGroupActivity.this, uri, imvAvatar);
                    imvAvatar.setPadding(5, 5, 5, 5);
                }
            }
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        selectedImageUri = data.getData();
                        AndroidUtil.setUriToImageViewRec(ChatGroupActivity.this, selectedImageUri, imvPreviewImage);
                        imvPreviewImage.setVisibility(View.VISIBLE);
                        btnClosePreview.setVisibility(View.VISIBLE);
                    }
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
                sendMessage(message);
            }
        });

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ChatGroupActivity.this)
                        .compress(512)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(512, 512)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickerLauncher.launch(intent);
                                return null;
                            }
                        });
            }
        });

        btnClosePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageUri = null;
                AndroidUtil.setUriToImageViewRec(ChatGroupActivity.this, null, imvPreviewImage);
                imvPreviewImage.setVisibility(View.GONE);
                btnClosePreview.setVisibility(View.GONE);
            }
        });

    }

    private void sendMessage(String message) {
        if (selectedImageUri != null) {
            uploadImageAndSendMessage(message);
        } else {
            if (message.isEmpty()) {
                return;
            }
            sendText(message);
        }
    }

    private void uploadImageAndSendMessage(String message) {
        FirebaseUtil.uploadImage(selectedImageUri, new FirebaseUtil.OnImageUploadListener() {
            @Override
            public void onImageUploadSuccess(String imageUrl) {
                if (message.isEmpty()) {
                    sendImage(imageUrl);
                } else {
                    sendImage(imageUrl);
                    sendText(message);
                }
            }

            @Override
            public void onImageUploadFailure(Exception e) {
                AndroidUtil.showToast(ChatGroupActivity.this, e.getMessage());
            }
        });
    }

    private void sendText(String message) {
        String encryptedMessage = EncryptionUtil.encrypt(message);
        groupModel.setLastSentMessageTimestamp(Timestamp.now());
        groupModel.setLastMessageSenderId(currentUserId);
        groupModel.setLastMessage(message);
        FirebaseUtil.getGroupReference(groupId).set(groupModel);

        MessageModel messageModel = new MessageModel(encryptedMessage, currentUserId, currentUsername, Timestamp.now());

        FirebaseUtil.getGroupMessageCollection(groupId).add(messageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    txtChat.setText("");
//                    sendNotification(message);
                    DocumentReference documentReference = task.getResult();
                    if (documentReference != null) {
                        String messageId = documentReference.getId();
                        messageModel.setMessageId(messageId);

                        FirebaseUtil.getGroupMessageCollection(groupId).document(messageId).set(messageModel);
                    }
                }
            }
        });
    }

    private void sendImage(String imageUrl) {
        String encryptedImageUrl = EncryptionUtil.encryptImage(imageUrl.getBytes());
        groupModel.setLastSentMessageTimestamp(Timestamp.now());
        groupModel.setLastMessageSenderId(currentUserId);
        groupModel.setLastMessage("Sent a photo");
        FirebaseUtil.getGroupReference(groupId).set(groupModel);

        MessageModel messageModel = new MessageModel(currentUserId, currentUsername, Timestamp.now(), encryptedImageUrl);

        FirebaseUtil.getGroupMessageCollection(groupId).add(messageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    txtChat.setText("");
//                    sendNotification("Sent a photo");

                    selectedImageUri = null;
                    AndroidUtil.setUriToImageViewRec(ChatGroupActivity.this, null, imvPreviewImage);
                    imvPreviewImage.setVisibility(View.GONE);
                    btnClosePreview.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setUpMessageGroupRecyclerView() {
        Query query = FirebaseUtil.getGroupMessageCollection(groupId)
                .orderBy("sentTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(query, MessageModel.class).build();

        if (adapter == null) {
            adapter = new MessageGroupRecyclerAdapter(options, getApplicationContext(), groupModel);
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