import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {
    private EditText editText1, editText2, editText3;
    private Button btn_Add, btn_Update, btn_Search, btn_Clear;
    private ListView listView;
    ArrayList<Student> listStudent;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    StudentListAdapter studentListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        //------------------- GUI -------------------
        listView = (ListView) findViewById(R.id.list_view);
        editText1 = (EditText) findViewById(R.id.editTextMatrics);
        editText2 = (EditText) findViewById(R.id.editTextName);
        editText3 = (EditText) findViewById(R.id.editTextCourse);
        btn_Add = (Button) findViewById(R.id.buttonADD);
        btn_Update = (Button) findViewById(R.id.buttonUPDATE);
        btn_Search = (Button) findViewById(R.id.buttonSEARCH);
        btn_Clear = (Button) findViewById(R.id.buttonCLEAR);
        //------------------- GUI -------------------

        listStudent = new ArrayList<>();
        listView.setAdapter(studentListAdapter);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Student");

        //------------------- Listener -------------------
        btn_Add.setOnClickListener(this);
        btn_Search.setOnClickListener(this);
        btn_Update.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        //------------------- Listener -------------------
    }//------------------- Closing OnCreate -------------------

    private void addStudent(final View view) {
        final String matrics = editText1.getText().toString().trim();
        final String name = editText2.getText().toString();
        final String course = editText3.getText().toString();

        if (TextUtils.isEmpty(matrics)) {
            editText1.setError("Matrics is Empty");
        }

        if (TextUtils.isEmpty(name)) {
            editText2.setError("Name is Empty");
        }

        if (TextUtils.isEmpty(course)) {
            editText3.setError("Course is Empty");
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Student").child(matrics);
        final Student student = new Student(matrics, name, course);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    databaseReference.setValue(student);
                    clearText(view);
                    Toast.makeText(StudentActivity.this, matrics + " successfully added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StudentActivity.this, matrics + " ALREADY EXIST", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void removeStudent(final int position){
        String selectedMatrics = listStudent.get(position).getMatrics();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to remove " + selectedMatrics + " ?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Student student = listStudent.get(position);
                        databaseReference = FirebaseDatabase.getInstance().getReference("Student").child(student.getMatrics());
                        databaseReference.removeValue();
                        Toast.makeText(StudentActivity.this, student.getMatrics() + " DELETED", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void updateStudent(View view){
        final String matrics = editText1.getText().toString().trim();
        final String name = editText2.getText().toString();
        final String course = editText3.getText().toString();

        if(TextUtils.isEmpty(matrics)){
            editText1.setError("Matrics is Empty");
        }

        if(TextUtils.isEmpty(name)){
            editText2.setError("Name is Empty");
        }

        if(TextUtils.isEmpty(course)){
            editText3.setError("Course is Empty");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to update " + matrics + " ?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference = FirebaseDatabase.getInstance().getReference("Student").child(matrics);
                        Student student = new Student(matrics, name, course);
                        databaseReference.setValue(student);
                        Toast.makeText(getApplicationContext(), "Data Updated", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void promptInputDialog(View view){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.prompt_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(promptView);

        final EditText input = (EditText)promptView.findViewById(R.id.inputET);
        builder.setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        searchStudentMatrics(input.getText().toString());
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void searchStudentMatrics(final String matrics){
        databaseReference = FirebaseDatabase.getInstance().getReference("Student");
        Query query = databaseReference.orderByChild("matrics").equalTo(matrics);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Student student = dataSnapshot1.getValue(Student.class);
                    if(student.getMatrics().equals(matrics)){
                        Toast.makeText(StudentActivity.this, matrics + " FOUND", Toast.LENGTH_SHORT).show();
                        editText1.setText(student.getMatrics());
                        editText2.setText(student.getName());
                        editText3.setText(student.getCourse());
                        return;
                    }
                }
                Toast.makeText(StudentActivity.this, matrics + " NOT FOUND", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void clearText(View view){
        editText1.setText(null);
        editText1.setEnabled(true);
        editText1.setTextColor(Color.BLACK);
        editText2.setText(null);
        editText3.setText(null);
    }

    @Override
    public void onClick(View view) {
        if(view == btn_Add){
            addStudent(view);
        }

        if(view == btn_Update){
            updateStudent(view);
        }

        if(view == btn_Search){
            promptInputDialog(view);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Student student = listStudent.get(position);
        editText1.setText(student.getMatrics());
        editText1.setEnabled(false);
        editText1.setTextColor(Color.RED);
        editText2.setText(student.getName());
        editText3.setText(student.getCourse());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        removeStudent(position);
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listStudent.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Student student = dataSnapshot1.getValue(Student.class);
                    listStudent.add(student);
                }
                StudentListAdapter studentListAdapter = new StudentListAdapter(StudentActivity.this, listStudent);
                listView.setAdapter(studentListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.logout_menu){
            onStop();
            onDestroy();
            finish();
            Intent intent = new Intent(StudentActivity.this, MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
