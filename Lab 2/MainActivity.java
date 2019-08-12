import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList <Item> list = new ArrayList<>();
    Item item; //oop
    TextView totalTextPrice;
    ListView lv1;
    ShoppingListAdapter adapter; //custom adapter class
    DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText itemNameET = (EditText)findViewById(R.id.editName);
        final EditText itemPriceET = (EditText)findViewById(R.id.editPrice);
        Button addItemButton = (Button)findViewById(R.id.buttonAdd);
        Button clearTextButton = (Button)findViewById(R.id.buttonCancel);
        Button clearListTextButton = (Button)findViewById(R.id.buttonClearList);
        lv1 = (ListView)findViewById(R.id.ListView_01);
        totalTextPrice = (TextView) findViewById(R.id.textTotalPrice);

        adapter = new ShoppingListAdapter(this,R.layout.custom_list,list);
        lv1.setAdapter(adapter);
        registerForContextMenu(lv1); //register listview to perform contextmenu

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName;
                double itemPrice;

                if(itemNameET.getText().toString().equals("") || itemPriceET.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Incomplete Details", Toast.LENGTH_SHORT).show();
                    return;
                }

                itemName = itemNameET.getText().toString();
                itemPrice = Double.parseDouble(itemPriceET.getText().toString());

                item = new Item(itemName, itemPrice);
                list.add(item);
                lv1.setAdapter(adapter); //refresh listview

                calculateTotalPrice(totalTextPrice);

                itemNameET.setText(null);
                itemPriceET.setText(null);
            }
        });

        clearTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemNameET.setText(null);
                itemPriceET.setText(null);
            }
        });

        clearListTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                lv1.setAdapter(adapter);
                calculateTotalPrice(totalTextPrice);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Choose Your Option");
        getMenuInflater().inflate(R.menu.remove_menu, menu); //optionmenu.xml
        //groupId, itemId, order, title
        //menu.add(0, v.getId(), 0, "Remove");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        //if(item.getTitle() == "Remove")
        switch (item.getItemId()){
            case R.id.removeOption: //menu option id

                Toast.makeText(getApplicationContext(), "One Item Removed", Toast.LENGTH_SHORT).show();
                list.remove(index);
                lv1.setAdapter(adapter); //refresh listview
                calculateTotalPrice(totalTextPrice);
                return  true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void calculateTotalPrice(TextView totalTextPrice){
        double totalPrice = 0.0;
        for(int i = 0; i < list.size(); i++){
            totalPrice += list.get(i).getpPrice();
        }
        totalTextPrice.setText(String.valueOf(df.format(totalPrice)));
    }
}
