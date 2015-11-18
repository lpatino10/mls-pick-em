package com.example.loganpatino.mlspickem;

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

    private List<GameInfo> matches;

    public MyRecyclerAdapter(List<GameInfo> matches) {
        this.matches = new ArrayList<>();
        this.matches.addAll(matches);
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new MatchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MatchViewHolder holder, int position) {
        GameInfo matchup = matches.get(position);
        holder.homeLogo.setImageResource(matchup.getHomeLogo());
        holder.awayLogo.setImageResource(matchup.getAwayLogo());
        holder.date.setText(matchup.getDate());
        holder.time.setText(matchup.getTime() + " PM");

        holder.homeLogo.setOnClickListener(new View.OnClickListener() {
            boolean clicked = false;

            @Override
            public void onClick(View v) {
                if (clicked) {
                    holder.homeLogo.setBackgroundColor(Color.TRANSPARENT);
                    clicked = false;
                }
                else {
                    holder.homeLogo.setBackgroundColor(0xFFBDBDBD);
                    holder.draw.setBackgroundColor(Color.TRANSPARENT);
                    holder.awayLogo.setBackgroundColor(Color.TRANSPARENT);
                    clicked = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }
}
