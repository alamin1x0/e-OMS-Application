package com.alamin1x0.e_oms.ui

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
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
import com.alamin1x0.e_oms.databinding.ActivityLoginBinding
import com.alamin1x0.e_oms.model.LoginResponse
import com.alamin1x0.e_oms.model.UserLoginModel
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

class LoginActivity<IOException : Any> : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    var LoginLocationName: String = ""
    var LoginLocationLat: String = ""
    var LoginLocationLng: String = ""
    var LoginDeviceName: String = ""
    var LoginDeviceID: String = ""


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_CODE = 100
    private val permission_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

    private lateinit var locationManager: LocationManager

    var premissions = arrayOf("android.permission.POST_NOTIFICATIONS")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        requestRuntimePermission()

        requestPermissions(premissions, 80)

        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        currentLocation()

        LoginLocationLat = binding.signInLocationLat.text.toString()
        LoginLocationLng = binding.signInLocationLat.text.toString()

        if (!isLocationEnabled) {
            val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(locationSettingsIntent, REQUEST_CODE)
        }

        binding.userRegistion.setOnClickListener {
            startActivity(Intent(this, RegisiterActivity::class.java))
        }

        LoginDeviceName = "${Build.BRAND}" + " ${Build.MODEL}"
        LoginDeviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        binding.signIn.setOnClickListener {
            val userName = binding.userEmail.text.toString()
            val password = binding.userPassword.text.toString()

            if (userName.isEmpty()) {
                binding.userEmail.setError("Username")
                binding.userEmail.requestFocus()
                return@setOnClickListener
            } else if (password.isEmpty()) {
                binding.userPassword.setError("Password")
                binding.userPassword.requestFocus()
                return@setOnClickListener
            } else {
                Config.showDialog(this)

                val preferences = this.getSharedPreferences("users", MODE_PRIVATE)
                val editor = preferences.edit()

                editor.putString("userName", binding.userEmail.text.toString())
                editor.apply()

                val loginRequest = UserLoginModel(
                    userName,
                    password,
                    LoginLocationName,
                    LoginLocationLat,
                    LoginLocationLng,
                    LoginDeviceName,
                    LoginDeviceID,
                )

                val call =
                    ApiUtilities.getInstance().create(ApiInterface::class.java)
                        .userLogin(loginRequest)

                call.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@LoginActivity,
                                response.body()!!.msg,
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()

                        }else{
                            Config.hideDialog()
                            Toast.makeText(this@LoginActivity, response.errorBody().toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Config.hideDialog()
                        Toast.makeText(
                            this@LoginActivity,
                            t.localizedMessage.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })

            }
        }
    }


    private fun requestRuntimePermission() {
        if (checkSelfPermission(permission_ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(
                this,
                "Permission Granted. You can use the API that requests the runtime permission",
                Toast.LENGTH_SHORT
            ).show()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                permission_ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(this)
                .setMessage("This app requires Final Location permission")
                .setTitle("Permission Granted")
                .setCancelable(false)
                .setPositiveButton("Ok") { dialogInterface: DialogInterface, _: Int ->
                    ActivityCompat.requestPermissions(
                        this@LoginActivity,
                        arrayOf(permission_ACCESS_FINE_LOCATION),
                        REQUEST_CODE
                    )
                    dialogInterface.dismiss()
                }
                .setNegativeButton("No") { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                .show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission_ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    permission_ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])
            ) {
                AlertDialog.Builder(this)
                    .setMessage("Please Allow Location")
                    .setTitle("Permission Granted")
                    .setCancelable(false)
                    .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int -> }
                    .setPositiveButton("Setting") { dialogInterface: DialogInterface, _: Int ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                        dialogInterface.dismiss()
                    }
                    .show()
            } else {
                requestRuntimePermission()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (isLocationEnabled) {
                // Location services are enabled, proceed with your logic
            } else {
                // Location services are still not enabled, handle accordingly
            }
        }
    }


    private fun currentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission_ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {

                    val geocoder = Geocoder(this, Locale.getDefault())
                    try {
                        val addresses: List<Address> =
                            geocoder.getFromLocation(it.latitude, it.longitude, 1)!!
                        LoginLocationLat = "${addresses[0].latitude}"
                        LoginLocationLng = "${addresses[0].longitude}"
                        LoginLocationName = "${addresses[0].getAddressLine(0)}, " + " ${addresses[0].adminArea}"


                    } catch (e: java.io.IOException) {
                        throw RuntimeException(e)
                    }

                }
            }
    }
}