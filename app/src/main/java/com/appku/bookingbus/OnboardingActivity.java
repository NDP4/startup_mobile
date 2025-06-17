package com.appku.bookingbus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.material.button.MaterialButton;

public class OnboardingActivity extends AppCompatActivity {
    private void animateText(TextView textView, String text, long duration) {
        int length = text.length();
        long delay = duration / length;

        Handler handler = new Handler();
        for (int i = 0; i <= length; i++) {
            final int index = i;
            handler.postDelayed(() -> {
                textView.setText(text.substring(0, index));
            }, delay * i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        setContentView(R.layout.activity_onboarding);

        View imageIllustration = findViewById(R.id.imageIllustration);
        View ivTravel = findViewById(R.id.ivTravel);
        MaterialButton btnGetStarted = findViewById(R.id.btnGetStarted);
        TextView tvDescription = findViewById(R.id.tvDescription);

        // Initially hide the views
        imageIllustration.setTranslationY(-300f);
        imageIllustration.setAlpha(0f);
        ivTravel.setAlpha(0f); // Initially hide ivTravel
        btnGetStarted.setTranslationY(300f);
        btnGetStarted.setAlpha(0f);
        tvDescription.setText("");

        // Create animations for imageIllustration
        ObjectAnimator imageSlideDown = ObjectAnimator.ofFloat(imageIllustration, "translationY", -300f, 0f);
        ObjectAnimator imageAlpha = ObjectAnimator.ofFloat(imageIllustration, "alpha", 0f, 1f);

        // Create animation for ivTravel
        ObjectAnimator travelFadeIn = ObjectAnimator.ofFloat(ivTravel, "alpha", 0f, 1f);
        travelFadeIn.setDuration(1000);

        // Create animations for button
        ObjectAnimator buttonSlideUp = ObjectAnimator.ofFloat(btnGetStarted, "translationY", 300f, 0f);
        ObjectAnimator buttonAlpha = ObjectAnimator.ofFloat(btnGetStarted, "alpha", 0f, 1f);

        // Combine animations
        AnimatorSet imageAnimSet = new AnimatorSet();
        imageAnimSet.playTogether(imageSlideDown, imageAlpha);
        imageAnimSet.setDuration(1000);

        AnimatorSet buttonAnimSet = new AnimatorSet();
        buttonAnimSet.playTogether(buttonSlideUp, buttonAlpha);
        buttonAnimSet.setDuration(1000);

        // Play all animations
        AnimatorSet allAnimSet = new AnimatorSet();
        allAnimSet.playSequentially(imageAnimSet, travelFadeIn, buttonAnimSet);
        allAnimSet.start();

        // Start typewriter effect after image animation
        imageAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                String description = getString(R.string.onboarding_description);
                animateText(tvDescription, description, 2000);
            }
        });

        btnGetStarted.setOnClickListener(v -> {
            // Exit animations
            ObjectAnimator imageSlideUp = ObjectAnimator.ofFloat(imageIllustration, "translationY", 0f, -300f);
            ObjectAnimator imageFadeOut = ObjectAnimator.ofFloat(imageIllustration, "alpha", 1f, 0f);

            // Add fade out animation for ivTravel
            ObjectAnimator travelFadeOut = ObjectAnimator.ofFloat(ivTravel, "alpha", 1f, 0f);

            ObjectAnimator buttonSlideDown = ObjectAnimator.ofFloat(btnGetStarted, "translationY", 0f, 300f);
            ObjectAnimator buttonFadeOut = ObjectAnimator.ofFloat(btnGetStarted, "alpha", 1f, 0f);

            ObjectAnimator descriptionFadeOut = ObjectAnimator.ofFloat(tvDescription, "alpha", 1f, 0f);

            AnimatorSet exitAnimSet = new AnimatorSet();
            exitAnimSet.playTogether(
                    imageSlideUp,
                    imageFadeOut,
                    travelFadeOut,
                    buttonSlideDown,
                    buttonFadeOut,
                    descriptionFadeOut
            );
            exitAnimSet.setDuration(500);

            exitAnimSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    startActivity(new Intent(OnboardingActivity.this, AuthActivity.class));
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });

            exitAnimSet.start();
        });
    }
}