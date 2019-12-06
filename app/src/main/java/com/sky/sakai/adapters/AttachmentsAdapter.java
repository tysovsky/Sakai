package com.sky.sakai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sky.sakai.R;
import com.sky.sakai.models.Attachment;
import com.sky.sakai.models.Grade;

import java.util.List;

public class AttachmentsAdapter extends ArrayAdapter<Attachment> {
    public AttachmentsAdapter(@NonNull Context context, List<Attachment> attachments) {
        super(context, 0, attachments);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_attachment, parent, false);

        TextView tvName = convertView.findViewById(R.id.tv_attachment_name);

        Attachment a = getItem(position);
        tvName.setText(a.Name);

        return convertView;
    }
}
