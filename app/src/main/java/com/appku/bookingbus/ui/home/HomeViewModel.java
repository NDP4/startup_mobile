package com.appku.bookingbus.ui.home;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;
import com.appku.bookingbus.utils.SessionManager;

import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.data.model.Bus;
import com.appku.bookingbus.api.response.BusListResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<Bus>> mBuses;

    public HomeViewModel(Application application) {
        super(application);
        mText = new MutableLiveData<>();
        mText.setValue("Hi, John!");
        mBuses = new MutableLiveData<>();
        loadBuses();
    }

    private void loadBuses() {
        // Get token from SessionManager
        SessionManager sessionManager = new SessionManager(getApplication());
        String token = "Bearer " + sessionManager.getToken();
        
        // Make API call with token
        ApiClient.getInstance().getService().getBuses(token).enqueue(new Callback<BusListResponse>() {
            @Override
            public void onResponse(Call<BusListResponse> call, Response<BusListResponse> response) {
                android.util.Log.d("HomeViewModel", "Response code: " + response.code());
                if (response.isSuccessful()) {
                    android.util.Log.d("HomeViewModel", "Response body: " + response.body());
                    if (response.body() != null && response.body().isSuccess()) {
                        android.util.Log.d("HomeViewModel", "Bus list size: " + response.body().getData().getData().size());
                        mBuses.setValue(response.body().getData().getData());
                    } else {
                        android.util.Log.e("HomeViewModel", "Response unsuccessful or null");
                    }
                } else {
                    android.util.Log.e("HomeViewModel", "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<BusListResponse> call, Throwable t) {
                // Handle error
                t.printStackTrace();
            }
        });
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<Bus>> getBuses() {
        return mBuses;
    }
}