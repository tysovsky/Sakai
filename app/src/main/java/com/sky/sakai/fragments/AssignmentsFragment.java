package com.sky.sakai.fragments;

import android.app.Activity;
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
import com.sky.sakai.models.Site;
import com.sky.sakai.network.NetworkManager;
import com.sky.sakai.storage.DatabaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AssignmentsFragment extends Fragment implements NetworkManager.OnAssignmentsRequestListener {

    private ListView lvAssignmentsListView;
    private AssignmentsAdapter adapter;
    private ArrayList<Assignment> mAssignments;
    private Site site = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignments, container, false);

        lvAssignmentsListView = view.findViewById(R.id.lv_assignments);

        if(getArguments() != null && getArguments().containsKey("site"))
            site = (Site)getArguments().getSerializable("site");

        if (site == null) {
            mAssignments = DatabaseManager.getInstance().getAllAssignments();
            NetworkManager.getInstance().getAllAssignments(this);
        }
        else{
            mAssignments = site.getAssignments();
            NetworkManager.getInstance().getAssignmentsForSite(site.Id, this);
        }

        adapter = new AssignmentsAdapter(getContext(), mAssignments);

        lvAssignmentsListView.setAdapter(adapter);

        lvAssignmentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("assignment", adapter.getItem(position));
                Navigation.findNavController(view).navigate(R.id.nav_assignment_details, bundle);

            }
        });



        return view;
    }


    @Override
    public void onAssignmentsReceived(final ArrayList<Assignment> assignments) {

        final ArrayList<Assignment> newAssignments = new ArrayList<>();
        DatabaseManager db = DatabaseManager.getInstance();
        for (Assignment assignment: assignments){
            if(db.saveAssignment(assignment))
                newAssignments.add(assignment);
        }


        if(getActivity() == null){
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(newAssignments.size() > 0){
                    mAssignments.addAll(newAssignments);
                    Collections.sort(mAssignments, new Comparator<Assignment>() {
                        @Override
                        public int compare(Assignment o1, Assignment o2) {
                            return o2.DueTime.compareTo(o1.DueTime);
                        }
                    });
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }
}
