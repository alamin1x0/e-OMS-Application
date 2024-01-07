package com.alamin1x0.e_oms.model

data class UserAttendanceModel(

    var userName: String,
    var AttLocationName: String,
    var AttLocationLat: String,
    var attLocationLng: String,
    var attDeviceName: String,
    var attDeviceID: String,
    var attDeviceBatteryHealth: String,
)