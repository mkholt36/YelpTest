package com.example.mholt2587.yelptest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

   private ListView mListView;

    Button getLL;

    // GPSTracker class
    GPSTracker gps;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

//        getLL = (Button)findViewById(R.id.getLL);
//        getLL.setOnClickListener(new View.OnClickListener() {
//            @Override            public void onClick(View view) {
//
//
//                if (ContextCompat.checkSelfPermission(mContext,
//                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//
//                        && ActivityCompat.checkSelfPermission(mContext,
//                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(MainActivity.this,
//                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//
//                } else {
//                    Toast.makeText(mContext, "You need to have granted permission", Toast.LENGTH_SHORT).show();
//                    gps = new GPSTracker(mContext, MainActivity.this);
//
//                    // Check if GPS enabled
//
//                    if (gps.canGetLocation()) {
//
//                        double latitude = gps.getLatitude();
//                        double longitude = gps.getLongitude();
//
//                        // \n is for new line
//
//                        Toast.makeText(getApplicationContext(),
//                                "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
//                    } else {
//
//                        // Can't get location.
//
//                        // GPS or network is not enabled.
//
//                        // Ask user to enable GPS/network in settings.
//
//                        gps.showSettingsAlert();
//                    }
//                }
//
//            }
//        });

        mListView = (ListView) findViewById(R.id.list_view);

        String term = "restuarant";
        double latitude = 40.015;
        double longitude = -105.271;



        gps = new GPSTracker(mContext, MainActivity.this);

        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        else {
            gps.showSettingsAlert();
        }

        final String yelpUrl = "https://api.yelp.com/v3/businesses/search?term=" + term + "&latitude=" + latitude +  "&longitude=" + longitude;
        Log.d(TAG, String.valueOf(latitude));
        Log.d(TAG, String.valueOf(longitude));


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(yelpUrl)
                .header("Authorization", "Bearer OkHCf4HExwOWu_BTa2P3QLyOEOeWMj040H2u7Fgi-d7m-CYyPyEoaet-QKfRgFJy_Ai31KNdKu_ke25XEy6dzAb6fu-A9cNn8RwftomjYE-xbgy9XoSk_BVmXiTEWXYx")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {




            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                Log.d(TAG, "failure");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    final String jsonData = response.body().string();
                    Log.v(TAG, "THIS IS MY JSONDATA " + jsonData);

                    if (response.isSuccessful()) {
                        Log.d(TAG, yelpUrl);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    getCurrentDetails(jsonData);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                        Log.v(TAG, jsonData);

                    }
                }
                catch (IOException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }




            }
        });

        Log.d(TAG, "Main UI code is running!");

    }

//    @Override    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        switch (requestCode) {
//            case 1: {
//                // If request is cancelled, the result arrays are empty.
//
//                if (grantResults.length > 0
//
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    gps = new GPSTracker(mContext, MainActivity.this);
//
//
//                    // Check if GPS enabled
//
//                    if (gps.canGetLocation()) {
//
//                        double latitude = gps.getLatitude();
//                        double longitude = gps.getLongitude();
//
//                        // \n is for new line
//
//                        Toast.makeText(getApplicationContext(),
//                                "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
//                    } else {
//                        // Can't get location.
//
//                        // GPS or network is not enabled.
//
//                        // Ask user to enable GPS/network in settings.
//
//                        gps.showSettingsAlert();
//                    }
//
//                } else {
//
//                    // permission denied, boo! Disable the
//
//                    // functionality that depends on this permission.
//                    Toast.makeText(mContext, "You need to grant permission", Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//        }
//    }



    private void getCurrentDetails(String jsonData) throws JSONException {


        JSONObject yelpJSON = new JSONObject(jsonData);
        JSONArray businessesJSON = yelpJSON.getJSONArray("businesses");
        Restaurant[] restaurants  = new Restaurant[businessesJSON.length()];
        String[] nameArray = new String[businessesJSON.length()];
        for (int i = 0; i < businessesJSON.length(); i++) {
            JSONObject restaurantJSON = businessesJSON.getJSONObject(i);

            String name = restaurantJSON.getString("name");
            String phone = restaurantJSON.optString("display_phone", "Phone not available");
            String website = restaurantJSON.getString("url");
            double rating = restaurantJSON.getDouble("rating");


            String imageUrl = restaurantJSON.getString("image_url");

            double latitude = (double) restaurantJSON.getJSONObject("coordinates").getDouble("latitude");

            double longitude = (double) restaurantJSON.getJSONObject("coordinates").getDouble("longitude");

            ArrayList<String> address = new ArrayList<>();
            JSONArray addressJSON = restaurantJSON.getJSONObject("location")
                    .getJSONArray("display_address");
            for (int y = 0; y < addressJSON.length(); y++) {
                address.add(addressJSON.get(y).toString());
            }

            ArrayList<String> categories = new ArrayList<>();
            JSONArray categoriesJSON = restaurantJSON.getJSONArray("categories");

            for (int y = 0; y < categoriesJSON.length(); y++) {
                categories.add(categoriesJSON.getJSONObject(y).getString("title"));
            }
            Restaurant restaurant = new Restaurant(name, phone, website, rating, imageUrl,
                     address, latitude, longitude, categories);
            restaurants[i] = restaurant;
            nameArray[i] = restaurants[i].getName();

        }

        RestaurantAdapter adapter = new RestaurantAdapter(this, restaurants);
        mListView.setAdapter(adapter);

        //loop through restaurants array and print out restaurants[i] to log
        for (int i = 0; i < businessesJSON.length(); i++) {

            Log.d(TAG, restaurants[i].getName());
            Log.d(TAG, restaurants[i].getPhone());
            Log.d(TAG, restaurants[i].getWebsite());
            Log.d(TAG, String.valueOf(restaurants[i].getRating()));
            Log.d(TAG, restaurants[i].getImageUrl());
            Log.d(TAG, String.valueOf(restaurants[i].getLatitude()));
            Log.d(TAG, String.valueOf(restaurants[i].getLongitude()));
            Log.d(TAG, String.valueOf(restaurants[i].getAddress()));
            Log.d(TAG, String.valueOf(restaurants[i].getCategories()));
        }

    }


}

