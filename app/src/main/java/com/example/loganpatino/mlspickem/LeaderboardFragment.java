package com.example.loganpatino.mlspickem;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class LeaderboardFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter<Profile, LeaderboardViewHolder> mRecyclerAdapter;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mProfileRef = mRootRef.child("profiles");
    private DatabaseReference mPicksRef = mRootRef.child("picks");
    private static Context mContext;
    private String mId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utility.PREFS_FILE, Context.MODE_PRIVATE);
        mId = sharedPreferences.getString(Utility.LOGIN_ID, null);
        updateCorrectPicks();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.leaderboard_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        Query leaderboardQuery = mProfileRef.orderByChild("totalCorrectPicks");
        mRecyclerAdapter = new FirebaseRecyclerAdapter<Profile, LeaderboardViewHolder>(Profile.class, R.layout.leaderboard_item_view, LeaderboardViewHolder.class, leaderboardQuery) {
            @Override
            protected void populateViewHolder(LeaderboardViewHolder leaderboardViewHolder, Profile profile, int i) {
                leaderboardViewHolder.setPosition(i);
                leaderboardViewHolder.setProfilePic(profile.getProfilePic());
                leaderboardViewHolder.setName(profile.getName());
                leaderboardViewHolder.setCorrectPicks(profile.getTotalCorrectPicks());
            }
        };
        mRecyclerView.setAdapter(mRecyclerAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecyclerAdapter.cleanup();
    }

    private void updateCorrectPicks() {
        mPicksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int newThisWeekCorrectPicks = 0;
                for (DataSnapshot idSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot pickSnapshot : idSnapshot.getChildren()) {
                        String gameKey = pickSnapshot.getKey();
                        Log.d("KEY_TEST", gameKey);
                        Utility.Selection gamePick = Utility.getSelectionFromString(pickSnapshot.getValue(String.class));
                        Game currentGame = Utility.getGameFromKey(gameKey);

                        if (Utility.getTextColorFromSelection(currentGame, gamePick) == Color.GREEN) {
                            newThisWeekCorrectPicks++;
                        }
                    }

                    final int finalNewThisWeekCorrectPicks = newThisWeekCorrectPicks;
                    final DatabaseReference userProfileRef = mProfileRef.child(mId);
                    userProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Profile userProfile = dataSnapshot.getValue(Profile.class);
                            int oldTotalCorrectPicks = userProfile.getTotalCorrectPicks();
                            int newTotalCorrectPicks = finalNewThisWeekCorrectPicks + userProfile.getPreviousCorrectPicks();

                            Log.d("PICK_TEST", "oldTotalCorrectPicks: " + oldTotalCorrectPicks + "   newTotalCorrectPicks: " + newTotalCorrectPicks);

                            if (newTotalCorrectPicks < oldTotalCorrectPicks) {
                                Map<String, Object> updateMap = new HashMap<>();
                                updateMap.put("totalCorrectPicks", newTotalCorrectPicks);
                                updateMap.put("previousCorrectPicks", newTotalCorrectPicks);
                                userProfileRef.updateChildren(updateMap);
                            }
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

    private static class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        private TextView position;
        private ImageView profilePic;
        private TextView name;
        private TextView correctPicks;

        public LeaderboardViewHolder(View itemView) {
            super(itemView);
            position = (TextView)itemView.findViewById(R.id.position);
            profilePic = (ImageView)itemView.findViewById(R.id.profile_pic);
            name = (TextView)itemView.findViewById(R.id.name);
            correctPicks = (TextView)itemView.findViewById(R.id.correct_picks);
        }

        public void setPosition(int position) {
            this.position.setText(String.valueOf(++position));
        }

        public void setProfilePic(String profilePic) {
            Picasso.with(mContext).load(profilePic).into(this.profilePic);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setCorrectPicks(int correctPicks) {
            correctPicks = correctPicks * -1;
            String correctPickStr = correctPicks + "/" + Utility.totalGamesPlayed;
            this.correctPicks.setText(correctPickStr);
        }
    }
}
