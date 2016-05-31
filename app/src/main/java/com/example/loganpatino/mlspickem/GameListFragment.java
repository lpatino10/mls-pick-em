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
        final String[] oldFirstDate = {mSharedPreferences.getString(Utility.FIRST_GAME_DATE, null)};

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

                            String newFirstDate = Utility.gameList.get(0).getDate();
                            Log.d("TIME_TEST", "oldFirstDate: " + oldFirstDate[0] + "  newFirstDate: " + newFirstDate);
                            if ((oldFirstDate[0] != null) && !oldFirstDate[0].equals(newFirstDate)) {
                                clearUserPicks();
                            }

                            oldFirstDate[0] = newFirstDate;
                            mSharedPreferences.edit().putString(Utility.FIRST_GAME_DATE, oldFirstDate[0]).apply();
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

    private void clearUserPicks() {
        DatabaseReference userRef = mPicksRef.child(mId);
        userRef.setValue(null);
        /*for (int i = 0; i < Utility.gameList.size(); i++) {
            String key = Utility.getKeyFromGame(Utility.gameList.get(i));
            userRef.child(key).removeValue();
        }*/
        Log.d("DEBUG", "user picks have been cleared");
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
}
