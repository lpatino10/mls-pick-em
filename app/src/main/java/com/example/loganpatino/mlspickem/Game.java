package com.example.loganpatino.mlspickem;

/**
 * Created by loganpatino on 4/16/16.
 */

public class Game {

    private String home;
    private String away;
    private int homeScore;
    private int awayScore;
    private String time;
    private String date;
    private Utility.Selection selection;

    public Game() {

    }

    public Game(String home, String away, int homeScore, int awayScore, String time, String date) {
        this.home = home;
        this.away = away;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.time = time;
        this.date = date;
        selection = Utility.Selection.NONE;
    }


    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getAway() {
        return away;
    }

    public void setAway(String away) {
        this.away = away;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Utility.Selection getSelection() {
        return selection;
    }

    public void setSelection(Utility.Selection selection) {
        this.selection = selection;
    }
}
