package com.example.hilo.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hilo.R;
import com.example.hilo.model.MessageAiModel;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MessageAiRecyclerAdapter extends FirestoreRecyclerAdapter<MessageAiModel, MessageAiRecyclerAdapter.MessageAiModelViewHolder> {
    private Context context;
    public MessageAiRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MessageAiModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageAiRecyclerAdapter.MessageAiModelViewHolder holder, int position, @NonNull MessageAiModel model) {
        int dp = (int) (context.getResources().getDisplayMetrics().density * 10f);
        if (model.getSenderId().equals(FirebaseUtil.getCurrentUserId())) {
            holder.layoutLeftMessage.setVisibility(View.GONE);
            holder.layoutRightMessage.setVisibility(View.VISIBLE);
            holder.txtRightMessage.setText(model.getMessage());
            holder.layoutRightMessage.setPadding(dp, dp, dp, dp);
            holder.layoutRightMessage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_dark_primaryContainer)));
            holder.txtRightMessage.setTextColor(ContextCompat.getColor(context, R.color.md_theme_dark_onPrimaryContainer));
        } else {
            holder.layoutLeftMessage.setVisibility(View.VISIBLE);
            holder.layoutRightMessage.setVisibility(View.GONE);
            holder.txtLeftMessage.setText(model.getMessage());
            holder.layoutLeftMessage.setPadding(dp, dp, dp, dp);
            holder.layoutLeftMessage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_light_primaryContainer)));
            holder.txtLeftMessage.setTextColor(ContextCompat.getColor(context, R.color.md_theme_light_onPrimaryContainer));
        }
    }

    @NonNull
    @Override
    public MessageAiRecyclerAdapter.MessageAiModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_message_ai_row, parent, false);
        return new MessageAiRecyclerAdapter.MessageAiModelViewHolder(view, context);
    }

    public class MessageAiModelViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutLeftMessage, layoutRightMessage;
        private TextView txtLeftMessage, txtRightMessage;
        private Context context;

        public MessageAiModelViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;

            layoutLeftMessage = itemView.findViewById(R.id.layoutLeftMessage);
            layoutRightMessage = itemView.findViewById(R.id.layoutRightMessage);
            txtLeftMessage = itemView.findViewById(R.id.txtLeftMessage);
            txtRightMessage = itemView.findViewById(R.id.txtRightMessage);
        }
    }
}
