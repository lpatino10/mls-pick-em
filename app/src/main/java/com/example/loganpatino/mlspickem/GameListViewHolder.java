package com.example.loganpatino.mlspickem;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by loganpatino on 5/28/15.
 */
public class GameListViewHolder extends RecyclerView.ViewHolder {

    protected TextView date;
    protected TextView time;
    protected ImageView homeLogo;
    protected ImageView awayLogo;
    protected TextView score;
    protected TextView pick;

    public GameListViewHolder(View itemView) {
        super(itemView);
        homeLogo = (ImageView)itemView.findViewById(R.id.home_team);
        awayLogo = (ImageView)itemView.findViewById(R.id.away_team);
        date = (TextView)itemView.findViewById(R.id.date_string);
        time = (TextView)itemView.findViewById(R.id.time_string);
        score = (TextView)itemView.findViewById(R.id.score_text);
        pick = (TextView)itemView.findViewById(R.id.pick);
    }
}
