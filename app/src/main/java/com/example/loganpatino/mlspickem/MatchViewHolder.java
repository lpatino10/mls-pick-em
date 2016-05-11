package com.example.loganpatino.mlspickem;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by loganpatino on 5/28/15.
 */
public class MatchViewHolder extends RecyclerView.ViewHolder {

    protected RelativeLayout homeLayout;
    protected RelativeLayout awayLayout;
    protected TextView date;
    protected TextView time;
    protected RelativeLayout drawLayout;
    protected ImageView homeLogo;
    protected ImageView awayLogo;
    protected TextView score;
    protected TextView pick;
    protected TextView homeWin;
    protected TextView draw;
    protected TextView awayWin;

    public MatchViewHolder(View itemView, final Utility.Screen cardType, final RecyclerView.Adapter<MatchViewHolder> adapter) {
        super(itemView);
        homeLogo = (ImageView)itemView.findViewById(R.id.home_team);
        awayLogo = (ImageView)itemView.findViewById(R.id.away_team);
        date = (TextView)itemView.findViewById(R.id.date_string);
        time = (TextView)itemView.findViewById(R.id.time_string);
        homeLayout = (RelativeLayout)itemView.findViewById(R.id.homeLayout);
        awayLayout = (RelativeLayout)itemView.findViewById(R.id.awayLayout);
        drawLayout = (RelativeLayout)itemView.findViewById(R.id.drawLayout);
        score = (TextView)itemView.findViewById(R.id.score_text);
        pick = (TextView)itemView.findViewById(R.id.pick);

        if (cardType == Utility.Screen.EDIT) {
            homeWin = (TextView)itemView.findViewById(R.id.home_win);
            draw = (TextView)itemView.findViewById(R.id.draw);
            awayWin = (TextView)itemView.findViewById(R.id.away_win);

            homeWin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utility.games.get(getAdapterPosition()).getSelection() == Utility.Selection.HOME_WIN) {
                        Utility.games.get(getAdapterPosition()).setSelection(Utility.Selection.NONE);
                    }
                    else {
                        Utility.games.get(getAdapterPosition()).setSelection(Utility.Selection.HOME_WIN);
                    }
                    adapter.notifyItemChanged(getAdapterPosition());
                }
            });

            draw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utility.games.get(getAdapterPosition()).getSelection() == Utility.Selection.DRAW) {
                        Utility.games.get(getAdapterPosition()).setSelection(Utility.Selection.NONE);
                    }
                    else {
                        Utility.games.get(getAdapterPosition()).setSelection(Utility.Selection.DRAW);
                    }
                    adapter.notifyItemChanged(getAdapterPosition());
                }
            });

            awayWin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utility.games.get(getAdapterPosition()).getSelection() == Utility.Selection.AWAY_WIN) {
                        Utility.games.get(getAdapterPosition()).setSelection(Utility.Selection.NONE);
                    }
                    else {
                        Utility.games.get(getAdapterPosition()).setSelection(Utility.Selection.AWAY_WIN);
                    }
                    adapter.notifyItemChanged(getAdapterPosition());
                }
            });
        }

    }
}
