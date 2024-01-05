package com.alamin1x0.e_oms.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AttendanceResponse(

    @SerializedName("msg")
    @Expose
    var msg: String? = null,

    @SerializedName("status")
    @Expose
    var status: String? = null

)