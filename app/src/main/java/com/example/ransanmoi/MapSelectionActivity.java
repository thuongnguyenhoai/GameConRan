package com.example.ransanmoi;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MapSelectionActivity extends AppCompatActivity {
    private int selectedMapId = -1; // -1 means no map selected

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_selection);

        // Khởi tạo nút Back
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Khởi tạo nút Save
        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMapId != -1) {
                    // TODO: Implement save functionality
                    Toast.makeText(MapSelectionActivity.this, "Đã lưu lựa chọn bản đồ!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MapSelectionActivity.this, "Vui lòng chọn một bản đồ!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Khởi tạo các map thumbnails
        initializeMapThumbnails();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void initializeMapThumbnails() {
        // Map 1 - Vũ Trụ
        findViewById(R.id.mapClassic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMap(v, 1);
            }
        });

        // Map 2 - Rừng Xanh
        findViewById(R.id.mapForest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMap(v, 2);
            }
        });

        // Map 3 - Sa Mạc
        findViewById(R.id.mapDesert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMap(v, 3);
            }
        });

        // Map 4 - Đại Dương
        findViewById(R.id.mapIce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMap(v, 4);
            }
        });

        // Map 5 - Núi Lửa
        findViewById(R.id.mapVolcano).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMap(v, 5);
            }
        });

        // Map 6 - Thành Phố
        findViewById(R.id.mapCity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMap(v, 6);
            }
        });
    }

    private void selectMap(View selectedView, int mapId) {
        // Reset all map backgrounds to default
        findViewById(R.id.mapClassic).setBackgroundResource(R.drawable.map_thumbnail_background);
        findViewById(R.id.mapForest).setBackgroundResource(R.drawable.map_thumbnail_background);
        findViewById(R.id.mapDesert).setBackgroundResource(R.drawable.map_thumbnail_background);
        findViewById(R.id.mapIce).setBackgroundResource(R.drawable.map_thumbnail_background);
        findViewById(R.id.mapVolcano).setBackgroundResource(R.drawable.map_thumbnail_background);
        findViewById(R.id.mapCity).setBackgroundResource(R.drawable.map_thumbnail_background);

        // Highlight selected map
        selectedView.setBackgroundResource(R.drawable.map_thumbnail_selected);
        selectedMapId = mapId;
    }
} 