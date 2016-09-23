package com.example.loganpatino.mlspickem;

import android.net.Uri;

/**
 * Created by loganpatino on 5/20/16.
 */

public class Profile {

    private String name;
    private String profilePic;
    private int previousCorrectPicks;
    private int thisWeekCorrectPicks;
    private int totalCorrectPicks;

    public Profile() {

    }

    public Profile(String name, String profilePic, int previousCorrectPicks, int thisWeekCorrectPicks, int totalCorrectPicks) {
        this.name = name;
        this.profilePic = profilePic;
        this.previousCorrectPicks = previousCorrectPicks;
        this.thisWeekCorrectPicks = thisWeekCorrectPicks;
        this.totalCorrectPicks = totalCorrectPicks;
    }

    public String getName() {
        return name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public int getPreviousCorrectPicks() {
        return previousCorrectPicks;
    }

    public int getThisWeekCorrectPicks() {
        return thisWeekCorrectPicks;
    }

    public int getTotalCorrectPicks() {
        return totalCorrectPicks;
    }
}
