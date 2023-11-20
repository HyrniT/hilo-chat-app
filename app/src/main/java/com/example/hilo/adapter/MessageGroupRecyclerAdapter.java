package com.example.hilo.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hilo.R;
import com.example.hilo.model.MessageModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MessageGroupRecyclerAdapter extends FirestoreRecyclerAdapter<MessageModel, MessageGroupRecyclerAdapter.MessageGroupModelViewHolder> {
    private Context context;
    public MessageGroupRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageGroupModelViewHolder holder, int position, @NonNull MessageModel model) {
        if (model != null) {
            if (model.getSenderId().equals(FirebaseUtil.getCurrentUserId())) {
                holder.layoutLeftMessage.setVisibility(View.GONE);
                holder.layoutRightMessage.setVisibility(View.VISIBLE);
                holder.txtRightMessage.setText(model.getMessage());
                if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
                    holder.imgRightMessage.setVisibility(View.VISIBLE);
                    holder.layoutRightMessage.setBackgroundResource(0);
                    holder.layoutRightMessage.setPadding(0, 0, 0, 0);
                    AndroidUtil.setUriToImageViewRec(context, Uri.parse(model.getImageUrl()), holder.imgRightMessage);
                } else {
                    holder.imgRightMessage.setVisibility(View.GONE);
                }
            } else {
                // Khi thêm 1 tin nhắn mới vào Adapter thì có position là 0 -> tin nhắn cũ nhất có position là getItemCount() - 1
                // Khi load dữ liệu từ Firebase, Adapter này sẽ load từ 0 đến getItemCount() - 1
                // Kiểm tra position của tin nhắn hiện tại có phải kế cuối không
                if (position < getItemCount() - 1) {
                    MessageModel nextMessage = getItem(position + 1);
                    if (nextMessage != null && nextMessage.getSenderId().equals(model.getSenderId())) {
                        // Nếu tin nhắn tiếp theo và tin nhắn hiện tại từ cùng một người, ẩn tên người gửi
                        holder.txtSenderName.setVisibility(View.GONE);
                    } else {
                        // Nếu tin nhắn tiếp theo và tin nhắn hiện tại từ các người khác nhau, hiển thị tên người gửi
                        holder.txtSenderName.setVisibility(View.VISIBLE);
                        holder.txtSenderName.setText(model.getSenderName());
                    }
                } else {
                    // Nếu là tin nhắn cuối cùng, hiển thị tên người gửi
                    holder.txtSenderName.setVisibility(View.VISIBLE);
                    holder.txtSenderName.setText(model.getSenderName());
                }
                holder.layoutLeftMessage.setVisibility(View.VISIBLE);
                holder.layoutRightMessage.setVisibility(View.GONE);
                holder.txtLeftMessage.setText(model.getMessage());
                if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
                    holder.imgLeftMessage.setVisibility(View.VISIBLE);
                    holder.layoutLeftMessage.setBackgroundResource(0);
                    holder.layoutLeftMessage.setPadding(0, 0, 0, 0);
                    AndroidUtil.setUriToImageViewRec(context, Uri.parse(model.getImageUrl()), holder.imgLeftMessage);
                } else {
                    holder.imgLeftMessage.setVisibility(View.GONE);
                }
            }
        }
    }

    @NonNull
    @Override
    public MessageGroupModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_message_group_row, parent, false);
        return new MessageGroupRecyclerAdapter.MessageGroupModelViewHolder(view);
    }

    public class MessageGroupModelViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutLeftMessage, layoutRightMessage;
        private TextView txtLeftMessage, txtRightMessage, txtSenderName;
        private ImageView imgLeftMessage ,imgRightMessage;

        public MessageGroupModelViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutLeftMessage = itemView.findViewById(R.id.layoutLeftMessage);
            layoutRightMessage = itemView.findViewById(R.id.layoutRightMessage);
            txtLeftMessage = itemView.findViewById(R.id.txtLeftMessage);
            txtRightMessage = itemView.findViewById(R.id.txtRightMessage);
            txtSenderName = itemView.findViewById(R.id.txtSenderName);
            imgLeftMessage = itemView.findViewById(R.id.imgLeftMessage);
            imgRightMessage = itemView.findViewById(R.id.imgRightMessage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        MessageModel selectedMessage = getItem(position);
                        if (selectedMessage != null) {
                            // Hiển thị index của tin nhắn được chọn bằng Toast
                            AndroidUtil.showToast(context, String.valueOf(position));
                        }
                    }
                }
            });
        }
    }
}
