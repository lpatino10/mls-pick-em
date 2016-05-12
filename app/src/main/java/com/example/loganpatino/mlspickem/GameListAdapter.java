package com.example.loganpatino.mlspickem;

import android.content.Context;
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

    public GameListAdapter(List<Game> matches) {
        this.matches = new ArrayList<>();
        this.matches.addAll(matches);
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

        if (matchup.getSelection() == Utility.Selection.HOME_WIN) {
            holder.pick.setText("hw");
        }
        else if (matchup.getSelection() == Utility.Selection.DRAW) {
            holder.pick.setText("draw");
        }
        else if (matchup.getSelection() == Utility.Selection.AWAY_WIN) {
            holder.pick.setText("aw");
        }
        else {
            holder.pick.setText("none");
        }



    }

    @Override
    public int getItemCount() {
        return matches.size();
    }
}
