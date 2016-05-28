package com.example.loganpatino.mlspickem;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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
import java.util.Map;

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

    public static int totalGamesPlayed;
    public static boolean hasDataBeenLoaded;

    public enum Selection {
        HOME_WIN,
        DRAW,
        AWAY_WIN,
        NONE
    }

    public static Map<Game, Selection> gameMap;
    public static List<Game> gameList;
    public static String startDate;
    public static String endDate;

    public static String getDisplayDate(String date) {
        String year = String.valueOf(getYearFromDateString(date));
        int month = getMonthFromDateString(date);
        String monthStr = new DateFormatSymbols().getMonths()[month-1];
        String day = String.valueOf(getDayFromDateString(date));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("E", Locale.US);

        Calendar c = Calendar.getInstance();
        Date d = null;
        try {
            d = dateFormat.parse(day + "/" + month + "/" + year);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(d);
        String dayOfWeek = dayOfWeekFormat.format(d);

        return dayOfWeek + ", " + monthStr + " " + day;
    }

    public static String getDisplayTime(String time) {
        int hour = getHourFromTimeString(time);
        String timeOfDay;
        if (hour <= 12) {
            timeOfDay = " AM";
        }
        else {
            timeOfDay = " PM";
        }

        String hourStr = String.valueOf(hour%12);
        String minute = String.valueOf(getMinuteFromTimeString(time));

        if (minute.equals("0")) {
            minute = "00";
        }

        return hourStr + ":" + minute + timeOfDay;
    }

    public static String getDateString(int year, int month, int day) {
        String monthStr = String.valueOf(month);
        if (monthStr.length() < 2) {
            monthStr = "0" + monthStr;
        }

        String dayString = String.valueOf(day);
        if (dayString.length() < 2) {
            dayString = "0" + dayString;
        }

        return year + "-" + monthStr + "-" + dayString;
    }

    public static boolean haveGamesStarted() {
        boolean result = false;

        String firstGameDate = Utility.gameList.get(0).getDate();
        int firstGameYear = getYearFromDateString(firstGameDate);
        int firstGameMonth = getMonthFromDateString(firstGameDate);
        int firstGameDay = getDayFromDateString(firstGameDate);

        String firstGameTime = Utility.gameList.get(0).getTime();
        int firstGameHour = getHourFromTimeString(firstGameTime);
        int firstGameMin = getMinuteFromTimeString(firstGameTime);

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

    public static String getKeyFromGame(Game game) {
        String home = game.getHome();
        String away = game.getAway();

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

        for (int i = 0; i < gameList.size(); i++) {
            Game currentGame = gameList.get(i);
            if ((currentGame.getHome().equals(home)) && (currentGame.getAway().equals(away))) {
                return currentGame;
            }
        }

        return null;
    }

    public static Selection getSelectionFromString(String selectionStr) {
        Selection selection;

        switch (selectionStr) {
            case "Home Win":
                selection = Selection.HOME_WIN;
                break;
            case "Away Win":
                selection = Selection.AWAY_WIN;
                break;
            case "Draw":
                selection = Selection.DRAW;
                break;
            default:
                selection = Selection.NONE;
        }

        return selection;
    }

    public static String getStringFromSelection(Selection selection) {
        String selectionStr;

        if (selection == Selection.HOME_WIN) {
            selectionStr = "Home Win";
        }
        else if (selection == Selection.AWAY_WIN) {
            selectionStr = "Away Win";
        }
        else if (selection == Selection.DRAW) {
            selectionStr = "Draw";
        }
        else {
            selectionStr = "None";
        }

        return selectionStr;
    }

    public static int getTextColorFromSelection(Game game, Selection selection) {
        int homeScore = game.getHomeScore();
        int awayScore = game.getAwayScore();
        int textColor;

        if (homeScore > awayScore) {
            if (selection == Utility.Selection.HOME_WIN) {
                textColor = Color.GREEN;
            }
            else {
                textColor = Color.RED;
            }
        }
        else if (awayScore > homeScore) {
            if (selection == Utility.Selection.AWAY_WIN) {
                textColor = Color.GREEN;
            }
            else {
                textColor = Color.RED;
            }
        }
        else if ((homeScore != -1) && (homeScore == awayScore)) {
            if (selection == Utility.Selection.DRAW) {
                textColor = Color.GREEN;
            }
            else {
                textColor = Color.RED;
            }
        }
        else {
            textColor = -1;
        }

        return textColor;
    }

    public static int getYearFromDateString(String date) {
        int firstSeparator = date.indexOf('-');
        return Integer.valueOf(date.substring(0, firstSeparator));
    }

    public static int getMonthFromDateString(String date) {
        int firstSeparator = date.indexOf('-');
        String monthAndDay = date.substring(firstSeparator+1);
        int secondSeparator = monthAndDay.indexOf('-');
        return Integer.valueOf(monthAndDay.substring(0, secondSeparator));
    }

    public static int getDayFromDateString(String date) {
        int firstSeparator = date.indexOf('-');
        String monthAndDay = date.substring(firstSeparator+1);
        int secondSeparator = monthAndDay.indexOf('-');
        return Integer.valueOf(monthAndDay.substring(secondSeparator+1));
    }

    public static int getHourFromTimeString(String time) {
        int separator = time.indexOf(':');
        return Integer.valueOf(time.substring(0, separator));
    }

    public static int getMinuteFromTimeString(String time) {
        int separator = time.indexOf(':');
        return Integer.valueOf(time.substring(separator+1));
    }

}
