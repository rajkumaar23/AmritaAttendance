package com.krca.attendance;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class DateChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_chooser);
        Bundle b=getIntent().getExtras();
        final String filename=b.getString("filename");
        final DatePicker datePicker=findViewById(R.id.datePicker);
        Button proceed = findViewById(R.id.proceedDate);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DateChooserActivity.this,TakeAttendance.class);
                intent.putExtra("filename",filename);
                intent.putExtra("date",datePicker.getDayOfMonth()+"-"+(datePicker.getMonth()+1)+"-"+datePicker.getYear());
                startActivity(intent);
            }
        });

        Button currentDate=findViewById(R.id.setcurrentdate);
        currentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String formattedDate = df.format(c);
                Log.e("Today's date : ",formattedDate);
                Intent intent=new Intent(DateChooserActivity.this,TakeAttendance.class);
                intent.putExtra("filename",filename);
                intent.putExtra("date",formattedDate);
                startActivity(intent);
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
