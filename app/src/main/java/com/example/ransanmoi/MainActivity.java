package com.example.ransanmoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPlayGame, btnLeaderboard, btnSelectMap, btnSelectSkin, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Initialize buttons
        initializeViews();
        
        // Set click listeners
        btnPlayGame.setOnClickListener(this);
        btnLeaderboard.setOnClickListener(this);
        btnSelectMap.setOnClickListener(this);
        btnSelectSkin.setOnClickListener(this);
        btnExit.setOnClickListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        btnPlayGame = findViewById(R.id.btnPlayGame);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);
        btnSelectMap = findViewById(R.id.btnSelectMap);
        btnSelectSkin = findViewById(R.id.btnSelectSkin);
        btnExit = findViewById(R.id.btnExit);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnPlayGame) {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btnLeaderboard) {
            // Mở màn hình bảng xếp hạng
            Intent intent = new Intent(this, HighScoresActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btnSelectMap) {
            // TODO: Show map selection
            Toast.makeText(this, "Chọn bản đồ", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.btnSelectSkin) {
            // TODO: Show skin selection
            Toast.makeText(this, "Chọn ngoại hình", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.btnExit) {
            finish();
        }
    }
}