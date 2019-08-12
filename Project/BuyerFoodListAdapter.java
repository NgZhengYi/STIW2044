package food_system.project.stiw2044.com.stiw2044_project;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BuyerFoodListAdapter extends ArrayAdapter<Food> {
    private Activity context;
    private ArrayList<Food> foodArrayList;
    private StorageReference storageReference;

    public BuyerFoodListAdapter(Activity context, ArrayList<Food> foodArrayList) {
        super(context, R.layout.custom_listview_buyer_shopping, foodArrayList);
        this.context = context;
        this.foodArrayList = foodArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View arrayView = layoutInflater.inflate(R.layout.custom_listview_buyer_shopping, null, true);
        storageReference = FirebaseStorage.getInstance().getReference();

        TextView foodSeller_TV = (TextView) arrayView.findViewById(R.id.custom_LV_buyer_shopping_ET1);
        TextView foodName_TV = (TextView) arrayView.findViewById(R.id.custom_LV_buyer_shopping_ET2);
        TextView foodPrice_TV = (TextView) arrayView.findViewById(R.id.custom_LV_buyer_shopping_ET3);
        ImageView foodImage = (ImageView) arrayView.findViewById(R.id.custom_LV_buyer_food_Image);

        Food food = foodArrayList.get(position);
        foodSeller_TV.setText(food.getFoodOwnerName());
        foodName_TV.setText(food.getFoodName());
        foodPrice_TV.setText(food.getFoodPrice());
        storageReference.child("Food/"+food.getFoodID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext()).load(uri).resize(300, 400).into(foodImage);
            }
        });

        return arrayView;
    }
}
