package com.voidabhi.travelapp;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.voidabhi.travelapp.models.Place;
import com.voidabhi.travelapp.utils.NetUtils;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.voidabhi.travelapp.utils.NetUtils.showToast;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private AsyncHttpClient client;

    private Handler handler = new Handler();

    List<Place> places;

    private String category;
    private String distance;
    private LatLng location;

    // Constants
    public static final float ZOOM_LEVEL= 17.0f;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!NetUtils.isGooglePlayServicesAvailable(this)){
            showToast(MapsActivity.this,R.string.services_na);
            finish();
        }

        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();


        category = getIntent().getStringExtra("category");
        distance = getIntent().getStringExtra("distance");
        location = new LatLng(getIntent().getDoubleExtra("latitude", 0.0d),getIntent().getDoubleExtra("longitude",0.0d));


        searchPlaces(getRequestURL());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // onMapReady Callback

    @Override
    public void onMapReady(GoogleMap googleMap) {

        showToast(MapsActivity.this,R.string.message_tap_marker);

        mMap = googleMap;

        if(location!=null) {
            setUpMap();
        }

    }

    // Helpers

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
            mapFragment.getMapAsync(MapsActivity.this);
        }
    }

    private void setUpMap() {

        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title("You are here")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));

    }

    private void searchPlaces(String url) {

        if(NetUtils.isOnline(MapsActivity.this)) {

            client = new AsyncHttpClient();

            client.get(MapsActivity.this,url,new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    try {
                        showToast(MapsActivity.this,response.getJSONArray("results").length()+" places found");
                        parseJson(response.getJSONArray("results"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast(MapsActivity.this,R.string.unexpected);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    showToast(MapsActivity.this,R.string.unexpected);
                }
            });



        } else {
            showToast(MapsActivity.this,R.string.message_internet);
        }
    }

    public void parseJson(final JSONArray results) {


        Runnable parseJsonRunnable = new Runnable() {

            @Override
            public void run() {

                places = Place.fromJsonArray(results);

                if (places != null) {
                    addMarkers();
                } else {
                    showToast(MapsActivity.this,R.string.unexpected);
                }

            }
        };

        handler.post(parseJsonRunnable);

    }

    public void addMarkers() {

        Runnable addMarkersRunnable = new Runnable() {

            private Set<PoiTarget> poiTargets = null;


            @Override
            public void run() {
                if (mMap == null || places==null || places.size() == 0) {
                    return;
                }

                poiTargets = new HashSet<PoiTarget>();
                mMap.clear();
                PoiTarget pt;
                for (int i = 0; i < places.size(); i++) {
                    Marker marker =  mMap.addMarker(new MarkerOptions()
                            .position(places.get(i).getLocation())
                            .title(places.get(i).getName()));

                    pt = new PoiTarget(marker);
                    poiTargets.add(pt);
                    Picasso.with(getApplicationContext())
                            .load(places.get(i).getIconUrl())
                            .centerCrop()
                            .resize(50,50)
                            .into(pt);

                }

                // Setting zoom level
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,ZOOM_LEVEL));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));;

                // Setting on marker click
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        playAudio(R.raw.audio_2);
                        return false;
                    }
                });
            }


            class PoiTarget implements Target {

                private Marker m;

                public PoiTarget(Marker m) { this.m = m; }

                @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    m.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    poiTargets.remove(this);
                }

                @Override public void onBitmapFailed(Drawable errorDrawable) {
                    poiTargets.remove(this);
                }

                @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            }

        };


        handler.post(addMarkersRunnable);

    }

    public void playAudio(final int resid) {

        Runnable playAudioSongRunnable = new Runnable() {


            public static final int NOTIFICATION_ID = 1;

            @Override
            public void run() {

                MediaPlayer mMediaPlayer = MediaPlayer.create(getApplicationContext(), resid);
                mMediaPlayer.start();
                generateNotification(getApplicationContext(), R.string.notification_title, R.string.notification_content);
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mMediaPlayer) {
                        removeNotification(getApplicationContext());
                        mMediaPlayer.release();
                    }

                });

                showToast(MapsActivity.this,"Playing sample audio file");

            }

                private void generateNotification(Context context,int titleId, int messageId) {

                    String title = getResources().getString(titleId);
                    String message = getResources().getString(messageId);

                    NotificationCompat.Builder builder =  new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setStyle(new NotificationCompat.InboxStyle());

                    // Add as notification
                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(NOTIFICATION_ID, builder.build());
                }

                private  void removeNotification(Context context){
                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancelAll();

                }
        };

        handler.post(playAudioSongRunnable);


    }

    public String getRequestURL() {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + this.location.latitude + "," + this.location.longitude);
        googlePlacesUrl.append("&radius=" + this.distance);
        googlePlacesUrl.append("&types=" + this.category);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=AIzaSyCgzVjQb0s6R9zgWtL1sXiN6tkEsjPOVFc");

         return googlePlacesUrl.toString();
    }


}
