package com.example.mholt2587.yelptest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

    private String mYelpResponse;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




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
                    String jsonData = response.body().string();
                    Log.v(TAG, "THIS IS MY JSONDATA " + jsonData);

                    if (response.isSuccessful()) {
                        Log.d(TAG, yelpUrl);
                        //mYelpResponse = getCurrentDetails(jsonData);
                        getCurrentDetails(jsonData);

                        Log.v(TAG, jsonData);

                    }
                }
                catch (IOException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
                catch(JSONException e){
                    e.printStackTrace();
                }



            }
        });

        Log.d(TAG, "Main UI code is running!");

    }

    private void getCurrentDetails(String jsonData) throws JSONException {


        JSONObject yelpJSON = new JSONObject(jsonData);
        JSONArray businessesJSON = yelpJSON.getJSONArray("businesses");
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

            Log.d(TAG, name);
            Log.d(TAG, phone);
            Log.d(TAG, website);
            Log.d(TAG, String.valueOf(rating));
            Log.d(TAG, imageUrl);
            Log.d(TAG, String.valueOf(latitude));
            Log.d(TAG, String.valueOf(longitude));
            Log.d(TAG, String.valueOf(address));
            Log.d(TAG, String.valueOf(categories));



        }




        //return new YelpResponse();
    }


}
