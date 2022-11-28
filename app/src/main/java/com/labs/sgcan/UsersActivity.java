package com.labs.sgcan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class UsersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String DB_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);



        final String DB_ID = getIntent().getStringExtra("DB_ID");
        this.DB_ID = DB_ID;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(UsersActivity.this, AddUserActivity.class);
                it.putExtra("DB_ID", DB_ID);
                startActivity(it);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Guardian");


        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager LM = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Guardian");
        ParseObject parse = new ParseObject("Guardian");
        try{
            parse = q.get(DB_ID);
        } catch(ParseException e){

        }

        ParseQuery<ParseObject> p = ParseQuery.getQuery("Users");
        p.whereEqualTo("guardian", parse.getString("ID"));
        List<ParseObject> users = null;
        try{
            users = p.find();
            if(!users.isEmpty()){
                UsersAdapter adapter = new UsersAdapter(this, users);
                rv.setLayoutManager(LM);
                rv.setAdapter(adapter);
                rv.setVisibility(View.VISIBLE);
            }
        }catch(ParseException e){

        }



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.users, menu);
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
            Intent it1 = new Intent(UsersActivity.this, SettingsActivity.class);
            it1.putExtra("DB_ID", DB_ID);
            startActivity(it1);
            return true;
        } else if(id == R.id.action_signout){
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor e = settings.edit();
            e.putInt("signed", 0);
            e.putString("DB_ID", "NIL");
            e.apply();
            Intent it1 = new Intent(UsersActivity.this, MainActivity.class);
            startActivity(it1);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            Intent it = new Intent(UsersActivity.this, HomeActivity.class);
            it.putExtra("DB_ID", DB_ID);
            startActivity(it);

        } else if (id == R.id.nav_users) {

        } else if (id == R.id.nav_profile) {
            Intent it = new Intent(UsersActivity.this, ProfileActivity.class);
            it.putExtra("DB_ID", DB_ID);
            startActivity(it);

        } else if (id == R.id.nav_missing) {
            Intent it = new Intent(UsersActivity.this, MissingActivity.class);
            it.putExtra("DB_ID", DB_ID);
            startActivity(it);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
