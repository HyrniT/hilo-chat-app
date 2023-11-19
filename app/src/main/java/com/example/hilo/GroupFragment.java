package com.example.hilo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hilo.adapter.ChatroomRecyclerAdapter;
import com.example.hilo.adapter.GroupRecyclerAdapter;
import com.example.hilo.model.ChatroomModel;
import com.example.hilo.model.GroupModel;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class GroupFragment extends Fragment {
    private RecyclerView recyclerViewGroup;
    private GroupRecyclerAdapter adapter;
    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_group, container, false);
        recyclerViewGroup = view.findViewById(R.id.recyclerViewGroup);

        setUpRecyclerViewGroup();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void setUpRecyclerViewGroup() {
        Query query = FirebaseUtil.getGroupsCollection()
                .whereArrayContains("userIds", FirebaseUtil.getCurrentUserId())
                .orderBy("lastSentMessageTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<GroupModel> options = new FirestoreRecyclerOptions.Builder<GroupModel>()
                .setQuery(query, GroupModel.class).build();

        adapter = new GroupRecyclerAdapter(options, getContext());
        recyclerViewGroup.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGroup.setAdapter(adapter);
        adapter.startListening();
    }
}