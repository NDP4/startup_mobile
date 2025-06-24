// ApiService.java
package com.appku.bookingbus.api;

import com.appku.bookingbus.api.request.BookingRequest;
import com.appku.bookingbus.api.request.LoginRequest;
import com.appku.bookingbus.api.request.RegisterRequest;
import com.appku.bookingbus.api.request.PaymentRequest;
import com.appku.bookingbus.api.response.AuthResponse;
import com.appku.bookingbus.api.response.ApiResponse;
import com.appku.bookingbus.api.response.BookingResponse;
import com.appku.bookingbus.api.response.UserDetailResponse;
import com.appku.bookingbus.api.response.BusListResponse;
import com.appku.bookingbus.api.response.BusResponse;
import com.appku.bookingbus.api.response.BookingListResponse;
import com.appku.bookingbus.api.response.BookingDetailResponse;
import com.appku.bookingbus.api.response.PaymentResponse;
import com.appku.bookingbus.data.model.UserResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("user/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("user/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("user/detail")
    Call<UserDetailResponse> getUserDetail(@Header("Authorization") String token);
    @GET("users/{id}")
    Call<UserResponse> getUserProfile(
            @Header("Authorization") String token,
            @Path("id") int userId
    );
    
    @GET("buses")
    Call<BusListResponse> getBuses(@Header("Authorization") String token);

    @GET("buses/{id}")
    Call<BusResponse> getBusById(@Header("Authorization") String token, @Path("id") int id);

    @POST("buses/{bus}/book")
    Call<BookingResponse> createBooking(
            @Header("Authorization") String token,
            @Path("bus") int busId,
            @Body BookingRequest request
    );

    @GET("bookings")
    Call<BookingListResponse> getBookings(@Header("Authorization") String token);

    @GET("bookings/{id}")
    Call<BookingDetailResponse> getBookingDetail(@Header("Authorization") String token, @Path("id") int bookingId);

    @POST("payments")
    Call<PaymentResponse> createPayment(
        @Header("Authorization") String token,
        @Body PaymentRequest paymentRequest
    );
}