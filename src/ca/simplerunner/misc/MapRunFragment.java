package ca.simplerunner.misc;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import ca.simplerunner.R;
import ca.simplerunner.app.RunView;
import ca.simplerunner.database.Database;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * This class handles creating the Map view as shown
 * in the RunView Activity on the 'Map' tab
 * 
 * @author Abe Friesen
 * 
 */
public class MapRunFragment extends Fragment {

	Database db;
	RunStat stat;
	RunView parent;
	long runID;
	GoogleMap map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View myView = inflater.inflate(R.layout.mapview, container, false);
		parent = (RunView) getActivity();
		this.runID = parent.getRunID();
		db = new Database(parent);
		stat = db.getRunStat(runID);
		setUpMapIfNeeded();

		return myView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (map != null)
			setUpMap();
		else
			setUpMapIfNeeded();
	}

	/*
	 * Plot the run on the Map
	 */
	public void setUpMap() {
		ArrayList<LocationStat> locStats = db.getRunCoordinates(runID);
		ArrayList<LatLng> coords = new ArrayList<LatLng>();
		for(LocationStat ls : locStats) {
			coords.add(new LatLng(ls.getLat(), ls.getLng()));
		}
		PolylineOptions opts = new PolylineOptions();
		opts.width(3);
//		opts.color(getResources().getColor(R.color.blue2));
		opts.addAll(coords);
		int mid = coords.size()/2;
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(coords.get(mid), 13));
		map.addPolyline(opts);
	}

	/*
	 * Try to instantiate the map
	 */
	public void tryGetMap() {
		map = ((SupportMapFragment) getActivity().getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
	}

	/*
	 * Set up the map if possible
	 */
	public void setUpMapIfNeeded() {
		if (map == null) {
			tryGetMap();
			if (map != null) {
				setUpMap();
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		db.closeDB();
		if(!parent.isFinishing())
			parent.removeMapFragment();
		map = null;
	}
}
