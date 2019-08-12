package food_system.project.stiw2044.com.stiw2044_project;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SellerHomeFragment extends Fragment {
    private final String TAG = "SellerHomeFragment";

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View view = layoutInflater.inflate(R.layout.fragment_seller_home, container, false);

        return view;
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        super.onDestroyView();
    }
}
