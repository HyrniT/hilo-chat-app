package com.example.hilo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hilo.ContactFragment;
import com.example.hilo.GroupFragment;

public class ViewPagerChatAdapter extends FragmentStateAdapter {
    public ViewPagerChatAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
            default:
                return new ContactFragment();
            case 1:
                return new GroupFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
