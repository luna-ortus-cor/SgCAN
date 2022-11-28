package com.labs.sgcan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String DB_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DB_ID = getIntent().getStringExtra("DB_ID");

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean b = settings.getBoolean("voicecontrol", false);
        final TextToSpeech tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        tts.setLanguage(Locale.US);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        final EditText username = findViewById(R.id.usernameField);
        final EditText fn = findViewById(R.id.fnField);
        final EditText ln = findViewById(R.id.lnField);
        final EditText phone = findViewById(R.id.phoneField);
        final EditText address = findViewById(R.id.addressField);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Guardian");
        ParseObject p = null;
        try{
            p = query.get(DB_ID);
        }catch(ParseException e){

        }
        username.setText(p.getString("username"));
        fn.setText(p.getString("fn"));
        ln.setText(p.getString("ln"));
        phone.setText(p.getString("mobile"));
        address.setText(p.getString("address"));
        final ParseObject pp = p;
        Button save = findViewById(R.id.saveBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = valid(username.getText().toString(), fn.getText().toString(), ln.getText().toString(), phone.getText().toString(),
                        address.getText().toString());
                switch(i){
                    case 0:
                        pp.put("username", username.getText().toString());
                        pp.put("fn", fn.getText().toString());
                        pp.put("ln", ln.getText().toString());
                        pp.put("mobile", phone.getText().toString());
                        pp.put("address", address.getText().toString());
                        pp.saveInBackground();
                        Toast.makeText(getApplicationContext(), "Personal particulars updated successfully!", Toast.LENGTH_SHORT).show();
                        if(b){
                            tts.speak("Personal particulars updated successfully!", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
                        if(b){
                            tts.speak("Username cannot be empty", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "First name cannot be empty", Toast.LENGTH_SHORT).show();
                        if(b){
                            tts.speak("First name cannot be empty", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "Last name cannot be empty", Toast.LENGTH_SHORT).show();
                        if(b){
                            tts.speak("Last name cannot be empty", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        break;
                    case 4:
                        Toast.makeText(getApplicationContext(), "Phone number is invalid", Toast.LENGTH_SHORT).show();
                        if(b){
                            tts.speak("Phone number is invalid", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        break;
                    case 5:
                        Toast.makeText(getApplicationContext(), "Address cannot be empty", Toast.LENGTH_SHORT).show();
                        if(b){
                            tts.speak("Address cannot be empty", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        break;
                }


            }
        });
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
        getMenuInflater().inflate(R.menu.profile, menu);
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
            Intent it1 = new Intent(ProfileActivity.this, SettingsActivity.class);
            it1.putExtra("DB_ID", DB_ID);
            startActivity(it1);
            return true;
        }else if(id == R.id.action_signout){
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor e = settings.edit();
            e.putInt("signed", 0);
            e.putString("DB_ID", "NIL");
            e.apply();
            Intent it1 = new Intent(ProfileActivity.this, MainActivity.class);
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
            Intent it1 = new Intent(ProfileActivity.this, HomeActivity.class);
            it1.putExtra("DB_ID", DB_ID);
            it1.putExtra("hasmissing", false);
            startActivity(it1);
        } else if (id == R.id.nav_users) {
            Intent it1 = new Intent(ProfileActivity.this, UsersActivity.class);
            it1.putExtra("DB_ID", DB_ID);
            it1.putExtra("hasmissing", false);
            startActivity(it1);
        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_missing) {
            Intent it1 = new Intent(ProfileActivity.this, MissingActivity.class);
            it1.putExtra("DB_ID", DB_ID);
            it1.putExtra("hasmissing", false);
            startActivity(it1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public int valid(String username, String fn, String ln, String mobile, String address){
        if(username.isEmpty()){
            return 1;
        } else if(fn.isEmpty()){
            return 2;
        } else if(ln.isEmpty()){
            return 3;
        } else if(!mobile.matches("\\d{8}")) {
            return 4;
        } else if(address.isEmpty()){
            return 5;
        }

        return 0;
    }
}
