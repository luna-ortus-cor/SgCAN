package com.labs.sgcan;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateUsersActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_LOAD = 2;
    ImageView iv;
    String obj;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        final String DB_ID = getIntent().getStringExtra("DB_ID");
        final String objectID = getIntent().getStringExtra("instance");
        obj = objectID;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean b = settings.getBoolean("voicecontrol", false);
        final TextToSpeech tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        tts.setLanguage(Locale.US);

        final Button cancel = findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(UpdateUsersActivity.this, UsersActivity.class);
                it.putExtra("DB_ID", DB_ID);
                startActivity(it);
                finish();
            }
        });

        final Button save = findViewById(R.id.saveBtn);
        final ImageView im = findViewById(R.id.imageView);
        iv = im;
        final EditText fn = findViewById(R.id.fnField);
        final EditText ln = findViewById(R.id.lnField);
        final EditText phone = findViewById(R.id.phoneField);
        final EditText address = findViewById(R.id.addressField);
        final EditText condition = findViewById(R.id.conditionField);
        final EditText remark = findViewById(R.id.remarkField);

        ParseQuery<ParseObject> q = ParseQuery.getQuery("Users");
        ParseObject p = null;
        try{
            p = q.get(objectID);
        }catch(ParseException e){

        }
        ParseFile pf = p.getParseFile("profilepic");
        pf.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if(e == null){
                    Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
                    iv.setImageBitmap(b);
                }
            }
        });
        fn.setText(p.getString("fn"));
        ln.setText(p.getString("ln"));
        phone.setText(p.getString("mobile"));
        address.setText(p.getString("address"));
        condition.setText(p.getString("condition"));
        remark.setText(p.getString("remarks"));
        iv.setOnClickListener(new View.OnClickListener() {
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = valid(fn.getText().toString(), ln.getText().toString(), phone.getText().toString(), address.getText().toString(),
                        condition.getText().toString());
                switch(i){
                    case 0:
                        update(fn.getText().toString(), ln.getText().toString(), phone.getText().toString(), address.getText().toString(),
                                condition.getText().toString(), remark.getText().toString());
                        Toast.makeText(getApplicationContext(), "Particulars updated successfully!", Toast.LENGTH_SHORT).show();
                        if(b){
                            tts.speak("Particulars updated successfully!", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        Intent it = new Intent(UpdateUsersActivity.this, UsersActivity.class);
                        it.putExtra("DB_ID", DB_ID);
                        startActivity(it);
                        finish();
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

    public void update(String fn, String ln, String mobile, String address, String condition, String remarks){
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Users");
        ParseObject p = null;
        try{
            p = q.get(obj);
        }catch(ParseException e){

        }
        p.put("fn", fn);
        p.put("ln", ln);
        p.put("mobile", mobile);
        p.put("address", address);
        p.put("condition", condition);
        p.put("remarks", remarks);
        Bitmap b = ((BitmapDrawable)iv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imByte = baos.toByteArray();
        ParseFile pf = new ParseFile(p.getString("ID")+".png", imByte);
        p.put("profilepic", pf);
        try {
            p.save();
        }catch(ParseException e){

        }
        p.saveInBackground();

    }
}
