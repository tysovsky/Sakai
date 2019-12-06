package com.sky.sakai.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sky.sakai.R;
import com.sky.sakai.activities.MainActivity;
import com.sky.sakai.adapters.ClassPagerAdapter;
import com.sky.sakai.models.Announcement;
import com.sky.sakai.models.Site;

import java.util.ArrayList;

public class ClassFragment extends Fragment {

    ClassPagerAdapter sectionsPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_class, container, false);

        Site site = (Site) getArguments().getSerializable("site");


        ((MainActivity)getActivity()).setTitle(site.Title);

        if (sectionsPagerAdapter == null)
            sectionsPagerAdapter = new ClassPagerAdapter(getContext(), getChildFragmentManager(), site);

        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        return view;
    }
}
