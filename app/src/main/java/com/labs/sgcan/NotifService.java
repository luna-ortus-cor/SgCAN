package com.labs.sgcan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.core.app.NotificationCompat;

public class NotifService extends Service {
    private static Timer timer = new Timer();
    //final SharedPreferences settings = HomeActivity.settings;
    public NotifService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        startService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }


    public void startService(){
        timer.scheduleAtFixedRate(new mainTask(), 0, 5000);
    }

    public class mainTask extends TimerTask{
        @Override
        public void run(){
            SharedPreferences settings = App.settings;
            String DB_ID = settings.getString("DB_ID", "NIL");
            System.out.println(DB_ID);
            if(!DB_ID.equals("NIL")){
                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Guardian");
                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Users");
                ParseObject p = null;
                try{
                    p = query1.get(DB_ID);
                    String id = p.getString("ID");
                    query2.whereEqualTo("guardian", id);
                    List<ParseObject> objects = query2.find();
                    for(ParseObject o: objects){
                        if(o.getBoolean("isScan") && o.getBoolean("isNotSeen")){
                            System.out.println("DETECTED");
                            //send notif
                            if(o.getInt("status") == 0){
                                Intent notifIntent = new Intent(getBaseContext(), HomeActivity.class);
                                notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                PendingIntent pi = PendingIntent.getActivity(getBaseContext(), 0, notifIntent, 0);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), "0")
                                        .setSmallIcon(R.drawable.logo)
                                        .setContentTitle("Important!")
                                        .setContentText(o.getString("fn") + " " + o.getString("ln") + " was seen")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                        .setLights(Color.RED, 1000, 1000)
                                        .setAutoCancel(true)
                                        .setContentIntent(pi);
                                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                NotificationChannel nc = new NotificationChannel("0", "notmissing", NotificationManager.IMPORTANCE_HIGH);
                                nm.createNotificationChannel(nc);
                                builder.setChannelId("0");
                                nm.notify(0, builder.build());
                                o.put("isScan", false);
                                o.save();

                            } else {
                                //missing
                                Intent notifIntent1 = new Intent(getBaseContext(), HomeActivity.class);
                                notifIntent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                PendingIntent pi1 = PendingIntent.getActivity(getBaseContext(), 0, notifIntent1, 0);
                                NotificationCompat.Builder builder1 = new NotificationCompat.Builder(getBaseContext(), "1")
                                        .setSmallIcon(R.drawable.logo)
                                        .setContentTitle("Important!")
                                        .setContentText(o.getString("fn") + " " + o.getString("ln") + " was found")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                        .setLights(Color.RED, 1000, 1000)
                                        .setAutoCancel(true)
                                        .setContentIntent(pi1);
                                NotificationManager nm1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                NotificationChannel nc1 = new NotificationChannel("1", "notmissing", NotificationManager.IMPORTANCE_HIGH);
                                nm1.createNotificationChannel(nc1);
                                builder1.setChannelId("1");
                                nm1.notify(1, builder1.build());
                                o.put("isScan", false);
                                o.save();

                            }
                        }
                    }

                }catch(ParseException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void createNotificationChannel(String CHANNEL_ID, String name, String description) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
