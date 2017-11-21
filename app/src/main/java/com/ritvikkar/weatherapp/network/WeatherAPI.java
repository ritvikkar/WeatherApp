package com.ritvikkar.weatherapp.network;

import com.ritvikkar.weatherapp.data.WeatherApp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface WeatherAPI {

    @GET("weather")
    Call<WeatherApp> getCityByID(@Query("id") String city_id, @Query("appid") String app_id);

    @GET("weather")
    Call<WeatherApp> getCityByName(@Query("q") String city_name, @Query("units") String units, @Query("appid") String app_id);


}
