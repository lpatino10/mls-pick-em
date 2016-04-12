package com.example.loganpatino.mlspickem;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
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
    protected TextView draw;

    public MatchViewHolder(View itemView) {
        super(itemView);
        homeLogo = (ImageView)itemView.findViewById(R.id.home_team);
        awayLogo = (ImageView)itemView.findViewById(R.id.away_team);
        date = (TextView)itemView.findViewById(R.id.date_string);
        time = (TextView)itemView.findViewById(R.id.time_string);
        draw = (TextView)itemView.findViewById(R.id.draw);
        homeLayout = (RelativeLayout)itemView.findViewById(R.id.homeLayout);
        awayLayout = (RelativeLayout)itemView.findViewById(R.id.awayLayout);
        drawLayout = (RelativeLayout)itemView.findViewById(R.id.drawLayout);

        /*homeLogo.setOnClickListener(new View.OnClickListener() {

            boolean clicked = false;

            @Override
            public void onClick(View v) {
                if (clicked) {
                    homeLogo.setBackgroundColor(Color.TRANSPARENT);
                    clicked = false;
                }
                else {
                    homeLogo.setBackgroundColor(0xFFBDBDBD);
                    draw.setBackgroundColor(Color.TRANSPARENT);
                    awayLogo.setBackgroundColor(Color.TRANSPARENT);
                    clicked = true;
                }
            }
        });

        awayLogo.setOnClickListener(new View.OnClickListener() {

            boolean clicked = false;

            @Override
            public void onClick(View v) {
                if (clicked) {
                    awayLogo.setBackgroundColor(Color.TRANSPARENT);
                    clicked = false;
                }
                else {
                    awayLogo.setBackgroundColor(0xFFBDBDBD);
                    draw.setBackgroundColor(Color.TRANSPARENT);
                    homeLogo.setBackgroundColor(Color.TRANSPARENT);
                    clicked = true;
                }
            }
        });

        draw.setOnClickListener(new View.OnClickListener() {

            boolean clicked = false;

            @Override
            public void onClick(View v) {
                if (clicked) {
                    draw.setBackgroundColor(Color.TRANSPARENT);
                    clicked = false;
                }
                else {
                    draw.setBackgroundColor(0xFFBDBDBD);
                    homeLogo.setBackgroundColor(Color.TRANSPARENT);
                    awayLogo.setBackgroundColor(Color.TRANSPARENT);
                    clicked = true;
                }
            }
        });*/


    }
}
