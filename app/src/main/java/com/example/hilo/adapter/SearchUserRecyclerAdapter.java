package com.example.hilo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hilo.R;
import com.example.hilo.model.UserModel;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {
    private Context context;
    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.txtUsername.setText(model.getUsername());
        holder.txtPhone.setText(model.getPhone());
        if (model.getUserId().equals(FirebaseUtil.currentUserId())) {
            holder.txtUsername.setText(model.getUsername() + " (Me)");
        }

    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_search_row, parent, false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUsername, txtPhone;
        private ImageView imvAvatar;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);
        }
    }
}
