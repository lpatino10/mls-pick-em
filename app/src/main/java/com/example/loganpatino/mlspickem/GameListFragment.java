package com.example.loganpatino.mlspickem;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
    private Firebase mRef = new Firebase("https://mls-pick-em.firebaseio.com/");

    public static GameListFragment newInstance(FloatingActionButton fab) {
        GameListFragment gameListFragment = new GameListFragment();

        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putSerializable("fab", gson.toJson(fab));
        gameListFragment.setArguments(args);

        return gameListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Utility.games == null) {
            Utility.games = new ArrayList<>();

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

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_list_layout, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(new GameListAdapter(Utility.games, getActivity().getApplicationContext()));

        return view;
    }

    private void getCurrentWeek(List<Game> tempGames) {
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
    }

    private void getUserPicks() {

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                recyclerView.swapAdapter(new GameListAdapter(Utility.games, getActivity().getApplicationContext()), true);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void setDefaultPicks() {

        final ArrayList<String> keys = new ArrayList<>();
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    for (int i = 0; i < Utility.games.size(); i++) {
                        Firebase newRef = mRef.push();
                        newRef.setValue("None");
                        Log.d("getKeyTest", newRef.getKey());
                        keys.add(newRef.getKey());

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utility.PREFS_FILE, Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String keyList = gson.toJson(keys);
                        Log.d("SAVE_TEST", keyList);
                        sharedPreferences.edit().putString(Utility.KEYS, keyList).apply();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
