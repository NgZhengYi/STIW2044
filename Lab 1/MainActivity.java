import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private double activityValue;
    private double calorie;
    private int age, height, weight, roundOff;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCalculate = (Button) this.findViewById(R.id.button);
        final EditText txAge = (EditText) this.findViewById(R.id.editText_1);
        final EditText txHeight = (EditText) this.findViewById(R.id.editText_3);
        final EditText txWeight = (EditText) this.findViewById(R.id.editText_4);
        final RadioGroup groupRG = (RadioGroup) this.findViewById(R.id.radioGroup);
        final RadioButton maleRB = (RadioButton) this.findViewById(R.id.radioButtonMale);
        final RadioButton femaleRB = (RadioButton) this.findViewById(R.id.radioButtonFemale);
        final Spinner spinMode = (Spinner) this.findViewById(R.id.spinner_ac);
        final TextView resultTV = (TextView) this.findViewById(R.id.textView_7);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.exercise_array,
        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMode.setAdapter(adapter);
        spinMode.setOnItemSelectedListener(this);

        //button function
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (txAge.getText().toString().equals("") || txHeight.getText().toString().equals("") ||
                        txWeight.getText().toString().equals("") || groupRG.getCheckedRadioButtonId() == -1) {
                    resultTV.setText("Incomplete Information !");
                } else {
                    age = Integer.parseInt(txAge.getText().toString());
                    height = Integer.parseInt(txHeight.getText().toString());
                    weight = Integer.parseInt(txWeight.getText().toString());

                    if (maleRB.isChecked()) {
                        calorie = (10 * weight + 6.25 * height - 5 * age + 5) * activityValue;
                        roundOff = (int) calorie;
                        if (activityValue == 1) {
                            resultTV.setText("You need " + roundOff + " calories/day to maintain your weight");
                        } else {
                            resultTV.setText("You need " + roundOff + " calories/day to maintain your weight\n" +
                                    "You need " + (roundOff - 500) + " calories/day to lose 0.5kg per week\n" +
                                    "You need " + (roundOff - 1000) + " calories/day to lose 1.0kg per week\n" +
                                    "You need " + (roundOff + 500) + " calories/day to gain 0.5kg per week\n" +
                                    "You need " + (roundOff + 1000) + " calories/day to gain 0.5kg per week");
                        }
                    } else if (femaleRB.isChecked()) {
                        calorie = (10 * weight + 6.25 * height - 5 * age - 161) * activityValue;
                        roundOff = (int) calorie;
                        if (activityValue == 1) {
                            resultTV.setText("You need " + roundOff + " calories/day to maintain your weight");
                        } else {
                            resultTV.setText("You need " + roundOff + " calories/day to maintain your weight\n" +
                                    "You need " + (roundOff - 500) + " calories/day to lose 0.5kg per week\n" +
                                    "You need " + (roundOff - 1000) + " calories/day to lose 1.0kg per week\n" +
                                    "You need " + (roundOff + 500) + " calories/day to gain 0.5kg per week\n" +
                                    "You need " + (roundOff + 1000) + " calories/day to gain 0.5kg per week");
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l){
        //String text = adapterView.getItemAtPosition(position).toString();
        //Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT).show();
        switch(position){
            case 0:
                activityValue = 1;
                break;
            case 1:
                activityValue = 1.2;
                break;
            case 2:
                activityValue = 1.375;
                break;
            case 3:
                activityValue = 1.55;
                break;
            case 4:
                activityValue = 1.725;
                break;
            case 5:
                activityValue = 1.9;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
