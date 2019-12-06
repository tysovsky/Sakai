package com.sky.sakai.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sky.sakai.R;
import com.sky.sakai.adapters.AttachmentsAdapter;
import com.sky.sakai.models.Announcement;
import com.sky.sakai.models.Assignment;

public class AssignmentDetailsFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignment_details, container, false);

        Assignment assignment = (Assignment) getArguments().getSerializable("assignment");

        TextView tvTitle = view.findViewById(R.id.tv_assignment_title);
        TextView tvBody = view.findViewById(R.id.tv_assignment_body);

        tvTitle.setText(assignment.Title);
        tvBody.setText(assignment.Instructions);

        if(assignment.Attachments.size() > 0){
            ListView attachmentsListView = view.findViewById(R.id.lv_attachments);
            AttachmentsAdapter adapter = new AttachmentsAdapter(getContext(), assignment.Attachments);
            attachmentsListView.setAdapter(adapter);
        }

        return view;
    }
}
