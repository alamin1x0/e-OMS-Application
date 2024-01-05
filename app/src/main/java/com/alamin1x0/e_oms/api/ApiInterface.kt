package com.alamin1x0.e_oms.api

import com.alamin1x0.e_oms.model.AttendanceLocationModel
import com.alamin1x0.e_oms.model.AttendanceResponse
import com.alamin1x0.e_oms.model.LoginResponse
import com.alamin1x0.e_oms.model.RegistionResponse
import com.alamin1x0.e_oms.model.UserAttendanceModel
import com.alamin1x0.e_oms.model.UserAttendanceResponse
import com.alamin1x0.e_oms.model.UserLoginModel
import com.alamin1x0.e_oms.model.UserRegistionModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @Headers("User-Agent: PostmanRuntime/7.36.0")
    @POST("user_registration")
    fun userRegistion(@Body userRegistionModel: UserRegistionModel): Call<RegistionResponse>

    @Headers("User-Agent: PostmanRuntime/7.36.0")
    @POST("user_login")
    fun userLogin(@Body userLogin: UserLoginModel): Call<LoginResponse>


    @Headers("User-Agent: PostmanRuntime/7.36.0")
    @POST("user_attendance_location")
    fun userAttendanceLocation(@Body attendanceLocationModel: AttendanceLocationModel): Call<AttendanceResponse>


    @Headers("User-Agent: PostmanRuntime/7.36.0")
    @POST("user_attendance")
    fun userAttendance(@Body userAttendanceModel: UserAttendanceModel): Call<UserAttendanceResponse>


}