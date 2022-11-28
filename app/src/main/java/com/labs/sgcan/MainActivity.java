package com.labs.sgcan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    //DB_guardian DB = new DB_guardian(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final TextView register = findViewById(R.id.registerText);
        final Button login = findViewById(R.id.loginBtn);
        final EditText username = findViewById(R.id.userField);
        final EditText pw = findViewById(R.id.pwField);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(it);
            }
        });
        if(settings.getInt("signed", -1) == 1){
            Intent it = new Intent(MainActivity.this, HomeActivity.class);
            it.putExtra("DB_ID", settings.getString("DB_ID", "NIL"));
            startActivity(it);
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String USER = username.getText().toString();
                String PW = pw.getText().toString();

                //comment out below after initialization
                /*
                if(USER.equals("init") && PW.equals("init")){
                    ParseObject po = new ParseObject("gCount");
                    po.put("count", "0");
                    po.saveInBackground();

                    ParseObject po2 = new ParseObject("uCount");
                    po2.put("count", "0");
                    po2.saveInBackground();
                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                }
                */

                /*
                SharedPreferences.Editor e = settings.edit();
                String id = "";
                e.putString("DB_ID", id);
                int signed = 0;
                e.putInt("signed", signed);
                e.apply();
                */

                //Toast.makeText(getApplicationContext(), String.valueOf(settings.getInt("signed", -1)), Toast.LENGTH_SHORT).show();





                boolean logged = false;
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Guardian");
                query.whereEqualTo("username", USER);
                List<ParseObject> results = null;
                try{
                    results = query.find();
                    if(!results.isEmpty()){
                        for(ParseObject p: results){
                            if(PW.equals(p.getString("pw"))){
                                logged = true;
                                String objectID = p.getObjectId();
                                Intent it = new Intent(MainActivity.this, HomeActivity.class);
                                it.putExtra("DB_ID", objectID);
                                it.putExtra("hasmissing", false);
                                startActivity(it);
                                finish();
                                break;
                            }
                        }
                    }
                }catch(ParseException ee){

                }
                /*
                Cursor res = DB.getAllData();
                while(res.moveToNext()){
                    if(USER.equals(res.getString(1)) && PW.equals(res.getString(6))){
                        logged = true;
                        Intent it = new Intent(MainActivity.this, HomeActivity.class);
                        it.putExtra("DB_ID", res.getString(0));
                        startActivity(it);
                        finish();
                    }
                }*/

                if(!logged){
                    Toast.makeText(getApplicationContext(), "Incorrect login credentials, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
