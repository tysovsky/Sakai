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
import com.sky.sakai.adapters.GradesAdapter;
import com.sky.sakai.models.Announcement;
import com.sky.sakai.models.Grade;
import com.sky.sakai.models.Site;
import com.sky.sakai.network.NetworkManager;
import com.sky.sakai.storage.DatabaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GradesFragment extends Fragment implements NetworkManager.OnGradesRequestListener {

    private ListView lvGrades;
    private GradesAdapter adapter;
    private ArrayList<Grade> mGrades;
    private Site site;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades, container, false);



        lvGrades = view.findViewById(R.id.lv_grades);

        if(getArguments() != null && getArguments().containsKey("site"))
            site = (Site)getArguments().getSerializable("site");


        mGrades = DatabaseManager.getInstance().getGradesForSite(site.Id);
        adapter = new GradesAdapter(getContext(), mGrades);
        lvGrades.setAdapter(adapter);

        NetworkManager.getInstance().getGradesForSite(site.Id, this);

        return view;
    }

    @Override
    public void onGradesReceived(final ArrayList<Grade> grades) {
        final ArrayList<Grade> newGrades = new ArrayList<>();
        DatabaseManager db = DatabaseManager.getInstance();
        for (Grade grade: grades){
            if(db.saveGrade(grade))
                newGrades.add(grade);
        }

        if(getActivity() == null){
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(newGrades.size() > 0){
                    mGrades.addAll(newGrades);

                    Collections.sort(mGrades, new Comparator<Grade>() {
                        @Override
                        public int compare(Grade o1, Grade o2) {
                            return o2.ItemName.compareTo(o1.ItemName);
                        }
                    });

                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
