package com.sky.sakai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sky.sakai.R;
import com.sky.sakai.models.Announcement;
import com.sky.sakai.models.Assignment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class AssignmentsAdapter extends ArrayAdapter<Assignment> {

    public AssignmentsAdapter(@NonNull Context context, List<Assignment> assignments) {
        super(context, 0, assignments);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_assignment, parent, false);

        TextView tvTitle = convertView.findViewById(R.id.tv_assignment_title);
        TextView tvBody = convertView.findViewById(R.id.tv_assignment_body);
        TextView tvDate = convertView.findViewById(R.id.tv_assignment_date);
        ImageView ivAttachment = convertView.findViewById(R.id.iv_attachment);

        Assignment a = getItem(position);

        if(a.Attachments.size() > 0){
            ivAttachment.setVisibility(View.VISIBLE);
        }


        tvTitle.setText(a.Title);
        tvBody.setText(a.Instructions);

        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        tvDate.setText("Due Date: " + df.format(a.DueTime));


        return convertView;
    }
}
