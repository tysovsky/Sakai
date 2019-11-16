package com.sky.sakai.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.sky.sakai.R;
import com.sky.sakai.adapters.AnnouncementsAdapter;
import com.sky.sakai.models.Announcement;
import com.sky.sakai.network.NetworkManager;

import java.util.ArrayList;

public class AnnouncementsFragment extends Fragment implements NetworkManager.OnAnnouncementsRequestListener {

    private ListView lvAnnouncementsListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcements, container, false);

        lvAnnouncementsListView = view.findViewById(R.id.lv_announcements);

        NetworkManager.getInstance().getAllAnnouncements(300, 300, this);

        return view;
    }

    @Override
    public void onAnnouncementsReceived(final ArrayList<Announcement> announcements) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AnnouncementsAdapter adapter = new AnnouncementsAdapter(getContext(), announcements);
                lvAnnouncementsListView.setAdapter(adapter);

                lvAnnouncementsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("announcement", adapter.getItem(position));
                        Navigation.findNavController(view).navigate(R.id.nav_announcement_details, bundle);

                    }
                });
            }
        });
    }
}
