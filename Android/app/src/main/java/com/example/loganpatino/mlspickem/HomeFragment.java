package com.example.loganpatino.mlspickem;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {

    private CoordinatorLayout mCoordinatorLayout;
    private boolean onMainScreen = true;
    Fragment mGameListFragment;
    Fragment mEditFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        mCoordinatorLayout = (CoordinatorLayout)view.findViewById(R.id.coordinator);
        mGameListFragment = new GameListFragment();
        mEditFragment = new EditFragment();

        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame);

        if (fragment == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.content_frame, mGameListFragment);
            fragmentTransaction.commit();
        }

        // set icon
        final FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_lock_outline_white_24dp);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if ((Utility.hasDataBeenLoaded) && (!Utility.haveGamesStarted())) {

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
                    if (Utility.hasDataBeenLoaded) {
                        Snackbar snackbar = Snackbar.make(mCoordinatorLayout,
                                "Games have already started!",
                                Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
            }
        });

        return view;
    }

    public void clickAction() {
        //Toast.makeText(getActivity().getApplicationContext(), String.valueOf(Utility.totalGamesPlayed), Toast.LENGTH_SHORT).show();

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
