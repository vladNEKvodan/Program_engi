package com.example.weatherprogram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;


public class Settengss extends AppCompatActivity {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch tSwitch, wSwitch, pSwitch;
    String temp = "Off", wind = "Off", press = "Off";
    Button set;
    String t, w, p;


    protected void onPause() {
        super.onPause();

        // пишем нужное в SharedPreferences
        SharedPreferences.Editor ed = getSharedPreferences("test", Context.MODE_PRIVATE).edit();
        ed.putBoolean("tswitchState", tSwitch.isChecked());
        ed.putBoolean("wswitchState", wSwitch.isChecked());
        ed.putBoolean("pswitchState", pSwitch.isChecked());
        ed.apply();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settengss);
        tSwitch = findViewById(R.id.swithTemp);
        wSwitch = findViewById(R.id.switchWind);
        pSwitch = findViewById(R.id.switchPressure);
        set = findViewById(R.id.Set);

        SharedPreferences prefs = getSharedPreferences("test", Context.MODE_PRIVATE);
        boolean tswitchState = prefs.getBoolean("tswitchState", false);
        boolean wswitchState = prefs.getBoolean("wswitchState", false);
        boolean pswitchState = prefs.getBoolean("pswitchState", true);
        tSwitch.setChecked(tswitchState);
        wSwitch.setChecked(wswitchState);
        pSwitch.setChecked(pswitchState);
        if (tSwitch.isChecked()) {
            t = "On";
        }
        else {
            t = "Off";
        }
        if (wSwitch.isChecked()) {
            w = "On";
        }
        else {
            w = "Off";
        }
        if (pSwitch.isChecked()) {
            p = "On";
        }
        else {
            p = "Off";
        }
        temp = t;
        wind = w;
        press = p;
        tSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)  {
                temp = "On";
            } else {
                temp = "Off";
            }
        });
        wSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)  {
                wind = "On";
            } else {
                wind = "Off";
            }
        });
        pSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)  {
                press = "On";
            } else {
                press = "Off";
            }
        });
        set.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("Temp", temp);
            intent.putExtra("Wind", wind);
            intent.putExtra("Press", press);
            setResult(RESULT_OK, intent);
            finish();
        });

    }
}