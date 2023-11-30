package com.example.hilo.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hilo.PhotoViewActivity;
import com.example.hilo.R;
import com.example.hilo.model.GroupModel;
import com.example.hilo.model.MessageModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

public class MessageGroupRecyclerAdapter extends FirestoreRecyclerAdapter<MessageModel, MessageGroupRecyclerAdapter.MessageGroupModelViewHolder> {
    private Context context;
    private GroupModel groupModel;

    public MessageGroupRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MessageModel> options, Context context, GroupModel groupModel) {
        super(options);
        this.context = context;
        this.groupModel = groupModel;
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageGroupModelViewHolder holder, int position, @NonNull MessageModel model) {
        if (model != null) {
            int dp = (int) (context.getResources().getDisplayMetrics().density * 10f);
            if (model.getDeleted()) {
                if (model.getSenderId().equals(FirebaseUtil.getCurrentUserId())) {
                    holder.layoutLeftMessage.setVisibility(View.GONE);
                    holder.layoutRightMessage.setVisibility(View.VISIBLE);
                    holder.layoutRightMessage.setPadding(dp, dp, dp, dp);
                    holder.layoutRightMessage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_light_surfaceVariant)));
                    holder.txtRightMessage.setTextColor(ContextCompat.getColor(context, R.color.md_theme_light_outline));
                    holder.txtRightMessage.setText("Message deleted");
                    holder.imgRightMessage.setVisibility(View.GONE);
                } else {
                    holder.layoutRightMessage.setVisibility(View.GONE);
                    holder.layoutLeftMessage.setVisibility(View.VISIBLE);
                    holder.txtSenderName.setVisibility(View.GONE);
                    holder.layoutLeftMessage.setPadding(dp, dp, dp, dp);
                    holder.layoutLeftMessage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_light_surfaceVariant)));
                    holder.txtLeftMessage.setTextColor(ContextCompat.getColor(context, R.color.md_theme_light_outline));
                    holder.txtLeftMessage.setText("Message deleted");
                    holder.imgLeftMessage.setVisibility(View.GONE);
                }
            } else {
                if (model.getSenderId().equals(FirebaseUtil.getCurrentUserId())) {
                    holder.layoutLeftMessage.setVisibility(View.GONE);
                    holder.layoutRightMessage.setVisibility(View.VISIBLE);
                    holder.txtRightMessage.setText(model.getMessage());
                    holder.layoutRightMessage.setPadding(dp, dp, dp, dp);
                    holder.txtSenderName.setVisibility(View.GONE);
                    holder.layoutRightMessage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_dark_primaryContainer)));
                    holder.txtRightMessage.setTextColor(ContextCompat.getColor(context, R.color.md_theme_dark_onPrimaryContainer));

                    if (model.getImageUrl() == null) {
                        holder.imgRightMessage.setVisibility(View.GONE);
                    } else {
                        holder.txtSenderName.setVisibility(View.GONE);
                        holder.imgRightMessage.setVisibility(View.VISIBLE);
                        holder.layoutRightMessage.setPadding(0, 0, 0, 0);
                        AndroidUtil.setUriToImageViewRec(context, Uri.parse(model.getImageUrl()), holder.imgRightMessage);
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
                            if (nextMessage.getDeleted()) {
                                holder.txtSenderName.setVisibility(View.VISIBLE);
                            }
                        } else {
                            // Nếu tin nhắn tiếp theo và tin nhắn hiện tại từ các người khác nhau, hiển thị tên người gửi
                            holder.txtSenderName.setVisibility(View.VISIBLE);

                        }
                        holder.txtSenderName.setText(model.getSenderName());

                    } else {
                        // Nếu là tin nhắn cuối cùng, hiển thị tên người gửi
                        holder.txtSenderName.setVisibility(View.VISIBLE);
                        holder.txtSenderName.setText(model.getSenderName());
                    }
                    holder.layoutLeftMessage.setVisibility(View.VISIBLE);
                    holder.layoutRightMessage.setVisibility(View.GONE);
                    holder.txtLeftMessage.setText(model.getMessage());
                    holder.layoutLeftMessage.setPadding(dp, dp, dp, dp);
                    holder.layoutLeftMessage.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_light_primaryContainer)));
                    holder.txtLeftMessage.setTextColor(ContextCompat.getColor(context, R.color.md_theme_light_onPrimaryContainer));

                    if (model.getImageUrl() == null) {
                        holder.imgLeftMessage.setVisibility(View.GONE);
                    } else {
                        holder.imgLeftMessage.setVisibility(View.VISIBLE);
                        holder.layoutLeftMessage.setPadding(0, 0, 0, 0);
                        AndroidUtil.setUriToImageViewRec(context, Uri.parse(model.getImageUrl()), holder.imgLeftMessage);
                    }
                }
            }
        }
    }

    @NonNull
    @Override
    public MessageGroupModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_message_group_row, parent, false);
        return new MessageGroupRecyclerAdapter.MessageGroupModelViewHolder(view, context);
    }

    public class MessageGroupModelViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutLeftMessage, layoutRightMessage;
        private TextView txtLeftMessage, txtRightMessage, txtSenderName;
        private ImageView imgLeftMessage ,imgRightMessage;
        private Context context;

        public MessageGroupModelViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;

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
                        String imageUrl = selectedMessage.getImageUrl();
                        if (imageUrl != null) {
                            Intent intent = new Intent(context, PhotoViewActivity.class);
                            intent.putExtra("imageUrl", imageUrl);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        MessageModel selectedMessage = getItem(position);
                        if (selectedMessage != null && selectedMessage.getMessage() != null) {
                            itemView.setPadding(20, itemView.getPaddingTop(), 20, itemView.getPaddingBottom());
                            showPopupMenu(view, selectedMessage);
                        }
                    }
                    return true;
                }
            });

        }

        private void showPopupMenu(View anchorView, MessageModel message) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), anchorView);
            popupMenu.getMenuInflater().inflate(R.menu.popup_message_menu, popupMenu.getMenu());

            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                    itemView.setPadding(0, itemView.getPaddingTop(), 0, itemView.getPaddingBottom());
                }
            });

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.menu_pin) {
                        handlePin();
                    } else if (item.getItemId() == R.id.menu_delete) {
                        handleDelete();
                    } else if (item.getItemId() == R.id.menu_copy) {
                        handleCopy();
                    } else if (item.getItemId() == R.id.menu_reply) {
                        handleReply();
                    } else if (item.getItemId() == R.id.menu_forward) {
                        handleForward();
                    }

                    return true;
                }
            });

            // Add icon for menu item
            Menu menu = popupMenu.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                MenuItem menuItem = menu.getItem(i);
                SpannableString spannable = new SpannableString("    " + menuItem.getTitle());
                Drawable drawable = menuItem.getIcon();

                if (drawable != null) {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_CENTER);
                    spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }

                menuItem.setTitle(spannable);
            }

            if (message.getSenderId().equals(FirebaseUtil.getCurrentUserId())) {
                popupMenu.setGravity(Gravity.END);
            }

            popupMenu.show();
        }

        private void handlePin() {
            int position = getBindingAdapterPosition();
            MessageModel selectedMessage = getItem(position);
            if (selectedMessage != null && !selectedMessage.getPinned()) {
                String messageId = selectedMessage.getMessageId();
                String chatroomId = groupModel.getGroupId();
                selectedMessage.setPinned(true);

                FirebaseUtil.getChatroomMessageCollection(chatroomId).document(messageId).set(selectedMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AndroidUtil.showToast(context, "Pinned successfully");
                    }
                });
            }
        }
        private void handleDelete() {
            int position = getBindingAdapterPosition();
            MessageModel selectedMessage = getItem(position);

            if (selectedMessage != null && selectedMessage.getSenderId().equals(FirebaseUtil.getCurrentUserId())) {
                String messageId = selectedMessage.getMessageId();
                String groupId = groupModel.getGroupId();
                selectedMessage.setDeleted(true);

                FirebaseUtil.getGroupMessageCollection(groupId).document(messageId).set(selectedMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            if (position == 0) {
                                groupModel.setLastSentMessageTimestamp(Timestamp.now());
                                groupModel.setLastMessage("Deleted a message");
                                FirebaseUtil.getGroupReference(groupId).set(groupModel);
                            }

                            AndroidUtil.showToast(context, "Deleted successfully");
                        }
                    }
                });
            } else {
                AndroidUtil.showToast(context, "Cannot delete");
            }
        }

        private void handleCopy() {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager != null) {
                int position = getBindingAdapterPosition();
                MessageModel selectedMessage = getItem(position);
                if (selectedMessage != null) {
                    String messageText = selectedMessage.getMessage();
                    ClipData clipData = ClipData.newPlainText("message", messageText);
                    clipboardManager.setPrimaryClip(clipData);

                    AndroidUtil.showToast(context, "Copied");
                }
            }
        }

        private void handleReply() {}
        private void handleForward() {}
    }
}
