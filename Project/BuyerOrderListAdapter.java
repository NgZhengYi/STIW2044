package food_system.project.stiw2044.com.stiw2044_project;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BuyerOrderListAdapter extends ArrayAdapter<Order> {
    private Activity context;
    private ArrayList<Order> orderArrayList;

    public BuyerOrderListAdapter(Activity context, ArrayList<Order> orderArrayList) {
        super(context, R.layout.custom_listview_buyer_orderlist, orderArrayList);
        this.context = context;
        this.orderArrayList = orderArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View orderView = layoutInflater.inflate(R.layout.custom_listview_buyer_orderlist, null, true);

        TextView TV1 = (TextView)orderView.findViewById(R.id.cus_buyerTV_orderID);
        TextView TV2 = (TextView)orderView.findViewById(R.id.cus_buyerTV_orderTime);
        TextView TV3 = (TextView)orderView.findViewById(R.id.cus_buyerTV_foodStallName);
        TextView TV4 = (TextView)orderView.findViewById(R.id.cus_buyerTV_foodName);

        Order order = orderArrayList.get(position);
        TV1.setText(order.getOrderID());
        TV2.setText(order.getOrderTime());
        TV3.setText(order.getFoodSellerName());
        TV4.setText(order.getFoodName());

        return orderView;
    }
}
