package com.example.ransanmoi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HighScoresActivity extends AppCompatActivity {
    private ListView listView;
    private List<HighScore> highScores;
    private HighScoreAdapter adapter;
    private static final String PREFS_NAME = "SnakeGamePrefs";
    private static final String HIGH_SCORES_KEY = "highScores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        // Khởi tạo các view
        listView = findViewById(R.id.listViewScores);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Xử lý sự kiện click nút trở về
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng activity hiện tại và trở về MainActivity
            }
        });

        loadHighScores();
        setupAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại điểm số mỗi khi activity được hiển thị
        loadHighScores();
        setupAdapter();
    }

    private void setupAdapter() {
        // Sắp xếp điểm số từ cao đến thấp
        Collections.sort(highScores, new Comparator<HighScore>() {
            @Override
            public int compare(HighScore score1, HighScore score2) {
                return Integer.compare(score2.getScore(), score1.getScore());
            }
        });

        // Giới hạn chỉ hiển thị 10 điểm cao nhất
        if (highScores.size() > 10) {
            highScores = new ArrayList<>(highScores.subList(0, 10));
        }

        // Nếu adapter đã tồn tại, cập nhật dữ liệu
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(highScores);
            adapter.notifyDataSetChanged();
        } else {
            // Tạo adapter mới nếu chưa có
            adapter = new HighScoreAdapter(this, highScores);
            listView.setAdapter(adapter);
        }
    }

    private void loadHighScores() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(HIGH_SCORES_KEY, null);
        
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<HighScore>>() {}.getType();
            highScores = gson.fromJson(json, type);
        }
        
        if (highScores == null) {
            highScores = new ArrayList<>();
        }
    }

    // Phương thức static để lưu điểm số mới
    public static void saveNewScore(Context context, int score) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(HIGH_SCORES_KEY, null);
        List<HighScore> scores;
        Gson gson = new Gson();

        if (json != null) {
            Type type = new TypeToken<ArrayList<HighScore>>() {}.getType();
            scores = gson.fromJson(json, type);
        } else {
            scores = new ArrayList<>();
        }

        // Thêm điểm số mới
        scores.add(new HighScore(score));

        // Sắp xếp và giới hạn số lượng điểm số được lưu
        Collections.sort(scores, new Comparator<HighScore>() {
            @Override
            public int compare(HighScore score1, HighScore score2) {
                return Integer.compare(score2.getScore(), score1.getScore());
            }
        });

        // Chỉ lưu tối đa 50 điểm số để tránh tốn bộ nhớ
        if (scores.size() > 50) {
            scores = new ArrayList<>(scores.subList(0, 50));
        }

        // Lưu lại danh sách điểm số
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(HIGH_SCORES_KEY, gson.toJson(scores));
        editor.apply();
    }
} 