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

public class BuyerOrderedListFragment extends Fragment {
    private final String TAG = "BuyerOrderedListFragmen";
    private String BUYERNAME = null;
    private DatabaseReference databaseReference;
    private ArrayList<Order> orderArrayList;

    View view;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        BUYERNAME = getArguments().getString("NAME");
        Log.i(TAG, "Message : " + BUYERNAME);

        view = layoutInflater.inflate(R.layout.fragment_buyer_ordered_list, container, false);
        listView = (ListView)view.findViewById(R.id.listview_buyer_ordered_list);
        orderArrayList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Order");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange - DataSnapshot");
                orderArrayList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Order order = dataSnapshot1.getValue(Order.class);
                    if (order.getBuyerName().equals(BUYERNAME)) {
                        orderArrayList.add(order);
                    }
                }
                BuyerOrderListAdapter buyerOrderListAdapter = new BuyerOrderListAdapter(getActivity(), orderArrayList);
                listView.setAdapter(buyerOrderListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

}
