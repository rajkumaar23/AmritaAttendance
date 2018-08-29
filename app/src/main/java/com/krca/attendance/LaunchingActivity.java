package com.krca.attendance;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LaunchingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launching);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LaunchingActivity.this,AddClassActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        checkForPermission();

        ListView listView=findViewById(R.id.classeslist);


        final List<String> filenames=new ArrayList<>();
        File directory = new File(Environment.getExternalStorageDirectory()+"/AmritaAttendance");
        final File[] files = directory.listFiles();
        if(files!=null){
        for (int i = 0; i < files.length; i++) {
            //System.out.print(files[i].getName());
            filenames.add(files[i].getName().split("\\.")[0]);
        }}

        ArrayAdapter<String> classlist=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,filenames);
        listView.setAdapter(classlist);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(LaunchingActivity.this,DateChooserActivity.class);
                intent.putExtra("filename",files[i].getName());
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final File pdfFile = new File(Environment.getExternalStorageDirectory() + "/AmritaAttendance/" + files[i].getName());
                if (pdfFile.exists()) {
                    final ArrayList<String> qPaperOptions = new ArrayList<>();
                    qPaperOptions.add("Rename");
                    qPaperOptions.add("Delete");
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(LaunchingActivity.this); //Read Update
                    ArrayAdapter<String> optionsAdapter = new ArrayAdapter<String>(LaunchingActivity.this, android.R.layout.simple_list_item_1, qPaperOptions);
                    alertDialog.setAdapter(optionsAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int pos) {
                            if(pos==1)
                            {
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LaunchingActivity.this);
                                alertDialog.setMessage("Are you sure you want to delete the file? ");
                                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        pdfFile.delete();
                                        Toast.makeText(LaunchingActivity.this,"File Deleted",Toast.LENGTH_SHORT).show();
                                        LaunchingActivity.this.recreate();

                                    }
                                });
                                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                                alertDialog.show();
                            }
                            else if(pos==0){
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LaunchingActivity.this);
                                alertDialog.setMessage("Rename file");
                                LinearLayout layout = new LinearLayout(LaunchingActivity.this);
                                layout.setOrientation(LinearLayout.VERTICAL);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(40, 0, 50, 0);
                                final EditText textBox = new EditText(LaunchingActivity.this);
                                layout.addView(textBox, params);

                                alertDialog.setView(layout);

                                alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        pdfFile.renameTo(new File(Environment.getExternalStorageDirectory() + "/AmritaAttendance/" + textBox.getText()+".xlsx"));
                                        LaunchingActivity.this.recreate();
                                    }
                                });
                                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                                alertDialog.show();
                            }

                        }
                    });
                    alertDialog.show();
                }
                return true;
            }
        });


        SwipeRefreshLayout refresh=findViewById(R.id.swipeRefreshContainer);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LaunchingActivity.this.recreate();
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.launching, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void checkForPermission()
    {
        if (ContextCompat.checkSelfPermission(LaunchingActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LaunchingActivity.this,
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
                    ListView listView=findViewById(R.id.classeslist);


                    List<String> filenames=new ArrayList<>();
                    File directory = new File(Environment.getExternalStorageDirectory()+"/AmritaAttendance");
                    File[] files = directory.listFiles();
                    if(files!=null){
                    for (int i = 0; i < files.length; i++) {
                        //System.out.print(files[i].getName());
                        filenames.add(files[i].getName().split("\\.")[0]);
                    }

                    ArrayAdapter<String> classlist=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,filenames);
                    listView.setAdapter(classlist);}
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    Snackbar.make(findViewById(android.R.id.content),"Permission denied to read your External storage",Snackbar.LENGTH_SHORT).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
