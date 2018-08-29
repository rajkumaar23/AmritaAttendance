package com.krca.attendance;

import android.os.Environment;
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
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        Bundle b=getIntent().getExtras();
        String filename=b.getString("filename");
        String date=b.getString("date");

        TextView textView=findViewById(R.id.attendanceDateTextView);
        textView.setText("Attendance for "+date);

        final ListView studentsList=findViewById(R.id.studentsList);
        ArrayList<Student> students=new ArrayList<Student>();
        final StudentAdapter studentsAdapter;


        final File file = new File(Environment.getExternalStorageDirectory() + "/"
                + "AmritaAttendance/"+filename);
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
            count=sheet.getRows();

            sheet1.addCell(new Label(currentFreePos,0,date));
            for(int i=1;i<count;++i)
            {
                sheet1.addCell(new Label(currentFreePos,i,"Present"));
                students.add(new Student(sheet.getCell(0,i).getContents(),true));

            }



        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }catch (BiffException e) {
            e.printStackTrace();
        } finally {

            studentsAdapter=new StudentAdapter(this,students);
            studentsList.setAdapter(studentsAdapter);
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

        }




        Button save=findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Student> students=new ArrayList<Student>();
                Workbook workbook = null;
                WritableWorkbook copy=null;

                Sheet sheet=null;
                WritableSheet sheet1=null;
                for(int j=0;j<count-1;++j){
                    final Student currentStudent = studentsAdapter.getItem(j);
                    if(!currentStudent.getmPresence())
                    {

                        try {

                            workbook = Workbook.getWorkbook(file);
                            copy= Workbook.createWorkbook(file,workbook);
                            sheet = workbook.getSheet(0);
                            sheet1=copy.getSheet(0);
                            currentFreePos=sheet.getColumns();
                            WritableCell cell=sheet1.getWritableCell(currentFreePos,j);
                            Label l = (Label) cell;
                            l.setString("Absent");

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (BiffException e) {
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

                        }
                    }
                }
            }
        });


    }
}
