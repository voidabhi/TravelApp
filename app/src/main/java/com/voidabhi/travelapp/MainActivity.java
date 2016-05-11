package com.voidabhi.travelapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.voidabhi.travelapp.utils.NetUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.voidabhi.travelapp.utils.NetUtils.showToast;


public class MainActivity extends ActionBarActivity {
 
 
    // mapping ui elements
    @InjectView(R.id.spinner_categories) Spinner categoriesSpinner;
    @InjectView(R.id.spinner_distances) Spinner distancesSpinner;
    @InjectView(R.id.button_go) Button go;

    String[] categoriesArray = null;
    String[] distancesArray = null;

    private static final long GPS_UPDATE_FREQUENCY = 5000;
    private static final float GPS_UPDATE_DISTANCE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

	// Initializing butter knife instance
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

                    showToast(MainActivity.this,R.string.fetching_location);

                        Intent mapsIntent = new Intent(getApplicationContext(), MapsActivity.class);
                        mapsIntent.putExtra("category", category.toLowerCase());
                        mapsIntent.putExtra("distance", distance.split(" ")[0] + "000");

                        Location location = getCurrentLocation();

                        if(location!=null) {

                            mapsIntent.putExtra("latitude", location.getLatitude());
                            mapsIntent.putExtra("longitude", location.getLongitude());
                            showToast(MainActivity.this,R.string.message_maps_loading);
                            startActivity(mapsIntent);
                        }
                        else
                             showToast(MainActivity.this,R.string.cannot_access_location);

                } else {

                    showToast(MainActivity.this,R.string.message_internet);

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
	
    public Location getCurrentLocation()
    {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE );
        Location location = null;
        boolean isGPSEnabled = locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER );
        boolean isNetworkEnabled = locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER );
        if ( !( isGPSEnabled || isNetworkEnabled ) )
			showToast(MainActivity.this,"GPS and Network not available");
        else
        {
            if ( location == null )
            {
                if ( isGPSEnabled )
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_UPDATE_FREQUENCY, GPS_UPDATE_DISTANCE, (android.location.LocationListener) null);
                    location = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
                }

                if ( isNetworkEnabled )
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_UPDATE_FREQUENCY, GPS_UPDATE_DISTANCE, (android.location.LocationListener) null);
                    location = locationManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER );
                }
            }
        }
        return location;
    }
	
}
