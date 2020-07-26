package com.weather.app.interfaces

import com.weather.app.models.CurrentWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET
    fun getCurrentWeather(@Url url:String, @Query("q") cityName: String, @Query("appid") apiKey: String, @Query("units") units: String): Call<CurrentWeatherResponse>

    @GET
    fun getCurrentWeatherLatLng(@Url url:String,@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("appid") apiKey: String,
                                @Query("units") units: String): Call<CurrentWeatherResponse>
}