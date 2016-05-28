package com.example.loganpatino.mlspickem;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        getDateRange();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mNavigationView.setCheckedItem(R.id.home);
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame);

        if (fragment == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.navigation_content, new HomeFragment());
            fragmentTransaction.commit();
        }
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
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass;

        switch (id) {
            case R.id.home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.leaderboard:
                fragmentClass = LeaderboardFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment)fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.navigation_content, fragment);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getDateRange() {
        java.util.Date currentDate = new java.util.Date(); // initializes to current date
        Calendar currentCal = new GregorianCalendar();
        Calendar nextWeek = new GregorianCalendar(2016, Calendar.MARCH, 7); // first Monday

        currentCal.setTime(currentDate);

        while (nextWeek.getTime().before(currentCal.getTime())) { // moves nextWeek to next week's Monday
            nextWeek.add(Calendar.WEEK_OF_YEAR, 1);
        }

        Calendar lastWeek = new GregorianCalendar();
        lastWeek.setTime(nextWeek.getTime());
        lastWeek.add(Calendar.WEEK_OF_YEAR, -1); // lastWeek is 1 week before nextWeek

        int currentYear = currentCal.get(Calendar.YEAR);
        int firstDay = lastWeek.get(Calendar.DAY_OF_MONTH);
        int lastDay = nextWeek.get(Calendar.DAY_OF_MONTH);
        int firstMonth = lastWeek.get(Calendar.MONTH) + 1;
        int lastMonth = nextWeek.get(Calendar.MONTH) + 1;

        Utility.startDate = Utility.getDateString(currentYear, firstMonth, firstDay);
        Utility.endDate = Utility.getDateString(currentYear, lastMonth, lastDay);
    }
}
