package com.ritvikkar.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ritvikkar.weatherapp.adapter.WeatherAdapter;
import com.ritvikkar.weatherapp.data.Location;
import com.ritvikkar.weatherapp.data.WeatherApp;
import com.ritvikkar.weatherapp.network.WeatherAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String CITY_NAME = "CITY_NAME";
    private WeatherAdapter weatherAdapter;

    @BindView(R.id.layoutContent)
    CoordinatorLayout layoutContent;

    public static final int REQUEST_NEW_CITY = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        ((MainApplication) getApplication()).openRealm();

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCityActivity();
            }
        });

        setupAdapter();

        setupNavigationDrawer();

        ButterKnife.bind(this);
    }

    private void setupNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupAdapter() {
        RealmResults<Location> allCities = getRealm().where(Location.class).findAll();
        Location citiesArray[] = new Location[allCities.size()];
        List<Location> locationsResult = new ArrayList<>(Arrays.asList(allCities.toArray(citiesArray)));

        updateInfo(locationsResult);

        weatherAdapter = new WeatherAdapter(locationsResult, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(weatherAdapter);
    }

    private void updateInfo(List<Location> locations) {
        for (Location location: locations) {
            getLocationInfo(location);
        }
    }

    private void getLocationInfo(final Location location) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.txt_apiurl_base))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final WeatherAPI weatherAPI = retrofit.create(WeatherAPI.class);

        Call<WeatherApp> call = weatherAPI.getCityByName(location.getCity(),
                getString(R.string.txt_units_metric), AddWeatherActivity.API_KEY);
        call.enqueue(new Callback<WeatherApp>() {
            @Override
            public void onResponse(Call<WeatherApp> call, Response<WeatherApp> response) {
                if (response.body() == null) {
                    showSnackBarMessage(getString(R.string.txt_add_cancel) + location.getCity());
                } else {
                    getRealm().beginTransaction();
                    location.setValues(response.body().getName(),
                            response.body().getMain().getTemp(),
                            response.body().getWeather().get(0).getDescription(),
                            response.body().getWeather().get(0).getIcon());
                    getRealm().commitTransaction();
                }
            }

            @Override
            public void onFailure(Call<WeatherApp> call, Throwable t) {
                showSnackBarMessage(getString(R.string.action_hide));
            }
        });
    }

    public Realm getRealm() {
        return ((MainApplication)getApplication()).getRealmPlaces();
    }


    private void showAddCityActivity() {
            Intent intentStart = new Intent()
                    .setClass(WeatherActivity.this, AddWeatherActivity.class);
            startActivityForResult(intentStart, REQUEST_NEW_CITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle values = data.getExtras();
        switch (resultCode) {
            case RESULT_OK:
                getRealm().beginTransaction();
                Location location = getRealm().createObject(Location.class, UUID.randomUUID().toString());
                location.setValues((String) values.get(AddWeatherActivity.NAME),
                        (double) values.get(AddWeatherActivity.TEMP),
                        (String) values.get(AddWeatherActivity.DESC),
                        (String) values.get(AddWeatherActivity.ICON));
                getRealm().commitTransaction();
                weatherAdapter.addLocation(location);
                break;

            case RESULT_CANCELED:
                showSnackBarMessage(getString(R.string.txt_add_cancel));
                break;
        }
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(layoutContent,
                message,
                Snackbar.LENGTH_LONG
        ).setAction(R.string.action_hide, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    public void showDetailsLocationActivity(String name) {
        Intent intentStart = new Intent(WeatherActivity.this,
                DetailsActivity.class);
        intentStart.putExtra(CITY_NAME, name);
        startActivity(intentStart);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add) {
            showAddCityActivity();
        } else if (id == R.id.nav_info) {
            showSnackBarMessage(getString(R.string.txt_application_creator));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void deleteItem(Location location) {
        getRealm().beginTransaction();
        location.deleteFromRealm();
        getRealm().commitTransaction();
    }
}
