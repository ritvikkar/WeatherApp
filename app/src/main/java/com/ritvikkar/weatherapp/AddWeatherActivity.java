package com.ritvikkar.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.ritvikkar.weatherapp.data.Weather;
import com.ritvikkar.weatherapp.data.WeatherApp;

import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

public class AddWeatherActivity extends AppCompatActivity {

    @BindView(R.id.etItemName)
    EditText etItemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weather);

        initCreate();
    }

    private void initCreate() {
        etItemName.getText();

    }

    @OnClick(R.id.btnAdd)
    void addClicked(){

    }
}
