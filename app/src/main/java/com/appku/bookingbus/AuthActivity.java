package com.appku.bookingbus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.api.ApiService;
import com.appku.bookingbus.api.response.AuthResponse;
import com.appku.bookingbus.api.request.LoginRequest;
import com.appku.bookingbus.api.request.RegisterRequest;
import com.appku.bookingbus.utils.BiometricManager;
import com.appku.bookingbus.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private ApiService apiService;
//    private GoogleSignInClient googleSignInClient;
    private BiometricManager biometricManager;
    private static final int RC_SIGN_IN = 9001;


    private void animateText(TextView textView, String text, long duration) {
        textView.setText("");
        int length = text.length();

        // Check if text is empty
        if (length == 0) {
            return;
        }

        long delay = duration / length;
        Handler handler = new Handler();

        for (int i = 0; i <= length; i++) {
            final int index = i;
            handler.postDelayed(() -> {
                textView.setText(text.substring(0, index));
            }, delay * i);
        }
    }

    private void performExitAnimation(View imageView, View... buttons) {
        ObjectAnimator imageSlideUp = ObjectAnimator.ofFloat(imageView, "translationY", 0f, -300f);
        ObjectAnimator imageFadeOut = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f);

        AnimatorSet imageAnimSet = new AnimatorSet();
        imageAnimSet.playTogether(imageSlideUp, imageFadeOut);
        imageAnimSet.setDuration(500);

        List<Animator> buttonAnimations = new ArrayList<>();
        for (View button : buttons) {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f);
            buttonAnimations.add(fadeOut);
        }

        AnimatorSet buttonAnimSet = new AnimatorSet();
        buttonAnimSet.playTogether(buttonAnimations);
        buttonAnimSet.setDuration(500);

        AnimatorSet exitAnimSet = new AnimatorSet();
        exitAnimSet.playTogether(imageAnimSet, buttonAnimSet);
        exitAnimSet.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        sessionManager = new SessionManager(this);
        setupApiService();
//        setupGoogleSignIn();

        biometricManager = new BiometricManager(this, new BiometricManager.BiometricAuthListener() {
            @Override
            public void onBiometricAuthenticationSuccess() {
                // Handle successful authentication
                startMainActivity();
            }

            @Override
            public void onBiometricAuthenticationError(int errorCode, String error) {
                showError("Biometric authentication error: " + error);
            }
        });

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        setContentView(R.layout.activity_auth);

        View imageIllustration = findViewById(R.id.imageIllustration);
        TextView tvSubtitle = findViewById(R.id.tvSubtitle);
        // MaterialButton btnGoogle = findViewById(R.id.btnGoogle); // dihapus
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);

        String subtitleText = tvSubtitle.getText().toString();

        // Initially hide all views
        imageIllustration.setTranslationY(-300f);
        imageIllustration.setAlpha(0f);
        tvSubtitle.setText("");
        // btnGoogle.setAlpha(0f); // dihapus
        btnLogin.setAlpha(0f);
        btnRegister.setAlpha(0f);

        // Image animation
        ObjectAnimator imageSlideDown = ObjectAnimator.ofFloat(imageIllustration, "translationY", -300f, 0f);
        ObjectAnimator imageAlpha = ObjectAnimator.ofFloat(imageIllustration, "alpha", 0f, 1f);

        AnimatorSet imageAnimSet = new AnimatorSet();
        imageAnimSet.playTogether(imageSlideDown, imageAlpha);
        imageAnimSet.setDuration(1000);

        // Button animations
        // ObjectAnimator googleAlpha = ObjectAnimator.ofFloat(btnGoogle, "alpha", 0f, 1f); // dihapus
        ObjectAnimator loginAlpha = ObjectAnimator.ofFloat(btnLogin, "alpha", 0f, 1f);
        ObjectAnimator registerAlpha = ObjectAnimator.ofFloat(btnRegister, "alpha", 0f, 1f);

        AnimatorSet buttonAnimSet = new AnimatorSet();
        buttonAnimSet.playTogether(loginAlpha, registerAlpha);
        buttonAnimSet.setDuration(1000);

        // biometric button in login
//        MaterialButton btnBiometric = findViewById(R.id.btnBiometric);
//        btnBiometric.setVisibility(biometricManager.isBiometricEnabled() ? View.VISIBLE : View.GONE);
//        btnBiometric.setOnClickListener(v -> biometricManager.authenticate());

        // Play all animations in sequence
        AnimatorSet allAnimSet = new AnimatorSet();
        allAnimSet.playSequentially(imageAnimSet, buttonAnimSet);
        allAnimSet.start();

        // Start typewriter effect after image animation
        imageAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Use the saved subtitle text
                animateText(tvSubtitle, subtitleText, 1500);
            }
        });

        // Handle button clicks
        View.OnClickListener buttonClickListener = v -> {
            performExitAnimation(imageIllustration, /*btnGoogle,*/ btnLogin, btnRegister);
            new Handler().postDelayed(() -> {
                // Handle different button actions here
                // if (v.getId() == R.id.btnGoogle) {
                //     handleGoogleSignIn();
                // } else 
                if (v.getId() == R.id.btnLogin) {
                    showLoginBottomSheet();
                } else if (v.getId() == R.id.btnRegister) {
                    showRegisterBottomSheet();
                }
            }, 500);
        };

        // btnGoogle.setOnClickListener(v -> signInWithGoogle()); // dihapus
        btnLogin.setOnClickListener(v -> showLoginBottomSheet());
        btnRegister.setOnClickListener(v -> showRegisterBottomSheet());
    }

    private void setupApiService() {
        apiService = ApiClient.getInstance().getService();
    }

