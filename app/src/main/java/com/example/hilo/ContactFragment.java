package com.example.hilo;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hilo.adapter.ChatroomRecyclerAdapter;
import com.example.hilo.model.ChatroomModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ContactFragment extends Fragment {
    private RecyclerView recyclerViewChatroom;
    private ChatroomRecyclerAdapter adapter;
    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_contact, container, false);
        recyclerViewChatroom = view.findViewById(R.id.recyclerViewChatroom);

        setUpRecyclerViewChatroom();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        renewRecyclerViewChatroom();
    }

    private void setUpRecyclerViewChatroom() {
        Query query = FirebaseUtil.getChatroomsCollection()
                .whereArrayContains("userIds", FirebaseUtil.getCurrentUserId())
                .orderBy("lastSentMessageTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class).build();

        adapter = new ChatroomRecyclerAdapter(options, getContext());
        recyclerViewChatroom.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChatroom.setAdapter(adapter);
        adapter.startListening();
    }

    private void renewRecyclerViewChatroom() {
        adapter.notifyDataSetChanged();
    }
}