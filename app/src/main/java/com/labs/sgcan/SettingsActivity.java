package com.labs.sgcan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean b = settings.getBoolean("voicecontrol", false);

        final String DB_ID = getIntent().getStringExtra("DB_ID");

        final LinearLayout pwLL = findViewById(R.id.pwLL);
        pwLL.setVisibility(View.GONE);

        final EditText old = findViewById(R.id.oldpw);
        final EditText newpw = findViewById(R.id.newpw);
        final EditText newpw2 = findViewById(R.id.checkpw);

        final TextView pwchange = findViewById(R.id.pwchange);
        pwchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pwLL.getVisibility() == View.GONE){
                    pwLL.setVisibility(View.VISIBLE);

                } else if(pwLL.getVisibility() == View.VISIBLE){
                    pwLL.setVisibility(View.GONE);
                }

            }
        });

        final TextToSpeech tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        tts.setLanguage(Locale.US);


        Button change = findViewById(R.id.changeBtn);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Guardian");
                ParseObject p = null;
                try{
                    p = query.get(DB_ID);
                }catch(ParseException e){

                }
                String pw = p.getString("pw");
                if(old.getText().toString().equals(pw)){
                    if(!newpw.getText().toString().isEmpty() && !newpw2.getText().toString().isEmpty()){
                        if(newpw.getText().toString().equals(newpw2.getText().toString())){

                            pwLL.setVisibility(View.GONE);
                            p.put("pw", newpw.getText().toString());
                            try{
                                p.save();
                            }catch(ParseException ee){
                                p.saveInBackground();
                            }
                            Toast.makeText(getApplicationContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                            if(b){
                                tts.speak("Password changed successfully", TextToSpeech.QUEUE_FLUSH, null, null);
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Retyped password does not match", Toast.LENGTH_SHORT).show();
                            if(b){
                                tts.speak("Retyped password does not match", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
                        if(b){
                            tts.speak("Password cannot be empty", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Current password incorrect", Toast.LENGTH_SHORT).show();
                    if(b){
                        tts.speak("Current password incorrect", TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }

            }
        });

        Button back = findViewById(R.id.backBtn);
        Button save = findViewById(R.id.saveBtn);

        final Switch s = findViewById(R.id.voicecontrol);
        s.setChecked(b);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent it = new Intent(SettingsActivity.this, getCallingActivity().getClass());
                it.putExtra("DB_ID", DB_ID);
                startActivity(it);*/
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to be updated when we have new settings
                /*
                Intent it = new Intent(SettingsActivity.this, getCallingActivity().getClass());
                it.putExtra("DB_ID", DB_ID);
                startActivity(it);*/

                boolean b = s.isChecked();
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor e = settings.edit();
                e.putBoolean("voicecontrol", b);
                e.apply();
                Toast.makeText(getApplicationContext(), "Settings changed successfully", Toast.LENGTH_SHORT).show();
                if(b){
                    tts.speak("Settings changed successfully", TextToSpeech.QUEUE_FLUSH, null, null);
                }

                finish();
            }
        });


    }
}
