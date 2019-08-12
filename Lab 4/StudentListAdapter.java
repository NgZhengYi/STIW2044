import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StudentListAdapter extends ArrayAdapter<Student> {
    private Activity context;
    ArrayList<Student> students;

    public StudentListAdapter(@NonNull Activity context, @NonNull ArrayList<Student> students) {
        super(context, R.layout.custom_list, students);
        this.context = context;
        this.students = students;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String studentMatrics = getItem(position).getMatrics();
        String studentName = getItem(position).getName();
        String studentCourse = getItem(position).getCourse();

        Student student = new Student(studentMatrics, studentName, studentCourse);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.custom_list, parent, false);

        TextView tvMatrics = (TextView)convertView.findViewById(R.id.TV_Matrics);
        TextView tvName = (TextView)convertView.findViewById(R.id.TV_Name);
        TextView tvCourse = (TextView)convertView.findViewById(R.id.TV_Course);
        tvMatrics.setText(studentMatrics);
        tvName.setText(studentName);
        tvCourse.setText(studentCourse);

        return convertView;
    }
}
