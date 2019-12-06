package com.sky.sakai.adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sky.sakai.R;
import com.sky.sakai.fragments.AnnouncementsFragment;
import com.sky.sakai.fragments.AssignmentsFragment;
import com.sky.sakai.fragments.GradesFragment;
import com.sky.sakai.models.Announcement;
import com.sky.sakai.models.Site;

public class ClassPagerAdapter extends FragmentStatePagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.menu_announcements, R.string.menu_assignments, R.string.menu_grades};
    private final Context mContext;
    private Site mSite;

    public ClassPagerAdapter(Context context, FragmentManager fm, Site site) {
        super(fm);
        mContext = context;
        mSite = site;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment f;
        Bundle args = new Bundle();
        args.putSerializable("site", mSite);

        switch (position){
            case 0:
                f = new AnnouncementsFragment();
                break;
            case 1:
                f = new AssignmentsFragment();
                break;
            case 2:
                f = new GradesFragment();
                break;
            default:
                return new AnnouncementsFragment();

        }

        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 3;
    }
}
