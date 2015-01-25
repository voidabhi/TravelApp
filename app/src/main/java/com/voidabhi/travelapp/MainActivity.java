package com.voidabhi.travelapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.voidabhi.travelapp.utils.NetUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {


    @InjectView(R.id.spinner_categories) Spinner categoriesSpinner;
    @InjectView(R.id.spinner_distances) Spinner distancesSpinner;
    @InjectView(R.id.button_go) Button go;

    String[] categoriesArray = null;
    String[] distancesArray = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        categoriesArray = getResources().getStringArray(R.array.categoriesArray);
        distancesArray = getResources().getStringArray(R.array.distancesArray);

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,categoriesArray);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> distancesAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,distancesArray);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoriesSpinner.setAdapter(categoriesAdapter);
        distancesSpinner.setAdapter(distancesAdapter);


        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String category = categoriesArray[categoriesSpinner.getSelectedItemPosition()];
                String distance = distancesArray[distancesSpinner.getSelectedItemPosition()];


                if(NetUtils.isOnline(MainActivity.this)) {

                    Intent mapsIntent = new Intent(getApplicationContext(),MapsActivity.class);
                    mapsIntent.putExtra("category",category.toLowerCase());
                    mapsIntent.putExtra("distance",distance.split(" ")[0]+"000");
                    Toast.makeText(getApplicationContext(), R.string.message_maps_loading, Toast.LENGTH_LONG).show();
                    Location location = getCurrentLocation();
                    mapsIntent.putExtra("latitude",location.getLatitude());
                    mapsIntent.putExtra("longitude",location.getLongitude());

                    startActivity(mapsIntent);

                } else {

                    Toast.makeText(getApplicationContext(), R.string.message_internet, Toast.LENGTH_LONG).show();

                }

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }


    // Helpers

    public Location getCurrentLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);

        return location;
    }
}
