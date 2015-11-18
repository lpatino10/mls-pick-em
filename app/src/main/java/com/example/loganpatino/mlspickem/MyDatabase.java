package com.example.loganpatino.mlspickem;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by loganpatino on 5/17/15.
 */

public class MyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "MLS_Schedule_2015.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public List<GameInfo> getMatches() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        Date currentDate = new Date(); // initializes to current date
        Calendar currentCal = new GregorianCalendar();
        Calendar nextWeek = new GregorianCalendar(2015, Calendar.MAY, 18); // first Monday

        currentCal.setTime(currentDate);
        //Log.d("Time Test", currentCal.toString());

        while (nextWeek.getTime().before(currentCal.getTime())) { // moves nextWeek to next week's Monday
            nextWeek.add(Calendar.WEEK_OF_YEAR, 1);
        }

        Calendar lastWeek = new GregorianCalendar();
        lastWeek.setTime(nextWeek.getTime());
        lastWeek.add(Calendar.WEEK_OF_YEAR, -1); // lastWeek is 1 week before nextWeek

        //Log.d("startCal test", nextWeek.toString());
        //Log.d("lastWeek test", lastWeek.toString());


        String[] sqlSelect = {"0 _id", "Home_Team", "Away_Team", "Day", "Month", "Year", "Hour", "Minute"};
        String sqlTables = "MLS_Matches";

        //Log.d("Month Test", String.valueOf(nextWeek.get(Calendar.MONTH)));

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null); // month + 1 because Jan = 0

        c.moveToFirst();

        List<GameInfo> matches = new ArrayList<>();
        int position = 0;

        /*
         * prints out each matchup in the log!!
         */
        while (!c.isAfterLast()) {
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH); // sets numeric date with English format
            int month = c.getInt(4); // month column
            int day = c.getInt(3); // day column
            int year = c.getInt(5); // year column

            Date currentGame = new Date();

            try {
                currentGame = df.parse(month + "/" + day + "/" + year); // parses current date
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Log.d("CURRENT", df.format(currentGame));
            //Log.d("LAST", df.format(lastWeek.getTime()));

            //Log.d("comparison_test", String.valueOf(currentGame.after(lastWeek.getTime())));

            if (currentGame.before(nextWeek.getTime()) && currentGame.after(lastWeek.getTime())) { // if game is this week
                String s1 = c.getString(1); // home team column
                String s2 = c.getString(2); // away team column
                int homePicId = 0;
                int awayPicId = 0;

                try {
                    Class res = R.drawable.class;
                    Field field = res.getField(s1);
                    homePicId = field.getInt(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Class res = R.drawable.class;
                    Field field = res.getField(s2);
                    awayPicId = field.getInt(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int hour = c.getInt(6); // hour column
                String minute = c.getString(7); // minute column

                if (minute.equals("0")) {
                    minute = "00";
                }

                matches.add(position, new GameInfo(homePicId, awayPicId, hour, minute, day, month, year));
                position++;
            }

            c.moveToNext();
        }

        c.close();
        return matches;

    }

}
