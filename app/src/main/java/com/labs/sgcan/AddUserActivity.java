package com.labs.sgcan;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AddUserActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_LOAD = 2;
    ImageView iv;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        final String DB_ID = getIntent().getStringExtra("DB_ID");

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean b = settings.getBoolean("voicecontrol", false);
        final TextToSpeech tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        tts.setLanguage(Locale.US);

        final Button cancel = findViewById(R.id.cancelBtn);
        final Button add = findViewById(R.id.addUserBtn);
        final EditText fn = findViewById(R.id.fnField);
        final EditText ln = findViewById(R.id.lnField);
        final EditText mobile = findViewById(R.id.mobileField);
        final EditText address = findViewById(R.id.addressField);
        final EditText condition = findViewById(R.id.conditionField);
        final EditText remarks = findViewById(R.id.remarkField);
        final ImageView pfp = findViewById(R.id.pfpView);
        iv = pfp;
        iv.setImageResource(R.drawable.navprofile);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(AddUserActivity.this, UsersActivity.class);
                it.putExtra("DB_ID", DB_ID);
                startActivity(it);
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = valid(fn.getText().toString(), ln.getText().toString(), mobile.getText().toString(), address.getText().toString(),
                        condition.getText().toString());
                switch(i){
                    case 0:
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("uCount");
                        query.getInBackground("hCQ5HpdirW", new GetCallback<ParseObject>() {
                            public void done(final ParseObject result, ParseException e) {
                                if (e == null) {
                                    System.out.println(result);
                                    //update(result.getString("count"));
                                    //Toast.makeText(getApplicationContext(), result.getString("count"), Toast.LENGTH_SHORT).show();
                                    createObject(result.getString("count"), fn.getText().toString(), ln.getText().toString(), mobile.getText().toString(), address.getText().toString(),
                                            condition.getText().toString(), remarks.getText().toString(), DB_ID);

                                    ParseQuery<ParseObject> q = ParseQuery.getQuery("Guardian");
                                    //q.whereEqualTo("objectID", DB_ID);
                                    //List<ParseObject> results = null;
                                    try{
                                        //results = q.find();
                                        //ParseObject p = results.get(0);
                                        ParseObject p = q.get(DB_ID);
                                        String original = p.getString("users");
                                        String updated = original + result.getString("count") + ",";
                                        p.put("users", updated);
                                        p.saveInBackground();
                                        //update list of user under guardian
                                    }catch(ParseException ee){

                                    }

                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("uCount");

                                    // Retrieve the object by id
                                    //gCount.increment("count");
                                    query.getInBackground("hCQ5HpdirW", new GetCallback<ParseObject>() {
                                        public void done(ParseObject entity, ParseException e) {
                                            if (e == null) {
                                                // Update the fields we want to
                                                entity.put("count", String.valueOf(Integer.valueOf(result.getString("count")) + 1));

                                                // All other fields will remain the same
                                                entity.saveInBackground();
                                            }
                                        }
                                    });
                                    Toast.makeText(getApplicationContext(), "Successfully added user!", Toast.LENGTH_SHORT).show();
                                    if(b){
                                        tts.speak("Successfully added user!", TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                    Intent it = new Intent(AddUserActivity.this, UsersActivity.class);
                                    it.putExtra("DB_ID", DB_ID);
                                    startActivity(it);
                                    finish();
                                } else {
                                    // something went wrong
                                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                                    if(b){
                                        tts.speak("Error occurred", TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            }
                        });
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "First name cannot be empty", Toast.LENGTH_SHORT).show();
                        if(b){
                            tts.speak("First name cannot be empty", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "Last name cannot be empty", Toast.LENGTH_SHORT).show();
                        if(b){
                            tts.speak("Last name cannot be empty", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "Phone number is invalid", Toast.LENGTH_SHORT).show();
                        if(b){
                            tts.speak("Phone number is invalid", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        break;
                    case 4:
                        Toast.makeText(getApplicationContext(), "Address cannot be empty", Toast.LENGTH_SHORT).show();
                        if(b){
                            tts.speak("Address cannot be empty", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        break;
                    case 5:
                        Toast.makeText(getApplicationContext(), "Condition cannot be empty", Toast.LENGTH_SHORT).show();
                        if(b){
                            tts.speak("Condition cannot be empty", TextToSpeech.QUEUE_FLUSH, null, null);
                        }

                        break;

                }



            }
        });

        pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder pfp = new AlertDialog.Builder(view.getContext());
                LayoutInflater factory = LayoutInflater.from(view.getContext());
                final View v = factory.inflate(R.layout.layout_pfp_selection, null);
                pfp.setView(v);
                pfp.setPositiveButton(R.string.takepicture, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dispatchTakePictureIntent();
                    }
                });

                pfp.setNeutralButton(R.string.takegallery, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dispatchGalleryIntent();
                    }
                });
                pfp.show();
            }
        });

    }

    public void createObject(String ID, String fn, String ln, String mobile, String address, String condition, String remarks, String dbid) {
        try{
            ParseObject po = new ParseObject("Users");
            ParseQuery<ParseObject> q = ParseQuery.getQuery("Guardian");

            //q.whereEqualTo("objectID", dbid);
            //List<ParseObject> results = null;
            ParseObject p = new ParseObject("Guardian");
            try{
                //results = q.find();
                p = q.get(dbid);
                //p = results.get(0);
                //update list of user under guardian
            }catch(ParseException ee){

            }
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ID.getBytes(StandardCharsets.UTF_8));
            po.put("ID", ID);
            po.put("fn", fn);
            po.put("ln", ln);
            po.put("mobile", mobile);
            po.put("address", address);
            po.put("condition", condition);
            po.put("remarks", remarks);
            po.put("guardian", p.getString("ID"));
            po.put("status", 0); //0 for ok, 1 for missing
            po.put("sha256", Base64.getEncoder().encodeToString(hash));
            po.put("lat", "NIL");
            po.put("lon", "NIL");
            Bitmap b = ((BitmapDrawable)iv.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imByte = baos.toByteArray();
            ParseFile pf = new ParseFile(ID+".png", imByte);
            po.put("profilepic", pf);
            po.put("isScan", false);
            po.put("isNotSeen", false);

            // Saves the new object.
            // Notice that the SaveCallback is totally optional!
            try{
                po.save();
            }catch(ParseException e){

            }
            po.saveInBackground();
        }catch(NoSuchAlgorithmException e){

        }

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void dispatchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_LOAD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            iv.setImageBitmap(imageBitmap);
        }

        if (requestCode == REQUEST_IMAGE_LOAD && resultCode == RESULT_OK){
            Uri target = data.getData();
            Bitmap bitmap;
            try{
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(target));
                iv.setImageBitmap(bitmap);
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    public int valid(String fn, String ln, String mobile, String address, String condition){
        if(fn.isEmpty()){
            return 1;
        } else if(ln.isEmpty()){
            return 2;
        } else if(!mobile.matches("\\d{8}")) {
            return 3;
        } else if(address.isEmpty()){
            return 4;
        } else if(condition.isEmpty()){
            return 5;
        }
        return 0;
    }
}
