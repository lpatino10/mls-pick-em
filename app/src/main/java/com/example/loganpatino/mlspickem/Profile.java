package com.example.loganpatino.mlspickem;

import android.net.Uri;

/**
 * Created by loganpatino on 5/20/16.
 */

public class Profile {

    private String name;
    private String profilePic;
    private int correctPicks;

    public Profile() {

    }

    public Profile(String name, String profilePic, int correctPicks) {
        this.name = name;
        this.profilePic = profilePic;
        this.correctPicks = correctPicks;
    }

    public String getName() {
        return name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public int getCorrectPicks() {
        return correctPicks;
    }
}
