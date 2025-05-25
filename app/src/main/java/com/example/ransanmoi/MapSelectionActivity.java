package com.example.ransanmoi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class MapSelectionActivity extends AppCompatActivity {
    private View selectedMap = null;
    private static final String PREFS_NAME = "GameSettings";
    private static final String SELECTED_MAP_KEY = "selectedMap";
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_selection);

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize views
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnSave = findViewById(R.id.btnSave);
        
        // Setup click listeners for maps
        setupMapClickListeners();

        // Load previously selected map
        String savedMap = settings.getString(SELECTED_MAP_KEY, "map_vu_tru");
        highlightSavedMap(savedMap);

        btnBack.setOnClickListener(v -> finish());
        
        btnSave.setOnClickListener(v -> {
            if (selectedMap != null) {
                String mapName = getSelectedMapName();
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(SELECTED_MAP_KEY, mapName);
                editor.apply();
                
                Toast.makeText(this, "Đã lưu lựa chọn bản đồ", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Vui lòng chọn một bản đồ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMapClickListeners() {
        int[] mapIds = {
            R.id.mapClassic, R.id.mapForest, R.id.mapDesert,
            R.id.mapIce, R.id.mapVolcano, R.id.mapCity
        };

        for (int id : mapIds) {
            View mapView = findViewById(id);
            mapView.setOnClickListener(v -> selectMap(v));
        }
    }

    private void selectMap(View view) {
        // Remove highlight from previously selected map
        if (selectedMap != null) {
            selectedMap.setBackground(getDrawable(R.drawable.map_thumbnail_background));
        }

        // Highlight new selection
        selectedMap = view;
        selectedMap.setBackground(getDrawable(R.drawable.map_thumbnail_selected_background));

        // Optional: Add selection animation
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.map_selection_animation);
        selectedMap.startAnimation(scaleAnimation);
    }

    private String getSelectedMapName() {
        if (selectedMap == null) return "map_vu_tru";
        
        int id = selectedMap.getId();
        if (id == R.id.mapClassic) {
            return "map_vu_tru";
        } else if (id == R.id.mapForest) {
            return "map_rung_xanh";
        } else if (id == R.id.mapDesert) {
            return "map_sa_mac";
        } else if (id == R.id.mapIce) {
            return "map_dai_duong";
        } else if (id == R.id.mapVolcano) {
            return "map_hang_pha_le";
        } else if (id == R.id.mapCity) {
            return "map_bang_tuyet";
        }
        return "map_vu_tru";
    }

    private void highlightSavedMap(String savedMap) {
        int mapViewId;
        if ("map_rung_xanh".equals(savedMap)) {
            mapViewId = R.id.mapForest;
        } else if ("map_sa_mac".equals(savedMap)) {
            mapViewId = R.id.mapDesert;
        } else if ("map_dai_duong".equals(savedMap)) {
            mapViewId = R.id.mapIce;
        } else if ("map_hang_pha_le".equals(savedMap)) {
            mapViewId = R.id.mapVolcano;
        } else if ("map_bang_tuyet".equals(savedMap)) {
            mapViewId = R.id.mapCity;
        } else {
            mapViewId = R.id.mapClassic;
        }
        
        View mapView = findViewById(mapViewId);
        if (mapView != null) {
            selectMap(mapView);
        }
    }
} 