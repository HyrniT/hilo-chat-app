package com.example.hilo.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hilo.R;
import com.example.hilo.model.ChatroomModel;
import com.example.hilo.model.MessageModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.EncryptionUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
            holder.txtMessage.setText(EncryptionUtil.decrypt(model.getMessage()));
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
        private ImageButton btnUnpin;
        private static final float SWIPE_THRESHOLD = 200;

        public PinMessageModelViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            rowPinMessage = itemView.findViewById(R.id.rowPinMessage);
            btnUnpin = itemView.findViewById(R.id.btnUnpin);

            final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float deltaX = e2.getX() - e1.getX();

                    if (Math.abs(deltaX) > SWIPE_THRESHOLD) {
                        if (deltaX < 0) {
                            btnUnpin.setVisibility(View.VISIBLE);
                            rowPinMessage.setTranslationX((int) context.getResources().getDisplayMetrics().density * -65f);
                        } else {
                            btnUnpin.setVisibility(View.GONE);
                            rowPinMessage.setTranslationX(0);
                        }
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });

            rowPinMessage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });

            btnUnpin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleUnPin();
                }
            });
        }

        private void handleUnPin() {
            int position = getBindingAdapterPosition();
            MessageModel selectedMessage = getItem(position);
            if (selectedMessage != null) {
                String messageId = selectedMessage.getMessageId();
                String chatroomId = chatroomModel.getChatroomId();
                selectedMessage.setPinned(false);

                FirebaseUtil.getChatroomMessageCollection(chatroomId).document(messageId).set(selectedMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AndroidUtil.showToast(context, "Unpinned successfully");
                    }
                });
            }
        }
    }
}
