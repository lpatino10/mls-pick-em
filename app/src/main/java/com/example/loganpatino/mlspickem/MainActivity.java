package com.example.loganpatino.mlspickem;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private boolean onMainScreen = true;
    Fragment mGameListFragment;
    Fragment mEditFragment;
    private ArrayList<Game> games;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        mGameListFragment = new GameListFragment();
        mEditFragment = new EditFragment();

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame);

        if (fragment == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.content_frame, mGameListFragment);
            fragmentTransaction.commit();
        }

        // set icon
        final FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_lock_outline_white_24dp);

        fab.setOnClickListener(new View.OnClickListener() {

            //boolean clicked = false;

            @Override
            public void onClick(View v) {
                if (onMainScreen) {
                    fab.setImageResource(R.drawable.ic_lock_open_white_24dp);
                    onMainScreen = false;
                }
                else {
                    fab.setImageResource(R.drawable.ic_lock_outline_white_24dp);
                    onMainScreen = true;
                }

                clickAction();
            }
        });

    }

    public void clickAction() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment;

        if (onMainScreen) {
            fragment = mGameListFragment;
        }
        else {
            fragment = mEditFragment;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }


}
