package food_system.project.stiw2044.com.stiw2044_project;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BuyerAboutFragment extends Fragment {
    private final String TAG = "BuyerAboutFragment";

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View view = layoutInflater.inflate(R.layout.fragment_about_app, container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    }
}
