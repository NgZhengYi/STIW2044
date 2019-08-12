package food_system.project.stiw2044.com.stiw2044_project;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SellerSalesListFragment extends Fragment {
    private final String TAG = "SellerSalesListFragment";
    private String OWNERNAME = null;
    private DatabaseReference databaseReference;
    private ArrayList<Order> orderArrayList;

    View view;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        OWNERNAME = getArguments().getString("NAME");
        Log.i(TAG, "Message : " + OWNERNAME);

        view = layoutInflater.inflate(R.layout.fragment_seller_sales, container, false);
        listView = (ListView)view.findViewById(R.id.listview_seller_sales);
        orderArrayList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Order");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange - DataSnapshot");
                orderArrayList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Order order = dataSnapshot1.getValue(Order.class);
                    if (order.getFoodSellerName().equals(OWNERNAME)) {
                        orderArrayList.add(order);
                    }
                }
                SellerSalesListAdapter sellerSalesListAdapter = new SellerSalesListAdapter(getActivity(), orderArrayList);
                listView.setAdapter(sellerSalesListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

}
