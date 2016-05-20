package com.example.loganpatino.mlspickem;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class LeaderboardFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter<Profile, LeaderboardViewHolder> mRecyclerAdapter;
    private DatabaseReference mProfileRef = FirebaseDatabase.getInstance().getReference().child("profiles");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.leaderboard_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mRecyclerAdapter = new FirebaseRecyclerAdapter<Profile, LeaderboardViewHolder>(Profile.class, R.layout.leaderboard_item_view, LeaderboardViewHolder.class, mProfileRef) {
            @Override
            protected void populateViewHolder(LeaderboardViewHolder leaderboardViewHolder, Profile profile, int i) {
                leaderboardViewHolder.setContext(getActivity().getApplicationContext());
                leaderboardViewHolder.setProfilePic(profile.getProfilePic());
                leaderboardViewHolder.setName(profile.getName());
                leaderboardViewHolder.setCorrectPicks(profile.getCorrectPicks());
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

    private static class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        private ImageView profilePic;
        private TextView name;
        private TextView correctPicks;
        private Context context;

        public LeaderboardViewHolder(View itemView) {
            super(itemView);
            profilePic = (ImageView)itemView.findViewById(R.id.profile_pic);
            name = (TextView)itemView.findViewById(R.id.name);
            correctPicks = (TextView)itemView.findViewById(R.id.correct_picks);
        }

        public void setProfilePic(String profilePic) {
            Picasso.with(context).load(profilePic).into(this.profilePic);
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setCorrectPicks(int correctPicks) {
            this.correctPicks.setText(String.valueOf(correctPicks));
        }
    }
}
