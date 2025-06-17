package com.appku.bookingbus.ui.profile;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.appku.bookingbus.utils.SessionManager;

public class ProfileViewModel extends AndroidViewModel {
    private final SessionManager sessionManager;
    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final MutableLiveData<String> userEmail = new MutableLiveData<>();

    public ProfileViewModel(Application application) {
        super(application);
        sessionManager = new SessionManager(application);
        loadUserData();
    }

    private void loadUserData() {
        userName.setValue(sessionManager.getUserName());
        userEmail.setValue(sessionManager.getUserEmail());
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getUserEmail() {
        return userEmail;
    }

    public void logout() {
        sessionManager.logout();
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
}