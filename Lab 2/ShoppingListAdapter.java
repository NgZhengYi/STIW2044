import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ShoppingListAdapter extends ArrayAdapter<Item>{
    private Context context;
    int resource;
    DecimalFormat df = new DecimalFormat("#0.00");

    public ShoppingListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Item> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //getting shopping list item information
        String itemName = getItem(position).getpName();
        double itemPrice = getItem(position).getpPrice();

        Item item = new Item (itemName, itemPrice);

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView tvName = (TextView)convertView.findViewById(R.id.tv1);
        TextView tvPrice = (TextView)convertView.findViewById(R.id.tv2);
        tvName.setText(itemName);
        tvPrice.setText(String.valueOf(df.format(itemPrice)));

        return convertView;
    }
}
