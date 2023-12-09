package com.example.hilo;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.EncryptionUtil;
import com.github.chrisbanes.photoview.PhotoView;

public class PhotoViewActivity extends AppCompatActivity {
    private PhotoView photoView;
    private ImageButton btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        String imageUrl = getIntent().getStringExtra("imageUrl");

        photoView = findViewById(R.id.photoView);
        btnClose = findViewById(R.id.btnClose);

        try {
            byte[] decryptedImageUrlBytes = EncryptionUtil.decryptImage(imageUrl);
            if (decryptedImageUrlBytes != null) {
                String decryptedImageUrl = new String(decryptedImageUrlBytes);

                Glide.with(this)
                        .load(decryptedImageUrl)
                        .into(photoView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
