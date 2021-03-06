package com.krca.attendance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
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

    boolean saving=false;
    Workbook workbook = null;
    Sheet sheet=null;
    private int currentFreePos;
    int count,flag=1;
    String date;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        Bundle b=getIntent().getExtras();
        final String filename=b.getString("filename");
        date=b.getString("date");

        TextView textView=findViewById(R.id.attendanceDateTextView);
        textView.setText("Attendance for "+date);

        final ListView studentsList=findViewById(R.id.studentsList);
        ArrayList<Student> students=new ArrayList<Student>();
        final StudentAdapter studentsAdapter;
        file = new File(Environment.getExternalStorageDirectory() + "/"
                + "Attendance/"+filename);


        
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

            studentsAdapter = new StudentAdapter(this, students);
            studentsList.setAdapter(studentsAdapter);

        }


       checkRedundancy();


        final Button save=findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> abs = studentsAdapter.getAbsentees();
                Workbook workbook = null;
                WritableWorkbook copy = null;
                Sheet sheet = null;
                WritableSheet sheet1 = null;
                if (!saving) {
                    try {

                        workbook = Workbook.getWorkbook(file);
                        copy = Workbook.createWorkbook(file, workbook);
                        sheet = workbook.getSheet(0);
                        sheet1 = copy.getSheet(0);
                        currentFreePos = sheet.getColumns();

                        if (flag > 0) {
                            sheet1.addCell(new Label(currentFreePos, 0, date));

                            for (int j = 1; j < count; ++j) {
                                if (!sheet.getCell(0, j).getContents().trim().isEmpty()) {
                                    if (!abs.contains(String.valueOf(j - 1))) {
                                        sheet1.addCell(new Label(currentFreePos, j, ""));
                                        sheet1.addCell(new Label(currentFreePos, j, "Present"));
                                    } else {
                                        sheet1.addCell(new Label(currentFreePos, j, ""));
                                        sheet1.addCell(new Label(currentFreePos, j, "ABSENT"));
                                    }
                                }

                            }
                        }
                        saving=true;

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (BiffException e) {
                        e.printStackTrace();
                    } catch (WriteException e) {
                        e.printStackTrace();
                    } finally {
                        if (workbook != null) {
                            workbook.close();
                            try {
                                copy.write();
                                copy.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (WriteException e) {
                                e.printStackTrace();
                            }

                        }
                        showSnackbar("Data entered for " + date);

                    }
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
    boolean checkRedundancy(){
        for(int k=0;k<currentFreePos;++k)
        {
            if(sheet.getCell(k,0).getContents().equals(date))
            {
                final AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(TakeAttendance.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(TakeAttendance.this);
                }
                builder.setCancelable(false);
                builder.setMessage("Attendance is already entered for this date. Please open it as spreadsheet for further editing.")
                        .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                Uri data = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.setDataAndType(data, "application/vnd.ms-excel");
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(Intent.createChooser(intent, "Open the file"));
                                }
                                else
                                    showSnackbar("No app found for opening spreadsheets.");
                                TakeAttendance.this.finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                TakeAttendance.this.finish();
                            }
                        })
                        .show();

                return true;
            }
        }
return false;
    }
}
