package com.example.hilo.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hilo.CreateGroupActivity;
import com.example.hilo.R;
import com.example.hilo.model.UserModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SearchUserGroupRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserGroupRecyclerAdapter.UserModelViewHolder> {
    private Context context;
    public SearchUserGroupRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchUserGroupRecyclerAdapter.UserModelViewHolder holder, int position, @NonNull UserModel model) {
        if (model != null) {
            holder.bind(model);
            holder.txtUsername.setText(model.getUsername());
            holder.txtPhone.setText(model.getPhone());

            FirebaseUtil.getOtherUserAvatarReference(model.getUserId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtil.setUriToImageView(context, uri, holder.imvAvatar);
                    }
                }
            });

            if (context != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.isChecked) {
                            holder.cbUser.setChecked(false);
                        } else {
                            holder.cbUser.setChecked(true);
                        }
                    }
                });

            }
        }
    }

    @NonNull
    @Override
    public SearchUserGroupRecyclerAdapter.UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_search_user_group, parent, false);
        return new SearchUserGroupRecyclerAdapter.UserModelViewHolder(view);
    }

    public class UserModelViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUsername, txtPhone;
        private ImageView imvAvatar;
        private CheckBox cbUser;
        private boolean isChecked = false;
        private String userId;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);
            cbUser = itemView.findViewById(R.id.cbUser);
        }

        public void bind(UserModel model) {
            userId = model.getUserId();
            cbUser.setChecked(CreateGroupActivity.selectedUserIds.contains(userId));
            cbUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    UserModelViewHolder.this.isChecked = isChecked;

                    if (isChecked) {
                        CreateGroupActivity.selectedUserIds.add(userId);
                    } else {
                        CreateGroupActivity.selectedUserIds.remove(userId);
                    }
                }
            });
        }
    }
}
