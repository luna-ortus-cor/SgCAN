package com.labs.sgcan;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolders> {
    private Context context;
    private List<ParseObject> userslist;

    public class ViewHolders extends RecyclerView.ViewHolder{
        private TextView name, mobile, address, condition, remark, location;
        private ImageView iv;
        private View vview;
        public ViewHolders(View v){
            super(v);
            this.vview = v;
            name = v.findViewById(R.id.nameText);
            mobile = v.findViewById(R.id.mobileText);
            address = v.findViewById(R.id.addressText);
            location = v.findViewById(R.id.locationText);
            condition = v.findViewById(R.id.conditionText);
            remark = v.findViewById(R.id.remarkText);
            iv = v.findViewById(R.id.profilepic);

        }

        public View getView(){
            return this.vview;
        }
    }

    public UsersAdapter(Context context, List<ParseObject> userslist){
        this.context = context;
        this.userslist = userslist;
    }

    @Override
    public ViewHolders onCreateViewHolder(ViewGroup parent, int viewType){
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cardview, parent, false);
        return new ViewHolders(itemview);
    }

    @Override
    public void onBindViewHolder(final ViewHolders vh, int position){
        final ParseObject p = userslist.get(position);
        final int pos = position;
        vh.name.setText(p.getString("fn") + " " + p.getString("ln"));
        vh.mobile.setText(p.getString("mobile"));
        vh.address.setText(p.getString("address"));
        vh.condition.setText(p.getString("condition"));
        if(p.getString("remarks").equals("")){
            vh.remark.setText("NIL");
        } else {
            vh.remark.setText(p.getString("remarks"));
        }
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            final double llat = Double.valueOf(p.getString("lat"));
            final double llon = Double.valueOf(p.getString("lon"));
            addresses = gcd.getFromLocation(llat, llon, 1);
            if (addresses.size() > 0) {
                Address a = addresses.get(0);
                String place = a.toString();
                place = a.getAddressLine(0);
                vh.location.setText(place);
            }
        }catch(IOException e){

        }catch(NumberFormatException ee){
            vh.location.setText("No available last seen location");
        }

        ParseFile pf = p.getParseFile("profilepic");
        pf.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if(e == null){
                    Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
                    vh.iv.setImageBitmap(b);
                }
            }
        });

        final View vv = vh.getView();
        if(p.getInt("status") == 1){
            vv.setBackgroundResource(R.color.missing);
        } else {
            vv.setBackgroundResource(R.color.transparent);
        }
        vv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder userinfo = new AlertDialog.Builder(view.getContext());
                LayoutInflater factory = LayoutInflater.from(view.getContext());
                final View v = factory.inflate(R.layout.layout_additional_info, null);

                TextView tv = v.findViewById(R.id.SHAtext);
                ImageView iv = v.findViewById(R.id.qrView);
                tv.setText(p.getString("sha256"));
                try{
                    iv.setImageBitmap(encodeAsBitmap(p.getString("sha256")));
                }catch(WriterException e){

                }
                userinfo.setView(v);
                userinfo.setPositiveButton("Edit User", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String g = p.getString("guardian");
                        ParseQuery<ParseObject> pp = ParseQuery.getQuery("Guardian");
                        pp.whereEqualTo("ID", g);
                        List<ParseObject> l = null;
                        try{
                            l = pp.find();
                        }catch(ParseException e){

                        }
                        ParseObject o = l.get(0);
                        Intent it = new Intent(userinfo.getContext(), UpdateUsersActivity.class);
                        it.putExtra("DB_ID", o.getObjectId());
                        it.putExtra("instance", p.getObjectId());
                        userinfo.getContext().startActivity(it);
                    }
                });
                userinfo.setNegativeButton("Delete User", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        p.deleteInBackground();
                        userslist.remove(pos);
                        UsersAdapter.this.notifyDataSetChanged();
                    }
                });
                if(p.getInt("status") == 0){
                    userinfo.setNeutralButton("Report Missing", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            p.put("status", 1);
                            p.saveInBackground();
                            //reload usersactivity
                            UsersAdapter.this.notifyDataSetChanged();

                        }
                    });
                } else {
                    userinfo.setNeutralButton("Report Found", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            p.put("status", 0);
                            p.put("lat", "NIL");
                            p.put("lon", "NIL");
                            p.saveInBackground();
                            //reload usersactivity
                            UsersAdapter.this.notifyDataSetChanged();

                        }
                    });
                }

                userinfo.show();

            }
        });

    }

    @Override
    public int getItemCount(){
        return userslist.size();
    }

    public Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 500, 500, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        return bitmap;
    }


}
