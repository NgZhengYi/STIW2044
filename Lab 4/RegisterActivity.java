import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText emailET, passwordET;
    private Button registerButton, backButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();

        //------------------ Register for GUI Component ------------------
        emailET = (EditText)findViewById(R.id.user_Email);
        passwordET = (EditText)findViewById(R.id.user_Password);
        registerButton = (Button)findViewById(R.id.register_Button);
        backButton = (Button)findViewById(R.id.back_Button);
        //------------------ Register for GUI Component ------------------

        progressDialog = new ProgressDialog(this);
        registerButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    private void registerAccount(){
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        //Error notification
        if(TextUtils.isEmpty(email)){
            emailET.setError("Email is empty");
        }

        if(TextUtils.isEmpty(password)){
            passwordET.setError("Password is empty");
            return;
        }

        progressDialog.setMessage("Registering Account");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            //User sucessful register
                            Toast.makeText(RegisterActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Could Not Register Your Account", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if(view == registerButton){
            registerAccount();
        }

        if(view == backButton){
            returnToMainActivity(view);
        }
    }

    public void returnToMainActivity(View view){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}
