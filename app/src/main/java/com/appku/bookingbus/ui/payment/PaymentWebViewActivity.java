package com.appku.bookingbus.ui.payment;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.CookieManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.appku.bookingbus.databinding.ActivityPaymentWebviewBinding;

public class PaymentWebViewActivity extends AppCompatActivity {
    private ActivityPaymentWebviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String paymentUrl = getIntent().getStringExtra("payment_url");
        Log.d("PaymentWebView", "payment_url: " + paymentUrl);

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Enable cookies
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webView, true);

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("payment_status=success")) {
                    // Payment success
                    setResult(RESULT_OK);
                    finish();
                    return true;
                }
                return false;
            }
        });

        if (paymentUrl != null && !paymentUrl.trim().isEmpty()) {
            binding.webView.loadUrl(paymentUrl);
        } else {
            Toast.makeText(this, "URL pembayaran tidak valid!", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
