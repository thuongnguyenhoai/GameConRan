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
import android.content.SharedPreferences;

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
    private Paint snakeEyePaint;
    private Paint snakeScalePaint;
    private Paint snakeTonguePaint;
    private float animationOffset;
    private long lastUpdateTime;
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
    private RectF backButtonRect;
    private Paint backButtonPaint;
    private Context context;

    public GameView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        snake = new ArrayList<>();
        direction = "RIGHT";
        isPlaying = true;
        score = 0;
        animationOffset = 0;
        lastUpdateTime = System.currentTimeMillis();

        snakePaint = new Paint();
        snakePaint.setColor(Color.parseColor("#4CAF50"));
        snakePaint.setStyle(Paint.Style.FILL);
        snakePaint.setAntiAlias(true);

        snakeEyePaint = new Paint();
        snakeEyePaint.setColor(Color.BLACK);
        snakeEyePaint.setStyle(Paint.Style.FILL);
        snakeEyePaint.setAntiAlias(true);

        snakeScalePaint = new Paint();
        snakeScalePaint.setColor(Color.parseColor("#388E3C"));
        snakeScalePaint.setStyle(Paint.Style.STROKE);
        snakeScalePaint.setStrokeWidth(2);
        snakeScalePaint.setAntiAlias(true);

        snakeTonguePaint = new Paint();
        snakeTonguePaint.setColor(Color.parseColor("#FF1744"));
        snakeTonguePaint.setStyle(Paint.Style.STROKE);
        snakeTonguePaint.setStrokeWidth(3);
        snakeTonguePaint.setAntiAlias(true);

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
        backButtonRect = new RectF();

        backButtonPaint = new Paint();
        backButtonPaint.setColor(Color.WHITE);
        backButtonPaint.setStyle(Paint.Style.STROKE);
        backButtonPaint.setStrokeWidth(4);
        backButtonPaint.setAntiAlias(true);
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

        // Set back button size and position (mirror of pause button)
        backButtonRect.set(
                20,
                20,
                pauseButtonSize + 20,
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

        // Update animation
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000f;
        animationOffset += deltaTime * 2;
        if (animationOffset > 1) animationOffset -= 1;
        lastUpdateTime = currentTime;

        // Draw snake body with smooth curves
        if (snake.size() > 0) {
            Path snakePath = new Path();
            Point first = snake.get(0);
            float startX = first.x * cellSize + cellSize/2;
            float startY = first.y * cellSize + cellSize/2;
            snakePath.moveTo(startX, startY);

            for (int i = 1; i < snake.size(); i++) {
                Point current = snake.get(i);
                float endX = current.x * cellSize + cellSize/2;
                float endY = current.y * cellSize + cellSize/2;
                
                // Calculate control points for smooth curve
                float controlX = (startX + endX) / 2;
                float controlY = (startY + endY) / 2;
                
                snakePath.quadTo(controlX, controlY, endX, endY);
                
                startX = endX;
                startY = endY;
            }

            // Draw main body
            Paint bodyPaint = new Paint(snakePaint);
            bodyPaint.setStrokeWidth(cellSize * 0.8f);
            bodyPaint.setStyle(Paint.Style.STROKE);
            bodyPaint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawPath(snakePath, bodyPaint);

            // Draw scales pattern
            for (int i = 0; i < snake.size(); i++) {
                Point p = snake.get(i);
                float centerX = p.x * cellSize + cellSize/2;
                float centerY = p.y * cellSize + cellSize/2;
                float scaleSize = cellSize * 0.3f;
                
                // Draw diamond pattern scales with animation
                float offset = (animationOffset + i * 0.1f) % 1;
                for (int j = 0; j < 4; j++) {
                    float angle = (float) (j * Math.PI/2 + offset * Math.PI);
                    float scaleX = centerX + (float)Math.cos(angle) * scaleSize;
                    float scaleY = centerY + (float)Math.sin(angle) * scaleSize;
                    canvas.drawCircle(scaleX, scaleY, 2, snakeScalePaint);
                }
            }

            // Draw snake head
            if (snake.size() > 0) {
                Point head = snake.get(0);
                float headX = head.x * cellSize + cellSize/2;
                float headY = head.y * cellSize + cellSize/2;
                
                // Draw head shape
                canvas.drawCircle(headX, headY, cellSize * 0.4f, snakePaint);
                
                // Draw eyes
                float eyeOffset = cellSize * 0.2f;
                float eyeSize = cellSize * 0.1f;
                
                // Determine eye positions based on direction
                float leftEyeX = headX, leftEyeY = headY;
                float rightEyeX = headX, rightEyeY = headY;
                
                switch (direction) {
                    case "RIGHT":
                        leftEyeX = headX + eyeOffset;
                        rightEyeX = headX + eyeOffset;
                        leftEyeY = headY - eyeOffset;
                        rightEyeY = headY + eyeOffset;
                        break;
                    case "LEFT":
                        leftEyeX = headX - eyeOffset;
                        rightEyeX = headX - eyeOffset;
                        leftEyeY = headY - eyeOffset;
                        rightEyeY = headY + eyeOffset;
                        break;
                    case "UP":
                        leftEyeX = headX - eyeOffset;
                        rightEyeX = headX + eyeOffset;
                        leftEyeY = headY - eyeOffset;
                        rightEyeY = headY - eyeOffset;
                        break;
                    case "DOWN":
                        leftEyeX = headX - eyeOffset;
                        rightEyeX = headX + eyeOffset;
                        leftEyeY = headY + eyeOffset;
                        rightEyeY = headY + eyeOffset;
                        break;
                }
                
                canvas.drawCircle(leftEyeX, leftEyeY, eyeSize, snakeEyePaint);
                canvas.drawCircle(rightEyeX, rightEyeY, eyeSize, snakeEyePaint);
                
                // Draw tongue with animation
                float tongueLength = cellSize * 0.4f;
                float tongueOffset = (float) (Math.sin(animationOffset * Math.PI * 2) * cellSize * 0.1f);
                
                Path tonguePath = new Path();
                float tongueStartX = headX;
                float tongueStartY = headY;
                float tongueEndX = headX;
                float tongueEndY = headY;
                
                switch (direction) {
                    case "RIGHT":
                        tongueStartX = headX + cellSize * 0.4f;
                        tongueEndX = tongueStartX + tongueLength + tongueOffset;
                        break;
                    case "LEFT":
                        tongueStartX = headX - cellSize * 0.4f;
                        tongueEndX = tongueStartX - tongueLength - tongueOffset;
                        break;
                    case "UP":
                        tongueStartY = headY - cellSize * 0.4f;
                        tongueEndY = tongueStartY - tongueLength - tongueOffset;
                        break;
                    case "DOWN":
                        tongueStartY = headY + cellSize * 0.4f;
                        tongueEndY = tongueStartY + tongueLength + tongueOffset;
                        break;
                }
                
                tonguePath.moveTo(tongueStartX, tongueStartY);
                tonguePath.lineTo(tongueEndX, tongueEndY);
                
                // Fork the tongue
                float forkSize = cellSize * 0.2f;
                switch (direction) {
                    case "RIGHT":
                        tonguePath.moveTo(tongueEndX - forkSize, tongueEndY - forkSize);
                        tonguePath.lineTo(tongueEndX, tongueEndY);
                        tonguePath.lineTo(tongueEndX - forkSize, tongueEndY + forkSize);
                        break;
                    case "LEFT":
                        tonguePath.moveTo(tongueEndX + forkSize, tongueEndY - forkSize);
                        tonguePath.lineTo(tongueEndX, tongueEndY);
                        tonguePath.lineTo(tongueEndX + forkSize, tongueEndY + forkSize);
                        break;
                    case "UP":
                        tonguePath.moveTo(tongueEndX - forkSize, tongueEndY + forkSize);
                        tonguePath.lineTo(tongueEndX, tongueEndY);
                        tonguePath.lineTo(tongueEndX + forkSize, tongueEndY + forkSize);
                        break;
                    case "DOWN":
                        tonguePath.moveTo(tongueEndX - forkSize, tongueEndY - forkSize);
                        tonguePath.lineTo(tongueEndX, tongueEndY);
                        tonguePath.lineTo(tongueEndX + forkSize, tongueEndY - forkSize);
                        break;
                }
                
                canvas.drawPath(tonguePath, snakeTonguePaint);
            }
        }

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

        // Draw back button
        canvas.drawRoundRect(backButtonRect, 10, 10, backButtonPaint);
        
        // Draw back arrow
        float centerX = backButtonRect.centerX();
        float centerY = backButtonRect.centerY();
        float arrowSize = pauseButtonSize * 0.4f;
        
        Path arrowPath = new Path();
        // Arrow head
        arrowPath.moveTo(centerX + arrowSize/2, centerY - arrowSize/2);
        arrowPath.lineTo(centerX - arrowSize/2, centerY);
        arrowPath.lineTo(centerX + arrowSize/2, centerY + arrowSize/2);
        // Arrow body
        arrowPath.moveTo(centerX - arrowSize/2, centerY);
        arrowPath.lineTo(centerX + arrowSize/2, centerY);
        
        Paint arrowPaint = new Paint(backButtonPaint);
        arrowPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(arrowPath, arrowPaint);

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

    private void saveScore() {
        SharedPreferences prefs = context.getSharedPreferences("SnakeGame", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        // Save current score if it's a high score
        int currentHighScore = prefs.getInt("highScore", 0);
        if (score > currentHighScore) {
            editor.putInt("highScore", score);
        }
        
        // Save score to history
        String scoresStr = prefs.getString("highScores", "");
        String newScore = score + "|" + System.currentTimeMillis();
        if (scoresStr.isEmpty()) {
            scoresStr = newScore;
        } else {
            scoresStr = scoresStr + "," + newScore;
        }
        editor.putString("highScores", scoresStr);
        editor.apply();
    }

    private void gameOver() {
        isPlaying = false;
        showGameOver = true;
        if (score > highScore) {
            highScore = score;
        }
        saveScore();
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
            // Check if back button was clicked
            if (backButtonRect.contains(x, y)) {
                // Return to main menu
                ((android.app.Activity) context).finish();
                return true;
            }
            
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