package com.alamin1x0.e_oms.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alamin1x0.e_oms.R
import com.alamin1x0.e_oms.api.ApiInterface
import com.alamin1x0.e_oms.api.ApiUtilities
import com.alamin1x0.e_oms.databinding.ActivityMainBinding
import com.alamin1x0.e_oms.model.AttendanceLocationModel
import com.alamin1x0.e_oms.model.AttendanceResponse
import com.alamin1x0.e_oms.model.UserAttendanceModel
import com.alamin1x0.e_oms.model.UserAttendanceResponse
import com.alamin1x0.e_oms.utils.NetworkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    var LoginLocationLat: String = ""
    var LoginLocationLng: String = ""
    var AttLocationName: String = ""
    var attDeviceName: String = ""
    var attDeviceID: String = ""

    var preferences: SharedPreferences? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_CODE = 100
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lateinit var dialog: AlertDialog
        dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded)
            .setView(R.layout.wifi_layout)
            .setCancelable(false)
            .create()

        val networkManager = NetworkManager(this)
        networkManager.observe(this) {

            if (!it) {
                if (!dialog.isShowing)
                    dialog.show()
            } else {
                if (dialog.isShowing)
                    dialog.hide()
            }
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        currentLocation()

        if (!isLocationEnabled) {
            val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(locationSettingsIntent, REQUEST_CODE)
        }

       preferences = this.getSharedPreferences("users", AppCompatActivity.MODE_PRIVATE)

        attDeviceName = "${Build.BRAND}" + " ${Build.MODEL}"
        attDeviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        binding.attendanceId.setOnClickListener {
            val userattendanceRequest = UserAttendanceModel(
                preferences!!.getString("userName", "")!!,
                AttLocationName,
                LoginLocationLat,
                LoginLocationLng,
                attDeviceName,
                attDeviceID
            )

            Log.d("userattendanceRequest", "onCreate: "+userattendanceRequest)

            val call =
                ApiUtilities.getInstance().create(ApiInterface::class.java)
                    .userAttendance(userattendanceRequest)

            call.enqueue(object : Callback<UserAttendanceResponse> {
                override fun onResponse(
                    call: Call<UserAttendanceResponse>,
                    response: Response<UserAttendanceResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, response.body()!!.msg, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            response.errorBody().toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UserAttendanceResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

            })

        }


        binding.buttonId.setOnClickListener {
            val attendanceRequest = AttendanceLocationModel(
                preferences!!.getString("userName", "")!!,
                LoginLocationLat,
                LoginLocationLng
            )
            Log.d("attendanceRequest", "onCreate: " + attendanceRequest)

            val call =
                ApiUtilities.getInstance().create(ApiInterface::class.java)
                    .userAttendanceLocation(attendanceRequest)

            call.enqueue(object : Callback<AttendanceResponse> {
                override fun onResponse(
                    call: Call<AttendanceResponse>,
                    response: Response<AttendanceResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, response.body()!!.msg, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            response.errorBody().toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AttendanceResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

            })
        }

    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun currentLocation() {

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {

                    val geocoder = Geocoder(this, Locale.getDefault())
                    try {
                        val addresses: List<Address> =
                            geocoder.getFromLocation(it.latitude, it.longitude, 1)!!
                        LoginLocationLat = "${addresses[0].latitude}"
                        LoginLocationLng = "${addresses[0].longitude}"
                        AttLocationName = "${addresses[0].getAddressLine(0)}, " + " ${addresses[0].adminArea}"


                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }

                }
            }
    }
}