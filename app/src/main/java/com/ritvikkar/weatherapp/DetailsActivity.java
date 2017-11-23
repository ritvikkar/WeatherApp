package com.ritvikkar.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ritvikkar.weatherapp.data.WeatherApp;
import com.ritvikkar.weatherapp.network.WeatherAPI;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvTemp)
    TextView tvTemp;
    @BindView(R.id.tvDesc)
    TextView tvDesc;
    @BindView(R.id.tvTempMax)
    TextView tvTempMax;
    @BindView(R.id.tvTempMin)
    TextView tvTempMin;
    @BindView(R.id.tvHumidity)
    TextView tvHumidity;
    @BindView(R.id.tvWind)
    TextView tvWind;
    @BindView(R.id.ivIcon)
    ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.txt_apiurl_base))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final WeatherAPI weatherAPI = retrofit.create(WeatherAPI.class);

        Call<WeatherApp> call = weatherAPI.getCityByName(
                getIntent().getExtras().get(WeatherActivity.CITY_NAME).toString(),
                getString(R.string.txt_units_metric),
                AddWeatherActivity.API_KEY);
        call.enqueue(new Callback<WeatherApp>() {
            @Override
            public void onResponse(Call<WeatherApp> call, Response<WeatherApp> response) {
                setUiFromData(response);
            }

            @Override
            public void onFailure(Call<WeatherApp> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        ButterKnife.bind(this);
    }

    private void setUiFromData(Response<WeatherApp> response) {
        tvName.setText(response.body().getName());
        tvTemp.setText(Double.toString(response.body().getMain().getTemp()));
        tvTempMax.setText(String.format(
                getString(R.string.txt_temp_cel),
                Double.toString(response.body().getMain().getTempMax())));
        tvTempMin.setText(String.format(
                getString(R.string.txt_temp_cel),
                Double.toString(response.body().getMain().getTempMin())));
        tvDesc.setText(response.body().getWeather().get(0).getDescription());
        tvHumidity.setText(String.format(
                getString(R.string.txt_humid_perc),
                Double.toString(response.body().getMain().getHumidity())));
        tvWind.setText(String.format(
                getString(R.string.txt_wind_ms),
                Double.toString(response.body().getWind().getSpeed())));
        String url = getString(R.string.txt_url_img_base) + response.body().getWeather().get(0).getIcon() + ".png";
        Glide.with(DetailsActivity.this).load(url).into(ivIcon);
    }
}
