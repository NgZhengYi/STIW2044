package food_system.project.stiw2044.com.stiw2044_project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SellerShopFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
        View.OnClickListener{
    private final String TAG = "SellerShopFragment";
    private String OWNERNAME = null;
    View view;
    private ArrayList<Food> foodArrayList;

    private ImageView IV;
    private EditText ET1, ET2, ET3;
    private Button addBTN, updateBTN, searchBTN, clearBTN;
    private ListView listView;

    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        OWNERNAME = getArguments().getString("NAME");
        Log.i(TAG, "Message : " + OWNERNAME);
        view = layoutInflater.inflate(R.layout.fragment_seller_shop, container, false);

        IV = (ImageView)view.findViewById(R.id.image_View_seller);
        ET1 = (EditText) view.findViewById(R.id.FoodID_ET);
        ET2 = (EditText) view.findViewById(R.id.FoodName_ET);
        ET3 = (EditText) view.findViewById(R.id.FoodPrice_ET);
        addBTN = (Button)view.findViewById(R.id.seller_shop_add);
        updateBTN = (Button)view.findViewById(R.id.seller_shop_update);
        searchBTN = (Button)view.findViewById(R.id.seller_shop_search);
        clearBTN = (Button)view.findViewById(R.id.seller_shop_view);
        listView = (ListView)view.findViewById(R.id.listview_seller_shop);

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        addBTN.setOnClickListener(this);
        updateBTN.setOnClickListener(this);
        searchBTN.setOnClickListener(this);
        clearBTN.setOnClickListener(this);
        IV.setOnClickListener(this);

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
                    if(food.getFoodOwnerName().equals(OWNERNAME)){
                        foodArrayList.add(food);
                    }
                }
                if (getActivity() != null) {
                    Log.i(TAG, "Activity Not NULL");
                    SellerFoodListAdapter sellerFoodListAdapter = new SellerFoodListAdapter(getActivity(), foodArrayList);
                    listView.setAdapter(sellerFoodListAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestoryView");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Food food = foodArrayList.get(position);
        ET1.setText(food.getFoodID());
        ET2.setText(food.getFoodName());
        ET3.setText(food.getFoodPrice());
        storageReference.child("Food/" + food.getFoodID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext()).load(uri).resize(300, 400).into(IV);
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        deleteFood(position);
        return false;
    }

    public void addFood(){
        Log.i(TAG, "Add Food");
        if(IV.getDrawable() == null || ET1.getText().toString().trim().length() == 0 || ET2.getText().toString().trim().length() == 0 ||
                ET3.getText().toString().trim().length() == 0){
            Log.i(TAG, "Incomplete Information to Add Food");
            Toast.makeText(getActivity(), "Incomplete Information", Toast.LENGTH_SHORT).show();
            return;
        }

        final String FOODID = ET1.getText().toString();
        final String FOODNAME = ET2.getText().toString();
        final String FOODPRICE = ET3.getText().toString();

        databaseReference = FirebaseDatabase.getInstance().getReference("Food");
        final Query query = databaseReference.orderByChild("foodID").equalTo(FOODID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Log.i(TAG, FOODID + " already exist in firebase");
                    Toast.makeText(getActivity(), "Dulpicate Food ID", Toast.LENGTH_LONG).show();
                    query.removeEventListener(this);
                    return;
                }
                query.removeEventListener(this);
                databaseReference = FirebaseDatabase.getInstance().getReference("Food").child(FOODID);
                Food food = new Food(OWNERNAME, FOODID, FOODNAME, FOODPRICE);
                databaseReference.setValue(food);
                try {
                    saveImage(FOODID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadImage(FOODID);
                clearText();
                Log.i(TAG, "New Food Added");
                Toast.makeText(getActivity(), "New Food Successfully Added", Toast.LENGTH_LONG).show();
                storageReference = firebaseStorage.getReference();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });
    }

    public void deleteFood(int position){
        Log.i(TAG, "Delete Food");
        final String selectedFOODID = foodArrayList.get(position).getFoodID();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Remove " + foodArrayList.get(position).getFoodName() + " ?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Delete Dialog : Cancel");
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Delete Dialog : Yes");
                        Food food = foodArrayList.get(position);
                        databaseReference = FirebaseDatabase.getInstance().getReference("Food").child(food.getFoodID());
                        databaseReference.removeValue();
                        deleteImage(food.getFoodID());
                        clearText();
                        Log.i(TAG, "Deleted : "+foodArrayList.get(position).getFoodID() + " / " + foodArrayList.get(position).getFoodName());
                        Toast.makeText(getActivity(), foodArrayList.get(position).getFoodName() + " DELETED", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void updateFood() throws IOException {
        Log.i(TAG, "Update Food");
        final String FOODID = ET1.getText().toString();
        final String FOODNAME = ET2.getText().toString();
        final String FOODPRICE = ET3.getText().toString();

        databaseReference = FirebaseDatabase.getInstance().getReference("Food").child(FOODID);
        Food food = new Food(OWNERNAME, FOODID, FOODNAME, FOODPRICE);
        databaseReference.setValue(food);
        deleteImage(food.getFoodID());
        saveImage(food.getFoodID());
        uploadImage(food.getFoodID());
        Toast.makeText(getActivity(), "FOOD UPDATED", Toast.LENGTH_LONG).show();
        storageReference = firebaseStorage.getReference();
    }

    public void promptSearchFood(){
        Log.i(TAG, "Prompt Search Food");
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.prompt_search_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(promptView);

        final EditText input = (EditText)promptView.findViewById(R.id.inputET);
        builder.setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Cancel Propmpt Search Dialog");
                        dialog.cancel();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Search For : "+ input);
                        searchFood(input.getText().toString());
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void searchFood(String inputText){
        Log.i(TAG, "Search Food");
        databaseReference = FirebaseDatabase.getInstance().getReference("Food");
        storageReference = firebaseStorage.getReference();
        Query query = databaseReference.orderByChild("Food").equalTo(inputText);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Food food = dataSnapshot1.getValue(Food.class);
                    ET1.setText(food.getFoodID());
                    ET2.setText(food.getFoodName());
                    ET3.setText(food.getFoodPrice());

                    storageReference.child("Food/" + food.getFoodID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(getContext()).load(uri).resize(300, 400).into(IV);
                        }
                    });
                    Toast.makeText(getActivity(), "Search Found !", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Search Found : "+inputText);
                    return;
                }
                Toast.makeText(getActivity(), "Search NOT Found !", Toast.LENGTH_LONG).show();
                Log.i(TAG, "Search NOT Found : "+inputText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void saveImage(final String FOODID) throws IOException {
        Log.i(TAG, "SaveImage : "+FOODID);
        BitmapDrawable bitmapDrawable = (BitmapDrawable)IV.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ContextWrapper contextWrapper = new ContextWrapper(getActivity());

        File fileDirectory = contextWrapper.getDir("Food", Context.MODE_PRIVATE);
        if(!fileDirectory.exists()){
            fileDirectory.mkdir();
        } else {
            File filePath = new File(fileDirectory, "/"+FOODID+".jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        }
    }

    public String imageLocation(String FOODID){
        ContextWrapper contextWrapper = new ContextWrapper(getActivity());
        File fileDirectory = contextWrapper.getDir("Food", Context.MODE_PRIVATE);
        File filePath = new File(fileDirectory, "/"+FOODID+".jpg");
        return filePath.toString();
    }

    public void uploadImage(final String FOODID) {
        Log.i(TAG, "UploadImage : "+FOODID);
        if (imageLocation(FOODID) != null) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("UPLOAD IMAGE");
            progressDialog.show();

            storageReference = storageReference.child("Food/" + FOODID);
            storageReference.putFile(Uri.fromFile(new File(imageLocation(FOODID))))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Log.i("TAG", "Image "+FOODID+" Upload Success");
                            Toast.makeText(getActivity(), "UPLOAD SUCCESS", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Log.i("TAG", "Image "+FOODID+" Upload Failed");
                            Toast.makeText(getActivity(), "UPLOAD FAILED", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.i(TAG, "IMAGE PATH NOT FOUND");
            Toast.makeText(getActivity(), "IMAGE PATH NOT FOUND", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteImage(String FOODID) {
        ContextWrapper contextWrapper = new ContextWrapper(getActivity());
        File fileDirectory = contextWrapper.getDir("Food", Context.MODE_PRIVATE);
        File filePath = new File(fileDirectory, "/"+FOODID+".jpg");
        if (filePath.exists()){
            filePath.delete();
        }

        storageReference = storageReference.child("Food/" + FOODID);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Delete Image Success : "+FOODID);
            }
        });
        storageReference = firebaseStorage.getReference();
    }

    public void clearText(){
        IV.setImageResource(0);
        IV.setBackgroundColor(Color.parseColor("#800000"));
        ET1.setText(null);
        ET2.setText(null);
        ET3.setText(null);
    }

    @Override
    public void onClick(View view) {
        if (view == addBTN){
            Log.i(TAG, "Click : Add Food");
            addFood();
        } else if (view == updateBTN){
            Log.i(TAG, "Click : Update Food");
            try {
                updateFood();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (view == searchBTN){
            Log.i(TAG, "Click : Search Food");
            promptSearchFood();
        } else if (view == IV){
            Log.i(TAG, "Click : Select Image");
            selectImage();
        } else if (view == clearBTN){
            Log.i(TAG, "Click : Clear View");
            clearText();
        }
    }

    //-------------------------------- SelectImage from Galary --------------------------------
    public void selectImage(){
        Log.i(TAG, "Select Image From Galary");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int reqWidth = 300, reqHeight = 400;
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && null != data){
            Uri uri = data.getData();
            if(uri != null){
                try {
                    Log.i(TAG, "Select Image : onActivityResult");
                    IV.setImageBitmap(resizeBitmap(uri, reqWidth, reqHeight));
                    IV.buildDrawingCache();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Resize Bitmap onActivityResult
    protected Bitmap resizeBitmap(Uri uri, int reqWidth, int reqHeight) throws IOException {
        Log.i(TAG, "Resize Bitmap");
        //InputStream inputStream = this.getContentResolver().openInputStream(uri);
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;
        options1.inDither = true; //optional
        options1.inPreferredConfig = Bitmap.Config.ARGB_8888; //optional
        BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri),null, options1);
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
            return BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri), null, options2);
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
