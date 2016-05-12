package com.example.loganpatino.mlspickem;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loganpatino on 5/28/15.
 */
public class GameListAdapter extends RecyclerView.Adapter<GameListViewHolder> {

    private List<Game> matches;
    private Context mContext;

    public GameListAdapter(List<Game> matches, Context context) {
        this.matches = new ArrayList<>();
        this.matches.addAll(matches);
        mContext = context;
    }

    @Override
    public GameListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_card_view, parent, false);
        return new GameListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GameListViewHolder holder, final int position) {
        final Game matchup = matches.get(position);
        holder.homeLogo.setImageResource(Utility.getLogoResource(matchup.getHome()));
        holder.awayLogo.setImageResource(Utility.getLogoResource(matchup.getAway()));
        holder.date.setText(Utility.getDateString(matchup.getDate()));
        holder.time.setText(Utility.getTimeString(matchup.getTime()));

        int homeScore = matchup.getHomeScore();
        int awayScore = matchup.getAwayScore();
        if (homeScore != -1) {
            String score = homeScore + " - " + awayScore;
            holder.score.setText(score);
        }
        else {
            holder.score.setText("vs.");
        }

        String userPick = mContext.getString(R.string.your_pick) + " ";
        if (matchup.getSelection() == Utility.Selection.HOME_WIN) {
            userPick = userPick + matchup.getHome();
            holder.pick.setText(userPick);
        }
        else if (matchup.getSelection() == Utility.Selection.DRAW) {
            userPick = userPick + mContext
                    .getString(R.string.draw);
            holder.pick.setText(userPick);
        }
        else if (matchup.getSelection() == Utility.Selection.AWAY_WIN) {
            userPick = userPick + matchup.getAway();
            holder.pick.setText(userPick);
        }
        else {
            userPick = userPick + mContext
                    .getString(R.string.none);
            holder.pick.setText(userPick);
        }



    }

    @Override
    public int getItemCount() {
        return matches.size();
    }
}
