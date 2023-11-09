package com.example.hilo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hilo.ChatGptActivity;
import com.example.hilo.R;
import com.example.hilo.model.ChatroomAiModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

// Muc dich tao lop nay la khong lay avatar tren firebase
public class ChatroomAiRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomAiModel, ChatroomAiRecyclerAdapter.ChatroomAiModelViewHolder> {
    private Context context;
    public ChatroomAiRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomAiModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomAiRecyclerAdapter.ChatroomAiModelViewHolder holder, int position, @NonNull ChatroomAiModel model) {
        if (model != null) {
//            FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
            FirebaseUtil.getChatroomAiReference(FirebaseUtil.getCurrentUserId() + "-chatgpt") // hard assign data
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                boolean isLastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.getCurrentUserId());
//                                UserModel otherUserModel = task.getResult().toObject(UserModel.class);

//                                FirebaseUtil.getOtherUserAvatarReference(otherUserModel.getUserId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Uri> task) {
//                                        if (task.isSuccessful()) {
//                                            Uri uri = task.getResult();
//                                            AndroidUtil.setUriToImageView(context, uri, holder.imvAvatar);
//                                        }
//                                    }
//                                });

//                                holder.txtUsername.setText(otherUserModel.getUsername());
                                holder.txtUsername.setText(model.getChatroomName());
                                if (isLastMessageSentByMe) {
                                    holder.txtLastMessage.setText("You: " + model.getLastMessage());
                                } else {
                                    holder.txtLastMessage.setText(model.getLastMessage());
                                }
                                holder.txtLastMessageTime.setText(AndroidUtil.timestampToString(model.getLastSentMessageTimestamp()));

                                if (context != null) {
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(context, ChatGptActivity.class);
//                                            AndroidUtil.passUserModel(intent, otherUserModel);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(intent);
                                        }
                                    });
                                }
                            }
                        }
                    }
            );
        }
    }

    @NonNull
    @Override
    public ChatroomAiRecyclerAdapter.ChatroomAiModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_chatroom_ai_row, parent, false);
        return new ChatroomAiRecyclerAdapter.ChatroomAiModelViewHolder(view);
    }

    public class ChatroomAiModelViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUsername, txtLastMessage, txtLastMessageTime;
//        private ImageView imvAvatar;

        public ChatroomAiModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            txtLastMessageTime = itemView.findViewById(R.id.txtLastMessageTime);
//            imvAvatar = itemView.findViewById(R.id.imvAvatar);
        }
    }
}
