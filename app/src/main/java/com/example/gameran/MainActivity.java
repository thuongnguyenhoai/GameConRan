package com.example.gameran;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton startButton = findViewById(R.id.startButton);
        MaterialButton highScoreButton = findViewById(R.id.highScoreButton);
        MaterialButton shareButton = findViewById(R.id.shareButton);
        TextView highScoreText = findViewById(R.id.highScoreText);

        // Display high score
        int highScore = getSharedPreferences("SnakeGame", MODE_PRIVATE)
            .getInt("highScore", 0);
        highScoreText.setText(String.valueOf(highScore));

        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });

        highScoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HighScoresActivity.class);
            startActivity(intent);
        });

        shareButton.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            shareIntent.putExtra(Intent.EXTRA_TEXT, 
                getString(R.string.share_message, highScore));
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update high score when returning to main menu
        TextView highScoreText = findViewById(R.id.highScoreText);
        int highScore = getSharedPreferences("SnakeGame", MODE_PRIVATE)
            .getInt("highScore", 0);
        highScoreText.setText(String.valueOf(highScore));
    }
}