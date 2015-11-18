package com.example.loganpatino.mlspickem;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<GameInfo> schedule;
    private MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setContentView(R.layout.activity_main);

        // set action bar color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFF44336));

        // set fab color and icon
        final FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(0xFF448AFF));
        fab.setImageResource(R.drawable.ic_lock_outline_white_24dp);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        db = new MyDatabase(this);
        schedule = db.getMatches();

        recyclerView.setAdapter(new MyRecyclerAdapter(schedule));

        fab.setOnClickListener(new View.OnClickListener() {

            //boolean clicked = false;

            @Override
            public void onClick(View v) {
                /*if (clicked) {
                    fab.setImageResource(R.drawable.ic_lock_outline_white_24dp);
                    clicked = false;
                }
                else {
                    fab.setImageResource(R.drawable.ic_lock_open_white_24dp);
                    clicked = true;
                }*/

                startEdit(v);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public void startEdit(View v) {
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }


}
