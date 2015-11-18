package com.example.loganpatino.mlspickem;

import android.media.Image;

import java.text.DateFormatSymbols;

/**
 * Created by loganpatino on 5/29/15.
 */
public class GameInfo {
    private int homeLogo;
    private int awayLogo;
    private int hour;
    private String minutes;
    private int day;
    private int month;
    private int year;

    public GameInfo(int homeLogo, int awayLogo, int hour, String minutes, int day, int month, int year) {
        this.homeLogo = homeLogo;
        this.awayLogo = awayLogo;
        this.hour = hour;
        this.minutes = minutes;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getHomeLogo() {
        return homeLogo;
    }

    public int getAwayLogo() {
        return awayLogo;
    }

    public String getTime() {
        return hour + ":" + minutes;
    }

    public String getDate() {
        String monthStr = new DateFormatSymbols().getMonths()[month-1];
        return  monthStr + " " + day;
    }
}

