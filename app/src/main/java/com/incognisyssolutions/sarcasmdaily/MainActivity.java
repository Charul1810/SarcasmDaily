package com.incognisyssolutions.sarcasmdaily;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ProgressDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.incognisyssolutions.sarcasmdaily.alarm.AlarmReceiver1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private static final String endpoint = "http://incognisys.com/SachinApps/Scripts/SarcasmApp/getAllImages.php";
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private RequestQueue mRequestQueue;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RelativeLayout relativeLayout;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toolbar.setLogo(R.drawable.ic_launcher);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        relativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayout1);






        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        showSnack(isInternetPresent);
        if (isInternetPresent) {
            fetchImages();
        } else {

        }



        Calendar calendar = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        // we can set time by open date and time picker dialog
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 58);
        calendar.set(Calendar.SECOND, 0);
        if (now.after(calendar)) {
            //Log.d("Hey","Added a day");
            calendar.add(Calendar.DATE, 1);
        }
        Intent intent1 = new Intent(getApplicationContext(), AlarmReceiver1.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), 0, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getApplicationContext()
                .getSystemService(getApplicationContext().ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // implement Handler to wait for 3 seconds and then update UI means update value of TextView
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // cancle the Visual indication of a refresh
                        mSwipeRefreshLayout.setRefreshing(false);
                        // Generate a random integer number
                        Random r = new Random();
                        int i1 = r.nextInt(80 - 65) + 65;
                        // set the number value in TextView
                        fetchimagesnopd();
                    }
                }, 2000);
            }
        });

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);
                //bundle.putString("url",)

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();

//                ft.replace(R.id.RelativeLayout1,newFragment);
//                ft.addToBackStack(null);

//                ft.commit();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //Rate APp Code
        AppRater.app_launched(this);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {

        } else {
            message = "Sorry! Not connected to internet";
            color = Color.WHITE;

            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }


    }

    @Override
    protected void onResume() {


        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            fetchImages();
        }
        super.onResume();
    }


    private void fetchImages() {

        pDialog.setMessage("Loading Images...");
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();

                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Image image = new Image();
                                image.setId(Integer.parseInt(object.getString("id")));
                                image.setName(object.getString("name"));
                                image.setThumbnailurl(object.getString("thumbnailurl"));
                                image.setImageurl(object.getString("imageurl"));

                                images.add(image);

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }


                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        });
        mRequestQueue.add(req);
    }

    private void fetchimagesnopd() {


        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();

                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Image image = new Image();
                                image.setId(Integer.parseInt(object.getString("id")));
                                image.setName(object.getString("name"));
                                image.setThumbnailurl(object.getString("thumbnailurl"));
                                image.setImageurl(object.getString("imageurl"));

                                images.add(image);

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }


                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        });
        mRequestQueue.add(req);
    }


    @Override
    public void onBackPressed() {

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }


}
