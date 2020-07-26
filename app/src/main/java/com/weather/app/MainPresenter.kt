package com.weather.app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.weather.app.interfaces.ApiService
import com.weather.app.models.CurrentWeatherResponse
import com.weather.app.models.ErrorModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainPresenter(val view: MainView){

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASEURL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val apiService = retrofit.create(ApiService::class.java)



    fun getWeatherData(url: String, cityName: String, context: Context) {
        val weatherDataCall = apiService.getCurrentWeather(url, cityName,  BuildConfig.WEATHERAPIKEY, context.getString(R.string.units_metric))

        weatherDataCall.enqueue(object: Callback<CurrentWeatherResponse> {
            override fun onResponse(
                call: Call<CurrentWeatherResponse>,
                response: Response<CurrentWeatherResponse>
            ) {
                if(response.body() != null) {
                    if(response.code() == 200) {
                        view.onSuccessGetCurrentWeatherData(response.body())
                    } else {
                        view.onFailedGetCurrentWeatherData()
                    }
                } else if(response.errorBody() != null) {
                    val errorModel = Gson().fromJson(response.errorBody()!!.string(), ErrorModel::class.java)
                    if(errorModel.message != null && errorModel.message.isNotEmpty()) {
                        view.onFailedGetCurrentWeatherData(errorModel.message)
                    } else view.onFailedGetCurrentWeatherData()
                }
            }

            override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
                view.onFailedGetCurrentWeatherData(t.message)
            }
        })
    }

    fun getWeatherData(url: String, lat: Double, lon: Double, context: Context) {
        val weatherDataCall = apiService.getCurrentWeatherLatLng(url, lat, lon,  BuildConfig.WEATHERAPIKEY, context.getString(R.string.units_metric))

        weatherDataCall.enqueue(object: Callback<CurrentWeatherResponse> {
            override fun onResponse(
                call: Call<CurrentWeatherResponse>,
                response: Response<CurrentWeatherResponse>
            ) {
                if(response.body() != null) {
                    if(response.code() == 200) {
                        view.onSuccessGetCurrentWeatherData(response.body())
                    } else {
                        view.onFailedGetCurrentWeatherData()
                    }
                } else if(response.errorBody() != null) {
                    val errorModel = Gson().fromJson(response.errorBody()!!.string(), ErrorModel::class.java)
                    if(errorModel.message != null && errorModel.message.isNotEmpty()) {
                        view.onFailedGetCurrentWeatherData(errorModel.message)
                    } else view.onFailedGetCurrentWeatherData()
                }
            }

            override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
                view.onFailedGetCurrentWeatherData()
            }
        })
    }

}

interface MainView {
    fun onSuccessGetCurrentWeatherData(response: CurrentWeatherResponse?)
    fun onFailedGetCurrentWeatherData(error: String?)
    fun onFailedGetCurrentWeatherData()
    fun showProgressDialog()
    fun hideProgressDialog()
}