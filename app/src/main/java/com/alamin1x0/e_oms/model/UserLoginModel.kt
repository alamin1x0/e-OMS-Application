package com.alamin1x0.e_oms.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserLoginModel(

    @SerializedName("userName")
    @Expose
    var userName: String? = null,

    @SerializedName("password")
    @Expose
    var password: String? = null,

    val LoginLocationName: String,
    val LoginLocationLat: String,
    val LoginLocationLng: String,
    val LoginDeviceName: String,
    val LoginDeviceID: String,
    val LoginDeviceBatteryHealth: String,
    )



