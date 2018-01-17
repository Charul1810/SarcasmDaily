package com.incognisyssolutions.sarcasmdaily;


/**
 * Created by CHARUL on 12-01-2018.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.artjimlop.altex.AltexImageDownloader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import butterknife.*;
import butterknife.OnClick;

import static com.incognisyssolutions.sarcasmdaily.R.drawable.ic_file_download;

public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<Image> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    private int selectedPosition = 0;
    ImageView icon_down, icon_share,icon_fav;
    public static final String MESSAGE_PROGRESS = "message_progress";
    private static final int PERMISSION_REQUEST_CODE = 1;

    public TextView txt_url;






    static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);




        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblTitle = (TextView) v.findViewById(R.id.title);
        txt_url = (TextView) v.findViewById(R.id.txt_url);
        icon_down = (ImageView) v.findViewById(R.id.icon_download);
        icon_share = (ImageView) v.findViewById(R.id.icon_share);
        images = (ArrayList<Image>)getArguments().get("images");
        selectedPosition = getArguments().getInt("position");

        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);

        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);




        setCurrentItem(selectedPosition);

        icon_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                downloadFile();


            }
        });


        icon_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // new DataShare(getActivity(),"Share Image").ShareAndLoadImage(txt_url.getText().toString());
                sharefile();

            }


        });



        return v;
    }




    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //  page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
//        lblCount.setText((position + 1) + " of " + images.size());
        Image image = images.get(position);
//        lblTitle.setText(image.getName());
//        lblDate.setText(image.getTimestamp());
        txt_url.setText(image.getImageurl());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL,R.style.Theme_AppCompat_Light_DarkActionBar
        );
    }





    private void sharefile(){

        if (checkPermission()) {

            Toast.makeText(getContext(),"Opening Sharing Options",Toast.LENGTH_SHORT).show();
            new DataShare(getActivity(),"Sent from Sarcasm Daily").ShareAndLoadImage(txt_url.getText().toString());

            //NAME_FOLDER is the name of the folder where you want to save the image.
        } else {
            requestPermission();
        }

    }


    private void downloadFile() {

        if (checkPermission()) {

            AltexImageDownloader.writeToDisk(getActivity(), txt_url.getText().toString(), "Sarcasm/"); //NAME_FOLDER is the name of the folder where you want to save the image.
        } else {
            requestPermission();
        }
    }




    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AltexImageDownloader.writeToDisk(getActivity(), txt_url.getText().toString(), "Sarcasm/"); //NAME_FOLDER is the name of the folder where you want to save the image.

                } else {

                    Snackbar.make(viewPager.findViewById(R.id.LinearLayout), "Permission Denied, Please allow to proceed !", Snackbar.LENGTH_LONG).show();

                }
                break;
        }
    }






    //  adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);

            Image image = images.get(position);

            Glide.with(getActivity()).load(image.getImageurl())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);

            container.addView(view);




            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
           container.removeView((View) object);
        }


    }


}