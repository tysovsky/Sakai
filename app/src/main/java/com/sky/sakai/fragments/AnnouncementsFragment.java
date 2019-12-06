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
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.room.Database;

import com.sky.sakai.R;
import com.sky.sakai.adapters.AnnouncementsAdapter;
import com.sky.sakai.models.Announcement;
import com.sky.sakai.models.Site;
import com.sky.sakai.network.NetworkManager;
import com.sky.sakai.storage.DatabaseManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AnnouncementsFragment extends Fragment implements NetworkManager.OnAnnouncementsRequestListener {

    private ListView lvAnnouncementsListView;
    private AnnouncementsAdapter announcementsAdapter;
    private final ArrayList<Announcement> mAnouncements = new ArrayList<>();

    private Site site = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcements, container, false);

        lvAnnouncementsListView = view.findViewById(R.id.lv_announcements);

        if(getArguments() != null && getArguments().containsKey("site"))
            site = (Site)getArguments().getSerializable("site");

        if (site == null) {
            mAnouncements.addAll(DatabaseManager.getInstance().getAllAnnouncements());
            NetworkManager.getInstance().getAllAnnouncements(300, 300, this);
        }
        else{
            mAnouncements.addAll(site.getAnnouncements());
            NetworkManager.getInstance().getAnnouncementsForSite(site.Id, this);
        }

        announcementsAdapter = new AnnouncementsAdapter(getContext(), mAnouncements);
        lvAnnouncementsListView.setAdapter(announcementsAdapter);

        lvAnnouncementsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("announcement", announcementsAdapter.getItem(position));
                Navigation.findNavController(view).navigate(R.id.nav_announcement_details, bundle);

            }
        });

        return view;
    }

    @Override
    public void onAnnouncementsReceived(final ArrayList<Announcement> announcements) {
        final ArrayList<Announcement> newAnnouncements = new ArrayList<>();
        DatabaseManager db = DatabaseManager.getInstance();
        for (Announcement announcement: announcements){
            if(db.saveAnnouncement(announcement))
                newAnnouncements.add(announcement);
        }

        if(getActivity() == null){
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(newAnnouncements.size() > 0){
                    mAnouncements.addAll(newAnnouncements);

                    Collections.sort(mAnouncements, new Comparator<Announcement>() {
                        @Override
                        public int compare(Announcement o1, Announcement o2) {
                            return o2.CreationDate.compareTo(o1.CreationDate);
                        }
                    });

                    announcementsAdapter.notifyDataSetChanged();

                }
            }
        });


    }
}
