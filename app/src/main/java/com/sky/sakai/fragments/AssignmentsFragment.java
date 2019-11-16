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
import com.sky.sakai.adapters.AssignmentsAdapter;
import com.sky.sakai.models.Announcement;
import com.sky.sakai.models.Assignment;
import com.sky.sakai.network.NetworkManager;

import java.util.ArrayList;

public class AssignmentsFragment extends Fragment implements NetworkManager.OnAssignmentsRequestListener {

    private ListView lvAssignmentsListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignments, container, false);

        lvAssignmentsListView = view.findViewById(R.id.lv_assignments);

        NetworkManager.getInstance().getAllAssignments(this);

        return view;
    }


    @Override
    public void onAssignmentsReceived(final ArrayList<Assignment> assignments) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AssignmentsAdapter adapter = new AssignmentsAdapter(getContext(), assignments);
                lvAssignmentsListView.setAdapter(adapter);
            }
        });
    }
}
