package com.example.loganpatino.mlspickem;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by loganpatino on 4/12/16.
 */
public class GameListFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<GameListViewHolder> mAdapter;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mPicksRef = mRootRef.child("picks");
    private DatabaseReference mGamesRef = mRootRef.child("games");
    private String mId;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getActivity().getSharedPreferences(Utility.PREFS_FILE, Context.MODE_PRIVATE);
        mId = mSharedPreferences.getString(Utility.LOGIN_ID, null);
        mContext = getActivity().getApplicationContext();

        if (!Utility.hasDataBeenLoaded) {
            instantiateDataSet();
        }

        addDataSetListener();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_list_layout, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        mAdapter = new GameListAdapter();
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private void instantiateDataSet() {
        Utility.gameMap = new HashMap<>();
        Utility.gameList = new ArrayList<>();
        String id = mSharedPreferences.getString(Utility.LOGIN_ID, null);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference gamesRef = rootRef.child("games");
        final DatabaseReference userPicksRef = rootRef.child("picks").child(id);

        Query query = gamesRef.orderByChild("date").startAt(Utility.startDate).endAt(Utility.endDate);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean calculatedTotalGames = false;
                for (final DataSnapshot game : dataSnapshot.getChildren()) {
                    if (!calculatedTotalGames) {
                        Utility.totalGamesPlayed = Integer.valueOf(game.getKey());
                        calculatedTotalGames = true;
                    }

                    Game currentGame = game.getValue(Game.class);
                    if (currentGame.getHomeScore() > -1) {
                        Utility.totalGamesPlayed++;
                    }
                    String key = Utility.getKeyFromGame(currentGame);

                    DatabaseReference gamePickRef = userPicksRef.child(key);
                    gamePickRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Game currentGame = game.getValue(Game.class);
                            if (dataSnapshot.getValue() == null) {
                                Utility.gameMap.put(currentGame, Utility.Selection.NONE);
                                Utility.gameList.add(currentGame);
                            } else {
                                Utility.gameMap.put(currentGame, Utility.getSelectionFromString(dataSnapshot.getValue(String.class)));
                                Utility.gameList.add(currentGame);
                            }

                            mAdapter.notifyDataSetChanged();
                            Utility.hasDataBeenLoaded = true;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addDataSetListener() {
        Query query = mGamesRef.orderByChild("date").startAt(Utility.startDate).endAt(Utility.endDate);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // won't happen
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Game changedGame = dataSnapshot.getValue(Game.class);
                String gameKey = Utility.getKeyFromGame(changedGame);
                Game oldGame = Utility.getGameFromKey(gameKey);

                // if a game has finished, update count
                if ((oldGame.getHomeScore() == -1) && (changedGame.getHomeScore() > -1)) {
                    Utility.totalGamesPlayed++;
                }

                List<Game> newGameList = new ArrayList<>();
                for (int i = 0; i < Utility.gameList.size(); i++) {
                    if (Utility.gameList.get(i) == oldGame) {
                        newGameList.add(changedGame);
                    }
                    else {
                        newGameList.add(Utility.gameList.get(i));
                    }
                }
                Utility.gameList = newGameList;

                Utility.Selection selection = Utility.gameMap.get(oldGame);
                Utility.gameMap.remove(oldGame);
                Utility.gameMap.put(changedGame, selection);

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // won't happen
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // won't happen
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class GameListAdapter extends RecyclerView.Adapter<GameListViewHolder> {

        @Override
        public GameListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(mContext).inflate(R.layout.list_card_view, parent, false);
            return new GameListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(GameListViewHolder holder, int position) {
            Game currentGame = Utility.gameList.get(position);

            holder.setDate(currentGame.getDate());
            holder.setTime(currentGame.getTime());
            holder.setHomeLogo(currentGame.getHome());
            holder.setAwayLogo(currentGame.getAway());
            holder.setScore(currentGame.getHomeScore(), currentGame.getAwayScore());
            holder.setPickAndColor(currentGame, Utility.gameMap.get(currentGame));
        }

        @Override
        public int getItemCount() {
            return Utility.gameList.size();
        }
    }

    private class GameListViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView time;
        private ImageView homeLogo;
        private ImageView awayLogo;
        private TextView score;
        private TextView pick;

        public GameListViewHolder(View itemView) {
            super(itemView);
            homeLogo = (ImageView)itemView.findViewById(R.id.home_team);
            awayLogo = (ImageView)itemView.findViewById(R.id.away_team);
            date = (TextView)itemView.findViewById(R.id.date_string);
            time = (TextView)itemView.findViewById(R.id.time_string);
            score = (TextView)itemView.findViewById(R.id.score_text);
            pick = (TextView)itemView.findViewById(R.id.pick);
        }

        public void setDate(String date) {
            this.date.setText(Utility.getDisplayDate(date));
        }

        public void setTime(String time) {
            this.time.setText(Utility.getDisplayTime(time));
        }

        public void setHomeLogo(String home) {
            this.homeLogo.setImageResource(Utility.getLogoResource(home));
        }

        public void setAwayLogo(String away) {
            this.awayLogo.setImageResource(Utility.getLogoResource(away));
        }

        public void setScore(int homeScore, int awayScore) {
            if (homeScore != -1) {
                String score = homeScore + " - " + awayScore;
                this.score.setText(score);
            }
            else {
                this.score.setText(mContext.getString(R.string.middle));
            }
        }

        public void setPickAndColor(Game game, Utility.Selection pick) {
            String userPick = mContext.getString(R.string.your_pick) + " " + Utility.getStringFromSelection(pick);
            this.pick.setText(userPick);

            int textColor = Utility.getTextColorFromSelection(game, pick);
            if (textColor == -1) {
                this.pick.setTextColor(ContextCompat.getColor(mContext, android.R.color.primary_text_light));
            }
            else {
                this.pick.setTextColor(textColor);
            }

        }
    }































    /*private void getCurrentWeek(List<Game> tempGames) {
        Utility.games.clear();
        String oldFirstDate = mSharedPreferences.getString(Utility.FIRST_GAME_DATE, null);

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

        int gameCount = 0;
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

            if (date.before(lastWeek.getTime())) {
                gameCount++;
            }
            else if (date.before(nextWeek.getTime()) && date.after(lastWeek.getTime())) { // if game is this week
                Utility.games.add(currentGame);
            }
            else if (date.after(nextWeek.getTime())) {
                break;
            }
        }

        for (int i = 0; i < Utility.games.size(); i++) {
            if (Utility.games.get(i).getHomeScore() != -1) {
                gameCount++;
            }
        }
        Utility.gameCount = gameCount;

        String newFirstDate = Utility.getDateString(Utility.games.get(0).getDate());
        Log.d("TIME_TEST", "oldFirstDate: " + oldFirstDate + "  newFirstDate: " + newFirstDate);
        if ((oldFirstDate != null) && !oldFirstDate.equals(newFirstDate)) {
            clearUserPicks();
        }

        oldFirstDate = Utility.getDateString(Utility.games.get(0).getDate());
        mSharedPreferences.edit().putString(Utility.FIRST_GAME_DATE, oldFirstDate).apply();
    }*/

    /*private void getUserPicks() {
        DatabaseReference userRef = mPicksRef.child(mId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Game currentGame = Utility.getGameFromKey(snapshot.getKey());
                    assert currentGame != null;

                    switch (snapshot.getValue().toString()) {
                        case "Home Win":
                            currentGame.setSelection(Utility.Selection.HOME_WIN);
                            break;
                        case "Draw":
                            currentGame.setSelection(Utility.Selection.DRAW);
                            break;
                        case "Away Win":
                            currentGame.setSelection(Utility.Selection.AWAY_WIN);
                            break;
                        default:
                            currentGame.setSelection(Utility.Selection.NONE);
                    }
                }

                updateCorrectPicks();

                //mAdapter = new GameListAdapter(Utility.games, getActivity().getApplicationContext());
                //recyclerView.swapAdapter(mAdapter, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setDefaultPicks() {
        final DatabaseReference userRef = mPicksRef.child(mId);

        mPicksRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        //String key = Utility.getKeyFromGame(i);
                        Map<String, Object> pick = new HashMap<>();
                        //pick.put(key, "None");
                        userRef.updateChildren(pick);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

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
                    //getCurrentWeek(tempGames);
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
        DatabaseReference userRef = mPicksRef.child(mId);
        for (int i = 0; i < Utility.games.size(); i++) {
            //String key = Utility.getKeyFromGame(i);
            //userRef.child(key).removeValue();
        }
        Log.d("DEBUG", "user picks have been cleared");
    }

    private void updateCorrectPicks() {
        DatabaseReference picksRef = mRootRef.child("profiles").child(mId);
        int newCorrectCount = 0;

        for (int i = 0; i < Utility.games.size(); i++) {
            Game currentGame = Utility.games.get(i);
            int homeScore = currentGame.getHomeScore();
            int awayScore = currentGame.getAwayScore();

            if (homeScore > awayScore) {
                if (currentGame.getSelection() == Utility.Selection.HOME_WIN) {
                    newCorrectCount++;
                }
            }
            else if (awayScore > homeScore) {
                if (currentGame.getSelection() == Utility.Selection.AWAY_WIN) {
                    newCorrectCount++;
                }
            }
            else if ((homeScore == awayScore) && (homeScore > -1)) {
                if (currentGame.getSelection() == Utility.Selection.DRAW) {
                    newCorrectCount++;
                }
            }
        }

        int storedCorrectCount = mSharedPreferences.getInt(Utility.CURRENT_WEEK_CORRECT_PICKS, 0);

        Log.d("COUNT_TEST", "newCorrectCount: " + newCorrectCount + "   storedCorrectCount: " + storedCorrectCount);

        if (newCorrectCount > storedCorrectCount) {
            Map<String, Object> correctPicks = new HashMap<>();
            correctPicks.put("correctPicks", newCorrectCount);
            picksRef.updateChildren(correctPicks);
            mSharedPreferences.edit().putInt(Utility.CURRENT_WEEK_CORRECT_PICKS, newCorrectCount).apply();
        }
    }*/

}
