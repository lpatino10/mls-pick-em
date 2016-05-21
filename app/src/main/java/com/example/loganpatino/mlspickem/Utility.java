package com.example.loganpatino.mlspickem;

import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by loganpatino on 4/16/16.
 */
public class Utility {

    private static final String FIRE = "Chicago Fire";
    private static final String RAPIDS = "Colorado Rapids";
    private static final String CREW = "Columbus Crew SC";
    private static final String DC = "D.C. United";
    private static final String DC_NO_PERIODS = "DC United";
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
    public static final String LOGIN_ID = "id";
    public static final String FIRST_GAME_DATE = "date";
    public static final String IS_USER_LOGGED_IN = "login";
    public static final String CURRENT_WEEK_CORRECT_PICKS = "picks";

    public static int gameCount;

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
        String yearString = String.valueOf(date.getYear());
        String dayString = String.valueOf(date.getDay());
        String monthStringNum = String.valueOf(date.getMonth());
        String monthString = new DateFormatSymbols().getMonths()[month-1];

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("E", Locale.US);

        Calendar c = Calendar.getInstance();
        Date d = null;
        try {
            d = dateFormat.parse(dayString + "/" + monthStringNum + "/" + yearString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(d);
        String dayOfWeek = dayOfWeekFormat.format(d);

        return dayOfWeek + ", " + monthString + " " + day;
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

    public static String getKeyFromGame(int i) {
        String home = games.get(i).getHome();
        String away = games.get(i).getAway();

        if (home.equals(DC)) {
            home = DC_NO_PERIODS;
        }
        else if (away.equals(DC)) {
            away = DC_NO_PERIODS;
        }

        String key = home + "_" + away;;
        return key;
    }

    public static Game getGameFromKey(String key) {
        int separator = key.indexOf('_');
        String home = key.substring(0, separator);
        String away = key.substring(separator+1);

        if (home.equals(DC_NO_PERIODS)) {
            home = DC;
        }
        else if (away.equals(DC_NO_PERIODS)) {
            away = DC;
        }

        for (int i = 0; i < games.size(); i++) {
            Game currentGame = games.get(i);
            if ((currentGame.getHome().equals(home)) && (currentGame.getAway().equals(away))) {
                return currentGame;
            }
        }

        return null;
    }

}
