import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher,
        CompoundButton.OnCheckedChangeListener{
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private Button LoginButton, CancelButton;
    private EditText emailET, passwordET;
    private TextView register;
    private CheckBox checkBox;
    private ProgressDialog progressDialog;

    private static final String PREF_NAME = "prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        emailET = (EditText) findViewById(R.id.emailEText);
        passwordET = (EditText)findViewById(R.id.passwordEText);
        LoginButton = (Button)findViewById(R.id.login_button);
        CancelButton = (Button)findViewById(R.id.cancel_button);
        register = (TextView)findViewById(R.id.registerTV);
        checkBox = (CheckBox)findViewById(R.id.checkbox);

        LoginButton.setOnClickListener(this);
        CancelButton.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getBoolean(KEY_REMEMBER, false)){
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        emailET.setText(sharedPreferences.getString(KEY_USERNAME, ""));
        passwordET.setText(sharedPreferences.getString(KEY_PASSWORD, ""));
        emailET.addTextChangedListener(this);
        passwordET.addTextChangedListener(this);
        checkBox.setOnCheckedChangeListener(this);
    } //Close OnCreate Method

    private void userLogin(View view){
        final String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        //Checking email and password are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        //Displaying progress dialog
        progressDialog.setMessage("Logging in to your account");
        progressDialog.show();

        //Verify Email and Password
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Failed to Login",Toast.LENGTH_SHORT).show();
                        }else{
                            finish();
                            Toast.makeText(MainActivity.this, "Hello " + email, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,StudentActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    public void openRegisterPage(View view){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void cancelButton(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        onDestroy();
                    }
                })
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        if(view == LoginButton){
            userLogin(view);
        }

        if(view == CancelButton){
            cancelButton(view);
        }
    }

    private void managePrefs(){
        if(checkBox.isChecked()){
            editor.putString(KEY_USERNAME, emailET.getText().toString().trim());
            editor.putString(KEY_PASSWORD, passwordET.getText().toString().trim());
            editor.putBoolean(KEY_REMEMBER, true);
            editor.apply();
        }else{
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
}
