package com.example.hilo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hilo.ChatActivity;
import com.example.hilo.R;
import com.example.hilo.model.ChatroomModel;
import com.example.hilo.model.UserModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class ChatroomRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, ChatroomRecyclerAdapter.ChatroomModelViewHolder> {
    private Context context;

    public ChatroomRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        if (model != null) {
            FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                boolean isLastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.getCurrentUserId());
                                boolean isRead = model.getRead();
                                UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                                FirebaseUtil.getOtherUserAvatarReference(otherUserModel.getUserId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri uri = task.getResult();
                                            AndroidUtil.setUriToImageView(context, uri, holder.imvAvatar);
                                        }
                                    }
                                });

                                holder.txtUsername.setText(otherUserModel.getUsername());
                                if (isLastMessageSentByMe) {
                                    holder.txtLastMessage.setText("You: " + model.getLastMessage());
                                } else {
                                    holder.txtLastMessage.setText(model.getLastMessage());
                                    if (!isRead) {

                                        GradientDrawable drawable = (GradientDrawable) holder.layout.getBackground().mutate();
                                        int newStrokeColor = ContextCompat.getColor(context, R.color.md_theme_light_primary);
                                        drawable.setStroke(5, newStrokeColor);
                                        holder.layout.setBackground(drawable);
                                    }
                                }
                                holder.txtLastMessageTime.setText(AndroidUtil.timestampToString(model.getLastSentMessageTimestamp()));

                                if (context != null) {
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(context, ChatActivity.class);
                                            AndroidUtil.passUserModel(intent, otherUserModel);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(intent);
                                        }
                                    });
                                }
                            }
                        }
                    });
        }
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_chatroom_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUsername, txtLastMessage, txtLastMessageTime;
        private ImageView imvAvatar;
        private LinearLayout layout;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            txtLastMessageTime = itemView.findViewById(R.id.txtLastMessageTime);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
