@file:Suppress("DEPRECATION")

package com.weather.app.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.weather.app.R


class Utilities {

    val locationPermissionRequest = 1

    fun showAlertMessage(context: Context, msg: String){

        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(context.getString(R.string.app_name))
            .setMessage(msg)
            .setNegativeButton("OK"
            ) {
                    dialog, p1 -> dialog!!.cancel()
            }
            .setCancelable(false)
            .show()

    }

    fun checkForInternetConnection(context: Context) : Boolean{

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }


    fun checkAndRequestPermissions(activity: Activity): Boolean {

        val locationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        val listPermissionsNeeded = ArrayList<String>()
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toTypedArray(), locationPermissionRequest)
            return false
        }
        return true
    }

    fun checkEnableGPS(context: Context): Boolean {
        val locationProviders: String =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)

        if (locationProviders.contains(LocationManager.GPS_PROVIDER) && locationProviders.contains(
                LocationManager.NETWORK_PROVIDER)) {
            return true
        }
        return false
    }

    fun showLocationPermissionsNeededDialog(context: Context){
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
        alertDialog.setMessage(context.getString(R.string.permissions_denied))
            .setPositiveButton(context.getString(R.string.text_ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
        alertDialog.show()
    }

    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }
    }



}