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

public class SellerFoodListAdapter extends ArrayAdapter<Food> {
    private Activity context;
    private ArrayList<Food> foodArrayList;
    private StorageReference storageReference;

    public SellerFoodListAdapter(Activity context, ArrayList<Food> foodArrayList) {
        super(context, R.layout.custom_listview_seller_shop, foodArrayList);
        this.context = context;
        this.foodArrayList = foodArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View arrayView = layoutInflater.inflate(R.layout.custom_listview_seller_shop, null, true);
        storageReference = FirebaseStorage.getInstance().getReference();

        TextView foodID_TV = (TextView) arrayView.findViewById(R.id.custom_LV_seller_shop_ET1);
        TextView foodName_TV = (TextView) arrayView.findViewById(R.id.custom_LV_seller_shop_ET2);
        TextView foodPrice_TV = (TextView) arrayView.findViewById(R.id.custom_LV_seller_shop_ET3);
        ImageView foodImage = (ImageView) arrayView.findViewById(R.id.custom_LV_seller_shop_Image);

        Food food = foodArrayList.get(position);
        foodID_TV.setText(food.getFoodID());
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
