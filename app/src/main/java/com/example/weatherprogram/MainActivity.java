package com.example.weatherprogram;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.icu.util.TimeUnit;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
        if (requestCode == 1) {
            ttemp = data.getStringExtra("Temp");
            wwind = data.getStringExtra("Wind");
            press = data.getStringExtra("Press");
            Convert();
        }
        else{
            longitude = Double.parseDouble(data.getStringExtra("Long"));
            latitude = Double.parseDouble(data.getStringExtra("Lat"));
            System.out.println(longitude + " " + latitude);
        }


    }

    TextView tmp1, wind1, press1, tmp2, wind2, press2, tmp3, wind3, press3, tmp4, wind4, press4;

    TextView tmp5, wind5, press5, tvDate, todayTemp, todayWind, todayPres, day1, day2, day3, day4,
            day5, today, temp_metrics, wind_metrics, pres_metrics;

    String Value_todayTemp, Value_todayWind, Value_todayPres;

    EditText etTown;

    Button btnTown, btnLoc;

    ImageButton btnSett;

    ImageView weath_img, day1weather, day2weather, day3weather, day4weather, day5weather,
            todayTempImage, todayWindImage, todayPresImage;

    TextView[] tw_list = new TextView[20];

    String temp = "Off";
    String pres = "On";
    String wind = "Off";

    String ttemp = "Off";
    String wwind = "Off";
    String press = "Off";

    boolean flag = false;

    double longitude = 10.0;
    double latitude = 10.0;
    double longitude_sel = 0;
    double latitude_sel =0;

    Map<String, String> monthes = new HashMap<>();

    String[] values = new String[20];
    private static final String API = "50583ad4512d45cfeb03477aafef195d";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weath_img = findViewById(R.id.weather_image);

        day1 = findViewById(R.id.tvDay1);
        day2 = findViewById(R.id.tvDay2);
        day3 = findViewById(R.id.tvDay3);
        day4 = findViewById(R.id.tvDay4);
        day5 = findViewById(R.id.tvDay5);

        tmp1 = findViewById(R.id.temp_1);
        tmp2 = findViewById(R.id.temp_2);
        tmp3 = findViewById(R.id.temp_3);
        tmp4 = findViewById(R.id.temp_4);
        tmp5 = findViewById(R.id.temp_5);

        wind1 = findViewById(R.id.wind_1);
        wind2 = findViewById(R.id.wind_2);
        wind3 = findViewById(R.id.wind_3);
        wind4 = findViewById(R.id.wind_4);
        wind5 = findViewById(R.id.wind_5);

        press1 = findViewById(R.id.press_1);
        press2 = findViewById(R.id.press_2);
        press3 = findViewById(R.id.press_3);
        press4 = findViewById(R.id.press_4);
        press5 = findViewById(R.id.press_5);

        today = findViewById(R.id.today);
        etTown = findViewById(R.id.etTown);
        btnSett = findViewById(R.id.btnSettings);
        btnTown = findViewById(R.id.btnTown);
        btnLoc = findViewById(R.id.btnLocation);
        tvDate = findViewById(R.id.tvDate);

        todayTempImage = findViewById(R.id.todayTempImage);
        todayWindImage = findViewById(R.id.todayWindImage);
        todayPresImage = findViewById(R.id.todayPresImage);

        todayTemp = findViewById(R.id.todayTemp);
        todayWind = findViewById(R.id.todayWind);
        todayPres = findViewById(R.id.todayPres);
        day1weather = findViewById(R.id.day1Weather);
        day2weather = findViewById(R.id.day2Weather);
        day3weather = findViewById(R.id.day3Weather);
        day4weather = findViewById(R.id.day4Weather);
        day5weather = findViewById(R.id.day5Weather);

        temp_metrics = findViewById(R.id.temp_metrics);
        wind_metrics = findViewById(R.id.wind_metrics);
        pres_metrics = findViewById(R.id.pres_metrics);

        tw_list = new TextView[]{day1, tmp1, wind1, press1,
                day2, tmp2, wind2,press2,
                day3, tmp3, wind3, press3,
                day4, tmp4, wind4, press4,
                day5, tmp5, wind5, press5};

        monthes.put("01", "Января");
        monthes.put("02", "Февраля");
        monthes.put("03", "Марта");
        monthes.put("04", "Апреля");
        monthes.put("05", "Мая");
        monthes.put("06", "Июня");
        monthes.put("07", "Июля");
        monthes.put("08", "Августа");
        monthes.put("09", "Сентября");
        monthes.put("10", "Октября");
        monthes.put("11", "Ноября");
        monthes.put("12", "Декабря");

        System.out.println('2');
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String current_date = formatter.format(calendar.getTime());
        tvDate.setText(current_date.split(" ")[0].split("-")[2] + " " + monthes.get(current_date.split("-")[1]));
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            System.out.println("1");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }else{
            System.out.println("dssdf");
            LocationListener locationListener = new LocationListener() {

                // Состояние провайдера запускает эту функцию, когда напрямую переключаются три состояния: доступный, временно недоступный и отсутствие службы
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                // Эта функция срабатывает, когда провайдер включен, например, включен GPS
                @Override
                public void onProviderEnabled(String provider) {

                }

                // Эта функция срабатывает, когда провайдер отключен, например, отключен GPS
                @Override
                public void onProviderDisabled(String provider) {

                }

                // Эта функция запускается при изменении координат. Если Провайдер передает те же координаты, он не сработает
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        Log.e("Map", "Location changed : Lat: "
                                + location.getLatitude() + " Lng: "
                                + location.getLongitude());
                    }
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 0,locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            System.out.println(location);
            if(location != null){
                latitude = location.getLatitude (); // Долгота
                longitude = location.getLongitude (); // Широта
            }
        }



        btnSett.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Settengss.class);
            startActivityForResult(intent, 1);
        });



        //Choosing date part
        double finalLatitude = latitude_sel;
        double finalLongitude = longitude_sel;

        View.OnClickListener clickListener = view -> {
            if (view == btnTown){
                String town = etTown.getText().toString();
                if (town.equals("")) {
                    Toast.makeText(MainActivity.this, "Введите город", Toast.LENGTH_LONG).show();
                } else {
                    String url_by_town = "https://api.openweathermap.org/data/2.5/forecast?q=" + town + "&appid=" + API + "&units=metric";
                    new GetURLData().execute(url_by_town);
                    System.out.println(url_by_town);

                }
            }

            else if (view == btnLoc){

                Intent intent = new Intent(MainActivity.this, Geoinf.class);
                startActivityForResult(intent, 10);
                //System.out.println(longitude + "  " + latitude);
                String url_by_coords = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&appid=" + API + "&units=metric";
                new GetURLData().execute(url_by_coords);
                Toast.makeText(getApplicationContext(), "Глотни Спермы, ублюдок " + latitude + longitude, Toast.LENGTH_LONG).show();
                System.out.println(url_by_coords);

            }
        };
        btnLoc.setOnClickListener(clickListener);
        btnTown.setOnClickListener(clickListener);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetURLData extends AsyncTask<String,String,String> {

        protected void onPreExecute(){
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

            return null;
        }

        @SuppressLint({"SetTextI18n", "ResourceAsColor"})
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray list = jsonObject.getJSONArray("list");
                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String current_date = formatter.format(calendar.getTime());
                DecimalFormat decimalFormat = new DecimalFormat( "#" );
                String str;
                int j = 0;
                for (int i = 0; i < 8; i++) {
                    jsonObject = (JSONObject) list.get(i);
                    str = (String) jsonObject.get("dt_txt");
                    if (str.split(" ")[0].equals(current_date)) {
                            j+=1;
                    }
                    else {
                        break;
                    }
                }
                todayTempImage.setImageResource(R.drawable.temp);
                todayWindImage.setImageResource(R.drawable.wind);
                todayPresImage.setImageResource(R.drawable.pressure);

                jsonObject = (JSONObject) list.get(0);
                today.setText("Сегодня: ");
                if ((double)jsonObject.getJSONObject("main").get("temp") > 0){
                    String temp =  String.valueOf(jsonObject.getJSONObject("main").get("temp"));
                    Value_todayTemp = "+" + (Math.round((Float.parseFloat(temp)) * 100) / 100);
                }
                else{
                    String temp =  String.valueOf(jsonObject.getJSONObject("main").get("temp"));
                    Value_todayTemp = String.valueOf(Math.round((Float.parseFloat(temp)) * 100) / 100);
                }

                Value_todayWind = String.valueOf(Math.round(Float.parseFloat(String.valueOf(jsonObject.getJSONObject("wind").get("speed"))) * 100) / 100);
                Value_todayPres =(String.valueOf(jsonObject.getJSONObject("main").get("pressure")));
                SetImage(list, 0, weath_img);

                jsonObject = new JSONObject(result);
                list = jsonObject.getJSONArray("list");
                int count = 0;
                int image_number = 0;
                for (int i = 5+j; i < list.length(); i+=8){
                    jsonObject = (JSONObject) list.get(i);
                    if (image_number == 0)
                        SetImage(list, i, day1weather);
                    else if (image_number == 1)
                        SetImage(list, i, day2weather);
                    else if (image_number == 2)
                        SetImage(list, i, day3weather);
                    else if (image_number == 3)
                        SetImage(list, i, day4weather);
                    else if (image_number == 4)
                        SetImage(list, i, day5weather);
                    image_number += 1;
                    if ((Double) jsonObject.getJSONObject("main").get("temp") > 0){
                        values[count+1] = ("+" + decimalFormat.format(jsonObject.getJSONObject("main").get("temp")));
                        values[count+1] = ("+" + Math.round((Float.parseFloat(values[count + 1].substring(1))) * 10) / 10);
                    }else{
                        values[count+1] = (decimalFormat.format(jsonObject.getJSONObject("main").get("temp")));
                        values[count+1] = String.valueOf(Math.round((Float.parseFloat(values[count + 1])) * 10) / 10);
                    }
                    values[count] = (String.valueOf(jsonObject.get("dt_txt")).split(" ")[0].split("-")[2] + " " +
                            monthes.get(String.valueOf(jsonObject.get("dt_txt")).split(" ")[0].split("-")[1]));
                    values[count+2] = String.valueOf(Math.round(Float.parseFloat(String.valueOf(jsonObject.getJSONObject("wind").get("speed"))) * 100) / 100);
                    values[count+3] = (String.valueOf(jsonObject.getJSONObject("main").get("pressure")));
                    count+=4 ;
                }
                flag = true;
                temp = "Off";
                wind = "Off";
                pres = "On";


                count = 16;
                    SetImage(list, 39, day5weather);
                    jsonObject = (JSONObject) list.get(39);
                    if ((Double) jsonObject.getJSONObject("main").get("temp") > 0){
                        values[count+1] = ("+" + jsonObject.getJSONObject("main").get("temp"));
                        values[count+1] = ("+" + Math.round((Float.parseFloat(values[count + 1].substring(1))) * 100) / 100);
                    }
                    else {
                        values[count + 1] = (decimalFormat.format(jsonObject.getJSONObject("main").get("temp")));
                        values[count + 1] = String.valueOf(Math.round((Float.parseFloat(values[count + 1])) * 10) / 10);
                    }
                    values[count] = (String.valueOf(jsonObject.get("dt_txt")).split(" ")[0].split("-")[2] + " " +
                            monthes.get(String.valueOf(jsonObject.get("dt_txt")).split(" ")[0].split("-")[1]));
                    values[count+2] = String.valueOf(Math.round(Float.parseFloat(String.valueOf(jsonObject.getJSONObject("wind").get("speed"))) * 100) / 100);
                    values[count+3] = (String.valueOf(jsonObject.getJSONObject("main").get("pressure")));
                Convert();
            } catch (JSONException | NullPointerException e){
                Toast.makeText(MainActivity.this, "Такого города не существует", Toast.LENGTH_LONG).show();
                etTown.setTextColor(R.color.red);
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private void Convert() {
        if (values[0] != null) {
            if (!ttemp.equals(temp)) {
                if (ttemp.equals("On")) {
                    temp_metrics.setText("°F");
                    for (int i = 1; i < values.length; i += 4) {
                        System.out.println(values[i]);
                        if (Integer.parseInt(values[i]) > 0)
                            values[i] = String.valueOf((Math.round(Float.parseFloat(values[i].substring(1)) * 1.8 + 32) * 100) / 100);
                        else
                            values[i] = String.valueOf((Math.round(Float.parseFloat(values[i]) * 1.8 + 32) * 100) / 100);
                    }
                    // Дописать проверку на отрицательное число
                    Value_todayTemp = String.valueOf((Math.round(Float.parseFloat(Value_todayTemp.substring(1)) * 1.8 + 32) * 100) / 100);
                    todayTemp.setText(Value_todayTemp);

                }
                if (ttemp.equals("Off")) {
                    temp_metrics.setText("°C");
                    for (int i = 1; i < values.length; i += 4) {

                        values[i] = String.valueOf((Math.round((Float.parseFloat(values[i]) - 32) / 1.8) * 100) / 100);
                    }
                    Value_todayTemp = String.valueOf((Math.round((Float.parseFloat(String.valueOf(todayTemp.getText()).substring(1)) - 32) / 1.8) * 100) / 100);
                    todayTemp.setText(Value_todayTemp);

                }
            }

            char c = Value_todayTemp.charAt(0);
            if (Double.parseDouble(Value_todayTemp) > 0 && (c != '+')){
                todayTemp.setText("+" + Value_todayTemp);
            }else if (Double.parseDouble(Value_todayTemp) > 0)
                todayTemp.setText(Value_todayTemp);
            else
                todayTemp.setText(Value_todayTemp);

            for (int i = 1; i < values.length; i += 4) {
                c = values[i].charAt(0);
                if (Float.parseFloat(values[i]) > 0 )
                    if (c != '+') {
                        values[i] = "+" + values[i];
                }
            }
            if (!wwind.equals(wind)) {
                if (wwind.equals("On")) {
                    wind_metrics.setText("км/ч");
                    for (int i = 2; i < values.length; i += 4) {
                        values[i] = String.valueOf(Math.round(Float.parseFloat(values[i]) * 3.6 * 100) / 100);
                    }
                    Value_todayWind = String.valueOf(Math.round(Float.parseFloat(Value_todayWind) * 3.6 * 100) / 100);
                }
                if (wwind.equals("Off")) {
                    wind_metrics.setText("м/с");
                    for (int i = 2; i < values.length; i += 4) {
                        values[i] = String.valueOf(Math.round((Float.parseFloat(values[i]) / 3.6 + 1) * 100) / 100);
                    }
                    Value_todayWind = String.valueOf(Math.round((Float.parseFloat(Value_todayWind) / 3.6 + 1) * 100) / 100);

                }
            }
            todayWind.setText(Value_todayWind);
            if (!pres.equals(press)) {
                if (pres.equals("On")) {
                    pres_metrics.setText("мм.рт.ст");

                    for (int i = 3; i < values.length; i += 4) {
                        values[i] = String.valueOf(Math.round(Float.parseFloat(values[i]) / 1.333 * 100) / 100);
                    }
                    Value_todayPres = String.valueOf(Math.round(Float.parseFloat(Value_todayPres) / 1.333 * 100) / 100);
                }
                if (pres.equals("Off")) {
                    pres_metrics.setText("гПа");
                    for (int i = 3; i < values.length; i += 4) {
                        values[i] = String.valueOf(Math.round((Float.parseFloat(values[i]) * 1.333 + 1) * 100) / 100);
                    }
                    Value_todayPres = String.valueOf(Math.round((Float.parseFloat(Value_todayPres) * 1.333 + 1) * 100) / 100);
                }
            }
            todayPres.setText(Value_todayPres);
            temp = ttemp;
            wind = wwind;
            pres = press;
            for (int i = 0; i < 20; i++) {
                tw_list[i].setText(values[i]);
            }
        }
    }
    private void SetImage(@NonNull JSONArray list, int i, ImageView imageView) throws JSONException {
        JSONObject jsonObject1;
        JSONObject jsonObject = (JSONObject) list.get(i);
        jsonObject1 = jsonObject;
        list = (JSONArray) jsonObject.get("weather");
        jsonObject = (JSONObject) list.get(0);

        if (String.valueOf(jsonObject.get("main")).equals("Clouds") || String.valueOf(jsonObject.get("main")).equals("Clear")){
            if (Integer.parseInt(String.valueOf(jsonObject1.getJSONObject("clouds").get("all"))) < 50)
                imageView.setImageResource(R.drawable.sun);
            else
                imageView.setImageResource(R.drawable.cloud);
        }
        else if (String.valueOf(jsonObject.get("main")).equals("Rain"))
            imageView.setImageResource(R.drawable.rain);
        else if (String.valueOf(jsonObject.get("main")).equals("Snow"))
            imageView.setImageResource(R.drawable.snow);
    }
}
