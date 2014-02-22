package ca.simplerunner.misc;

import ca.simplerunner.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SplitRunFragment extends Fragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
 
        return inflater.inflate(R.layout.splitview, container, false);
    }
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

}
