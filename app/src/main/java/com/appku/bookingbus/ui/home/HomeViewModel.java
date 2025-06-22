package com.appku.bookingbus.ui.home;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.api.response.BusListResponse;
import com.appku.bookingbus.data.model.ListBus;
import com.appku.bookingbus.utils.CacheManager;
import com.appku.bookingbus.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {
    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<ListBus>> mBuses;
    private final SessionManager sessionManager;
    private final CacheManager cacheManager;
    private static final String CACHE_KEY_BUS_LIST = "bus_list";
    private static final String CACHE_KEY_BUS_COUNT = "bus_count";

    public HomeViewModel(Application application) {
        super(application);
        sessionManager = new SessionManager(application);
        cacheManager = new CacheManager(application);
        mText = new MutableLiveData<>();
        String userName = sessionManager.getUserName() != null ? sessionManager.getUserName() : "Guest";
        mText.setValue("Hi, " + userName + "!");
        mBuses = new MutableLiveData<>();
        loadBuses();
    }

    private void loadBuses() {
        // 1. Cek cache count
        String token = "Bearer " + sessionManager.getToken();
        ApiClient.getInstance().getService().getBuses(token).enqueue(new Callback<BusListResponse>() {
            @Override
            public void onResponse(Call<BusListResponse> call, Response<BusListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    int serverCount = response.body().getData().getData().size();
                    int cachedCount = (int) cacheManager.getLong(CACHE_KEY_BUS_COUNT);
                    if (serverCount == cachedCount) {
                        // Tidak ada perubahan, load dari cache
                        String cachedJson = cacheManager.getString(CACHE_KEY_BUS_LIST);
                        if (cachedJson != null) {
                            List<ListBus> cachedList = new Gson().fromJson(cachedJson, new TypeToken<List<ListBus>>(){}.getType());
                            mBuses.setValue(cachedList);
                            return;
                        }
                    }
                    // Ada perubahan, update cache dan UI
                    List<ListBus> busList = response.body().getData().getData();
                    mBuses.setValue(busList);
                    cacheManager.saveString(CACHE_KEY_BUS_LIST, new Gson().toJson(busList));
                    cacheManager.saveLong(CACHE_KEY_BUS_COUNT, serverCount);
                }
            }
            @Override
            public void onFailure(Call<BusListResponse> call, Throwable t) {
                // Jika gagal, coba load dari cache
                String cachedJson = cacheManager.getString(CACHE_KEY_BUS_LIST);
                if (cachedJson != null) {
                    List<ListBus> cachedList = new Gson().fromJson(cachedJson, new TypeToken<List<ListBus>>(){}.getType());
                    mBuses.setValue(cachedList);
                }
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