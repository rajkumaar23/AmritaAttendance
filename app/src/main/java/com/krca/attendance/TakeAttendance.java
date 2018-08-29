package com.krca.attendance;

import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class TakeAttendance extends AppCompatActivity {

    private int currentFreePos;
    int count,flag=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        Bundle b=getIntent().getExtras();
        final String filename=b.getString("filename");
        final String date=b.getString("date");

        TextView textView=findViewById(R.id.attendanceDateTextView);
        textView.setText("Attendance for "+date);

        final ListView studentsList=findViewById(R.id.studentsList);
        ArrayList<Student> students=new ArrayList<Student>();
        final StudentAdapter studentsAdapter;
        final File file = new File(Environment.getExternalStorageDirectory() + "/"
                + "AmritaAttendance/"+filename);


        Workbook workbook = null;
        Sheet sheet=null;
        try {

            workbook = Workbook.getWorkbook(file);
            sheet = workbook.getSheet(0);
            currentFreePos=sheet.getColumns();
            count=sheet.getRows();
            for(int i=1;i<count;++i)
            {
                students.add(new Student(sheet.getCell(0,i).getContents()));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } finally {

            studentsAdapter=new StudentAdapter(this,students);
            studentsList.setAdapter(studentsAdapter);
            if (workbook != null) {
                workbook.close();
            }

        }




        Button save=findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> abs=studentsAdapter.getAbsentees();
                Workbook workbook = null;
                WritableWorkbook copy=null;
                Sheet sheet=null;
                WritableSheet sheet1=null;
                        try {

                            workbook = Workbook.getWorkbook(file);
                            copy= Workbook.createWorkbook(file,workbook);
                            sheet = workbook.getSheet(0);
                            sheet1=copy.getSheet(0);
                            currentFreePos=sheet.getColumns();
                            for(int k=0;k<currentFreePos;++k)
                            {
                                if(sheet.getCell(k,0).getContents().equals(date))
                                {
                                    currentFreePos=k;
                                    flag=0;
                                    break;
                                }
                            }
                            if(flag>0){
                            sheet1.addCell(new Label(currentFreePos,0,date));

                            for(int j=1;j<count;++j) {
                                if(!abs.contains(String.valueOf(j-1)))
                                {
                                    sheet1.addCell(new Label(currentFreePos,j,"Present"));
                                }
                                else{
                                    sheet1.addCell(new Label(currentFreePos,j,"ABSENT"));
                                }

                            }}


                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (BiffException e) {
                            e.printStackTrace();
                        }catch (WriteException e) {
                            e.printStackTrace();
                        } finally {
                            if (workbook != null) {
                                workbook.close();
                                try {
                                    copy.write();
                                    copy.close();
                                }catch (IOException e) {
                                    e.printStackTrace();
                                }catch (WriteException e) {
                                    e.printStackTrace();
                                }

                            }
                            showSnackbar("Data entried for "+date);

                        }
                    }


        });


    }

    void showSnackbar(String msg){
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar
                .make(parentLayout, msg, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
