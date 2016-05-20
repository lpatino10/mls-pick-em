package com.example.loganpatino.mlspickem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton mSignInButton;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignInButton = (SignInButton)findViewById(R.id.sign_in_button);

        mSharedPreferences = getSharedPreferences(Utility.PREFS_FILE, Context.MODE_PRIVATE);
        if (mSharedPreferences.getBoolean(Utility.IS_USER_LOGGED_IN, false)) {
            mSharedPreferences.edit().putBoolean(Utility.IS_USER_LOGGED_IN, false).apply();
            //startActivity(new Intent(this, HomeFragment.class));
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("LOGIN", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String userId = acct.getId();
            Uri profilePic = acct.getPhotoUrl();
            String userName = acct.getDisplayName();
            saveId(userId);
            saveLoginStatus();
            saveProfileInfo(profilePic, userName, userId);
            startActivity(new Intent(this, NavigationDrawerActivity.class));
        } else {
            Log.d("LOGIN_TEST", "DIDNT WORK");
        }
    }

    private void saveId(String userId) {
        mSharedPreferences.edit().putString(Utility.LOGIN_ID, userId).apply();
    }

    private void saveLoginStatus() {
        mSharedPreferences.edit().putBoolean(Utility.IS_USER_LOGGED_IN, true).apply();
    }

    private void saveProfileInfo(Uri profilePic, String userName, String userId) {
        String profilePicString = String.valueOf(profilePic);

        DatabaseReference profileRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("profiles")
                .child(userId);

        Map<String, Object> profileInfo = new HashMap<>();
        profileInfo.put("name", userName);
        profileInfo.put("profilePic", profilePicString);
        profileInfo.put("correctPicks", 0);

        profileRef.updateChildren(profileInfo);
    }
}

