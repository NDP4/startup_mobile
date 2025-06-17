// ApiService.java
package com.appku.bookingbus.api;

import com.appku.bookingbus.api.request.LoginRequest;
import com.appku.bookingbus.api.request.RegisterRequest;
import com.appku.bookingbus.api.response.AuthResponse;
import com.appku.bookingbus.api.response.ApiResponse;
import com.appku.bookingbus.api.response.UserDetailResponse;
import com.appku.bookingbus.api.response.BusListResponse;
import com.appku.bookingbus.data.model.Bus;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("user/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("user/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("user/detail")
    Call<UserDetailResponse> getUserDetail(@Header("Authorization") String token);
    
    @GET("buses")
    Call<BusListResponse> getBuses(@Header("Authorization") String token);
}