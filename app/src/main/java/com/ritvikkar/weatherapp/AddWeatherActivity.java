package com.ritvikkar.weatherapp;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ritvikkar.weatherapp.data.Location;
import com.ritvikkar.weatherapp.data.WeatherApp;
import com.ritvikkar.weatherapp.network.WeatherAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddWeatherActivity extends AppCompatActivity {

    public static final String NAME = "NAME";
    public static final String TEMP = "TEMP";
    public static final String DESC = "DESC";
    public static final String ICON = "ICON";
    public static final String API_KEY = "58c650224e13868c1655dbebb7e650d9";
    public static final String EXISTS = "EXISTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weather);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnAdd)
    void addClicked(){
        EditText etAddCity = findViewById(R.id.etCityName);

        if (etAddCity.getText() == null) {
            etAddCity.setError(getString(R.string.txt_err_emptyfield));
        }
        else {
            realmCheckForDuplicates(etAddCity);

            Call<WeatherApp> call = getWeatherAppCall(etAddCity);

            call.enqueue(new Callback<WeatherApp>() {
                @Override
                public void onResponse(Call<WeatherApp> call, Response<WeatherApp> response) {
                    if (response.body() == null) {
                        Intent end = new Intent();
                        end.putExtra(EXISTS, false);
                        setResult(RESULT_CANCELED);
                    } else {
                        finishAddActivity(response);
                    }
                    finish();
                }

                @Override
                public void onFailure(Call<WeatherApp> call, Throwable t) {
                    showSnackbar(t);
                }
            });
        }
    }

    private void showSnackbar(Throwable t) {
        Snackbar.make(findViewById(R.id.layoutContent),
                t.getMessage(),
                Snackbar.LENGTH_LONG
        ).setAction(R.string.action_hide, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    private Call<WeatherApp> getWeatherAppCall(EditText etAddCity) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.txt_apiurl_base))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final WeatherAPI weatherAPI = retrofit.create(WeatherAPI.class);

        return weatherAPI.getCityByName(
                etAddCity.getText().toString(),
                getString(R.string.txt_units_metric),
                API_KEY);
    }

    private void realmCheckForDuplicates(EditText etAddCity) {
        RealmResults<Location> allItems = ((MainApplication) getApplication()).getRealmPlaces().where(Location.class).findAll();
        Location itemsArray[] = new Location[allItems.size()];
        List<Location> locationsResult = new ArrayList<>(Arrays.asList(allItems.toArray(itemsArray)));
        for (Location location: locationsResult) {
            if (location.getCity().equals(etAddCity.getText().toString())) {
                Intent end = new Intent();
                end.putExtra(EXISTS, true);
                setResult(RESULT_CANCELED, end);
                finish();
            }
        }
    }

    private void finishAddActivity(Response<WeatherApp> response) {
        Intent end = new Intent();
        end.putExtra(NAME, response.body().getName());
        end.putExtra(TEMP, response.body().getMain().getTemp());
        end.putExtra(DESC, response.body().getWeather().get(0).getDescription());
        end.putExtra(ICON, response.body().getWeather().get(0).getIcon());
        setResult(RESULT_OK, end);
    }
}
