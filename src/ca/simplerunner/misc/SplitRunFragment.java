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

/**
 * This class handles creating the Split view as shown
 * in the RunView Activity on the 'Split' tab
 * 
 * @author Abe Friesen
 * 
 */
public class SplitRunFragment extends Fragment {

	Database db;
	RunStat stat;
	long runID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View myView = inflater.inflate(R.layout.splitview, container, false);
		RunView parent = (RunView) getActivity();
		runID = parent.getRunID();
		db = new Database(parent);
		stat = db.getRunStat(runID);
		TextView dateHeader = (TextView) myView.findViewById(R.id.splitDateHeader);
		dateHeader.setText(stat.getDate());
		populateListView(myView);
		return myView;
	}

	/*
	 * Populate the list view with run split values
	 */
	private void populateListView(View myView) {
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
		ArrayList<SplitStat> splitStats = new ArrayList<SplitStat>();
		double prevLat = locStats.get(0).getLat();
		double prevLng = locStats.get(0).getLng();
		long prevTime = locStats.get(0).getTimestamp();
		long totalTime = 0;
		float[] results = new float[1];
		double distance = 0.0;
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
				long splitLength = locStats.get(i).getTimestamp() - prevTime;
				totalTime += splitLength;
				String timeStr = formatSplitTime(splitLength);
				splitStats.add(new SplitStat(km, timeStr));
				prevTime = locStats.get(i).getTimestamp();
			}
		}
		if(distance > 0) {
			int lastIndex = locStats.size() - 1;
			long splitLength = locStats.get(lastIndex).getTimestamp() - prevTime;
			totalTime += splitLength;
			long time = Long.valueOf(stat.getTime());
			splitLength += (time - totalTime);
			String timeStr = formatSplitTime(splitLength);
			double lastKM = formatLastSplit(distance) + km;
			splitStats.add(new SplitStat(lastKM, timeStr));
		}

		return splitStats;
	}

	/*
	 * Format the distance of the last split of the run
	 * into a 2 decimal double value
	 */
	private double formatLastSplit(double distance) {
		DecimalFormat df = new DecimalFormat("##.##");
		String formatted = df.format(distance/1000);
		return Double.valueOf(formatted);
	}

	/*
	 * Format the time of the split into a human readable
	 * time
	 */
	private String formatSplitTime(long splitLength) {
		String timeStr = String.valueOf(splitLength);
		return MainRunFragment.formatTime(timeStr);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		db.closeDB();
	}

	/**
	 * Private class for Split Statistics
	 */
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

	/**
	 * Custom Adapter used to populate a list view with Split Statistic values
	 */
	private class SplitAdapter extends ArrayAdapter<SplitStat> {

		private LayoutInflater inflater;
		public SplitAdapter(Context context, int textViewResourceId, List<SplitStat> objects) {
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

			TextView kmText = (TextView) layout.findViewById(R.id.km);
			TextView splitText = (TextView) layout.findViewById(R.id.splitTime);

			kmText.setText(String.valueOf(stat.getkm()));
			splitText.setText(stat.getSplit());

			return layout;
		}
	}
}
