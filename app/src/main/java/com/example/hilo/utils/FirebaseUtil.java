package com.example.hilo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.example.hilo.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class FirebaseUtil {
    public static String getCurrentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference getCurrentUserReference() {
        return FirebaseFirestore.getInstance().collection("users").document(getCurrentUserId());
    }

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static boolean isLoggedIn() {
        return getCurrentUserId() != null;
    }

    public static void logOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static DocumentReference getGroupReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("groups").document(chatroomId);
    }

    public static DocumentReference getChatroomAiReference(String chatroomId) {
        return getCurrentUserReference().collection("chatroomAis").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageCollection(String chatroomId) {
        return getChatroomReference(chatroomId).collection("messages");
    }

    public static CollectionReference getGroupMessageCollection(String chatroomId) {
        return getGroupReference(chatroomId).collection("messages");
    }

    public static CollectionReference getChatroomAiMessageCollection(String chatroomId) {
        return getChatroomAiReference(chatroomId).collection("messages");
    }

    public static CollectionReference getChatroomsCollection() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static CollectionReference getGroupsCollection() {
        return FirebaseFirestore.getInstance().collection("groups");
    }

    public static CollectionReference getChatroomAisCollection() {
        return getCurrentUserReference().collection("chatroomAis");
    }

    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(FirebaseUtil.getCurrentUserId())) {
            return getUsersCollection().document(userIds.get(1));
        } else {
            return getUsersCollection().document(userIds.get(0));
        }
    }

    public static StorageReference getCurrentUserAvatarReference() {
        return FirebaseStorage
                .getInstance()
                .getReference()
                .child("avatar")
                .child(FirebaseUtil.getCurrentUserId());
    }

    public static StorageReference getOtherUserAvatarReference(String otherUserId) {
        return FirebaseStorage
                .getInstance()
                .getReference()
                .child("avatar")
                .child(otherUserId);
    }

    public static StorageReference getGroupAvatarReference(String groupId) {
        return FirebaseStorage
                .getInstance()
                .getReference()
                .child("avatar")
                .child(groupId);
    }

    public interface OnImageUploadListener {
        void onImageUploadSuccess(String imageUrl);

        void onImageUploadFailure(Exception e);
    }

    public static void uploadImage(Uri imageUri, OnImageUploadListener listener) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");
        String imageName = "image_" + System.currentTimeMillis();
        StorageReference imageReference = storageReference.child(imageName);

        imageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        listener.onImageUploadSuccess(imageUrl);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onImageUploadFailure(e);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onImageUploadFailure(e);
            }
        });
    }

//    private static String getFileExtension(Context context, Uri uri) {
//        ContentResolver contentResolver = context.getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
//    }
}
