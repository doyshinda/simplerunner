package ca.simplerunner.misc;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ca.simplerunner.R;
import ca.simplerunner.app.RunView;
import ca.simplerunner.database.Database;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SplitRunFragment extends Fragment {
	
	Database db;
	RunStat stat;
	long runID;
	RunView parent;
	View myView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
 
        myView = inflater.inflate(R.layout.splitview, container, false);
        parent = (RunView) getActivity();
		runID = parent.getRunID();
		db = new Database(parent);
		stat = db.getRunStat(runID);
		TextView dateHeader = (TextView) myView.findViewById(R.id.splitDateHeader);
		dateHeader.setText(stat.getDate());
		populateListView();
		return myView;
    }
	
	/*
	 * Populate the list view with run split values
	 */
	private void populateListView() {
		ArrayList<LocationStat> locStats = db.getRunCoordinates(runID);
		ArrayList<SplitStat> splits = createSplits(locStats);
		
		SplitAdapter adapter = new SplitAdapter(getActivity(), R.layout.splitlistview, splits);

		ListView listview = (ListView) myView.findViewById(android.R.id.list);
		listview.setAdapter(adapter);
	}
	
	/*
	 * Create the split values based on the run stats
	 */
	private ArrayList<SplitStat> createSplits(ArrayList<LocationStat> locStats) {
		double prevLat = locStats.get(0).getLat();
		double prevLng = locStats.get(0).getLng();
		long initTime = locStats.get(0).getTimestamp();
		long totalTime = 0;
		float[] results = new float[1];
		double distance = 0.0;
		ArrayList<SplitStat> splitStats = new ArrayList<SplitStat>();
		double km = 0;
		
		for(int i = 1; i < locStats.size(); i++) {
			double currLat = locStats.get(i).getLat();
			double currLng = locStats.get(i).getLng();
			Location.distanceBetween(prevLat, prevLng, currLat, currLng, results);
			distance += results[0];
			prevLat = currLat;
			prevLng = currLng;
			if(distance >= 1000) {
				km += 1;
				distance = distance - 1000;
				long splitLength = locStats.get(i).getTimestamp() - initTime;
				totalTime += splitLength;
				String timeStr = String.valueOf(splitLength);
				timeStr = MainRunFragment.formatTime(timeStr);
				splitStats.add(new SplitStat(km, timeStr));
				initTime = locStats.get(i).getTimestamp();
				prevLat = currLat;
				prevLng = currLng;
			}
		}
		if(distance > 0) {
			int lastIndex = locStats.size() - 1;
			long splitLength = locStats.get(lastIndex).getTimestamp() - initTime;
			long time = Long.valueOf(stat.getTime());
			totalTime += splitLength;
			splitLength += (time - totalTime);
			String timeStr = String.valueOf(splitLength);
			timeStr = MainRunFragment.formatTime(timeStr);
			DecimalFormat df = new DecimalFormat("##.##");
			String formatted = df.format(distance/1000);
			splitStats.add(new SplitStat(Double.valueOf(formatted), timeStr));
		}
		
		return splitStats;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		db.closeDB();
	}
	
	private class SplitStat {
		
		double km;
		String split;
		
		public SplitStat(double km, String split) {
			this.km = km;
			this.split = split;
		}
		
		public double getkm() {
			return this.km;
		}
		
		public String getSplit() {
			return this.split;
		}
	}
	
	private class SplitAdapter extends ArrayAdapter<SplitStat> {

		private LayoutInflater inflater;
		public SplitAdapter(Context context, int textViewResourceId,	List<SplitStat> objects) {
			super(context, textViewResourceId, objects);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View layout = convertView;
			SplitStat stat = getItem(position);

			//Inflate the view
			if(convertView==null)
			{
				layout = inflater.inflate(R.layout.splitlistview, null);
			}

//			if(position % 2 == 0) {
//				layout.setBackgroundResource(R.drawable.listselector);
//			}
//			else {
//				layout.setBackgroundResource(R.drawable.listselector2);
//			}

			TextView kmText = (TextView) layout.findViewById(R.id.km);
			TextView splitText = (TextView) layout.findViewById(R.id.splitTime);

			kmText.setText(String.valueOf(stat.getkm()));
			splitText.setText(stat.getSplit());

			return layout;
		}
	}

}
