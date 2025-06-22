package com.appku.bookingbus.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.appku.bookingbus.R;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import java.util.List;

public class TabImageAdapter {
    private final Context context;
    private final List<String> imageUrls;
    private final TabLayout tabLayout;

    public TabImageAdapter(Context context, TabLayout tabLayout, List<String> imageUrls) {
        this.context = context;
        this.tabLayout = tabLayout;
        this.imageUrls = imageUrls;
        setupTabs();
    }

    private void setupTabs() {
        for (int i = 0; i < imageUrls.size(); i++) {
            View customView = LayoutInflater.from(context).inflate(R.layout.item_tab_image, null);
            ImageView imageView = customView.findViewById(R.id.ivTabImage);

            // Fix: Only load image if context is valid
            boolean canLoad = true;
            if (context instanceof Activity) {
                Activity act = (Activity) context;
                canLoad = !act.isFinishing() && !act.isDestroyed();
            }
            if (canLoad) {
                Glide.with(context.getApplicationContext())
                    .load(imageUrls.get(i))
                    .centerCrop()
                    .placeholder(R.drawable.bus_placeholder)
                    .error(R.drawable.bus_placeholder)
                    .override(60, 60)
                    .into(imageView);
            }

            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(customView);
                // Set initial opacity
                customView.setAlpha(i == 0 ? 1f : 0.6f);
            }
        }
    }
}