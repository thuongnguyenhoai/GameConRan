package com.example.ransanmoi;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private GameView gameView;
    private TextView tvScore;
    private Button btnUp, btnDown, btnLeft, btnRight;
    private ImageButton btnBack, btnPause;
    private Handler handler;
    private int score = 0;
    private static final long GAME_UPDATE_DELAY = 200; // Tốc độ cập nhật game (ms)
    private boolean dialogShowing = false;
    private Dialog gameOverDialog;
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        try {
            // Khởi tạo các view
            gameView = findViewById(R.id.gameView);
            tvScore = findViewById(R.id.tvScore);
            btnUp = findViewById(R.id.btnUp);
            btnDown = findViewById(R.id.btnDown);
            btnLeft = findViewById(R.id.btnLeft);
            btnRight = findViewById(R.id.btnRight);
            btnBack = findViewById(R.id.btnBack);
            btnPause = findViewById(R.id.btnPause);

            // Thiết lập sự kiện click cho các nút
            btnUp.setOnClickListener(this);
            btnDown.setOnClickListener(this);
            btnLeft.setOnClickListener(this);
            btnRight.setOnClickListener(this);
            btnBack.setOnClickListener(this);
            btnPause.setOnClickListener(this);

            // Khởi tạo dialog
            initGameOverDialog();

            // Thiết lập callback cho game over
            gameView.setGameOverCallback(new GameView.GameOverCallback() {
                @Override
                public void onGameOver(final int finalScore) {
                    showGameOverDialog(finalScore);
                }
            });

            // Khởi tạo handler cho game loop
            handler = new Handler();
            startGameLoop();

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi tạo game: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void togglePause() {
        isPaused = !isPaused;
        btnPause.setImageResource(isPaused ? 
            android.R.drawable.ic_media_play : 
            android.R.drawable.ic_media_pause);
        
        if (isPaused) {
            handler.removeCallbacksAndMessages(null);
        } else {
            startGameLoop();
        }
    }

    private void initGameOverDialog() {
        gameOverDialog = new Dialog(this);
        gameOverDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        gameOverDialog.setContentView(R.layout.dialog_game_over);
        gameOverDialog.setCancelable(false);
        
        // Làm cho dialog full width và có background trong suốt
        Window window = gameOverDialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, 
                           WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void showGameOverDialog(final int finalScore) {
        if (dialogShowing) return;
        dialogShowing = true;

        // Lưu điểm số mới
        HighScoresActivity.saveNewScore(this, finalScore);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tvFinalScore = gameOverDialog.findViewById(R.id.tvFinalScore);
                Button btnPlayAgain = gameOverDialog.findViewById(R.id.btnPlayAgain);
                Button btnHome = gameOverDialog.findViewById(R.id.btnHome);

                tvFinalScore.setText("Điểm số: " + finalScore);

                btnPlayAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogShowing = false;
                        gameOverDialog.dismiss();
                        gameView.restart();
                        score = 0;
                        tvScore.setText("Điểm: 0");
                        isPaused = false;
                        btnPause.setImageResource(android.R.drawable.ic_media_pause);
                        startGameLoop();
                    }
                });

                btnHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogShowing = false;
                        gameOverDialog.dismiss();
                        finish();
                    }
                });

                gameOverDialog.show();
            }
        });
    }

    private void startGameLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gameView != null && gameView.isPlaying() && !isPaused) {
                    gameView.update();
                    updateScore();
                    handler.postDelayed(this, GAME_UPDATE_DELAY);
                }
            }
        }, GAME_UPDATE_DELAY);
    }

    private void updateScore() {
        if (gameView != null) {
            score = gameView.getSnakeLength() - 3;
            tvScore.setText("Điểm: " + score);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBack) {
            showGameOverDialog(score);
            return;
        }
        
        if (v.getId() == R.id.btnPause) {
            togglePause();
            return;
        }

        if (!gameView.isPlaying() && !dialogShowing) {
            gameView.restart();
            startGameLoop();
            return;
        }

        if (!isPaused) {
            if (v.getId() == R.id.btnUp) {
                gameView.setDirection(3);
            } else if (v.getId() == R.id.btnDown) {
                gameView.setDirection(1);
            } else if (v.getId() == R.id.btnLeft) {
                gameView.setDirection(2);
            } else if (v.getId() == R.id.btnRight) {
                gameView.setDirection(0);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null && gameView.isPlaying() && !dialogShowing && !isPaused) {
            startGameLoop();
        }
    }

    @Override
    public void onBackPressed() {
        if (!dialogShowing) {
            showGameOverDialog(score);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameView != null && gameView.snakeSprite != null) {
            gameView.snakeSprite.recycle();
        }
    }
} 