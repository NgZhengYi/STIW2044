package food_system.project.stiw2044.com.stiw2044_project;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SellerSalesListAdapter extends ArrayAdapter<Order> {
    private Activity context;
    private ArrayList<Order> orderArrayList;

    public SellerSalesListAdapter(Activity context, ArrayList<Order> orderArrayList) {
        super(context, R.layout.custom_listview_seller_saleslist, orderArrayList);
        this.context = context;
        this.orderArrayList = orderArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View orderView = layoutInflater.inflate(R.layout.custom_listview_seller_saleslist, null, true);

        TextView TV1 = (TextView)orderView.findViewById(R.id.cus_sellerTV_orderID);
        TextView TV2 = (TextView)orderView.findViewById(R.id.cus_sellerTV_orderTime);
        TextView TV3 = (TextView)orderView.findViewById(R.id.cus_sellerTV_cusName);
        TextView TV4 = (TextView)orderView.findViewById(R.id.cus_sellerTV_foodName);

        Order order = orderArrayList.get(position);
        TV1.setText(order.getOrderID());
        TV2.setText(order.getOrderTime());
        TV3.setText(order.getBuyerName());
        TV4.setText(order.getFoodName());

        return orderView;
    }
}
