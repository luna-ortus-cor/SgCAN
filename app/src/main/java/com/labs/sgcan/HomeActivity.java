package com.labs.sgcan;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.View;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    public static final int LOCATION_UPDATE_MIN_DISTANCE = 0;
    public static final int LOCATION_UPDATE_MIN_TIME = 0;
    private String DB_ID;
    MapView mv;
    GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private LocationManager LM;
    private IntentIntegrator qrScan;
    TextView testText;
    Marker m;
    Marker mm;
    boolean hasmissing;
    String ID;
    public static SharedPreferences settings;
    boolean b;
    TextToSpeech ttss;



/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.activity_home, container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        //v.setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) v.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) v.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mv = v.findViewById(R.id.mapView);
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DB_ID = getIntent().getStringExtra("DB_ID");
        hasmissing = getIntent().getBooleanExtra("hasmissing", false);


        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor e = settings.edit();
        e.putInt("signed", 1);
        e.putString("DB_ID", DB_ID);
        //e.putBoolean("voicecontrol", false);
        e.apply();
        final boolean bb = settings.getBoolean("voicecontrol", false);
        final TextToSpeech tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        tts.setLanguage(Locale.US);
        ttss = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        ttss.setLanguage(Locale.US);
        b = bb;
        //ttss = tts;

        ParseQuery<ParseObject> q = ParseQuery.getQuery("Guardian");
        try{
            ParseObject signin = q.get(DB_ID);
            String id = signin.getString("ID");
            ID = id;
            ParseInstallation.getCurrentInstallation().put("signinID", id);
            ParseInstallation.getCurrentInstallation().saveInBackground();

        }catch(ParseException e1){

        }

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Guardian");
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Users");
        ParseObject p = null;
        try{
            p = query1.get(DB_ID);
            String id = p.getString("ID");
            query2.whereEqualTo("guardian", id);
            List<ParseObject> objects = query2.find();
            for(ParseObject o: objects){
                o.put("isScan", false);
                o.put("isNotSeen", false);
                o.save();
            }

        }catch(ParseException ee){

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        qrScan = new IntentIntegrator(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrScan.initiateScan();
            }
        });

        FloatingActionButton fab2 = findViewById(R.id.fab2);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mv = findViewById(R.id.mapView);
        mv.onCreate(mapViewBundle);
        mv.getMapAsync(this);

        testText  = findViewById(R.id.testText);
        testText.setBackgroundResource(R.drawable.drawable_border);

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
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
        getMenuInflater().inflate(R.menu.home, menu);
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
            Intent it1 = new Intent(HomeActivity.this, SettingsActivity.class);
            it1.putExtra("DB_ID", DB_ID);
            startActivity(it1);
            return true;
        } else if(id == R.id.action_signout){
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor e = settings.edit();
            e.putInt("signed", 0);
            e.putString("DB_ID", "NIL");
            e.apply();
            Intent it1 = new Intent(HomeActivity.this, MainActivity.class);
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


        } else if (id == R.id.nav_users) {
            Intent it1 = new Intent(HomeActivity.this, UsersActivity.class);
            it1.putExtra("DB_ID", DB_ID);
            it1.putExtra("hasmissing", false);
            startActivity(it1);

        } else if (id == R.id.nav_profile) {
            Intent it1 = new Intent(HomeActivity.this, ProfileActivity.class);
            it1.putExtra("DB_ID", DB_ID);
            it1.putExtra("hasmissing", false);
            startActivity(it1);

        } else if (id == R.id.nav_missing) {
            Intent it1 = new Intent(HomeActivity.this, MissingActivity.class);
            it1.putExtra("DB_ID", DB_ID);
            it1.putExtra("hasmissing", false);
            startActivity(it1);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mv.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mv.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mv.onStop();
    }
    @Override
    protected void onPause() {
        mv.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mv.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mv.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mv.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        //gmap.setMinZoomPreference(12);
        gmap.setIndoorEnabled(true);
        gmap.setBuildingsEnabled(true);
        gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);

        getCurrentLocation();
        mv.onResume();
        //add other stuff
    }

    private LocationListener LL = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                drawCurrentMarker(location);
                LM.removeUpdates(LL);
            } else {

            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void getCurrentLocation() {
        boolean isGPSEnabled = LM.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = LM.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location = null;
        try{
            if (!(isGPSEnabled || isNetworkEnabled)) {
                //Snackbar.make(mv, R.string.error_location_provider, Snackbar.LENGTH_INDEFINITE).show();
                Toast.makeText(getApplicationContext(), "Oops, no location data provided", Toast.LENGTH_SHORT).show();
                if(b){
                    ttss.speak("Oops, no location data provided", TextToSpeech.QUEUE_FLUSH, null, null);
                }
            } else {
                if (isNetworkEnabled) {
                    LM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, LL);
                    location = LM.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                if (isGPSEnabled) {
                    LM.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, LL);
                    location = LM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
            if (location != null) {
                drawCurrentMarker(location);
            }
        } catch(SecurityException e){
            System.out.println("oops");
        }
    }

    private void drawCurrentMarker(Location location) {
        if (gmap != null) {
            gmap.clear();
            try {
                m.remove();
            }catch(Exception e){

            }
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions mo = new MarkerOptions()
                    .position(gps)
                    .title("Current Position");
            m = gmap.addMarker(mo);
            if(hasmissing){
                double lon = getIntent().getDoubleExtra("missingLon", 200);
                double lat = getIntent().getDoubleExtra("missingLat", 200);
                if(lon != 200 && lat != 200){
                    try{
                        mm.remove();
                    }catch(Exception ee){

                    }
                    LatLng ll = new LatLng(lat, lon);
                    MarkerOptions options = new MarkerOptions()
                            .position(ll)
                            .title("Missing Person");
                    mm = gmap.addMarker(options);

                    //drawMissingMarker(lat, lon);

                }
            }
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
            query.whereEqualTo("guardian", ID);
            List<ParseObject> result = null;
            try{
                result = query.find();
            }catch(ParseException e){

            }
            if(result != null){
                for(ParseObject p: result){
                    if(!p.getString("lat").equals("NIL") && !p.getString("lon").equals("NIL")){
                        double lat = Double.valueOf(p.getString("lat"));
                        double lon = Double.valueOf(p.getString("lon"));
                        LatLng L = new LatLng(lat, lon);
                        MarkerOptions op = new MarkerOptions()
                                .position(L)
                                .title(p.getString("fn") + " " + p.getString("ln"));
                        gmap.addMarker(op);
                    }
                }
            }
            Toast.makeText(getApplicationContext(), "Loading Coordinates", Toast.LENGTH_SHORT).show();
            if(b){
                ttss.speak("Loading Coordinates", TextToSpeech.QUEUE_FLUSH, null, null);
            }
            //gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 12));
            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 9));

        }
    }
    private void drawMissingMarker(double lat, double lon) {
        if (gmap != null) {
            LatLng gps = new LatLng(lat, lon);
            MarkerOptions mo = new MarkerOptions()
                    .position(gps)
                    .title("Missing Person");
            Marker mm = gmap.addMarker(mo);
            //gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 12));
            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 9));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == 1){

        }
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
                if(b){
                    ttss.speak("No Data Found", TextToSpeech.QUEUE_FLUSH, null, null);
                }
            } else {
                try{
                    String sha = result.getContents();
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
                    query.whereEqualTo("sha256", sha);
                    List<ParseObject> p = null;
                    try{
                        p = query.find();
                    }catch(ParseException e){

                    }
                    final ParseObject o = p.get(0);

                    boolean isGPSEnabled = LM.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    boolean isNetworkEnabled = LM.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    System.out.println(isGPSEnabled + " " + isNetworkEnabled);

                    String lat = "";
                    String lon = "";
                    Location location = null;
                    String guardian = "";

                    //Toast.makeText(getApplicationContext(), String.valueOf(isGPSEnabled) + ", " + String.valueOf(isNetworkEnabled), Toast.LENGTH_SHORT).show();
                    try{
                        if (!isGPSEnabled && !isNetworkEnabled) {
                            //Snackbar.make(mv, R.string.error_location_provider, Snackbar.LENGTH_INDEFINITE).show();
                            Toast.makeText(getApplicationContext(), "Oops, no location data available", Toast.LENGTH_SHORT).show();
                            if (b) {
                                ttss.speak("Oops, no location data available", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                        else {
                            if (isNetworkEnabled) {
                                LM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, LL);
                                location = LM.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            }else{
                                LM.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, LL);
                                location = LM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            }
                        }

                        guardian = o.getString("guardian");

                        /*
                        if((location==null)){
                            Toast.makeText(getApplicationContext(), "NULL", Toast.LENGTH_SHORT).show();
                        }*/

                        if (location != null) {
                            //get altitude also?
                            lat = String.valueOf(location.getLatitude());
                            lon = String.valueOf(location.getLongitude());
                            o.put("lat", lat);
                            o.put("lon", lon);
                            o.saveInBackground();
                            drawCurrentMarker(location);

                            //differentiate by found or missing status? //in service
                            o.put("isScan", true);
                            o.put("isNotSeen", true);
                            o.save();

                            /*
                            if(o.getInt("status") == 0){
                                //not missing

                            } else {
                                //missing

                            }
                            */

                            /*
                            JSONObject push = new JSONObject();
                            try{
                                push.put("data", "One of your subjects has been found");
                            }catch(JSONException e){

                            }
                            ParsePush notif = new ParsePush();
                            notif.setData(push);
                            ParseQuery<ParseInstallation> pq = ParseQuery.getQuery(ParseInstallation.class);
                            guardian = o.getString("guardian");
                            pq.whereEqualTo("signinID", guardian);
                            notif.setQuery(pq);
                            notif.sendInBackground();
                            */

                        }
                    } catch(SecurityException e){
                        System.out.println("oops");
                        e.printStackTrace();
                    }

                    //phone msg call of guardian
                    //testText.setText(result.getContents());
                    if(o.getInt("status") == 1){
                        testText.setText(R.string.missingcall);
                        ParseQuery<ParseObject> q = ParseQuery.getQuery("Guardian");

                        q.whereEqualTo("ID", guardian);
                        List<ParseObject> oo = null;
                        try {
                            oo = q.find();
                        }catch(ParseException e){

                        }
                        ParseObject g = oo.get(0);
                        final String phone = g.getString("mobile");
                        testText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                                try {
                                    startActivityForResult(callIntent, 1);
                                }catch(SecurityException e){
                                    Toast.makeText(getApplicationContext(), "Please allow application to make and manage phone calls", Toast.LENGTH_SHORT).show();
                                    if(b){
                                        ttss.speak("Please allow application to make and manage phone calls", TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            }
                        });
                    }

                    //if qr contains data
                /*
                try {

                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    textViewName.setText(obj.getString("name"));
                    textViewAddress.setText(obj.getString("address"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
                */
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                    if(b){
                        ttss.speak("Error occurred", TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
