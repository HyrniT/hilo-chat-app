package com.example.hilo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hilo.ChatGroupActivity;
import com.example.hilo.R;
import com.example.hilo.model.GroupModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class GroupRecyclerAdapter extends FirestoreRecyclerAdapter<GroupModel, GroupRecyclerAdapter.GroupModelViewHolder> {
    private Context context;
    public GroupRecyclerAdapter(@NonNull FirestoreRecyclerOptions<GroupModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupModelViewHolder holder, int position, @NonNull GroupModel model) {
        if (model != null) {
            String groupId = model.getGroupId();
            String lastMessageSenderId = model.getLastMessageSenderId();
            String currentUserId = FirebaseUtil.getCurrentUserId();

            boolean isLastMessageSentByMe = lastMessageSenderId != null && lastMessageSenderId.equals(currentUserId);
            FirebaseUtil.getGroupReference(groupId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        FirebaseUtil.getGroupAvatarReference(groupId).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri uri = task.getResult();
                                    AndroidUtil.setUriToImageView(context, uri, holder.imvAvatar);
                                }
                            }
                        });

                        holder.txtUsername.setText(model.getGroupName());

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
                                    Intent intent = new Intent(context, ChatGroupActivity.class);
                                    intent.putExtra("groupId", groupId);
                                    intent.putExtra("currentUserId", currentUserId);
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
    public GroupModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_chatroom_row, parent, false);
        return new GroupRecyclerAdapter.GroupModelViewHolder(view);
    }

    public class GroupModelViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUsername, txtLastMessage, txtLastMessageTime;
        private ImageView imvAvatar;

        public GroupModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            txtLastMessageTime = itemView.findViewById(R.id.txtLastMessageTime);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);
        }
    }
}
