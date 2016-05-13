package com.example.loganpatino.mlspickem;

import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by loganpatino on 4/16/16.
 */
public class Utility {

    private static final String FIRE = "Chicago Fire";
    private static final String RAPIDS = "Colorado Rapids";
    private static final String CREW = "Columbus Crew SC";
    private static final String DC = "D.C. United";
    private static final String DALLAS = "FC Dallas";
    private static final String DYNAMO = "Houston Dynamo";
    private static final String GALAXY = "LA Galaxy";
    private static final String IMPACT = "Montreal Impact";
    private static final String REVOLUTION = "New England Revolution";
    private static final String NYC = "New York City FC";
    private static final String RED_BULLS = "New York Red Bulls";
    private static final String ORLANDO = "Orlando City SC";
    private static final String UNION = "Philadelphia Union";
    private static final String TIMBERS = "Portland Timbers";
    private static final String RSL = "Real Salt Lake";
    private static final String EARTHQUAKES = "San Jose Earthquakes";
    private static final String SOUNDERS = "Seattle Sounders FC";
    private static final String SKC = "Sporting Kansas City";
    private static final String TORONTO = "Toronto FC";
    private static final String WHITECAPS = "Vancouver Whitecaps FC";

    public static final String PREFS_FILE = "prefs";
    public static final String KEYS = "keys";

    public enum Selection {
        HOME_WIN,
        DRAW,
        AWAY_WIN,
        NONE
    }

    public static ArrayList<Game> games;

    public static String getDateString(Game.Date date) {
        int day = date.getDay();
        int month = date.getMonth();

        String monthString = new DateFormatSymbols().getMonths()[month-1];

        return monthString + " " + day;
    }

    public static String getTimeString(Game.Time time) {
        String minuteStr = "00";
        if (time.getMinute() != 0) {
            minuteStr = String.valueOf(time.getMinute());
        }
        return time.getHour() + ":" + minuteStr + " PM";
    }

    public static boolean haveGamesStarted() {
        boolean result = false;

        Game.Date firstGameDate = Utility.games.get(0).getDate();
        int firstGameYear = firstGameDate.getYear();
        int firstGameMonth = firstGameDate.getMonth();
        int firstGameDay = firstGameDate.getDay();
        Game.Time firstGameTime = Utility.games.get(0).getTime();
        int firstGameHour = firstGameTime.getHour() + 12;
        int firstGameMin = firstGameTime.getMinute();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        if ((year >= firstGameYear) && (month >= firstGameMonth) && (day > firstGameDay)) {
            result = true;
        }
        else if ((year == firstGameYear) && (month == firstGameMonth) && (day == firstGameDay)) {
            if (hour > firstGameHour) {
                result = true;
            }
            else if (hour == firstGameHour) {
                if (min >= firstGameMin) {
                    result = true;
                }
            }
        }

        return result;
    }

    public static int getLogoResource(String teamName) {
        int id;

        switch (teamName) {
            case FIRE:
                id = R.drawable.fire;
                break;
            case RAPIDS:
                id = R.drawable.rapids;
                break;
            case CREW:
                id = R.drawable.crew;
                break;
            case DC:
                id = R.drawable.dc;
                break;
            case DALLAS:
                id = R.drawable.dallas;
                break;
            case DYNAMO:
                id = R.drawable.dynamo;
                break;
            case GALAXY:
                id = R.drawable.galaxy;
                break;
            case IMPACT:
                id = R.drawable.impact;
                break;
            case REVOLUTION:
                id = R.drawable.revs;
                break;
            case NYC:
                id = R.drawable.nyc;
                break;
            case RED_BULLS:
                id = R.drawable.rbny;
                break;
            case ORLANDO:
                id = R.drawable.orlando;
                break;
            case UNION:
                id = R.drawable.philly;
                break;
            case TIMBERS:
                id = R.drawable.portland;
                break;
            case RSL:
                id = R.drawable.rsl;
                break;
            case EARTHQUAKES:
                id = R.drawable.quakes;
                break;
            case SOUNDERS:
                id = R.drawable.seattle;
                break;
            case SKC:
                id = R.drawable.skc;
                break;
            case TORONTO:
                id = R.drawable.tfc;
                break;
            case WHITECAPS:
                id = R.drawable.vancouver;
                break;
            default:
                id = R.drawable.crew;
        }

        return id;
    }

}
