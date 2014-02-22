package ca.simplerunner.app;

import ca.simplerunner.R;
import ca.simplerunner.misc.TabsPagerAdapter;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

public class RunView extends FragmentActivity implements ActionBar.TabListener {
	
	ViewPager viewPager;
	TabsPagerAdapter tabAdapter;
	long runID;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.runview);
		this.runID = getIntent().getLongExtra("runID", 69);
		viewPager = (ViewPager) findViewById(R.id.pager);
        tabAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	
		actionBar.addTab(actionBar.newTab().setText("Main")
				.setTabListener(this), 0, true);
		actionBar.addTab(actionBar.newTab().setText("Split")
				.setTabListener(this), 1, false);
		actionBar.addTab(actionBar.newTab().setText("Map")
				.setTabListener(this), 2, false);
		
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			 
		    @Override
		    public void onPageSelected(int position) {
		        actionBar.setSelectedNavigationItem(position);
		    }
		 
		    @Override
		    public void onPageScrolled(int arg0, float arg1, int arg2) {
		    }
		 
		    @Override
		    public void onPageScrollStateChanged(int arg0) {
		    }
		});
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// Nothing to do here		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// Nothing to do here
	}
	
	/*
	 * Hack to remove GoogleMap
	 */
	public void removeMapFragment() {
		FragmentManager fm = getSupportFragmentManager();
		Fragment f = fm.findFragmentById(R.id.map);
		fm.beginTransaction().remove(f).commit();
	}
	
	@Override
	public void onBackPressed() {
		if(viewPager.getCurrentItem() >= 1) {
			removeMapFragment();
		}
	    this.finish();
	}
	
	public long getRunID() {
		return this.runID;
	}

}
