package com.labs.sgcan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MissingAdapter extends RecyclerView.Adapter<MissingAdapter.ViewHolders> {
    private Context context;
    private List<ParseObject> missinglist;

    private Animator a;


    public class ViewHolders extends RecyclerView.ViewHolder{
        private TextView name, lastseen, condition, remark;
        private ImageView iv, iV;
        private LinearLayout LL;
        private FrameLayout container;
        private View vview;
        private int duration;
        public ViewHolders(View v){
            super(v);
            this.vview = v;
            name = v.findViewById(R.id.nameText);
            lastseen = v.findViewById(R.id.lastseenText);
            condition = v.findViewById(R.id.conditionText);
            remark = v.findViewById(R.id.remarkText);
            iv = v.findViewById(R.id.pfp);
            iV = v.findViewById(R.id.pfpEnlarge);
            iV.setVisibility(View.GONE);
            LL = v.findViewById(R.id.LL);
            container = v.findViewById(R.id.container);
            duration = v.getResources().getInteger(android.R.integer.config_shortAnimTime);
        }

        public View getView(){
            return this.vview;
        }
    }

    public MissingAdapter(Context context, List<ParseObject> missinglist){
        this.context = context;
        this.missinglist = missinglist;

    }

    @Override
    public ViewHolders onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_missing, parent, false);
        return new ViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolders vh, int position){
        final ParseObject p = missinglist.get(position);
        vh.name.setText(p.getString("fn") + " " + p.getString("ln"));
        vh.condition.setText(p.getString("condition"));
        if(p.getString("remarks").equals("")){
            vh.remark.setText("NIL");
        } else {
            vh.remark.setText(p.getString("remarks"));
        }


        ParseFile pf = p.getParseFile("profilepic");
        pf.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if(e == null){
                    Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
                    vh.iv.setImageBitmap(b);
                    vh.iV.setImageBitmap(b);
                    vh.iV.setVisibility(View.GONE);
                }
            }
        });

        vh.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int d = vh.duration;
                //zoom(vh.iv, vh.iV, vh.container, d);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.activity_image_popup, null);
                ImageView IV = popupView.findViewById(R.id.enlarge);
                Bitmap b = ((BitmapDrawable) vh.iv.getDrawable()).getBitmap();
                IV.setImageBitmap(b);
                int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                int height = RelativeLayout.LayoutParams.WRAP_CONTENT;

                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });

        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;

        try{
            final double llat = Double.valueOf(p.getString("lat"));
            final double llon = Double.valueOf(p.getString("lon"));
            addresses = gcd.getFromLocation(llat, llon, 1);
            if(addresses.size() > 0){
                Address a = addresses.get(0);
                String place = a.toString();
                place = a.getAddressLine(0);
                vh.lastseen.setText(place);
            }
            final View vv = vh.getView();
            //or vv.setOnClickListener
            vh.LL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(vv.getContext(), HomeActivity.class);
                    String g = p.getString("guardian");
                    ParseQuery<ParseObject> p = ParseQuery.getQuery("Guardian");
                    p.whereEqualTo("ID", g);
                    List<ParseObject> l = null;
                    try{
                        l = p.find();
                    }catch(ParseException e){

                    }
                    ParseObject o = l.get(0);
                    it.putExtra("DB_ID", o.getObjectId());
                    it.putExtra("hasmissing", true);
                    it.putExtra("missingLon", llon);
                    it.putExtra("missingLat", llat);
                    vv.getContext().startActivity(it);

                }
            });
        }catch(IOException e){


        }catch(NumberFormatException ee){
            vh.lastseen.setText("No available last seen location");
        }


    }

    @Override
    public int getItemCount(){
        return missinglist.size();
    }

    private void zoom(final ImageView small, final ImageView big, FrameLayout container, final int duration){
        if(a != null){
            a.cancel();
        }
        //Animator a = animation;

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        small.getGlobalVisibleRect(startBounds);
        container.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()){
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        small.setAlpha(0f);
        big.setVisibility(View.VISIBLE);
        big.setPivotX(0f);
        big.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(big, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(big, View.Y, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(big, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(big, View.SCALE_Y, startScale, 1f));
        set.setDuration(duration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                a = null;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                a = null;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.start();
        a = set;

        final float startScaleFinal = startScale;
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (a != null) {
                    a.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(big, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(big,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(big,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(big,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(duration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        small.setAlpha(1f);
                        big.setVisibility(View.GONE);
                        a = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        small.setAlpha(1f);
                        big.setVisibility(View.GONE);
                        a = null;
                    }
                });
                set.start();
                a = set;
            }
        });
    }
}
