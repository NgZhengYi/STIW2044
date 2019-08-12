package food_system.project.stiw2044.com.stiw2044_project;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TabFragmentSignUp extends Fragment implements View.OnClickListener{
    private final String TAG = "TabFragmentSignUp";
    private EditText emailET, passwordET, nameET, phoneET, addressET;
    private RadioGroup userRoleRG;
    private Button registerButton;
    private RadioButton radioButton;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference BuyerDatabaseReference;
    private DatabaseReference SellerDatabaseReference;
    View view;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        view = layoutInflater.inflate(R.layout.tab_fragment_signup, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        //--------------------------- GUI Declaration ---------------------------
        emailET = (EditText) view.findViewById(R.id.user_Email);
        passwordET = (EditText) view.findViewById(R.id.user_Password);
        nameET = (EditText) view.findViewById(R.id.user_Name);
        phoneET = (EditText) view.findViewById(R.id.user_Phone);
        addressET = (EditText) view.findViewById(R.id.user_Address);
        userRoleRG = (RadioGroup) view.findViewById(R.id.radioGroup);
        registerButton = (Button) view.findViewById(R.id.register_Button);

        registerButton.setOnClickListener(this);
        //-------------------------------------------    -------------------------------------------
        progressDialog = new ProgressDialog(getActivity());
        firebaseAuth = FirebaseAuth.getInstance();
        BuyerDatabaseReference = FirebaseDatabase.getInstance().getReference("Buyer");
        SellerDatabaseReference = FirebaseDatabase.getInstance().getReference("Seller");

        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == registerButton){
            Log.i(TAG, "Button Click : Register");
            registerAccount();
        }
    }

    public void registerAccount(){
        int RadioButtonID = userRoleRG.getCheckedRadioButtonId();
        radioButton = (RadioButton) view.findViewById(RadioButtonID);

        final String Email = emailET.getText().toString();
        final String Password = passwordET.getText().toString();
        final String Name = nameET.getText().toString();
        final String Phone = phoneET.getText().toString();
        final String Address = addressET.getText().toString();
        final String Role = radioButton.getText().toString();

        //Check Incomplete User Register Detail
        if(TextUtils.isEmpty(Email) || !Email.contains("@"))
            emailET.setError("Invalid Email");

        if(Password.length() < 6)
            passwordET.setError("Invalid Password Length");

        if(TextUtils.isEmpty(Name))
            nameET.setError("Incomplete Name");

        if(TextUtils.isEmpty(Phone))
            phoneET.setError("Incomplete Phone Number");

        if(TextUtils.isEmpty(Address))
            addressET.setError("Incomplete Address");

        if(userRoleRG.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getActivity(), "Please Select Your Role", Toast.LENGTH_SHORT).show();
            return;
        }
        //--------------------------------------------------
        progressDialog.setMessage("Registering Your Account");
        progressDialog.show();

        if(Role.equals("Buyer"))
            registerBuyer(Email, Password, Name, Phone, Address, Role);
        else if (Role.equals("Seller"))
            registerSeller(Email, Password, Name, Phone, Address, Role);
    }

    public void registerBuyer(String buyerEmail, String buyerPassword, String buyerName, String buyerPhone, String buyerAddress, String yourRole){
        //Detect Dulpicate Buyer's Username
        BuyerDatabaseReference = FirebaseDatabase.getInstance().getReference("Buyer").child(buyerName);
        BuyerDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.i(TAG, "Username : " + buyerName + " Had Already Exist");
                    Toast.makeText(getActivity(), "Username Exist !", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });
        //Move on to Register Buyer Account
        firebaseAuth.createUserWithEmailAndPassword(buyerEmail, buyerPassword)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            Log.i(TAG, "Buyer FirebaseAuth Register : Fail");
                            Toast.makeText(getActivity(), "Could Not Register Your Account", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Log.i(TAG, "Buyer FirebaseAuth Register : Success");
                            BuyerDatabaseReference = FirebaseDatabase.getInstance().getReference("Buyer").child(buyerName);
                            final UserInfo userInfo = new UserInfo(buyerEmail, buyerPassword, buyerName, buyerPhone, buyerAddress, yourRole);
                            BuyerDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists()) {
                                        BuyerDatabaseReference.setValue(userInfo);
                                        clearText();
                                        Log.i(TAG, "FirebaseDatabase Add Buyer : Success");
                                        Toast.makeText(getActivity(), "Register Complete", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.w(TAG, "Database Error");
                                }
                            });
                        }
                    }});

    }

    public void registerSeller(String sellerEmail, String sellerPassword, String sellerName, String sellerPhone, String sellerAddress, String yourRole){
        //Detect Dulpicate Seller's Username
        SellerDatabaseReference = FirebaseDatabase.getInstance().getReference("Seller").child(sellerName);
        SellerDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.i(TAG, "Username : " + sellerName + " Had Already Exist");
                    Toast.makeText(getActivity(), "Username Exist !", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });
        //Move on to Register Seller Account
        firebaseAuth.createUserWithEmailAndPassword(sellerEmail, sellerPassword)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            Log.i(TAG, "Seller FirebaseAuth Register : Fail");
                            Toast.makeText(getActivity(), "Could Not Register Your Account", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Log.i(TAG, "Seller FirebaseAuth Register : Success");
                            SellerDatabaseReference = FirebaseDatabase.getInstance().getReference("Seller").child(sellerName);
                            final UserInfo userInfo = new UserInfo(sellerEmail, sellerPassword, sellerName, sellerPhone, sellerAddress, yourRole);
                            SellerDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists()) {
                                        SellerDatabaseReference.setValue(userInfo);
                                        clearText();
                                        Log.w(TAG, "FirebaseDatabase Add Seller : Success");
                                        Toast.makeText(getActivity(), "Register Complete", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.w(TAG, "Database Error");
                                }
                            });
                        }
                    }});
    }

    public void clearText(){
        emailET.setText(null);
        passwordET.setText(null);
        nameET.setText(null);
        phoneET.setText(null);
        addressET.setText(null);
        userRoleRG.clearCheck();
    }

}
