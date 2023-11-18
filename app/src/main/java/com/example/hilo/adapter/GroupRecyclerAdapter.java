package com.example.hilo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hilo.model.GroupModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class GroupRecyclerAdapter extends FirestoreRecyclerAdapter<GroupModel, GroupRecyclerAdapter.GroupModelViewHolder> {
    private Context context;
    public GroupRecyclerAdapter(@NonNull FirestoreRecyclerOptions<GroupModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupModelViewHolder holder, int position, @NonNull GroupModel model) {
        if (model != null) {

        }
    }

    @NonNull
    @Override
    public GroupModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    public class GroupModelViewHolder extends RecyclerView.ViewHolder {
        public GroupModelViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
