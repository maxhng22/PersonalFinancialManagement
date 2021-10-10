package com.example.personalfinancialmanagement;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.model.PlacesSearchResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HomeActivity extends AppCompatActivity {

    boolean mBounded;
    private AppBarLayout appBarLayout;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);

    private final SimpleDateFormat  monthFormat = new SimpleDateFormat("MMMM-yyyy", /*Locale.getDefault()*/Locale.ENGLISH);

    private CompactCalendarView compactCalendarView;

    private boolean isExpanded = false;

    private Intent intent;

    private final int REQUEST_LOCATION = 11;

    protected LocationManager locationManager;
    public static String extra_date = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH).format(new Date());

    public static String extra_month_date = new SimpleDateFormat("MMMM-yyyy",Locale.ENGLISH).format(new Date());

    public static HomeActivity homeActivity;

    public static String  filter="Daily";

    Alarm alarm = new Alarm();


    public void sendBroadcast() {
        Intent intent = new Intent("com.codinginflow.EXAMPLE_ACTION");
        intent.putExtra("com.codinginflow.EXTRA_TEXT", "Broadcast received");
        sendBroadcast(intent);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedText = intent.getStringExtra("com.codinginflow.EXTRA_TEXT");
//            textView.setText(receivedText);
        }
    };




    myService mServer;

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("BOOMBOOMTESTGPS","not working");
            mBounded = false;
            mServer = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("BOOMBOOMTESTGPS"," working");
            mBounded = true;
            myService.LocalBinder mLocalBinder = (myService.LocalBinder)service;
            mServer = mLocalBinder.getServerInstance();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("com.codinginflow.EXAMPLE_ACTION");
        registerReceiver(broadcastReceiver, filter);

        Intent mIntent = new Intent(this, myService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);

        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }

    private void OnGPS() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == 100) {
            if (!verifyAllPermissions(grantResults)) {
                Toast.makeText(getApplicationContext(), "No sufficient permissions", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean verifyAllPermissions(int[] grantResults) {

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeActivity = this;
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        new NearbySearch().run();

        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Write Function To enable gps
            OnGPS();
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,

                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        alarm.setAlarm(getApplicationContext());
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(navView, navController);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Calender");

        appBarLayout = findViewById(R.id.app_bar_layout);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // Collapsed
                    isExpanded = false;
                } else if (verticalOffset == 0) {
                    // Expanded
                    isExpanded = true;
                } else {
                    // Somewhere in between
                    isExpanded = false;
                }
            }
        });


        // Set up the CompactCalendarView
        compactCalendarView = findViewById(R.id.compactcalendar_view);

        // Force English
        compactCalendarView.setLocale(TimeZone.getDefault(), /*Locale.getDefault()*/Locale.ENGLISH);

        compactCalendarView.setShouldDrawDaysHeader(true);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                String myFormat = "dd-MM-yyyy";
                String monthString  = (String) DateFormat.format("MMMM-yyyy",  dateClicked);
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                if(filter.equals("Daily")){
                    extra_date = sdf.format(dateClicked);
                    extra_month_date=monthString;
                    setSubtitle(dateFormat.format(dateClicked));
                } else {
                    extra_date = monthString;
                    extra_month_date=monthString;
                    setSubtitle(monthString);
                }

                sendBroadcast();

                isExpanded = !isExpanded;
                appBarLayout.setExpanded(isExpanded, true);


            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                if(filter == "Daily") {
                    setSubtitle(dateFormat.format(firstDayOfNewMonth));
                } else {
                    setSubtitle(monthFormat.format(firstDayOfNewMonth));
                }
            }
        });

//        // Set current date to today

        setCurrentDate(new Date());

        final ImageView arrow = findViewById(R.id.date_picker_arrow);

        RelativeLayout datePickerButton = findViewById(R.id.date_picker_button);

        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.NestedScrollView);
        scrollView.setFillViewport(true);
        scrollView.setNestedScrollingEnabled(false);

        datePickerButton.setOnClickListener(v -> {
            float rotation = isExpanded ? 0 : 180;
            ViewCompat.animate(arrow).rotation(rotation).start();

            isExpanded = !isExpanded;
            appBarLayout.setExpanded(isExpanded, true);


        });


        startService(new Intent(HomeActivity.this, myService.class));
        Intent mIntent = new Intent(this, myService.class);

        this.bindService(mIntent, mConnection, BIND_AUTO_CREATE);

    }

    public static String getExtra_date() {
        return extra_date;
    }

    public static String getExtra_month() {
        return extra_month_date;
    }

    public static String getFilter() {
        return filter;
    }

    private void setCurrentDate(Date date) {
        setSubtitle(dateFormat.format(date));

    }

    @Override
    public void setTitle(CharSequence title) {
        TextView tvTitle = findViewById(R.id.title);

        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    private void setSubtitle(String subtitle) {
        TextView datePickerTextView = findViewById(R.id.date_picker_text_view);

        if (datePickerTextView != null) {
            datePickerTextView.setText(subtitle);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.add){
            OpenDialog();
        } else {
            OpenFilterDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void OpenDialog() {
        LinearLayout layout = new LinearLayout(HomeActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        final AlertDialog a = builder.setItems(new String[]{"Add Expense", "Add Income"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    intent = new Intent(HomeActivity.this, AddExpenseActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(HomeActivity.this, AddIncomeActivity.class);
                    startActivity(intent);
                }

            }
        }).setTitle("Add Transaction").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }

    private void OpenFilterDialog() {
        LinearLayout layout = new LinearLayout(HomeActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        final AlertDialog a = builder.setItems(new String[]{"Daily", "Monthly"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    filter ="Daily";

                    try{
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    Date date = format.parse(extra_date);
                    if(date!=null){
                        setSubtitle(dateFormat.format(date));
                    }else{
                        setSubtitle(dateFormat.format(new Date()));
                    }
                    sendBroadcast();
                    }catch(ParseException e){
                        setSubtitle(dateFormat.format(new Date()));
                    }

                } else {
                    filter ="Monthly";
                    try{
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Date date = format.parse(extra_date);
                        if(date!=null){
                            setSubtitle(monthFormat.format(date));
                        }else{
                            setSubtitle(monthFormat.format(new Date()));
                        }
                        sendBroadcast();
                    }catch(ParseException e){
                        setSubtitle(monthFormat.format(new Date()));
                    }


                }

                sendBroadcast();

            }
        }).setTitle("Display Spending").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }


}