import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity{
    EditText emailET, passwordET, nameET, phoneET, addressET;
    Button registerButton, backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //------------------ Register for GUI Component ------------------
        emailET = (EditText)findViewById(R.id.user_Email);
        passwordET = (EditText)findViewById(R.id.user_Password);
        nameET = (EditText)findViewById(R.id.user_Name);
        phoneET = (EditText)findViewById(R.id.user_Phone);
        addressET = (EditText)findViewById(R.id.user_Address);
        registerButton = (Button)findViewById(R.id.register_Button);
        backButton = (Button)findViewById(R.id.back_Button);
        //------------------ Register for GUI Component ------------------
    }

    public void registerAnAccount(final View view){
        final String userEmail = emailET.getText().toString();
        final String userPassword = passwordET.getText().toString();
        final String userName = nameET.getText().toString();
        final String userPhone = phoneET.getText().toString();
        final String userAddress = addressET.getText().toString();

        if(userAddress.trim().toString().equals("")) {
            emailET.setError("Incomplete User Email");
        }
        if(userPassword.trim().toString().equals("")){
            passwordET.setError("Incomplete Password");
            //if(userPassword.length() < 8)
            //passwordET.setError("Password too short"):
        }
        if(userName.trim().toString().equals("")){
            nameET.setError("Incomplete Name");
        }
        if(userPhone.trim().toString().equals("")){
            phoneET.setError("Incomplete Phone Number");
        }
        if(userAddress.trim().toString().equals("")){
            addressET.setError("Incomplete Address");
            return;
        }

        class RegisterAccount extends AsyncTask<Void,Void,String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String>hashMap = new HashMap<>();
                hashMap.put("username", userEmail);
                hashMap.put("password", userPassword);
                hashMap.put("name", userName);
                hashMap.put("address", userAddress);
                hashMap.put("phone", userPhone);

                RequestHandler requestHandler = new RequestHandler();
                String request = requestHandler.sendPostRequest("http://slumberjer.com/android/a172/midterm/register.php", hashMap);
                return request;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s.equals("duplicate")){
                    Toast.makeText(RegisterActivity.this, "The User Email has already exist", Toast.LENGTH_SHORT).show();
                } else if(s.equals("success")){
                    Toast.makeText(RegisterActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();
                    returnToMainActivity(view);
                } else if(s.equals("error")){
                    Toast.makeText(RegisterActivity.this, "Unable to Register", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
                }
            }
        }
        RegisterAccount registerAccount = new RegisterAccount();
        registerAccount.execute();
    }

    public void returnToMainActivity(View view){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}
