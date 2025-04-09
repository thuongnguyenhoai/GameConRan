package com.example.gameran;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HighScoresActivity extends AppCompatActivity {
    private ListView highScoresList;
    private List<HighScore> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        highScoresList = findViewById(R.id.highScoresList);
        Button backButton = findViewById(R.id.backButton);

        loadHighScores();
        
        HighScoreAdapter adapter = new HighScoreAdapter(this, scores);
        highScoresList.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());
    }

    private void loadHighScores() {
        scores = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("SnakeGame", MODE_PRIVATE);
        String scoresStr = prefs.getString("highScores", "");
        
        if (!scoresStr.isEmpty()) {
            String[] scoreEntries = scoresStr.split(",");
            for (String entry : scoreEntries) {
                String[] parts = entry.split("\\|");
                if (parts.length == 2) {
                    scores.add(new HighScore(
                        Integer.parseInt(parts[0]),
                        Long.parseLong(parts[1])
                    ));
                }
            }
        }
        
        Collections.sort(scores, (a, b) -> b.score - a.score);
    }

    private static class HighScore {
        int score;
        long date;

        HighScore(int score, long date) {
            this.score = score;
            this.date = date;
        }
    }

    private class HighScoreAdapter extends ArrayAdapter<HighScore> {
        private final SimpleDateFormat dateFormat;

        public HighScoreAdapter(Context context, List<HighScore> scores) {
            super(context, 0, scores);
            dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.high_score_item, parent, false);
            }

            HighScore score = getItem(position);
            if (score != null) {
                TextView rankText = convertView.findViewById(R.id.rankText);
                TextView dateText = convertView.findViewById(R.id.dateText);
                TextView scoreText = convertView.findViewById(R.id.scoreText);

                rankText.setText(getString(R.string.rank_format, position + 1));
                dateText.setText(dateFormat.format(new Date(score.date)));
                scoreText.setText(getString(R.string.score_format, score.score));
            }

            return convertView;
        }
    }
} 