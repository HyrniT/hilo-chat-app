package com.example.hilo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hilo.model.UserModel;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;

public class AndroidUtil {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModel(Intent intent, UserModel model) {
        intent.putExtra("username", model.getUsername());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getUserId());
        intent.putExtra("FCMToken", model.getFCMToken());
    }

    public static UserModel getUserModel(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFCMToken(intent.getStringExtra("FCMToken"));
        return userModel;
    }

    public static String timestampToString (Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    public static void setUriToImageView(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    public static void setUriToImageViewRec(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context)
                .load(imageUri)
                .into(imageView);
    }
}
