package nodomain.freeyourgadget.gadgetbridge.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import nodomain.freeyourgadget.gadgetbridge.activities.AbstractFragmentPagerAdapter;

public abstract class ChartFragmentAdapter extends AbstractFragmentPagerAdapter {

    public ChartFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public abstract int getCount();
    @NonNull
    public abstract Fragment getItem(int position);
    public abstract String getPageTitle(int position);
}
