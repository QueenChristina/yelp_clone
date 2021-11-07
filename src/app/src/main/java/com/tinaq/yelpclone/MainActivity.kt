package com.tinaq.yelpclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val TAG = "MainActivity"
private const val BASE_URL = "https://api.yelp.com/v3/"
val API_KEY = "4mUY1zBuyEt81B2DrFn4lHDs2dMjT8UQ83b_sr21HDx4Cp3MoVv1PNYrohLmYwrRoZE5Q2ahLCyj3UVEEL7m9JnNnO6s5MJXtIuBt3dDgzhCjLsarUJYZOSfXriFYXYx"
class MainActivity : AppCompatActivity() {
    private lateinit var rvRestaurants : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvRestaurants = findViewById<RecyclerView>(R.id.rvRestaurants);

        // set icon
//        val actionBar : ActionBar? = getActionBar()
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        getSupportActionBar()?.setIcon(R.drawable.yelp_logo_svg)

        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantsAdapter(this, restaurants)

        rvRestaurants.adapter = adapter
        rvRestaurants.layoutManager = LinearLayoutManager(this)

        val retrofit =
            Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build()
        val yelpService = retrofit.create(YelpService::class.java)
        yelpService.searchRestaurants("Bearer $API_KEY", "Avocado Toast", "New York").enqueue(object : Callback<YelpSearchResult> {
            override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                Log.i(TAG, "onResponse $response")
                val body = response.body()
                if (body == null) {
                    Log.w(TAG, "Did not receive valid response body from Yelp API...exiting")
                    return
                }
                restaurants.addAll(body.restaurants)
                adapter.notifyDataSetChanged()
            }



            override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }

        })
        // Networking requires non-main thread
    }
}