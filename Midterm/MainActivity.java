import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    TextView registerTV;
    EditText usernameTV, passwordTV;
    Button loginButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------------ Register for GUI Component ------------------
        registerTV = (TextView)findViewById(R.id.registerTV);
        usernameTV = (EditText)findViewById(R.id.usernameEText);
        passwordTV = (EditText)findViewById(R.id.passwordEText);
        loginButton = (Button)findViewById(R.id.login_button);
        cancelButton = (Button)findViewById(R.id.cancel_button);
        //------------------ Register for GUI Component ------------------
    }

    public void loginMyAccount(View view){
        final String loginUsername = usernameTV.getText().toString();
        final String loginPassword = passwordTV.getText().toString();

        if(loginUsername.trim().toString().equals("")){
            usernameTV.setError("Username is Empty");
        }

        if(loginPassword.trim().toString().equals("")){
            passwordTV.setError("Password is Empty");
            return;
        }

        class LoginAccount extends AsyncTask<Void,Void,String>{
            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("username", loginUsername);
                hashMap.put("password", loginPassword);

                RequestHandler requestHandler = new RequestHandler();
                String request = requestHandler.sendPostRequest(" http://slumberjer.com/android/a172/midterm/login.php", hashMap);
                return request;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s.equals("success")){
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                } else if(s.equals("failed")){
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
                }
            }
        }
        LoginAccount loginAccount = new LoginAccount();
        loginAccount.execute();
    }

    public void openRegisterPage(View view){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void cancelButton(View view){
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
