package com.example.user.smartphonemenu;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    EditText editText_Name, editText_Price, editText_Info;
    ImageView imageView;
    Button button_add, button_update, button_search, button_view;
    ListView listView;

    SQLiteDatabase sqLiteDatabase;
    private ArrayList<HashMap<String, String>> smartphoneList;
    ListAdapter adapter;
    static String passValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.image_View);
        editText_Name = (EditText)findViewById(R.id.editText1);
        editText_Price = (EditText)findViewById(R.id.editText2);
        editText_Info = (EditText)findViewById(R.id.editText3);
        listView = (ListView)findViewById(R.id.list_view);
        button_add = (Button)findViewById(R.id.button1);
        button_update = (Button)findViewById(R.id.button2);
        button_search = (Button)findViewById(R.id.button3);
        button_view = (Button)findViewById(R.id.button4);

        smartphoneList = new ArrayList<>();
        createDatabase();
        loadListData();
        registerForContextMenu(listView); //register listview to open context menu

        //-------------------------------- Button Fucntion --------------------------------
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int randomID = (int) Math.floor(Math.random() * 900000000) + 100000000;
                try {
                    insertData(String.valueOf(randomID));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchData();
            }
        });

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        button_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadListData();
                clearView();
            }
        });
        //-------------------------------- Button Function --------------------------------

       //-------------------------------- ImageView Funciton --------------------------------
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        //-------------------------------- ImageView Function --------------------------------
    }
    //-------------------------------- Closing onCreate Method --------------------------------

    //-------------------------------- Options Menu --------------------------------
    @Override // Open up selection of OptionMenu
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Choose the Option");
        getMenuInflater().inflate(R.menu.option_menu, menu); //refer to option_menu.xml
    }

    @Override // When OptionMenu is selected
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = adapterContextMenuInfo.position;

        switch (item.getItemId()){
            case R.id.removeSmartphone:
                removeData(index);                ;
                return true;
            case R.id.updateSmartphone:
                setText(index);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    //-------------------------------- Options Menu --------------------------------

    //-------------------------------- Database Function --------------------------------
    public void createDatabase(){
        sqLiteDatabase = this. openOrCreateDatabase("smartphonedb", MODE_PRIVATE, null);
        String sqlcode = "CREATE TABLE IF NOT EXISTS smartphone" +
                "(PHONEID VARCHAR, PHONENAME VARCHAR, PHONEPRICE VARCHAR, PHONEINFO VARCHAR, PRIMARY KEY (PHONEID));";
        sqLiteDatabase.execSQL(sqlcode);
    }

    public void insertData(String randomID)throws IOException{
        if (imageView.getDrawable() == null || editText_Name.getText().toString().trim().length() == 0 ||
                editText_Price.getText().toString().trim().length() == 0 || editText_Info.getText().toString().trim().length() == 0){
            Toast.makeText(this, "Incomplete Information", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneID = String.valueOf(randomID);
        String phoneName = editText_Name.getText().toString();
        String phonePrice = editText_Price.getText().toString();
        String phoneInfo = editText_Info.getText().toString();

        String sqlcode = "INSERT INTO smartphone (PHONEID,PHONENAME,PHONEPRICE,PHONEINFO) VALUES ('" +
                phoneID + "','" + phoneName + "','" + phonePrice + "','" + phoneInfo + "');";
        sqLiteDatabase.execSQL(sqlcode);
        saveImage(phoneID);
        Toast.makeText(this,phoneName +" sucessfully added into database", Toast.LENGTH_LONG).show();
        loadListData();
        clearView();
    }

    public void removeData(int index){
        // Creating a new alert dialog to confirm the delete
        String item = listView.getItemAtPosition(index).toString();
        String [] array = item.split("\\s*,\\s*");
        final String phoneID = array[3].replace("id=", "");
        final String phoneName = array[0].replace("{name=", "");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Are you sure to delete " + phoneName + " ?");
        // Selection 1
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sqlcode = "DELETE FROM smartphone WHERE PHONEID = '" + phoneID + "'";
                sqLiteDatabase.execSQL(sqlcode);
                deleteImage(phoneID);
                loadListData();
                Toast.makeText(getApplicationContext(), "Smartphone Removed", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close the dialog
                dialog.cancel();
            }
        }).show();
    }

    public void updateData() throws IOException {
        String phoneId = passValue; //= imageView.getTransitionName();
        String phoneName = editText_Name.getText().toString();
        String phonePrice = editText_Price.getText().toString();
        String phoneInfo = editText_Info.getText().toString();

        String sqlcode = "SELECT * FROM smartphone WHERE PHONEID = '" + phoneId + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(sqlcode, null);

        if(cursor.getCount() > 0){
            String sqlupdatecode = "UPDATE smartphone SET PHONENAME = '" + phoneName + "' , PHONEPRICE = '" + phonePrice + "' ," +
                    "PHONEINFO = '" + phoneInfo + "' WHERE PHONEID = '" + phoneId + "'";
            sqLiteDatabase.execSQL(sqlupdatecode);
            Toast.makeText(this, "Update Sucess", Toast.LENGTH_SHORT).show();
            deleteImage(phoneId);
            saveImage(phoneId);
            loadListData();
        } else {
            loadListData();
            Toast.makeText(this, "Update Fail", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchData(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Search");
        alertDialog.setMessage("Input Search Keyword");

        // Set an EditText view to get user input
        final EditText input = new EditText(MainActivity.this);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String keyword = input.getText().toString();

                //do what you want with your result
                String sqlcode = "SELECT * FROM smartphone WHERE PHONENAME LIKE '%" + keyword + "%'";
                sqLiteDatabase.rawQuery(sqlcode, null);
                Cursor cursor = sqLiteDatabase.rawQuery(sqlcode, null);
                smartphoneList.clear();

                if(cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        String phone_id = cursor.getString(cursor.getColumnIndex("PHONEID"));
                        String phone_name = cursor.getString(cursor.getColumnIndex("PHONENAME"));
                        String phone_price = cursor.getString(cursor.getColumnIndex("PHONEPRICE"));
                        String phone_info = cursor.getString(cursor.getColumnIndex("PHONEINFO"));

                        HashMap<String, String> smartphoneHashMap = new HashMap<>();
                        smartphoneHashMap.put("id", phone_id);
                        smartphoneHashMap.put("image", imageLocation(phone_id));
                        smartphoneHashMap.put("name", phone_name);
                        smartphoneHashMap.put("price", phone_price);
                        smartphoneHashMap.put("info", phone_info);
                        smartphoneList.add(smartphoneHashMap);
                        cursor.moveToNext();
                    }
                    adapter = new SimpleAdapter(
                            MainActivity.this, smartphoneList,
                            R.layout.custom_listview, new String[] {"image", "name", "price", "info"},
                            new int[] {R.id.imageView, R.id.textView1, R.id.textView2, R.id.textView3}
                    );
                    listView.setAdapter(adapter);
                } else {
                    loadListData();
                    Toast.makeText(getApplicationContext(),"No Data Found", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled
                dialog.cancel();
            }
        }).show();
    }
    //-------------------------------- End Database --------------------------------

    //-------------------------------- Methods --------------------------------
    public void setText(int index){
        passValue = null;
        String item = listView.getItemAtPosition(index).toString();
        String [] array = item.split("\\s*,\\s*");
        editText_Name.setText(array[0].replace("{name=", ""));
        editText_Price.setText(array[1].replace("price=", ""));
        editText_Info.setText(array[2].replace("info=", "").replace("}",""));
        loadImage(array[3].replace("id=",""));
        Toast.makeText(getApplicationContext(), array[3].replace("id=", ""), Toast.LENGTH_SHORT).show();
        passValue = array[3].replace("id=","");
    }
    //-------------------------------- Methods --------------------------------

    //-------------------------------- Process Function --------------------------------
    public void loadListData(){
        String sqlcode = "SELECT * FROM smartphone";
        Cursor cursor = sqLiteDatabase.rawQuery(sqlcode, null);
        smartphoneList.clear();

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String phone_id = cursor.getString(cursor.getColumnIndex("PHONEID"));
                String phone_name = cursor.getString(cursor.getColumnIndex("PHONENAME"));
                String phone_price = cursor.getString(cursor.getColumnIndex("PHONEPRICE"));
                String phone_info = cursor.getString(cursor.getColumnIndex("PHONEINFO"));

                HashMap<String, String> smartphoneHashMap = new HashMap<>();
                smartphoneHashMap.put("id", phone_id);
                smartphoneHashMap.put("image", imageLocation(phone_id));
                smartphoneHashMap.put("name", phone_name);
                smartphoneHashMap.put("price", phone_price);
                smartphoneHashMap.put("info", phone_info);
                smartphoneList.add(smartphoneHashMap);
                cursor.moveToNext();
            }
            adapter = new SimpleAdapter(
                    MainActivity.this, smartphoneList,
                    R.layout.custom_listview, new String[] {"image", "name", "price", "info"},
                    new int[] {R.id.imageView, R.id.textView1, R.id.textView2, R.id.textView3}
            );
            listView.setAdapter(adapter);
        } else {
            listView.setAdapter(null);
            Toast.makeText(this, "List is Empty", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearView(){
        imageView.setImageResource(R.color.colorAccent);
        editText_Name.setText(null);
        editText_Price.setText(null);
        editText_Info.setText(null);
    }
    //-------------------------------- End --------------------------------

    //-------------------------------- Image Handle --------------------------------
    public void saveImage(String imageID)throws IOException{
        BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());

        File fileDirectory = contextWrapper.getDir("smartphone", Context.MODE_PRIVATE);
        if(!fileDirectory.exists()){
            fileDirectory.mkdir();
        } else {
            File filePath = new File(fileDirectory, "/"+imageID+".jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        }
    }

    public void loadImage(String imageId){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File fileDirectory = contextWrapper.getDir("smartphone", Context.MODE_PRIVATE);
        File filePath = new File(fileDirectory, "/"+imageId+".jpg");
        if(filePath.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
    }

    public void deleteImage(String matric){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File fileDirectory = contextWrapper.getDir("smartphone", Context.MODE_PRIVATE);
        File filePath = new File(fileDirectory, "/"+matric+".jpg");
        if (filePath.exists()){
            filePath.delete();
        }
    }

    public String imageLocation(String imageId){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File fileDirectory = contextWrapper.getDir("smartphone", Context.MODE_PRIVATE);
        File filePath = new File(fileDirectory, "/"+imageId+".jpg");
        return filePath.toString();
    }
    //-------------------------------- Image Handle --------------------------------

    //-------------------------------- SelectImage from Galary --------------------------------
    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int reqWidth = 300, reqHeight = 450;
        if(requestCode == 2 && resultCode == RESULT_OK && null != data){
            Uri uri = data.getData();
            if(uri != null){
                try {
                    imageView.setImageBitmap(resizeBitmap(uri, reqWidth, reqHeight));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Resize Bitmap onActivityResult
    protected Bitmap resizeBitmap(Uri uri, int reqWidth, int reqHeight) throws IOException {
        //InputStream inputStream = this.getContentResolver().openInputStream(uri);
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;
        options1.inDither = true; //optional
        options1.inPreferredConfig = Bitmap.Config.ARGB_8888; //optional
        BitmapFactory.decodeStream(getContentResolver().openInputStream(uri),null, options1);
        //inputStream.close();
        int width, height, inSampleSize = 1;
        width = options1.outWidth;
        height = options1.outHeight;

        if(height > reqHeight || width > reqWidth){
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float)height/(float)reqHeight);
            final int widthRatio = Math.round((float)width/(float)reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = inSampleSize;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options2);
        }
        return null;
    }

    //convert Bitmap to byteArray
    private byte [] convertImage (Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        //byte [] bitMapData = byteArrayOutputStream.toByteArray();
        return byteArrayOutputStream.toByteArray();
    }
    //-------------------------------- End of SelectImage --------------------------------
}
