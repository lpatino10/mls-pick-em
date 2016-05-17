package com.example.loganpatino.mlspickem;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by loganpatino on 4/12/16.
 */
public class GameListFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<GameListViewHolder> mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Firebase mRef = new Firebase("https://mls-pick-em.firebaseio.com/");
    private String mId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utility.PREFS_FILE, Context.MODE_PRIVATE);
        mId = sharedPreferences.getString(Utility.LOGIN_ID, null);

        if (Utility.games == null) {
            Utility.games = new ArrayList<>();
            populateGameList();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_list_layout, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateGameList();
            }
        });

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        mAdapter = new GameListAdapter(Utility.games, getActivity().getApplicationContext());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private void getCurrentWeek(List<Game> tempGames) {
        Utility.games.clear();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utility.PREFS_FILE, Context.MODE_PRIVATE);
        String oldFirstDate = sharedPreferences.getString(Utility.FIRST_GAME_DATE, null);

        Date currentDate = new Date(); // initializes to current date
        Calendar currentCal = new GregorianCalendar();
        Calendar nextWeek = new GregorianCalendar(2016, Calendar.MARCH, 7); // first Monday

        currentCal.setTime(currentDate);

        while (nextWeek.getTime().before(currentCal.getTime())) { // moves nextWeek to next week's Monday
            nextWeek.add(Calendar.WEEK_OF_YEAR, 1);
        }

        Calendar lastWeek = new GregorianCalendar();
        lastWeek.setTime(nextWeek.getTime());
        lastWeek.add(Calendar.WEEK_OF_YEAR, -1); // lastWeek is 1 week before nextWeek

        for (int i = 0; i < tempGames.size(); i++) {
            Game currentGame = tempGames.get(i);
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH); // sets numeric date with English format
            int day = currentGame.getDate().getDay();
            int month = currentGame.getDate().getMonth();
            int year = currentGame.getDate().getYear();

            Date date = new Date();

            try {
                date = df.parse(month + "/" + day + "/" + year); // parses current date
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (date.before(nextWeek.getTime()) && date.after(lastWeek.getTime())) { // if game is this week
                Utility.games.add(currentGame);
            }
        }

        String newFirstDate = Utility.getDateString(Utility.games.get(0).getDate());
        if ((oldFirstDate != null) && !oldFirstDate.equals(newFirstDate)) {
            clearUserPicks();
        }

        oldFirstDate = Utility.getDateString(Utility.games.get(0).getDate());
        sharedPreferences.edit().putString(Utility.FIRST_GAME_DATE, oldFirstDate).apply();
    }

    private void getUserPicks() {
        String userUrl = "https://mls-pick-em.firebaseio.com/" + mId;
        final Firebase userRef = new Firebase(userUrl);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    switch (snapshot.getValue().toString()) {
                        case "Home Win":
                            Utility.games.get(i).setSelection(Utility.Selection.HOME_WIN);
                            break;
                        case "Draw":
                            Utility.games.get(i).setSelection(Utility.Selection.DRAW);
                            break;
                        case "Away Win":
                            Utility.games.get(i).setSelection(Utility.Selection.AWAY_WIN);
                            break;
                        default:
                            Utility.games.get(i).setSelection(Utility.Selection.NONE);
                    }

                    i++;
                }

                mAdapter = new GameListAdapter(Utility.games, getActivity().getApplicationContext());
                recyclerView.swapAdapter(mAdapter, true);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void setDefaultPicks() {
        String userUrl = "https://mls-pick-em.firebaseio.com/" + mId;
        final Firebase userRef = new Firebase(userUrl);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isIdSaved = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(mId)) {
                        isIdSaved = true;
                    }
                }

                if (!isIdSaved) {
                    for (int i = 0; i < Utility.games.size(); i++) {
                        userRef.push().setValue("None");
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void populateGameList() {
        RestClient.GameInterface service = RestClient.getClient();
        Call<CallResult> call = service.getSchedule();

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {
                if (response.isSuccessful()) {
                    CallResult result = response.body();
                    List<Game> tempGames = result.getGames();
                    getCurrentWeek(tempGames);
                    setDefaultPicks();
                    getUserPicks();

                    mSwipeRefreshLayout.setRefreshing(false);
                }
                else {
                    Log.d(getClass().getSimpleName(), "Response unsuccessful!");
                }
            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {
                Log.d(getClass().getSimpleName(), "Call failed!");
            }
        });
    }

    private void clearUserPicks() {
        mRef.setValue(mId, null);
    }
}