//    private void setupGoogleSignIn() {
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        googleSignInClient = GoogleSignIn.getClient(this, gso);
//    }

    // AuthActivity.java
    private void handleLogin(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    if (authResponse.isSuccess()) {
                        sessionManager.saveAuthToken(
                                authResponse.getData().getToken(),
                                authResponse.getData().getUser(),
                                authResponse.getData().getEmail()
                        );
                        startMainActivity();
                    } else {
                        // Log error message from response
                        Log.e("LoginError", "Login failed: " + authResponse.getMessage());
                        showError(authResponse.getMessage());
                    }
                } else {
                    try {
                        // Log error body for debugging
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error";
                        Log.e("LoginError", "Response not successful: " + errorBody);
                        showError("Login failed: " + errorBody);
                    } catch (IOException e) {
                        Log.e("LoginError", "Error reading error body", e);
                        showError("Login failed: Unable to read error response");
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                // Log network/other errors
                Log.e("LoginError", "Network error", t);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void handleRegister(String name, String email, String password, String phone) {
        RegisterRequest request = new RegisterRequest(name, email, password, phone);
        apiService.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    if (authResponse.isSuccess()) {
                        sessionManager.saveAuthToken(
                                authResponse.getData().getToken(),
                                authResponse.getData().getUser(),
                                authResponse.getData().getEmail()
                        );
                        startMainActivity();
                    } else {
                        showError(authResponse.getMessage());
                    }
                } else {
                    showError("Registration failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                showError("Network error: " + t.getMessage());
            }
        });
    }

//    private void signInWithGoogle() {
//        Intent signInIntent = googleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleGoogleSignInResult(task);
//        }
//    }

//    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            // Berhasil sign in
//            String idToken = account.getIdToken();
//            String email = account.getEmail();
//            String name = account.getDisplayName();
//
//            // Kirim data ke server
//            handleGoogleAuthWithApi(account);
//        } catch (ApiException e) {
//            // Tampilkan error yang lebih spesifik
//            String errorMessage;
//            switch (e.getStatusCode()) {
//                case GoogleSignInStatusCodes.DEVELOPER_ERROR:
//                    errorMessage = "Developer configuration error. Check OAuth setup.";
//                    break;
//                case GoogleSignInStatusCodes.NETWORK_ERROR:
//                    errorMessage = "Network error. Check your connection.";
//                    break;
//                default:
//                    errorMessage = "Google Sign In failed: " + e.getStatusCode();
//            }
//            showError(errorMessage);
//            e.printStackTrace();
//        }
//    }

//    private void handleGoogleAuthWithApi(GoogleSignInAccount account) {
//        // Implement your API call for Google authentication here
//        // This might be a separate endpoint in your API
//    }

    private void startMainActivity() {
        Intent intent = new Intent(this, BtActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            Log.e("AuthError", message);
        });
    }

    private void showLoginBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.layout_login_sheet, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        TextInputLayout tilEmail = view.findViewById(R.id.tilEmail);
        TextInputLayout tilPassword = view.findViewById(R.id.tilPassword);
        TextInputEditText emailInput = (TextInputEditText) tilEmail.getEditText();
        TextInputEditText passwordInput = (TextInputEditText) tilPassword.getEditText();
        MaterialButton btnLogin = view.findViewById(R.id.btnLogin);

        // Find and configure biometric button
        MaterialButton btnBiometric = view.findViewById(R.id.btnBiometric);
        btnBiometric.setVisibility(biometricManager.isBiometricEnabled() ? View.VISIBLE : View.GONE);
        btnBiometric.setOnClickListener(v -> {
            // Cache user data sebelum autentikasi biometrik
            String lastEmail = sessionManager.getLastLoginEmail();
            String lastUsername = sessionManager.getLastLoginUsername();
            String lastToken = sessionManager.getLastLoginToken();

            if (lastEmail != null && lastUsername != null && lastToken != null) {
                biometricManager.setCachedUserData(lastEmail, lastUsername, lastToken);
                biometricManager.authenticate();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please login with email first", Toast.LENGTH_SHORT).show();
            }
            biometricManager.authenticate();
            dialog.dismiss();
        });

        btnLogin.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                showError("Please fill all fields");
                return;
            }

            handleLogin(email, password);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showRegisterBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.layout_register_sheet, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        TextInputLayout tilName = view.findViewById(R.id.tilName);
        TextInputLayout tilEmail = view.findViewById(R.id.tilEmail);
        TextInputLayout tilPassword = view.findViewById(R.id.tilPassword);
        TextInputLayout tilPhone = view.findViewById(R.id.tilPhone);

        TextInputEditText nameInput = (TextInputEditText) tilName.getEditText();
        TextInputEditText emailInput = (TextInputEditText) tilEmail.getEditText();
        TextInputEditText passwordInput = (TextInputEditText) tilPassword.getEditText();
        TextInputEditText phoneInput = (TextInputEditText) tilPhone.getEditText();
        MaterialButton btnRegister = view.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String phone = phoneInput.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                showError("Please fill all fields");
                return;
            }

            handleRegister(name, email, password, phone);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void handleGoogleSignIn() {
        // Implement Google Sign-In logic
    }
}