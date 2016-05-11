package com.example.loganpatino.mlspickem;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by loganpatino on 4/16/16.
 */
public class Game implements Parcelable {

    private String home;
    private String away;
    private int homeScore;
    private int awayScore;
    private Time time;
    private Date date;
    private Utility.Selection selection;

    protected Game(Parcel in) {
        home = in.readString();
        away = in.readString();
        homeScore = in.readInt();
        awayScore = in.readInt();
        selection = (Utility.Selection)in.readSerializable();
    }


    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

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

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Utility.Selection getSelection() {
        return selection;
    }

    public void setSelection(Utility.Selection selection) {
        this.selection = selection;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(home);
        dest.writeString(away);
        dest.writeInt(homeScore);
        dest.writeInt(awayScore);
        dest.writeSerializable(selection);
    }

    public class Time {

        private int hour;
        private int minute;

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }
    }

    public class Date {

        private int year;
        private int month;
        private int day;

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }
    }
}
