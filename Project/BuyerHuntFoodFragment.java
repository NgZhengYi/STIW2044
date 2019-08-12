package food_system.project.stiw2044.com.stiw2044_project;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BuyerHuntFoodFragment extends Fragment implements AdapterView.OnItemLongClickListener {
    private final String TAG = "BuyerHuntFoodFragment";
    private String BUYERNAME = null;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ArrayList<Food>foodArrayList;

    View view;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        BUYERNAME = getArguments().getString("NAME");
        Log.i(TAG, "Message : " + BUYERNAME);
        view = layoutInflater.inflate(R.layout.fragment_buyer_shopping, container, false);

        listView = (ListView)view.findViewById(R.id.listview_buyer_shopping);
        listView.setOnItemLongClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Food");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        foodArrayList = new ArrayList<>();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
        storageReference = firebaseStorage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Food");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange - DataSnapshot");
                foodArrayList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Food food = dataSnapshot1.getValue(Food.class);
                        foodArrayList.add(food);
                }
                BuyerFoodListAdapter buyerFoodListAdapter = new BuyerFoodListAdapter(getActivity(), foodArrayList);
                listView.setAdapter(buyerFoodListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "List View Long Click : " + position);
        orderFood(position);
        return false;
    }

    public void orderFood(int position){
        Log.i(TAG, "Order Food");
        final String selectedFOODID = foodArrayList.get(position).getFoodID();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Order " + foodArrayList.get(position).getFoodName() + " ?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Order Food Dialog : Cancel");
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Order Food Dialog : Yes");
                        Food food = foodArrayList.get(position);
                        sendOrder(food.getFoodID(), food.getFoodOwnerName(), food.getFoodName());
                        Log.i(TAG, "Ordered : "+foodArrayList.get(position).getFoodID() + " / " + foodArrayList.get(position).getFoodName());
                        Toast.makeText(getActivity(), foodArrayList.get(position).getFoodName() + " Ordered", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void sendOrder(String FOODID, String FOODSELLERNAME, String FOODNAME){
        int randomID = (int) Math.floor(Math.random() * 900000000) + 100000000;
        databaseReference = FirebaseDatabase.getInstance().getReference("Order");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String orderTime = simpleDateFormat.format(calendar.getTime());
        Log.i(TAG, "Current Time : " + orderTime);

        databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(String.valueOf(randomID));
        final Order order = new Order(String.valueOf(randomID), orderTime, BUYERNAME, FOODID, FOODSELLERNAME, FOODNAME);
        databaseReference.setValue(order);
        Log.i(TAG, "Order Uploaded : " + String.valueOf(randomID));
    }
}
