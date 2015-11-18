package com.example.loganpatino.mlspickem;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setContentView(R.layout.activity_edit);

        // set action bar color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFF44336));

        // set fab color and icon
        final FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(0xFF448AFF));
        fab.setImageResource(R.drawable.ic_lock_open_white_24dp);

        // get rid of back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        fab.setOnClickListener(new View.OnClickListener() {

            //boolean clicked = false;

            @Override
            public void onClick(View v) {

                backToMain(v);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // doing nothing, want users to use fab to go back
    }

    public void backToMain(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
