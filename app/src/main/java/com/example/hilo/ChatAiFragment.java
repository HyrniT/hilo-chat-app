package com.example.hilo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hilo.adapter.ChatroomAiRecyclerAdapter;
import com.example.hilo.model.ChatroomAiModel;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class ChatAiFragment extends Fragment {

    private RecyclerView recyclerViewChatroomAi;
    private ChatroomAiRecyclerAdapter adapter;

    public ChatAiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat_ai, container, false);
        recyclerViewChatroomAi = view.findViewById(R.id.recyclerViewChatroomAi);

        setUpRecyclerViewChatroomAi();

        return view;
    }

    private void setUpRecyclerViewChatroomAi() {
        Query query = FirebaseUtil.getChatroomAisCollection()
                .orderBy("lastSentMessageTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomAiModel> options = new FirestoreRecyclerOptions.Builder<ChatroomAiModel>()
                .setQuery(query, ChatroomAiModel.class).build();

        adapter = new ChatroomAiRecyclerAdapter(options, getContext());
        recyclerViewChatroomAi.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChatroomAi.setAdapter(adapter);
        adapter.startListening();
    }
}