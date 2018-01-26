package com.example.mholt2587.yelptest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mListView = (ListView) findViewById(R.id.list_view);

        String term = "restuarant";
        double latitude = 40.015;
        double longitude = -105.271;
        final String yelpUrl = "https://api.yelp.com/v3/businesses/search?term=" + term + "&latitude=" + latitude +  "&longitude=" + longitude;



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

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nameArray);
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

