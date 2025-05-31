package com.example.ransanmoi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SnakeSkinSelectionActivity extends AppCompatActivity {
    private String selectedSkin = "";
    private static final String PREF_NAME = "SnakePrefs";
    private static final String SELECTED_SKIN_KEY = "selected_skin";
    private CardView lastSelectedCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake_skin_selection);

        // Khởi tạo các view
        CardView poisonSnakeCard = findViewById(R.id.poisonSnakeCard);
        CardView fireSnakeCard = findViewById(R.id.fireSnakeCard);
        CardView iceSnakeCard = findViewById(R.id.iceSnakeCard);
        CardView goldenSnakeCard = findViewById(R.id.goldenSnakeCard);
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnSave = findViewById(R.id.btnSave);

        // Load skin đã lưu trước đó
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        selectedSkin = prefs.getString(SELECTED_SKIN_KEY, "");

        // Thiết lập sự kiện click cho các card
        View.OnClickListener cardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Bỏ chọn card trước đó
                if (lastSelectedCard != null) {
                    lastSelectedCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                }

                // Đánh dấu card được chọn
                CardView selectedCard = (CardView) view;
                selectedCard.setCardBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                lastSelectedCard = selectedCard;

                if (view.getId() == R.id.poisonSnakeCard) {
                    selectedSkin = "Rắn Độc Tố";
                } else if (view.getId() == R.id.fireSnakeCard) {
                    selectedSkin = "Rắn Lửa";
                } else if (view.getId() == R.id.iceSnakeCard) {
                    selectedSkin = "Rắn Băng";
                } else if (view.getId() == R.id.goldenSnakeCard) {
                    selectedSkin = "Rắn Lục";
                }
            }
        };

        // Gán sự kiện click cho các card
        poisonSnakeCard.setOnClickListener(cardClickListener);
        fireSnakeCard.setOnClickListener(cardClickListener);
        iceSnakeCard.setOnClickListener(cardClickListener);
        goldenSnakeCard.setOnClickListener(cardClickListener);

        // Xử lý nút Quay Về
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Xử lý nút Lưu
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSkin.isEmpty()) {
                    Toast.makeText(SnakeSkinSelectionActivity.this,
                            "Vui lòng chọn một ngoại hình rắn", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lưu lựa chọn vào SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                editor.putString(SELECTED_SKIN_KEY, selectedSkin);
                editor.apply();

                Toast.makeText(SnakeSkinSelectionActivity.this,
                        "Đã lưu ngoại hình " + selectedSkin, Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Hiển thị skin đã chọn trước đó (nếu có)
        if (!selectedSkin.isEmpty()) {
            CardView cardToSelect = null;
            switch (selectedSkin) {
                case "Rắn Độc Tố":
                    cardToSelect = poisonSnakeCard;
                    break;
                case "Rắn Lửa":
                    cardToSelect = fireSnakeCard;
                    break;
                case "Rắn Băng":
                    cardToSelect = iceSnakeCard;
                    break;
                case "Rắn Lục":
                    cardToSelect = goldenSnakeCard;
                    break;
            }
            if (cardToSelect != null) {
                cardToSelect.setCardBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                lastSelectedCard = cardToSelect;
            }
        }
    }
} 