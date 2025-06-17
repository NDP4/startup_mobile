package com.appku.bookingbus.ui.home;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.api.response.BusListResponse;
import com.appku.bookingbus.data.model.ListBus;
import com.appku.bookingbus.utils.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {
    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<ListBus>> mBuses;
    private final SessionManager sessionManager;

    public HomeViewModel(Application application) {
        super(application);
        sessionManager = new SessionManager(application);
        mText = new MutableLiveData<>();
        String userName = sessionManager.getUserName() != null ? sessionManager.getUserName() : "Guest";
        mText.setValue("Hi, " + userName + "!");
        mBuses = new MutableLiveData<>();
        loadBuses();
    }

    private void loadBuses() {
        String token = "Bearer " + sessionManager.getToken();
        ApiClient.getInstance().getService().getBuses(token).enqueue(new Callback<BusListResponse>() {
            @Override
            public void onResponse(Call<BusListResponse> call, Response<BusListResponse> response) {
                Log.d("HomeViewModel", "Response code: " + response.code());
                if (response.isSuccessful()) {
                    Log.d("HomeViewModel", "Response body: " + response.body());
                    if (response.body() != null && response.body().isSuccess()) {
                        Log.d("HomeViewModel", "Bus list size: " + response.body().getData().getData().size());
                        mBuses.setValue(response.body().getData().getData());
                    } else {
                        Log.e("HomeViewModel", "Response unsuccessful or null");
                    }
                } else {
                    Log.e("HomeViewModel", "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<BusListResponse> call, Throwable t) {
                Log.e("HomeViewModel", "Error: " + t.getMessage());
            }
        });
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<ListBus>> getBuses() {
        return mBuses;
    }
}