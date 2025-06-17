package com.appku.bookingbus.api;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://bus.ndp.my.id/api/";
    private static ApiClient instance;
    private final ApiService service;

    private ApiClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    try {
                        return chain.proceed(request);
                    } catch (SocketTimeoutException e) {
                        throw new IOException("Connection timeout. Please check your internet connection and try again.");
                    } catch (ConnectException e) {
                        throw new IOException("Unable to connect to the server. Please try again later.");
                    } catch (Exception e) {
                        throw new IOException("Network error: " + e.getMessage());
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public ApiService getService() {
        return service;
    }
}