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
import com.sky.sakai.models.Grade;

import java.util.List;

public class GradesAdapter extends ArrayAdapter<Grade> {
    public GradesAdapter(@NonNull Context context, List<Grade> grades) {
        super(context, 0, grades);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_grade, parent, false);

        TextView tvItemName = convertView.findViewById(R.id.tv_item_name);
        TextView tvGrade = convertView.findViewById(R.id.tv_grade);
        TextView tvPoints = convertView.findViewById(R.id.tv_points);

        Grade g = getItem(position);
        tvItemName.setText(g.ItemName);
        tvGrade.setText(g.Grade);
        tvPoints.setText(g.Points);

        return convertView;
    }
}
