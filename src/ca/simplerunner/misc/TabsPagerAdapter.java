package ca.simplerunner.misc;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            // Main run fragment activity
            return new MainRunFragment();
        case 1:
            // Split run activity
            return new SplitRunFragment();
        case 2:
            // Map run fragment activity
            return new MapRunFragment();
        }
        return null;
    }
 
    @Override
    public int getCount() {
        return 3;
    }

}
