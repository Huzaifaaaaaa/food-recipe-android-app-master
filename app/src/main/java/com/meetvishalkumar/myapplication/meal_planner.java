package com.meetvishalkumar.myapplication;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.meetvishalkumar.myapplication.Loading_Animation.NoInternetDiaload;
import com.meetvishalkumar.myapplication.UserAccount.Profile;
import com.meetvishalkumar.myapplication.UserAccount.Splash_Login;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.muddz.styleabletoast.StyleableToast;

public class meal_planner extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static final float END_SCALE = 0.7f;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu_opener_image;
    LinearLayout contentView;
    Button createButton;
    Spinner item1spinner, item2spinner, item3spinner, item4spinner;
    EditText customerName, Day, qty1, qty2, qty3, qty4;
    Bitmap bmp, scaledbmp;
    int pageWidth = 1920;
    Date dateObj;
    DateFormat dateFormat;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_planner);

        createButton = findViewById(R.id.createButton);
        item1spinner = findViewById(R.id.item1spinner);
        item2spinner = findViewById(R.id.item2spinner);
        item3spinner = findViewById(R.id.item3spinner);
        item4spinner = findViewById(R.id.item4spinner);
        customerName = findViewById(R.id.customerName);
        Day = findViewById(R.id.Day);
        qty1 = findViewById(R.id.qty1);
        qty2 = findViewById(R.id.qty2);
        qty3 = findViewById(R.id.qty3);
        qty4 = findViewById(R.id.qty4);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.head);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 1920, 800, false);

        if (!checkInternet()) {
            NoInternetDiaload noInternetDialoag = new NoInternetDiaload(getApplicationContext());
            noInternetDialoag.setCancelable(false);
            noInternetDialoag.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            noInternetDialoag.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            noInternetDialoag.show();
        }

        menu_opener_image = findViewById(R.id.menu_opener_image);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        contentView = findViewById(R.id.content);


        navigationView();

        ActivityCompat.requestPermissions(this, new String[]{
//                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        createPDF();
    }

    //        Navigation Drawer Setting Start
    private void navigationView() {

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.Navigation_bar_item_Meal);
        menu_opener_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        animateNavigationDrawer();


    }

    private void animateNavigationDrawer() {
        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        drawerLayout.setScrimColor(getResources().getColor(R.color.dark_red));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Navigation_bar_item_Home:
                Intent intent = new Intent(meal_planner.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.Navigation_bar_item_Meal:
                StyleableToast.makeText(getApplicationContext(), "You are already on this Page", Toast.LENGTH_SHORT, R.style.OnActivity).show();


                break;
            case R.id.Navigation_bar_item_Tips:
                Intent intent1 = new Intent(meal_planner.this, Tips.class);
                startActivity(intent1);
                break;
            case R.id.Navigation_bar_item_login:
                Intent intent2 = new Intent(meal_planner.this, Splash_Login.class);
                startActivity(intent2);
                break;
            case R.id.Navigation_bar_item_logout:
                mAuth = FirebaseAuth.getInstance();
                FirebaseAuth.getInstance().signOut();
                StyleableToast.makeText(getApplicationContext(), "You Successfully Logged out", Toast.LENGTH_SHORT, R.style.OnActivity).show();
                Intent intent3 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent3);
                break;
            case R.id.Navigation_bar_item_Profile:
                Intent intent4 = new Intent(meal_planner.this, Profile.class);
                startActivity(intent4);
                break;


        }
        return true;
    }
    //        Navigation Drawer Setting End

    private boolean checkInternet() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void createPDF() {
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dateObj = new Date();

                if (customerName.getText().toString().length() == 0 ||
                        Day.getText().toString().length() == 0 ||
                        qty1.getText().toString().length() == 0 ||
                        qty2.getText().toString().length() == 0 ||
                        qty3.getText().toString().length() == 0 ||
                        qty4.getText().toString().length() == 0) {
                    Toast.makeText(meal_planner.this, "Some fields empty", Toast.LENGTH_LONG).show();
                } else {
                    PdfDocument myPdfDocument = new PdfDocument();
                    Paint myPaint = new Paint();
                    Paint titlePaint = new Paint();

                    PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(1920, 2010, 1).create();
                    PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
                    Canvas canvas = myPage1.getCanvas();

                    canvas.drawBitmap(scaledbmp,0,0, myPaint);

                    titlePaint.setTextAlign(Paint.Align.CENTER);
                    titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    titlePaint.setTextSize(70);
                    canvas.drawText("Tasty Tips", pageWidth/2, 270, titlePaint);

                    myPaint.setColor(Color.rgb(0,113, 188));
                    myPaint.setTextSize(30f);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText("Call: 0333-3847376", 1160, 40, myPaint);
                    canvas.drawText("0333-3847376", 1160, 80, myPaint);

                    titlePaint.setTextAlign(Paint.Align.CENTER);
                    titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
                    titlePaint.setTextSize(70);
                    canvas.drawText("Meal Plan", pageWidth/2, 500, titlePaint);

                    myPaint.setTextAlign(Paint.Align.LEFT);
                    myPaint.setTextSize(35f);
                    myPaint.setColor(Color.BLACK);
                    canvas.drawText("Your Name: "+customerName.getText(), 20, 590, myPaint);
                    canvas.drawText("Day: "+Day.getText(), 20, 640, myPaint);

                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText("PDF No: "+"56789", pageWidth-20, 590, myPaint);

                    dateFormat = new SimpleDateFormat("dd/MM/yy");
                    canvas.drawText("Date: "+dateFormat.format(dateObj), pageWidth-20, 640, myPaint);

                    dateFormat = new SimpleDateFormat("HH/mm/ss");
                    canvas.drawText("Time: "+dateFormat.format(dateObj), pageWidth-20, 640, myPaint);

                    myPaint.setStyle(Paint.Style.STROKE);
                    myPaint.setStrokeWidth(2);
                    canvas.drawRect(20,780, pageWidth-20, 860, myPaint);

                    myPaint.setTextAlign(Paint.Align.LEFT);
                    myPaint.setStyle(Paint.Style.FILL);
                    canvas.drawText("Si. No.", 40, 830, myPaint);
                    canvas.drawText("Item Description", 200, 830, myPaint);
                    canvas.drawText("Time", 700, 830, myPaint);
                    canvas.drawText("Qty", 900, 830, myPaint);

                    canvas.drawLine(180, 790, 180, 840, myPaint);
                    canvas.drawLine(680, 790, 680, 840, myPaint);
                    canvas.drawLine(880, 790, 880, 840, myPaint);
                    canvas.drawLine(1030, 790, 1030, 840, myPaint);

                    float total1 = 0, total2 = 0, total3 = 0, total4 = 0;
                    if(item1spinner.getSelectedItemPosition()!=0){
                        canvas.drawText("1. ", 40, 950, myPaint);
                        canvas.drawText(item1spinner.getSelectedItem().toString(), 200, 950, myPaint);
                        canvas.drawText("Breakfast", 700, 950, myPaint);
                        canvas.drawText(qty1.getText().toString(), 900, 950, myPaint);
                        total1 = Float.parseFloat(qty1.getText().toString())*item1spinner.getSelectedItemPosition();
                        myPaint.setTextAlign(Paint.Align.RIGHT);
                    }

                    if(item2spinner.getSelectedItemPosition()!=0){
                        canvas.drawText("2. ", 40, 1050, myPaint);
                        canvas.drawText(item2spinner.getSelectedItem().toString(), 200, 1050, myPaint);
                        canvas.drawText("Lunch", 700, 1050, myPaint);
                        canvas.drawText(qty2.getText().toString(), 900, 1050, myPaint);
                        total2 = Float.parseFloat(qty2.getText().toString())*item2spinner.getSelectedItemPosition();
                        myPaint.setTextAlign(Paint.Align.RIGHT);
                    }

                    if(item3spinner.getSelectedItemPosition()!=0){
                        canvas.drawText("3. ", 40, 1150, myPaint);
                        canvas.drawText(item3spinner.getSelectedItem().toString(), 200, 1150, myPaint);
                        canvas.drawText("Supper", 700, 1150, myPaint);
                        canvas.drawText(qty3.getText().toString(), 900, 1150, myPaint);
                        total2 = Float.parseFloat(qty3.getText().toString())*item3spinner.getSelectedItemPosition();
                        myPaint.setTextAlign(Paint.Align.RIGHT);
                    }

                    if(item4spinner.getSelectedItemPosition()!=0){
                        canvas.drawText("4. ", 40, 1250, myPaint);
                        canvas.drawText(item4spinner.getSelectedItem().toString(), 200, 1250, myPaint);
                        canvas.drawText("Dinner", 700, 1250, myPaint);
                        canvas.drawText(qty4.getText().toString(), 900, 1250, myPaint);
                        total2 = Float.parseFloat(qty4.getText().toString())*item4spinner.getSelectedItemPosition();
                        myPaint.setTextAlign(Paint.Align.RIGHT);
                    }


                    myPdfDocument.finishPage(myPage1);

                    File file = new File(Environment.getExternalStorageDirectory(), "/Meal.pdf");

                    try {
                        myPdfDocument.writeTo(new FileOutputStream(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    myPdfDocument.close();
                }
            }
        });
    }}