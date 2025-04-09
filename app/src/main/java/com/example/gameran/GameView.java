package com.example.gameran;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {
    private static final int GRID_SIZE = 20;
    private int cellSize;
    private int screenWidth;
    private int screenHeight;
    private ArrayList<Point> snake;
    private Point food;
    private String direction;
    private boolean isPlaying;
    private Paint snakePaint;
    private Paint foodPaint;
    private Paint backgroundPaint;
    private Paint gridPaint;
    private Paint scorePaint;
    private int score;
    private Handler handler;
    private static final long GAME_SPEED = 200;
    private float touchStartX, touchStartY;
    private Paint gameOverPaint;
    private Paint buttonPaint;
    private Paint buttonTextPaint;
    private boolean showGameOver;
    private int highScore;
    private float buttonLeft, buttonTop, buttonRight, buttonBottom;
    private Paint pauseButtonPaint;
    private float pauseButtonSize;
    private boolean isPaused;
    private RectF pauseButtonRect;

    public GameView(Context context) {
        super(context);
        init();
    }

    private void init() {
        snake = new ArrayList<>();
        direction = "RIGHT";
        isPlaying = true;
        score = 0;

        snakePaint = new Paint();
        snakePaint.setColor(Color.parseColor("#4CAF50"));
        snakePaint.setStyle(Paint.Style.FILL);

        foodPaint = new Paint();
        foodPaint.setColor(Color.parseColor("#F44336"));
        foodPaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#212121"));

        gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#424242"));
        gridPaint.setStrokeWidth(1);

        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(50);
        scorePaint.setTextAlign(Paint.Align.LEFT);

        handler = new Handler(Looper.getMainLooper());
        startGame();

        gameOverPaint = new Paint();
        gameOverPaint.setColor(Color.WHITE);
        gameOverPaint.setTextSize(80);
        gameOverPaint.setTextAlign(Paint.Align.CENTER);

        buttonPaint = new Paint();
        buttonPaint.setColor(Color.parseColor("#4CAF50"));
        buttonPaint.setStyle(Paint.Style.FILL);

        buttonTextPaint = new Paint();
        buttonTextPaint.setColor(Color.WHITE);
        buttonTextPaint.setTextSize(40);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);

        showGameOver = false;
        highScore = 0;

        pauseButtonPaint = new Paint();
        pauseButtonPaint.setColor(Color.WHITE);
        pauseButtonPaint.setStyle(Paint.Style.STROKE);
        pauseButtonPaint.setStrokeWidth(4);

        isPaused = false;
        pauseButtonRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        cellSize = Math.min(w, h) / GRID_SIZE;

        // Set pause button size and position
        pauseButtonSize = Math.min(w, h) / 12;
        pauseButtonRect.set(
                w - pauseButtonSize - 20,
                20,
                w - 20,
                pauseButtonSize + 20
        );

        // Initialize snake position
        snake.clear();
        snake.add(new Point(GRID_SIZE/2, GRID_SIZE/2));
        generateFood();
    }

    private void generateFood() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(GRID_SIZE);
            y = random.nextInt(GRID_SIZE);
        } while (isSnakeCell(x, y));
        food = new Point(x, y);
    }

    private boolean isSnakeCell(int x, int y) {
        for (Point p : snake) {
            if (p.x == x && p.y == y) return true;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background
        canvas.drawRect(0, 0, screenWidth, screenHeight, backgroundPaint);

        // Draw grid
        for (int i = 0; i <= GRID_SIZE; i++) {
            canvas.drawLine(i * cellSize, 0, i * cellSize, GRID_SIZE * cellSize, gridPaint);
            canvas.drawLine(0, i * cellSize, GRID_SIZE * cellSize, i * cellSize, gridPaint);
        }

        // Draw snake with gradient effect
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            float left = p.x * cellSize;
            float top = p.y * cellSize;

            // Gradient from head to tail
            int alpha = 255 - (i * 255 / snake.size());
            snakePaint.setAlpha(Math.max(alpha, 100));

            canvas.drawRect(left + 2, top + 2, left + cellSize - 2, top + cellSize - 2, snakePaint);
        }
        snakePaint.setAlpha(255);

        // Draw food with pulsing effect
        if (food != null) {
            float left = food.x * cellSize;
            float top = food.y * cellSize;
            float radius = cellSize/3 + (float)(Math.sin(System.currentTimeMillis() / 200.0) * 2);
            canvas.drawCircle(left + cellSize/2, top + cellSize/2, radius, foodPaint);
        }

        // Draw score
        canvas.drawText("Score: " + score, 30, screenHeight - 50, scorePaint);
        canvas.drawText("High Score: " + highScore, screenWidth - 200, screenHeight - 50, scorePaint);

        // Draw pause button
        canvas.drawRoundRect(pauseButtonRect, 10, 10, pauseButtonPaint);
        if (isPaused) {
            // Draw play triangle
            float centerX = pauseButtonRect.centerX();
            float centerY = pauseButtonRect.centerY();
            float size = pauseButtonSize * 0.4f;

            Paint trianglePaint = new Paint(pauseButtonPaint);
            trianglePaint.setStyle(Paint.Style.FILL);

            Path trianglePath = new Path();
            trianglePath.moveTo(centerX - size/3, centerY - size/2);
            trianglePath.lineTo(centerX - size/3, centerY + size/2);
            trianglePath.lineTo(centerX + size/2, centerY);
            trianglePath.close();

            canvas.drawPath(trianglePath, trianglePaint);
        } else {
            // Draw pause bars
            float barWidth = pauseButtonSize * 0.15f;
            float barHeight = pauseButtonSize * 0.4f;
            float centerX = pauseButtonRect.centerX();
            float centerY = pauseButtonRect.centerY();

            canvas.drawRect(
                    centerX - barWidth * 2,
                    centerY - barHeight/2,
                    centerX - barWidth,
                    centerY + barHeight/2,
                    pauseButtonPaint
            );
            canvas.drawRect(
                    centerX + barWidth,
                    centerY - barHeight/2,
                    centerX + barWidth * 2,
                    centerY + barHeight/2,
                    pauseButtonPaint
            );
        }

        // Draw game over screen
        if (showGameOver) {
            // Semi-transparent overlay
            Paint overlayPaint = new Paint();
            overlayPaint.setColor(Color.BLACK);
            overlayPaint.setAlpha(160);
            canvas.drawRect(0, 0, screenWidth, screenHeight, overlayPaint);

            // Game Over text
            canvas.drawText("Game Over!", screenWidth/2, screenHeight/3, gameOverPaint);
            canvas.drawText("Score: " + score, screenWidth/2, screenHeight/3 + 100, gameOverPaint);

            // Restart button
            buttonLeft = screenWidth/2 - 150;
            buttonTop = screenHeight/2 + 50;
            buttonRight = screenWidth/2 + 150;
            buttonBottom = screenHeight/2 + 150;

            canvas.drawRoundRect(buttonLeft, buttonTop, buttonRight, buttonBottom, 20, 20, buttonPaint);
            canvas.drawText("Tap to Restart", (buttonLeft + buttonRight)/2, (buttonTop + buttonBottom)/2 + 15, buttonTextPaint);
        }

        // Draw pause overlay
        if (isPaused && !showGameOver) {
            // Semi-transparent overlay
            Paint overlayPaint = new Paint();
            overlayPaint.setColor(Color.BLACK);
            overlayPaint.setAlpha(120);
            canvas.drawRect(0, 0, screenWidth, screenHeight, overlayPaint);

            // Paused text
            canvas.drawText("PAUSED", screenWidth/2, screenHeight/2, gameOverPaint);
            canvas.drawText("Tap the button to resume", screenWidth/2, screenHeight/2 + 100, buttonTextPaint);
        }
    }

    private void startGame() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPlaying) {
                    update();
                    invalidate();
                    handler.postDelayed(this, GAME_SPEED);
                }
            }
        }, GAME_SPEED);
    }

    private void update() {
        Point head = snake.get(0);
        Point newHead = new Point(head.x, head.y);

        switch (direction) {
            case "UP":
                newHead.y--;
                break;
            case "DOWN":
                newHead.y++;
                break;
            case "LEFT":
                newHead.x--;
                break;
            case "RIGHT":
                newHead.x++;
                break;
        }

        // Check collision with walls
        if (newHead.x < 0 || newHead.x >= GRID_SIZE ||
                newHead.y < 0 || newHead.y >= GRID_SIZE) {
            gameOver();
            return;
        }

        // Check collision with self
        if (isSnakeCell(newHead.x, newHead.y)) {
            gameOver();
            return;
        }

        snake.add(0, newHead);

        // Check if food is eaten
        if (newHead.x == food.x && newHead.y == food.y) {
            score += 10;
            generateFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void gameOver() {
        isPlaying = false;
        showGameOver = true;
        if (score > highScore) {
            highScore = score;
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (showGameOver) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                float x = event.getX();
                float y = event.getY();

                if (x >= buttonLeft && x <= buttonRight &&
                        y >= buttonTop && y <= buttonBottom) {
                    showGameOver = false;
                    init();
                    onSizeChanged(screenWidth, screenHeight, 0, 0);
                }
            }
            return true;
        }

        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Check if pause button was clicked
            if (pauseButtonRect.contains(x, y)) {
                isPaused = !isPaused;
                if (isPaused) {
                    pause();
                } else {
                    resume();
                }
                invalidate();
                return true;
            }

            if (!isPaused) {
                touchStartX = x;
                touchStartY = y;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP && !isPaused) {
            float dx = x - touchStartX;
            float dy = y - touchStartY;

            if (Math.abs(dx) > Math.abs(dy)) {
                // Horizontal swipe
                if (dx > 0 && !direction.equals("LEFT")) {
                    direction = "RIGHT";
                } else if (dx < 0 && !direction.equals("RIGHT")) {
                    direction = "LEFT";
                }
            } else {
                // Vertical swipe
                if (dy > 0 && !direction.equals("UP")) {
                    direction = "DOWN";
                } else if (dy < 0 && !direction.equals("DOWN")) {
                    direction = "UP";
                }
            }
        }
        return true;
    }

    public void pause() {
        isPlaying = false;
        isPaused = true;
        invalidate();
    }

    public void resume() {
        if (!showGameOver) {
            isPlaying = true;
            isPaused = false;
            startGame();
            invalidate();
        }
    }
} 