package com.sky.sakai.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.sky.sakai.R;
import com.sky.sakai.SakaiApplication;
import com.sky.sakai.adapters.AttachmentsAdapter;
import com.sky.sakai.models.Announcement;
import com.sky.sakai.models.Assignment;
import com.sky.sakai.models.Attachment;
import com.sky.sakai.network.NetworkManager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class AssignmentDetailsFragment extends Fragment implements NetworkManager.OnAttachmentDownloadedListener {
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
        TextView tvOpenDate = view.findViewById(R.id.tv_assignment_open_date);
        TextView tvDueDate = view.findViewById(R.id.tv_assignment_due_date);

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        tvTitle.setText(assignment.Title);
        tvBody.setText(assignment.Instructions);
        tvOpenDate.setText(df.format(assignment.OpenTime));
        tvDueDate.setText(df.format(assignment.DueTime));

        if(assignment.Attachments.size() > 0){
            ListView attachmentsListView = view.findViewById(R.id.lv_attachments);
            AttachmentsAdapter adapter = new AttachmentsAdapter(getContext(), assignment.Attachments);
            attachmentsListView.setAdapter(adapter);

            attachmentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Attachment a = adapter.getItem(position);
                    NetworkManager.getInstance().downloadAttachment(a, AssignmentDetailsFragment.this);
                }
            });
        }

        return view;
    }

    @Override
    public void onAttachmentDownloaded(String filename) {
        try{
            File file = new File(filename);

            // Get URI and MIME type of file
            Uri uri = FileProvider.getUriForFile(SakaiApplication.getAppContext(), SakaiApplication.getAppContext().getPackageName(), file);
            String mime = SakaiApplication.getAppContext().getContentResolver().getType(uri);

            // Open file with user selected app
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mime);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            PackageManager packageManager = SakaiApplication.getAppContext().getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            boolean isIntentSafe = activities.size() > 0;

            if(isIntentSafe)
                startActivity(intent);
            else{

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "No application to open this file found", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
