// app/src/main/java/com/appku/bookingbus/utils/BiometricManager.java
package com.appku.bookingbus.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

public class BiometricManager {
    private final Context context;
    private final BiometricAuthListener listener;
    private final SessionManager sessionManager;
    private final BiometricPrompt biometricPrompt;
    private final BiometricPrompt.PromptInfo promptInfo;

    private String cachedUserEmail;
    private String cachedUserName;
    private String cachedToken;

    public interface BiometricAuthListener {
        void onBiometricAuthenticationSuccess();
        void onBiometricAuthenticationError(int errorCode, String error);
    }

    public BiometricManager(Context context, BiometricAuthListener listener) {
        this.context = context;
        this.listener = listener;
        this.sessionManager = new SessionManager(context);

        // Setup biometric prompt
        Executor executor = ContextCompat.getMainExecutor(context);
        biometricPrompt = new BiometricPrompt((FragmentActivity) context,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                onAuthenticationSuccess();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (listener != null) {
                    listener.onBiometricAuthenticationError(errorCode, errString.toString());
                }
            }
        });

        // Configure prompt info
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();
    }

    public void authenticate() {
        biometricPrompt.authenticate(promptInfo);
    }

    public void setCachedUserData(String email, String userName, String token) {
        this.cachedUserEmail = email;
        this.cachedUserName = userName;
        this.cachedToken = token;
    }

    private void onAuthenticationSuccess() {
        if (cachedUserEmail != null && cachedUserName != null && cachedToken != null) {
            sessionManager.saveAuthToken(cachedToken, cachedUserName, cachedUserEmail);
            if (listener != null) {
                listener.onBiometricAuthenticationSuccess();
            }
        }
    }

    public boolean isBiometricEnabled() {
        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(context);
        int canAuthenticate = biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG);
        return canAuthenticate == androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS && sessionManager.isBiometricEnabled();
    }
}