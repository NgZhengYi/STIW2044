package food_system.project.stiw2044.com.stiw2044_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TabFragmentLogin extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        TextWatcher {
    View view;
    private EditText emailET, passwordET;
    private Button loginBTN, quitBTN;
    private CheckBox rememberCB, keep_loginCB;
    private RadioGroup userRoleRG;
    private RadioButton radioButton;

    private ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference BuyerDatabaseReference;
    private DatabaseReference SellerDatabaseReference;

    private static final String PREF_NAME = "prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private final String TAG = "TabFragmentLogin";

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        view = layoutInflater.inflate(R.layout.tab_fragment_login, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        //--------------------------- GUI Declaration ---------------------------
        emailET = (EditText) view.findViewById(R.id.emailEText);
        passwordET = (EditText) view.findViewById(R.id.passwordEText);
        loginBTN = (Button) view.findViewById(R.id.login_button);
        quitBTN = (Button) view.findViewById(R.id.cancel_button);
        rememberCB = (CheckBox) view.findViewById(R.id.remember_me);
        keep_loginCB = (CheckBox) view.findViewById(R.id.keep_signin);
        userRoleRG = (RadioGroup) view.findViewById(R.id.radioGroup);
        //-------------------------------------------    -------------------------------------------
        progressDialog = new ProgressDialog(getActivity());
        firebaseAuth = FirebaseAuth.getInstance();
        //--------------------------- Listener ---------------------------
        loginBTN.setOnClickListener(this);
        quitBTN.setOnClickListener(this);
        rememberCB.setOnCheckedChangeListener(this);
        keep_loginCB.setOnCheckedChangeListener(this);
        //-------------------------------------------    -------------------------------------------
        BuyerDatabaseReference = FirebaseDatabase.getInstance().getReference("Buyer");
        SellerDatabaseReference = FirebaseDatabase.getInstance().getReference("Seller");
        //--------------------------- SharedPreferences ---------------------------
        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getBoolean(KEY_REMEMBER, false)){
            rememberCB.setChecked(true);
            Log.i(TAG, "Remember Me : True");
        } else {
            rememberCB.setChecked(false);
            Log.i(TAG, "Remember Me : False");
        }
        emailET.setText(sharedPreferences.getString(KEY_USERNAME, ""));
        passwordET.setText(sharedPreferences.getString(KEY_PASSWORD, ""));

        return view;
    }//Closing onCreateView method

    private void userLogin() {
        int RadioButtonID = userRoleRG.getCheckedRadioButtonId();
        radioButton = (RadioButton) view.findViewById(RadioButtonID);
        final String email = emailET.getText().toString();
        final String password = passwordET.getText().toString();

        //Checking email and password are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "Please Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Please Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        if(userRoleRG.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getActivity(), "Please Select Your Role", Toast.LENGTH_SHORT).show();
            return;
        }

        final String role = radioButton.getText().toString();
        //Displaying progress dialog
        progressDialog.setMessage("Logging In Your Account");
        progressDialog.show();

        //Verify Email and Password
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i(TAG, "FirebaseAuth : Complete");
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.i(TAG, "FirebaseAuth : Successful");
                            userMenu(email, role);
                            //FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        } else {
                            Log.i(TAG, "Login Failed", task.getException());
                            Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void userMenu(String email, String role){
        Log.i(TAG, "Determine User Menu Path");

        if(role.equals("Buyer")){
            Log.i(TAG, "Role : Buyer");
            //BuyerDatabaseReference = FirebaseDatabase.getInstance().getReference("Buyer");
            Query query = BuyerDatabaseReference.orderByChild("email").equalTo(email);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.i(TAG, "DataSnapshot : onDataChange");
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        UserInfo userInfo = dataSnapshot1.getValue(UserInfo.class);
                        if(userInfo.getRole().equals(role)){
                            Log.i(TAG, "Buyer Login : Success");
                            Toast.makeText(getActivity(), "Welcome " + userInfo.getName(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(), BuyerMenuActivity.class);
                            intent.putExtra("NAME", userInfo.getName());
                            startActivity(intent);
                            Log.i(TAG, "Moving to BuyerMenuAcitivty");
                            getActivity().finish();
                            return;
                        }
                    }
                    Log.i(TAG, "Wrong Role Selected");
                    Toast.makeText(getActivity(), "Incorrect Role Selected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (role.equals("Seller")){
            Log.i(TAG, "Role : Seller");
            //SellerDatabaseReference = FirebaseDatabase.getInstance().getReference("Seller");
            Query query = SellerDatabaseReference.orderByChild("email").equalTo(email);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.i(TAG, "DataSnapshot : onDataChange");
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        UserInfo userInfo = dataSnapshot1.getValue(UserInfo.class);
                        if(userInfo.getRole().equals(role)){
                            Log.i(TAG, "Seller Login : Success");
                            Toast.makeText(getActivity(), "Welcome " + userInfo.getName(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(), SellerMenuActivity.class);
                            intent.putExtra("NAME", userInfo.getName());
                            startActivity(intent);
                            Log.i(TAG, "Moving to SellerMenuAcitivty");
                            getActivity().finish();
                            return;
                        }
                    }
                    Log.i(TAG, "Wrong Role Selected");
                    Toast.makeText(getActivity(), "Incorrect Role Selected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void managePrefs(){
        if(rememberCB.isChecked()){
            Log.i(TAG, "Remember Me : isChecked");
            editor.putString(KEY_USERNAME, emailET.getText().toString().trim());
            editor.putString(KEY_PASSWORD, passwordET.getText().toString().trim());
            editor.putBoolean(KEY_REMEMBER, true);
            editor.apply();
        }else{
            Log.i(TAG, "Remember Me : unChecked");
            editor.putBoolean(KEY_REMEMBER, false);
            editor.remove(KEY_USERNAME);//editor.putString(KEY_USERNAME, "");
            editor.remove(KEY_PASSWORD);//editor.putString(KEY_PASS,"");
            editor.apply();
        }       
    }
    
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        managePrefs();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        managePrefs();
    }

    @Override
    public void onClick(View view) {
        if (view == loginBTN) {
            Log.i(TAG, "Click : Login");
            userLogin();
        }

        if (view == quitBTN) {
            Log.i(TAG, "Click : Quit");
            quitButton();
        }
    }

    public void quitButton(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to quit ?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
