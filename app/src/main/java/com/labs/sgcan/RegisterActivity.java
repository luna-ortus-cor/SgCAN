package com.labs.sgcan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    //DB_guardian DB = new DB_guardian(this);
    FirebaseDatabase fbd = FirebaseDatabase.getInstance();
    String ID = "";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean b = settings.getBoolean("voicecontrol", false);
        final TextToSpeech tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        tts.setLanguage(Locale.US);

        final Button register = findViewById(R.id.regBtn);
        final TextView login = findViewById(R.id.loginText);
        final EditText username = findViewById(R.id.usernameField);
        final EditText fn = findViewById(R.id.fnField);
        final EditText ln = findViewById(R.id.lnField);
        final EditText phone = findViewById(R.id.phoneField);
        final EditText address = findViewById(R.id.addressField);
        final EditText pw = findViewById(R.id.passwordField);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = valid(username.getText().toString(), fn.getText().toString(), ln.getText().toString(), phone.getText().toString(),
                        address.getText().toString(), pw.getText().toString());
                switch(i){
                    case 0:
                        /*DB.insertData(username.getText().toString(), fn.getText().toString(), ln.getText().toString(), phone.getText().toString(),
                                address.getText().toString(), pw.getText().toString());*/
                        /*
                        DatabaseReference gCount = fbd.getReference("guardian_count/Count/count");

                        gCount.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String count = dataSnapshot.getValue(String.class);
                                update(count);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        saveGuardian(new Guardian(ID, username.getText().toString(), fn.getText().toString(), ln.getText().toString(), phone.getText().toString(),
                                address.getText().toString(), pw.getText().toString(), ""));
                        gCount.setValue(String.valueOf(Integer.valueOf(ID) + 1));
                        */

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("gCount");

                        // The query will search for a ParseObject, given its objectId.
                        // When the query finishes running, it will invoke the GetCallback
                        // with either the object, or the exception thrown
                        query.getInBackground("bFzqFvX4WV", new GetCallback<ParseObject>() {
                            public void done(final ParseObject result, ParseException e) {
                                if (e == null) {
                                    System.out.println(result);
                                    //update(result.getString("count"));
                                    //Toast.makeText(getApplicationContext(), result.getString("count"), Toast.LENGTH_SHORT).show();
                                    createObject(result.getString("count"), username.getText().toString(), fn.getText().toString(), ln.getText().toString(), phone.getText().toString(),
                                            address.getText().toString(), pw.getText().toString(), "");

                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("gCount");

                                    // Retrieve the object by id
                                    //gCount.increment("count");
                                    query.getInBackground("bFzqFvX4WV", new GetCallback<ParseObject>() {
                                        public void done(ParseObject entity, ParseException e) {
                                            if (e == null) {
                                                // Update the fields we want to
                                                entity.put("count", String.valueOf(Integer.valueOf(result.getString("count")) + 1));

                                                // All other fields will remain the same
                                                entity.saveInBackground();
                                            }
                                        }
                                    });
                                } else {
                                    // something went wrong
                                    Toast.makeText(getApplicationContext(), "oops", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });




                        Toast t = Toast.makeText(getApplicationContext(), "Registration Successful! Please login again.", Toast.LENGTH_SHORT);
                        t.show();
                        if(b){
                            tts.speak("Registration Successful! Please login again.", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        Intent it = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(it);
                        finish();
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
                    case 6:
                        Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
                        if(b){
                            tts.speak("Password cannot be empty", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        break;
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(it);
                finish();
            }
        });
    }

    public int valid(String username, String fn, String ln, String mobile, String address, String pw){
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
        } else if(pw.isEmpty()){
            return 6;
        }

        return 0;
    }
/*
    public void saveGuardian(Guardian g){
        DatabaseReference dr = fbd.getReference();
        dr
                .child("Guardians")
                .child(UUID.randomUUID().toString())
                .setValue(g.createGObject());
    }
    */

    public void update(String ID){
        this.ID = ID;
    }


    public void createObject(String ID, String username, String fn, String ln, String mobile, String address, String pw, String users) {
        ParseObject po = new ParseObject("Guardian");
        po.put("ID", ID);
        po.put("username", username);
        po.put("fn", fn);
        po.put("ln", ln);
        po.put("mobile", mobile);
        po.put("address", address);
        po.put("pw", pw);
        po.put("users", users);


        // Saves the new object.
        // Notice that the SaveCallback is totally optional!
        po.saveInBackground();
    }
}
