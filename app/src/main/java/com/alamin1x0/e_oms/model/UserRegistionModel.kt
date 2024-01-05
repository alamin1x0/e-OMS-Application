package com.alamin1x0.e_oms.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserRegistionModel(

    @SerializedName("userName")
    @Expose
    var userName: String? = null,

    @SerializedName("userPassword")
    @Expose
    var userPassword: String? = null,

    @SerializedName("userEmail")
    @Expose
    var userEmail: String? = null,

    @SerializedName("signInDeviceName")
    @Expose
    var signInDeviceName: String? = null,

    @SerializedName("signInDeviceID")
    @Expose
    var signInDeviceID: String? = null,

    @SerializedName("signInLocationLat")
    @Expose
    var signInLocationLat: String? = null,

    @SerializedName("signInLocationLng")
    @Expose
    var signInLocationLng: String? = null,

    @SerializedName("signInLocationName")
    @Expose
    var signInLocationName: String? = null,


    )