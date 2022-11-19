package com.example.weatherapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadingPG;
    private TextView cityNameTV, temperatureTV, conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        homeRL = findViewById(R.id.idRLHome);
        loadingPG = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRvWeather);
        cityEdt = findViewById(R.id.idTILCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSeach);
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModelArrayList);
        weatherRV.setAdapter(weatherRVAdapter);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions ( MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }
        Location location = locationManager.getLastKnownLocation ( LocationManager.NETWORK_PROVIDER);
        cityName = getCityName ( location.getLongitude (), location.getLatitude ());
        getWeatherInfo (cityName);
        searchIV.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                String city = cityEdt.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Plase enter city name", Toast.LENGTH_SHORT).show();
                } else{
                    cityNameTV.setText ( cityName);
                    getWeatherInfo ( city );
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText ( this, "Permissions granted..", Toast.LENGTH_SHORT).show ();
            } else {
                Toast.makeText ( this, "Please provide the permissions..", Toast.LENGTH_SHORT).show ();
                finish ();
            }
        }
    }
    private String getCityName(double longtude, double latitude){
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault ());
        try{
            List<Address> addresses = gcd.getFromLocation ( latitude, longtude, 10);
            for(Address adr:addresses){
                if(adr!= null){
                    String city = adr.getLocality();
                    if(city!=null && !city.equals ("")){
                        cityName = city;
                    } else{
                        Log.d("TAG", "City not found");
                        Toast.makeText ( this, "User city not found...", Toast.LENGTH_SHORT).show ();
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace ();
        }
        return cityName;
    }
    private void getWeatherInfo(String cityName){
        String url = "http://api.weatherapi.com/v1/current.json?key=39c854fa147c4fc49fe121237221911&q="+ cityName+"&aqi=no";
        cityNameTV.setText ( cityName );
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest ( Request.Method.GET, url, null, new Response.Listener<JSONObject> () {
            @Override
            public void onResponse(JSONObject response) {
                loadingPG.setVisibility ( View.GONE );
                homeRL.setVisibility ( View.VISIBLE );
                weatherRVModelArrayList.clear ();
                try {
                    String temperature = response.getJSONObject ("current").getString ("temp_c" );
                    temperatureTV.setText ( temperature+"°C" );
                    int isDay = response.getJSONObject ( "current" ).getInt ("is_day");
                    String condition = response.getJSONObject("current").getJSONObject ("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject ("icon").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText (condition);
                    if(isDay == 1){
                        Picasso.get().load("https://images.unsplash.com/photo-1632590811094-50d6ce47f859?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwcm9maWxlLXBhZ2V8M3x8fGVufDB8fHx8&w=1000&q=80").into(backIV);
                    } else{
                        Picasso.get().load("https://planetary.s3.amazonaws.com/assets/images/wallpaper/bg_planetary-society_wise-pleaides-schmidt_phone1215x2160.jpg").into(backIV);
                    }
                    JSONObject forecastObj = response.getJSONObject ("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray ("forecastday").getJSONObject ( 0 );
                    JSONArray hourArray = forecastO.getJSONArray ("hour");
                    for(int i=0; i<hourArray.length (); i++){
                        JSONObject hourObj = hourArray.getJSONObject ( i );
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString ("temp_c");
                        String img = hourObj.getJSONObject ("condition").getString ("icon");
                        String wind = hourObj.getString ( "wind_kph");
                        weatherRVModelArrayList.add ( new WeatherRVModel (time, temper, img, wind));
                    }
                    weatherRVAdapter.notifyDataSetChanged ();
                } catch (JSONException e) {
                    e.printStackTrace ();
                }
            }
        }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText ( MainActivity.this, "Please enter a valid city name..", Toast.LENGTH_SHORT).show ();
            }
        });
        requestQueue.add ( jsonObjectRequest);
    }
}