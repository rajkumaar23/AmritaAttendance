package com.krca.attendance;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class AddClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        checkForPermission();
        final EditText start=findViewById(R.id.startrollinput);
        final EditText end=findViewById(R.id.endrollinput);
        final EditText fileName=findViewById(R.id.filenameinput);
        final EditText firsthalf=findViewById(R.id.degreeinput);



        Button reset=findViewById(R.id.resetfilename);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start.setText("");
                end.setText("");
                fileName.setText("");
            }
        });

        Button add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkForPermission();
                hideKeyboard();
                if(start.getText().toString().isEmpty() || end.getText().toString().isEmpty() || fileName.getText().toString().isEmpty())
                {
                    Toast.makeText(AddClassActivity.this,"Please fill in all the spaces ! ",Toast.LENGTH_SHORT).show();
                }
                else{
                long startroll = Integer.parseInt(start.getText().toString());
                long endroll = Integer.parseInt(end.getText().toString());
                if (startroll < endroll) {

                    File apkStorage = null;
                    if (new CheckForSDCard().isSDCardPresent()) {

                        apkStorage = new File(
                                Environment.getExternalStorageDirectory() + "/"
                                        + "AmritaAttendance");
                    } else
                        Toast.makeText(AddClassActivity.this, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                    //If File is not present create directory
                    if (!apkStorage.exists()) {
                        apkStorage.mkdir();
                        Log.e("Directory Result ", "Directory Created.");
                    }

                    File file = new File(Environment.getExternalStorageDirectory() + "/"
                            + "AmritaAttendance/" + fileName.getText().toString() + ".xlsx");

                    WritableWorkbook myFirstWbook = null;

                    try {


                        myFirstWbook = Workbook.createWorkbook(file);

                        // create an Excel sheet
                        WritableSheet excelSheet = myFirstWbook.createSheet("Sheet 1", 0);
                        Label label = new Label(0, 0, "Roll Numbers");
                        excelSheet.addCell(label);
                        if(excelSheet.getCell(1,0).getContents().isEmpty())
                            Toast.makeText(AddClassActivity.this,"0,0 Empty",Toast.LENGTH_SHORT).show();
                        int j = 1;

                        for (long i = startroll; i <= endroll; ++i, ++j) {

                            String roll = firsthalf.getText().toString()+ String.valueOf(i);
                            excelSheet.addCell(new Label(0, j, roll));

                        }

                        myFirstWbook.write();

                        Toast.makeText(AddClassActivity.this,"File created : "+fileName.getText().toString()+".xls",Toast.LENGTH_SHORT).show();
                        Log.e("Written xls ", "Successs");


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }   finally {

                        if (myFirstWbook != null) {
                            try {
                                myFirstWbook.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (WriteException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                } else {

            Toast.makeText(AddClassActivity.this,"Roll Numbers not valid ! ",Toast.LENGTH_SHORT).show();
                }
            }}
        });



    }


    public void checkForPermission()
    {
        if (ContextCompat.checkSelfPermission(AddClassActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(AddClassActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(AddClassActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

}