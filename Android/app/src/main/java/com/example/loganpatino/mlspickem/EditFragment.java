package com.example.loganpatino.mlspickem;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.*;

/**
 * Created by loganpatino on 4/12/16.
 */
public class EditFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<EditListViewHolder> mAdapter;
    private FirebaseRecyclerAdapter<Game, EditListViewHolder> mFirebaseAdapter;
    public DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    protected DatabaseReference mPicksRef = mRootRef.child("picks");
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

        addDataSetListener();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_list_layout, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mAdapter = new EditListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private void addDataSetListener() {
        DatabaseReference userPicksRef = mPicksRef.child(mId);
        userPicksRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String gameKey = dataSnapshot.getKey();
                Utility.Selection selection = Utility.getSelectionFromString(dataSnapshot.getValue(String.class));
                Game addedGame = Utility.getGameFromKey(gameKey);

                Utility.gameMap.remove(addedGame);
                Utility.gameMap.put(addedGame, selection);

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String gameKey = dataSnapshot.getKey();
                Utility.Selection selection = Utility.getSelectionFromString(dataSnapshot.getValue(String.class));
                Game changedGame = Utility.getGameFromKey(gameKey);

                Utility.gameMap.remove(changedGame);
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

    private class EditListAdapter extends RecyclerView.Adapter<EditListViewHolder> {

        @Override
        public EditListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_card_view, parent, false);
            return new EditListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(EditListViewHolder holder, int position) {
            Game currentGame = Utility.gameList.get(position);

            holder.setDate(currentGame.getDate());
            holder.setTime(currentGame.getTime());
            holder.setHomeLogo(currentGame.getHome());
            holder.setAwayLogo(currentGame.getAway());
            holder.setScore();
            holder.setSelection(Utility.gameMap.get(currentGame));
        }

        @Override
        public int getItemCount() {
            return Utility.gameList.size();
        }
    }

    private class EditListViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView time;
        private ImageView homeLogo;
        private ImageView awayLogo;
        private TextView score;
        private TextView homeWin;
        private TextView draw;
        private TextView awayWin;

        public EditListViewHolder(View itemView) {
            super(itemView);
            homeLogo = (ImageView)itemView.findViewById(R.id.home_team);
            awayLogo = (ImageView)itemView.findViewById(R.id.away_team);
            date = (TextView)itemView.findViewById(R.id.date_string);
            time = (TextView)itemView.findViewById(R.id.time_string);
            score = (TextView)itemView.findViewById(R.id.score_text);
            homeWin = (TextView)itemView.findViewById(R.id.home_win);
            draw = (TextView)itemView.findViewById(R.id.draw);
            awayWin = (TextView)itemView.findViewById(R.id.away_win);

            setOnClickListeners();
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

        public void setScore() {
            this.score.setText(mContext.getString(R.string.middle));
        }

        public void setSelection(Utility.Selection selection) {
            if (selection == Utility.Selection.HOME_WIN) {
                this.homeWin.setSelected(true);
                this.draw.setSelected(false);
                this.awayWin.setSelected(false);
            }
            else if (selection == Utility.Selection.DRAW) {
                this.homeWin.setSelected(false);
                this.draw.setSelected(true);
                this.awayWin.setSelected(false);
            }
            else if (selection == Utility.Selection.AWAY_WIN) {
                this.homeWin.setSelected(false);
                this.draw.setSelected(false);
                this.awayWin.setSelected(true);
            }
            else {
                this.homeWin.setSelected(false);
                this.draw.setSelected(false);
                this.awayWin.setSelected(false);
            }
        }

        private void setOnClickListeners() {
            final DatabaseReference userPicksRef = mPicksRef.child(mId);
            final Map<String, Object> updateMap = new HashMap<>();

            homeWin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Game clickedGame = Utility.gameList.get(getAdapterPosition());
                    String key = Utility.getKeyFromGame(clickedGame);
                    Utility.Selection selection = Utility.gameMap.get(clickedGame);

                    if (selection == Utility.Selection.HOME_WIN) {
                        updateMap.put(key, Utility.getStringFromSelection(Utility.Selection.NONE));
                        userPicksRef.updateChildren(updateMap);
                    }
                    else {
                        updateMap.put(key, Utility.getStringFromSelection(Utility.Selection.HOME_WIN));
                        userPicksRef.updateChildren(updateMap);
                    }

                    mAdapter.notifyItemChanged(getAdapterPosition());
                }
            });

            draw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Game clickedGame = Utility.gameList.get(getAdapterPosition());
                    String key = Utility.getKeyFromGame(clickedGame);
                    Utility.Selection selection = Utility.gameMap.get(clickedGame);

                    if (selection == Utility.Selection.DRAW) {
                        updateMap.put(key, Utility.getStringFromSelection(Utility.Selection.NONE));
                        userPicksRef.updateChildren(updateMap);
                    }
                    else {
                        updateMap.put(key, Utility.getStringFromSelection(Utility.Selection.DRAW));
                        userPicksRef.updateChildren(updateMap);
                    }

                    mAdapter.notifyItemChanged(getAdapterPosition());
                }
            });

            awayWin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Game clickedGame = Utility.gameList.get(getAdapterPosition());
                    String key = Utility.getKeyFromGame(clickedGame);
                    Utility.Selection selection = Utility.gameMap.get(clickedGame);

                    if (selection == Utility.Selection.AWAY_WIN) {
                        updateMap.put(key, Utility.getStringFromSelection(Utility.Selection.NONE));
                        userPicksRef.updateChildren(updateMap);
                    }
                    else {
                        updateMap.put(key, Utility.getStringFromSelection(Utility.Selection.AWAY_WIN));
                        userPicksRef.updateChildren(updateMap);
                    }

                    mAdapter.notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }

}
