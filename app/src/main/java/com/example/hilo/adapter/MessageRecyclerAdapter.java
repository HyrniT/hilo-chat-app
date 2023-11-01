package com.example.hilo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hilo.R;
import com.example.hilo.model.MessageModel;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MessageRecyclerAdapter extends FirestoreRecyclerAdapter<MessageModel, MessageRecyclerAdapter.MessageModelViewHolder> {
    private Context context;

    public MessageRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public MessageModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_message_row, parent, false);
        return new MessageModelViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageModelViewHolder holder, int position, @NonNull MessageModel model) {
        if (model != null) {
            if (model.getSenderId().equals(FirebaseUtil.getCurrentUserId())) {
                holder.layoutLeftMessage.setVisibility(View.GONE);
                holder.layoutRightMessage.setVisibility(View.VISIBLE);
                holder.txtRightMessage.setText(model.getMessage());
            } else {
                holder.layoutLeftMessage.setVisibility(View.VISIBLE);
                holder.layoutRightMessage.setVisibility(View.GONE);
                holder.txtLeftMessage.setText(model.getMessage());
            }
        }
    }

    class MessageModelViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutLeftMessage, layoutRightMessage;
        private TextView txtLeftMessage, txtRightMessage;

        public MessageModelViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutLeftMessage = itemView.findViewById(R.id.layoutLeftMessage);
            layoutRightMessage = itemView.findViewById(R.id.layoutRightMessage);
            txtLeftMessage = itemView.findViewById(R.id.txtLeftMessage);
            txtRightMessage = itemView.findViewById(R.id.txtRightMessage);
        }
    }
}
