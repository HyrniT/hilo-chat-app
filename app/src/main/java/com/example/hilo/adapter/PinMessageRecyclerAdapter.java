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
import com.example.hilo.model.ChatroomModel;
import com.example.hilo.model.MessageModel;
import com.example.hilo.utils.AndroidUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class PinMessageRecyclerAdapter extends FirestoreRecyclerAdapter<MessageModel, PinMessageRecyclerAdapter.PinMessageModelViewHolder> {
    private Context context;
    private ChatroomModel chatroomModel;

    public PinMessageRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MessageModel> options, Context context) {
        super(options);
        this.context = context;
    }
    public PinMessageRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MessageModel> options, Context context, ChatroomModel chatroomModel) {
        super(options);
        this.context = context;
        this.chatroomModel = chatroomModel;
    }

    @Override
    protected void onBindViewHolder(@NonNull PinMessageRecyclerAdapter.PinMessageModelViewHolder holder, int position, @NonNull MessageModel model) {
        if (model != null) {
            holder.txtUsername.setText(model.getSenderName());
            holder.txtMessage.setText(model.getMessage());
            AndroidUtil.showToast(context, model.getMessage());
        } else {
            AndroidUtil.showToast(context, "NULLLLLLLLLLLLL");
        }
    }

    @NonNull
    @Override
    public PinMessageRecyclerAdapter.PinMessageModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_pin_message_row, parent, false);
        return new PinMessageRecyclerAdapter.PinMessageModelViewHolder(view);
    }

    public class PinMessageModelViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout rowPinMessage;
        private TextView txtUsername, txtMessage;

        public PinMessageModelViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            rowPinMessage = itemView.findViewById(R.id.rowPinMessage);

            rowPinMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
        }
    }
}
