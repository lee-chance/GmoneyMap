package com.chance.gmoneymap.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.chance.gmoneymap.Fragments.DownloadFragment;
import com.chance.gmoneymap.Fragments.MenuFragment;
import com.chance.gmoneymap.Fragments.NotifyFragment;
import com.chance.gmoneymap.Fragments.SearchFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private int mPageCount;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int mPageCount) {
        super(fm);
        this.mPageCount = mPageCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DownloadFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new MenuFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mPageCount;
    }
}
