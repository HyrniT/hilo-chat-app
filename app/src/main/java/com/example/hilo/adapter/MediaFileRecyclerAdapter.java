package com.example.hilo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hilo.PhotoViewActivity;
import com.example.hilo.R;
import com.example.hilo.model.ChatroomModel;
import com.example.hilo.model.MessageModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.EncryptionUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MediaFileRecyclerAdapter extends FirestoreRecyclerAdapter<MessageModel, MediaFileRecyclerAdapter.MediaFileModelViewHolder> {
    private Context context;

    public MediaFileRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MediaFileRecyclerAdapter.MediaFileModelViewHolder holder, int position, @NonNull MessageModel model) {
        String imageUrl = model.getImageUrl();
        if (imageUrl != null) {
            try {
                byte[] decryptedImageUrlBytes = EncryptionUtil.decryptImage(imageUrl);
                if (decryptedImageUrlBytes != null) {
                    String decryptedImageUrl = new String(decryptedImageUrlBytes);

                    AndroidUtil.setUriToImageViewRec(context, Uri.parse(decryptedImageUrl), holder.imageView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        holder.imageView.setTag(imageUrl);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clickedImageUrl = (String) v.getTag();
                if (clickedImageUrl != null) {
                    Intent intent = new Intent(context, PhotoViewActivity.class);
                    intent.putExtra("imageUrl", clickedImageUrl);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }

    @NonNull
    @Override
    public MediaFileRecyclerAdapter.MediaFileModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_media_item, parent, false);
        return new MediaFileModelViewHolder(view);
    }

    public class MediaFileModelViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public MediaFileModelViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

