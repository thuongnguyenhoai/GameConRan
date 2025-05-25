package com.example.ransanmoi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HighScore {
    private int score;
    private long timestamp;

    public HighScore(int score) {
        this.score = score;
        this.timestamp = System.currentTimeMillis();
    }

    public HighScore(int score, long timestamp) {
        this.score = score;
        this.timestamp = timestamp;
    }

    public int getScore() {
        return score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
} 