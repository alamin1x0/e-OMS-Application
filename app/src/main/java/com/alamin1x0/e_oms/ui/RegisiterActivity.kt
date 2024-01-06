package com.alamin1x0.e_oms.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.alamin1x0.e_oms.R
import com.alamin1x0.e_oms.api.ApiInterface
import com.alamin1x0.e_oms.api.ApiUtilities
import com.alamin1x0.e_oms.databinding.ActivityRegisiterBinding
import com.alamin1x0.e_oms.model.RegistionResponse
import com.alamin1x0.e_oms.model.UserRegistionModel
import com.alamin1x0.e_oms.utils.Config
import com.alamin1x0.e_oms.utils.NetworkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Locale

class RegisiterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisiterBinding

    var userName: String = ""
    var userPassword: String = ""
    var userEmail: String = ""
    var signInDeviceName: String = ""
    var signInDeviceID: String = ""
    var signInLocationLat: String = ""
    var signInLocationLng: String = ""
    var signInLocationName: String = ""

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_CODE = 100
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisiterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()


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

        signInLocationLat = binding.signInLocationLat.text.toString()
        signInLocationLng = binding.signInLocationLat.text.toString()

        if (!isLocationEnabled) {
            val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(locationSettingsIntent, REQUEST_CODE)
        }

        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        signInDeviceName = "${Build.BRAND}"  +  " ${Build.MODEL}"
        signInDeviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        binding.RegisterBtn.setOnClickListener {

            userName = binding.userName.text.toString()
            userEmail = binding.userEmail.text.toString()
            userPassword = binding.userPassword.text.toString()

            if (userName.isEmpty()) {
                binding.userName.setError("Username")
                binding.userName.requestFocus()
            } else if (userEmail.isEmpty()) {
                binding.userEmail.setError("Email")
                binding.userEmail.requestFocus()
            } else if (userPassword.isEmpty()) {
                binding.userPassword.setError("Password")
                binding.userPassword.requestFocus()
            } else {
                Config.showDialog(this)

                val preferences = this.getSharedPreferences("users", MODE_PRIVATE)
                val editor = preferences.edit()

                editor.putString("userName", binding.userName.text.toString())
                editor.putString("userPassword", binding.userPassword.text.toString())
                editor.apply()

                val call =
                    ApiUtilities.getInstance().create(ApiInterface::class.java)
                        .userRegistion(
                            UserRegistionModel(
                                userName,
                                userPassword,
                                userEmail,
                                signInDeviceName,
                                signInDeviceID,
                                signInLocationLat,
                                signInLocationLng,
                                signInLocationName
                            )
                        )

                call.enqueue(object : Callback<RegistionResponse> {
                    override fun onResponse(
                        call: Call<RegistionResponse>,
                        response: Response<RegistionResponse>
                    ) {

                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@RegisiterActivity,
                                response.body()!!.msg,
                                Toast.LENGTH_SHORT
                            ).show()

                            if (response.body()!!.status == "Success") {
                                startActivity(Intent(this@RegisiterActivity, MainActivity::class.java))
                                finish()
                            } else {
                                Config.hideDialog()
                                Toast.makeText(
                                    this@RegisiterActivity,
                                    response.body()!!.status,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Config.hideDialog()
                            Toast.makeText(
                                this@RegisiterActivity,
                                response.errorBody().toString(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<RegistionResponse>, t: Throwable) {
                        Config.hideDialog()
                        t.printStackTrace()
                        Log.d("error", "onFailure: " + t.message)
                        Toast.makeText(
                            this@RegisiterActivity,
                            t.localizedMessage.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
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
                        signInLocationLat = "${addresses[0].latitude}"
                        signInLocationLng = "${addresses[0].longitude}"
                        signInLocationName = "${addresses[0].getAddressLine(0)}, " + " ${addresses[0].adminArea}"

                        var signInLocationLatData = signInLocationLat!!.toDouble()
                        var signInLocationLngData = signInLocationLng!!.toDouble()
                        val totalLanth = signInLocationLatData - signInLocationLngData

                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }

                }
            }
    }
}