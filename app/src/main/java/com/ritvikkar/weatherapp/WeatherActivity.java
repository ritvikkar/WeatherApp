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

import butterknife.BindView;
import butterknife.ButterKnife;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCityActivity();
            }
        });

        weatherAdapter = new WeatherAdapter(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(weatherAdapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ButterKnife.bind(this);
    }

    private void showAddCityActivity() {
            Intent intentStart = new Intent()
                    .setClass(WeatherActivity.this, AddWeatherActivity.class);
            startActivityForResult(intentStart, REQUEST_NEW_CITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                Bundle values = data.getExtras();
                Location location = new Location((String) values.get(AddWeatherActivity.NAME),
                        (double) values.get(AddWeatherActivity.TEMP),
                        (String) values.get(AddWeatherActivity.DESC),
                        (String) values.get(AddWeatherActivity.ICON));
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add) {
            showAddCityActivity();
        } else if (id == R.id.nav_info) {
            showSnackBarMessage("Weather application created by Ritvik Kar");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
