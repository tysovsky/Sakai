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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class AnnouncementsAdapter extends ArrayAdapter<Announcement> {

    public AnnouncementsAdapter(@NonNull Context context, List<Announcement> announcements) {
        super(context, 0, announcements);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_announcement, parent, false);

        TextView tvTitle = convertView.findViewById(R.id.tv_announcement_title);
        TextView tvBody = convertView.findViewById(R.id.tv_announcement_body);
        TextView tvDate = convertView.findViewById(R.id.tv_announcement_date);
        TextView tvAuthor = convertView.findViewById(R.id.tv_announcement_author);
        ImageView ivAttachment = convertView.findViewById(R.id.iv_attachment);

        Announcement a = getItem(position);

        if(a.Attachments.size() > 0){
            ivAttachment.setVisibility(View.VISIBLE);
        }

        tvTitle.setText(a.Title);
        tvBody.setText(a.Body);
        tvAuthor.setText("by " + a.CreatedBy);

        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        tvDate.setText(df.format(a.CreationDate));


        return convertView;
    }
}
