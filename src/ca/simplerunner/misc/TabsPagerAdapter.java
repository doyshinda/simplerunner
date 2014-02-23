package ca.simplerunner.misc;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Custom class for swiping between tabs
 * 
 * @author Abe Friesen
 *
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
	
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            // Main run fragment
            return new MainRunFragment();
        case 1:
            // Split run fragment
            return new SplitRunFragment();
        case 2:
            // Map run fragment
            return new MapRunFragment();
        }
        return null;
    }
 
    /*
     * Return the number of tabs
     */
    @Override
    public int getCount() {
        return 3;
    }
}
