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

import com.example.hilo.adapter.MessageRecyclerAdapter;
import com.example.hilo.model.ChatroomModel;
import com.example.hilo.model.MessageModel;
import com.example.hilo.model.UserModel;
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
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
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
    private String currentUsername;
    private EditText txtChat;
    private TextView txtUsername;
    private ImageButton btnSend, btnBack, btnChooseImage, btnClosePreview;
    private RecyclerView recyclerViewMessage;
    private ImageView imvAvatar, imvPreviewImage;
    private String currentUserId, otherUserId, chatroomId;
    private MessageRecyclerAdapter adapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private ZegoSendCallInvitationButton btnPhoneCall, btnVideoCall;

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
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnVideoCall = findViewById(R.id.btnVideoCall);
        btnPhoneCall = findViewById(R.id.btnPhoneCall);
        imvPreviewImage = findViewById(R.id.imvPreviewImage);
        btnClosePreview = findViewById(R.id.btnClosePreview);

        imvPreviewImage.setVisibility(View.GONE);
        btnClosePreview.setVisibility(View.GONE);

        txtUsername.setText(otherUserModel.getUsername());


        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        selectedImageUri = data.getData();
                        AndroidUtil.setUriToImageViewRec(ChatActivity.this, selectedImageUri, imvPreviewImage);
                        imvPreviewImage.setVisibility(View.VISIBLE);
                        btnClosePreview.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        FirebaseUtil.getOtherUserAvatarReference(otherUserModel.getUserId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    AndroidUtil.setUriToImageView(ChatActivity.this, uri, imvAvatar);
                    imvAvatar.setPadding(5, 5, 5, 5);
                }
            }
        });

        FirebaseUtil.getCurrentUserReference().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    currentUsername = task.getResult().getString("username");
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
                ImagePicker.with(ChatActivity.this)
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
                AndroidUtil.setUriToImageViewRec(ChatActivity.this, null, imvPreviewImage);
                imvPreviewImage.setVisibility(View.GONE);
                btnClosePreview.setVisibility(View.GONE);
            }
        });

        txtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, ChatInfoActivity.class);
                intent.putExtra("chatroomId", chatroomId);
                intent.putExtra("otherUserId", otherUserModel.getUserId());
                intent.putExtra("otherUsername", otherUserModel.getUsername());
                intent.putExtra("otherPhone", otherUserModel.getPhone());
                startActivity(intent);
            }
        });

        setUpChatroomModel();
        setUpCall();
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
                                "",
                                true
                        );
                        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                    } else {
                        if (!chatroomModel.getLastMessageSenderId().equals(currentUserId)) {
                            chatroomModel.setRead(true);
                            FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                        }
                    }
                    setUpMessageRecyclerView();
                }
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
                AndroidUtil.showToast(ChatActivity.this, e.getMessage());
            }
        });
    }

    private void sendText(String message) {
        String encryptedMessage = EncryptionUtil.encrypt(message);
        chatroomModel.setLastSentMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(currentUserId);
        chatroomModel.setLastMessage(message);
        chatroomModel.setRead(false);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        MessageModel messageModel = new MessageModel(encryptedMessage, currentUserId, currentUsername, Timestamp.now());
        FirebaseUtil.getChatroomMessageCollection(chatroomId).add(messageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    txtChat.setText("");
                    sendNotification(message);
                    DocumentReference documentReference = task.getResult();
                    if (documentReference != null) {
                        String messageId = documentReference.getId();
                        messageModel.setMessageId(messageId);

                        FirebaseUtil.getChatroomMessageCollection(chatroomId).document(messageId).set(messageModel);
                    }
                }
            }
        });
    }

    private void sendImage(String imageUrl) {
        String encryptedImageUrl = EncryptionUtil.encryptImage(imageUrl.getBytes());
        MessageModel messageModel = new MessageModel(currentUserId, currentUsername, Timestamp.now(), encryptedImageUrl);

        chatroomModel.setLastSentMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(currentUserId);
        chatroomModel.setLastMessage("Sent a photo");
        chatroomModel.setRead(false);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        FirebaseUtil.getChatroomMessageCollection(chatroomId).add(messageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    txtChat.setText("");
                    sendNotification("Sent a photo");

                    selectedImageUri = null;
                    AndroidUtil.setUriToImageViewRec(ChatActivity.this, null, imvPreviewImage);
                    imvPreviewImage.setVisibility(View.GONE);
                    btnClosePreview.setVisibility(View.GONE);
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
            adapter = new MessageRecyclerAdapter(options, getApplicationContext(), chatroomModel);
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

    private void setUpCall() {
        btnPhoneCall.setIsVideoCall(false);
        btnPhoneCall.setResourceID("zego_uikit_call");
        btnPhoneCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(otherUserId, otherUserModel.getUsername())));

        btnVideoCall.setIsVideoCall(true);
        btnVideoCall.setResourceID("zego_uikit_call");
        btnVideoCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(otherUserId, otherUserModel.getUsername())));
    }
}