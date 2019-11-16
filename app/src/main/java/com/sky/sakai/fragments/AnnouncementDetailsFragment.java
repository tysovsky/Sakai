package com.sky.sakai.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sky.sakai.R;
import com.sky.sakai.models.Announcement;

public class AnnouncementDetailsFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement_details, container, false);

        Announcement announcement = (Announcement)getArguments().getSerializable("announcement");

        TextView tvTitle = view.findViewById(R.id.tv_announcement_title);
        TextView tvBody = view.findViewById(R.id.tv_announcement_body);

        tvTitle.setText(announcement.Title);
        tvBody.setText(announcement.Body);

        return view;
    }
}
