package com.weather.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.squareup.picasso.Picasso
import com.weather.app.models.CurrentWeatherResponse
import com.weather.app.utils.GPSProvider
import com.weather.app.utils.Utilities
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(), MainView, View.OnClickListener {



    private lateinit var presenter: MainPresenter
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var getLatLongObj: GPSProvider? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter(this)


        etSearch.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(source: Editable?) {
                if (source!!.isNotEmpty()){
                    btnClear.visibility = View.VISIBLE
                } else {
                    btnClear.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if(etSearch.text.isNotEmpty()) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(Utilities().checkForInternetConnection(this)) {
                        showProgressDialog()
                        Utilities().hideSoftKeyBoard(this,etSearch)
                        presenter.getWeatherData(getString(R.string.end_point), etSearch.text.toString(), this)
                        return@setOnEditorActionListener true
                    } else {
                        Utilities()
                            .showAlertMessage(this, getString(R.string.no_internet_connection))
                    }
                }
            }
            return@setOnEditorActionListener false
        }


        btnClear.setOnClickListener(this)
        getLatLongObj = GPSProvider.getinstance(this)
    }

    override fun onStart() {
        super.onStart()
        if (Utilities().checkEnableGPS(this)) {
            if (Utilities().checkAndRequestPermissions(this)) {
                if (Utilities().checkForInternetConnection(this))
                    getCurrentWeatherDataLatLng()
                else Utilities().showAlertMessage(this, getString(R.string.no_internet_connection))
            }
        } else {
            Utilities().showAlertMessage(this, getString(R.string.disabled_gps))
        }
    }

    override fun onSuccessGetCurrentWeatherData(response: CurrentWeatherResponse?) {
        response?.let {
            hideProgressDialog()
            if(etSearch.text.isEmpty()) {
                etSearch.setText(response.name)
            }

            val imgUrl = getString(R.string.imgUrl, response.weather[0].icon)
            Picasso.get().load(imgUrl).error(R.drawable.ic_sun).into(imgWeatherIcon)

            tvWeatherDegrees.text = response.main.temp.toInt().toString()
            tvWeatherFeelsLike.text = getString(R.string.weather_feels_like, response.main.feels_like.toInt().toString())
            tvWeatherDescription.text = response.weather[0].description.capitalize()

            tvWeatherPressure.text = getString(R.string.weather_pressure, response.main.pressure.toString())
            tvWeatherHumidity.text = getString(R.string.weather_humidity, response.main.humidity.toString())
            tvWeatherCloudCoverage.text = getString(R.string.weather_cloud, response.clouds.all.toString())
            tvWeatherVisibility.text = getString(R.string.weather_visibility, (response.visibility/1000).toString())

        }
    }

    override fun onFailedGetCurrentWeatherData(error: String?) {
        hideProgressDialog()
        error?.let { Utilities()
            .showAlertMessage(this, error) }
    }

    override fun onFailedGetCurrentWeatherData() {
        hideProgressDialog()
        Utilities().showAlertMessage(this, getString(R.string.default_error_message))
    }

    override fun showProgressDialog() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressDialog() {
        progressBar.visibility = View.GONE
    }

    override fun onClick(view: View?) {
        if(view != null){
            if(view.id == btnClear.id) {
                etSearch.text.clear()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Utilities().locationPermissionRequest -> {
                val perms = HashMap<String, Int>()
                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED) {
                        getCurrentWeatherDataLatLng()
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            Utilities().showLocationPermissionsNeededDialog(this)
                        } else {
                            Utilities().showLocationPermissionsNeededDialog(this)
                        }
                    }
                }
            }
        }
    }


    private fun getCurrentWeatherDataLatLng() {
        showProgressDialog()
        startGPS()
        try {
            latitude = getGetLatLongObj()!!.latitude
            longitude = getGetLatLongObj()!!.longitude
            presenter.getWeatherData(getString(R.string.end_point), latitude!!, longitude!!, this)
        } catch (e: Exception) {
            e.printStackTrace()
            stopGPS()
        }

    }

    private fun startGPS() {
        if (getLatLongObj != null)
            getLatLongObj!!.startGPS()
    }

    private fun stopGPS() {
        if (getLatLongObj != null) {
            getLatLongObj!!.stopLocationListening()
        }
    }

    fun getGetLatLongObj(): GPSProvider? {
        return getLatLongObj
    }

    override fun onStop() {
        super.onStop()
        stopGPS()
    }

}