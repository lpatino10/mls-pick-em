package com.example.loganpatino.mlspickem;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout mCoordinatorLayout;
    private boolean onMainScreen = true;
    Fragment mGameListFragment;
    Fragment mEditFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator);
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

            @Override
            public void onClick(View v) {
                boolean haveGamesLoaded = false;
                if (Utility.games.size() > 0) {
                    haveGamesLoaded = true;
                }

                if ((haveGamesLoaded) && (!Utility.haveGamesStarted())) {

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
                else {
                    if (haveGamesLoaded) {
                        Snackbar snackbar = Snackbar.make(mCoordinatorLayout,
                                "Games have already started!",
                                Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
            }
        });

    }

    public void clickAction() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment;

        if (onMainScreen) {
            savePicks();
            fragment = mGameListFragment;
        }
        else {
            fragment = mEditFragment;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    private void savePicks() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utility.PREFS_FILE, Context.MODE_PRIVATE);
        String keyList = sharedPreferences.getString(Utility.KEYS, null);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String[] keys = gson.fromJson(keyList, String[].class);

        Firebase ref = new Firebase("https://mls-pick-em.firebaseio.com/");
        for (int i = 0; i < Utility.games.size(); i++) {
            Utility.Selection currentSelection = Utility.games.get(i).getSelection();
            String pushVal = null;

            if (currentSelection == Utility.Selection.HOME_WIN) {
                pushVal = "Home Win";
            }
            else if (currentSelection == Utility.Selection.DRAW) {
                pushVal = "Draw";
            }
            else if (currentSelection == Utility.Selection.AWAY_WIN) {
                pushVal = "Away Win";
            }
            else {
                pushVal = "None";
            }
            ref.child(keys[i]).setValue(pushVal);
        }
    }
}
