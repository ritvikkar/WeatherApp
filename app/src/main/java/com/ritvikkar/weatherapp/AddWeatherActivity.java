package com.ritvikkar.weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.ritvikkar.weatherapp.data.WeatherApp;
import com.ritvikkar.weatherapp.network.WeatherAPI;

import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weather);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnAdd)
    void addClicked(){
        EditText etAddCity = (EditText) findViewById(R.id.etCityName);
        if (etAddCity.getText() == null) {
            etAddCity.setError("Enter Value");
        }
        else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            final WeatherAPI weatherAPI = retrofit.create(WeatherAPI.class);

            Call<WeatherApp> call = weatherAPI.getCityByName(etAddCity.getText().toString(),
                    "metric", API_KEY);
            call.enqueue(new Callback<WeatherApp>() {
                @Override
                public void onResponse(Call<WeatherApp> call, Response<WeatherApp> response) {
                    if (response.body() == null) {
                        setResult(RESULT_CANCELED);
                    } else {
                        Intent end = new Intent();
                        end.putExtra(NAME, response.body().getName());
                        end.putExtra(TEMP, response.body().getMain().getTemp());
                        end.putExtra(DESC, response.body().getWeather().get(0).getDescription());
                        end.putExtra(ICON, response.body().getWeather().get(0).getIcon());
                        setResult(RESULT_OK, end);
                    }
                    finish();

                }

                @Override
                public void onFailure(Call<WeatherApp> call, Throwable t) {
                    Toast.makeText(AddWeatherActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
