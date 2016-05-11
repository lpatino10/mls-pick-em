package com.example.loganpatino.mlspickem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loganpatino on 5/28/15.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MatchViewHolder> {

    private List<Game> matches;
    private Utility.Screen cardType;
    private Context context;

    public MyRecyclerAdapter(List<Game> matches, Utility.Screen cardType, Context context) {
        this.matches = new ArrayList<>();
        this.matches.addAll(matches);
        this.cardType = cardType;
        this.context = context;
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if (cardType == Utility.Screen.LIST) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_card_view, parent, false);
        }
        else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_card_view, parent, false);
        }


        return new MatchViewHolder(itemView, cardType, this);
    }

    @Override
    public void onBindViewHolder(final MatchViewHolder holder, final int position) {
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

        if (cardType == Utility.Screen.EDIT) {

            if (matchup.getSelection() == Utility.Selection.HOME_WIN) {
                holder.homeWin.setSelected(true);
                holder.draw.setSelected(false);
                holder.awayWin.setSelected(false);
            }
            else if (matchup.getSelection() == Utility.Selection.DRAW) {
                holder.homeWin.setSelected(false);
                holder.draw.setSelected(true);
                holder.awayWin.setSelected(false);
            }
            else if (matchup.getSelection() == Utility.Selection.AWAY_WIN) {
                holder.homeWin.setSelected(false);
                holder.draw.setSelected(false);
                holder.awayWin.setSelected(true);
            }
            else {
                holder.homeWin.setSelected(false);
                holder.draw.setSelected(false);
                holder.awayWin.setSelected(false);
            }

        }



    }

    @Override
    public int getItemCount() {
        return matches.size();
    }
}
