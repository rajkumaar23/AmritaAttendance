package com.krca.attendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class StudentAdapter extends ArrayAdapter<Student> {

    private Context mContext;

    ArrayList<String> absentees = new ArrayList<String>();
    public StudentAdapter(Activity context, ArrayList<Student> students) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, students);
        mContext=context;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.student_list_item, parent, false);
        }final Student currentStudent = getItem(position);


        TextView StudentID = (TextView) listItemView.findViewById(R.id.attendanceStudentId);
        StudentID.setText(currentStudent.getMstudentId());

        final CheckBox studentPresence = (CheckBox) listItemView.findViewById(R.id.attendanceCheckbox);
        studentPresence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!studentPresence.isChecked())
                {
                    absentees.add(String.valueOf(position));
                }
                else {
                    absentees.remove(String.valueOf(position));
                }

            }
        });
        studentPresence.setChecked(true);
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(studentPresence.isChecked())
                    studentPresence.setChecked(false);
                else
                    studentPresence.setChecked(true);
            }
        });
        return listItemView;
    }

    public ArrayList<String> getAbsentees(){
        return absentees;
    }
}
