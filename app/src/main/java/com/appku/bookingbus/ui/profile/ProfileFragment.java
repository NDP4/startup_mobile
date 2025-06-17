package com.appku.bookingbus.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.appku.bookingbus.AuthActivity;
import com.appku.bookingbus.R;
import com.appku.bookingbus.databinding.FragmentProfileBinding;
import com.appku.bookingbus.utils.BiometricManager;
import com.appku.bookingbus.utils.SessionManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private FragmentProfileBinding binding;
    private BiometricManager biometricManager;
    private SwitchMaterial switchBiometric;
    private SessionManager sessionManager;
    private TextView tvUserName;
    private TextView tvUserEmail;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi views
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        switchBiometric = view.findViewById(R.id.switchBiometric);

        // Inisialisasi session manager dan biometric manager
        if (getActivity() != null) {
            sessionManager = new SessionManager(getActivity());
            biometricManager = new BiometricManager(getActivity(), new BiometricManager.BiometricAuthListener() {
                @Override
                public void onBiometricAuthenticationSuccess() {
                    // Pastikan data session dipertahankan setelah autentikasi biometrik
                    String lastEmail = sessionManager.getLastLoginEmail();
                    String lastUsername = sessionManager.getLastLoginUsername();
                    String lastToken = sessionManager.getLastLoginToken();
                    if (lastEmail != null && lastUsername != null && lastToken != null) {
                        sessionManager.saveAuthToken(lastToken, lastUsername, lastEmail);
                        updateUI();
                    }
                }

                @Override
                public void onBiometricAuthenticationError(int errorCode, String error) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Setup tombol logout
        binding.btnLogout.setOnClickListener(v -> {
            if (mViewModel != null) {
                mViewModel.logout();
                startAuthActivity();
            }
        });

        // Setup switch biometric
        switchBiometric.setChecked(sessionManager != null && sessionManager.isBiometricEnabled());
        switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (sessionManager != null) {
                sessionManager.setBiometricEnabled(isChecked);
            }
        });

        // Periksa status login
        if (sessionManager == null || !sessionManager.isLoggedIn()) {
            startAuthActivity();
            return;
        }

        // Update UI dengan data user
        updateUI();
    }

    private void updateUI() {
        if (mViewModel != null) {
            mViewModel.getUserName().observe(getViewLifecycleOwner(), name -> {
                if (name != null) {
                    tvUserName.setText(name);
                }
            });

            mViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
                if (email != null) {
                    tvUserEmail.setText(email);
                }
            });
        }
    }

    private void startAuthActivity() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void checkBiometricAvailability() {
        BiometricPrompt.PromptInfo.Builder builder = new BiometricPrompt.PromptInfo.Builder();
        builder.setTitle("Checking biometric availability");

        androidx.biometric.BiometricManager biometricManager =
                androidx.biometric.BiometricManager.from(requireContext());

        int canAuthenticate = biometricManager.canAuthenticate(
                androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG);

        if (canAuthenticate == androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS) {
            switchBiometric.setEnabled(true);
        } else {
            switchBiometric.setEnabled(false);
            switchBiometric.setChecked(false);
            Toast.makeText(requireContext(),
                    "Biometric authentication not available",
                    Toast.LENGTH_SHORT).show();
        }
    }

//    private void startAuthActivity() {
//        Intent intent = new Intent(requireActivity(), AuthActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        requireActivity().finish();
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}