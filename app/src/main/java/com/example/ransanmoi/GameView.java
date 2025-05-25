package com.example.ransanmoi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {
    public interface GameOverCallback {
        void onGameOver(int finalScore);
    }

    private static final int GRID_SIZE = 16;
    private float cellSize;
    private ArrayList<Point> snake;
    private Point food;
    private int direction = 0; // 0: phải, 1: xuống, 2: trái, 3: lên
    private boolean isPlaying = false;
    private Paint snakePaint, foodPaint, borderPaint;
    private Random random;
    private GameOverCallback gameOverCallback;
    public SnakeSprite snakeSprite;

    // Thêm biến cho chuyển động nội suy
    private float interpolationProgress = 0f;
    private static final float MOVEMENT_SPEED = 4f; // Tốc độ di chuyển (ô/giây)
    private long lastUpdateTime;
    private ArrayList<Point> previousPositions;
    private ArrayList<Point> targetPositions;

    private boolean isPaused = false;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setGameOverCallback(GameOverCallback callback) {
        this.gameOverCallback = callback;
    }

    private void init() {
        snake = new ArrayList<>();
        previousPositions = new ArrayList<>();
        targetPositions = new ArrayList<>();
        random = new Random();
        
        // Khởi tạo Paint cho rắn
        snakePaint = new Paint();
        snakePaint.setColor(Color.GREEN);
        snakePaint.setStyle(Paint.Style.FILL);

        // Khởi tạo Paint cho mồi
        foodPaint = new Paint();
        foodPaint.setColor(Color.RED);
        foodPaint.setStyle(Paint.Style.FILL);

        // Khởi tạo Paint cho viền
        borderPaint = new Paint();
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4f);
        borderPaint.setAlpha(100);

        lastUpdateTime = System.currentTimeMillis();
        resetGame();
    }

    private void resetGame() {
        // Khởi tạo rắn ở giữa màn hình
        snake.clear();
        snake.add(new Point(GRID_SIZE/2, GRID_SIZE/2));
        snake.add(new Point(GRID_SIZE/2 - 1, GRID_SIZE/2));
        snake.add(new Point(GRID_SIZE/2 - 2, GRID_SIZE/2));
        
        // Khởi tạo vị trí nội suy
        previousPositions.clear();
        targetPositions.clear();
        for (Point p : snake) {
            previousPositions.add(new Point(p.x, p.y));
            targetPositions.add(new Point(p.x, p.y));
        }
        
        interpolationProgress = 0f;
        spawnFood();
        direction = 0;
        isPlaying = true;
        lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    private void gameOver() {
        isPlaying = false;
        if (gameOverCallback != null) {
            gameOverCallback.onGameOver(snake.size() - 3);
        }
    }

    private void spawnFood() {
        food = new Point(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
        // Đảm bảo mồi không xuất hiện trên thân rắn
        while (snake.contains(food)) {
            food = new Point(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellSize = (float) Math.min(w, h) / GRID_SIZE;
        // Khởi tạo snakeSprite sau khi có cellSize
        if (snakeSprite == null) {
            snakeSprite = new SnakeSprite(getContext(), (int)cellSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Vẽ viền cho khu vực chơi game
        canvas.drawRect(0, 0, GRID_SIZE * cellSize, GRID_SIZE * cellSize, borderPaint);

        // Vẽ mồi
        canvas.drawCircle(
            (food.x + 0.5f) * cellSize,
            (food.y + 0.5f) * cellSize,
            cellSize * 0.4f,
            foodPaint
        );

        // Chỉ cập nhật thời gian và nội suy khi không tạm dừng
        if (!isPaused) {
            // Cập nhật thời gian và nội suy
            long currentTime = System.currentTimeMillis();
            float deltaTime = (currentTime - lastUpdateTime) / 1000f;
            lastUpdateTime = currentTime;

            // Cập nhật tiến trình nội suy
            interpolationProgress += MOVEMENT_SPEED * deltaTime;
            if (interpolationProgress >= 1f) {
                // Hoàn thành một bước di chuyển
                interpolationProgress = 0f;
                updateSnakePosition();
            }
        }

        // Vẽ rắn với vị trí nội suy
        if (snakeSprite != null && snake.size() > 0) {
            // Vẽ đầu rắn
            float headX = lerp(previousPositions.get(0).x, targetPositions.get(0).x, interpolationProgress) * cellSize;
            float headY = lerp(previousPositions.get(0).y, targetPositions.get(0).y, interpolationProgress) * cellSize;
            snakeSprite.drawHead(canvas, headX, headY, direction);

            // Vẽ thân rắn
            for (int i = 1; i < snake.size() - 1; i++) {
                float currentX = lerp(previousPositions.get(i).x, targetPositions.get(i).x, interpolationProgress) * cellSize;
                float currentY = lerp(previousPositions.get(i).y, targetPositions.get(i).y, interpolationProgress) * cellSize;
                float prevX = lerp(previousPositions.get(i-1).x, targetPositions.get(i-1).x, interpolationProgress) * cellSize;
                float prevY = lerp(previousPositions.get(i-1).y, targetPositions.get(i-1).y, interpolationProgress) * cellSize;
                float nextX = lerp(previousPositions.get(i+1).x, targetPositions.get(i+1).x, interpolationProgress) * cellSize;
                float nextY = lerp(previousPositions.get(i+1).y, targetPositions.get(i+1).y, interpolationProgress) * cellSize;

                int fromDir = getDirectionFromCoords(currentX/cellSize, currentY/cellSize, prevX/cellSize, prevY/cellSize);
                int toDir = getDirectionFromCoords(nextX/cellSize, nextY/cellSize, currentX/cellSize, currentY/cellSize);
                
                snakeSprite.drawBody(canvas, currentX, currentY, fromDir, toDir);
            }

            // Vẽ đuôi rắn
            if (snake.size() > 1) {
                int lastIndex = snake.size() - 1;
                float tailX = lerp(previousPositions.get(lastIndex).x, targetPositions.get(lastIndex).x, interpolationProgress) * cellSize;
                float tailY = lerp(previousPositions.get(lastIndex).y, targetPositions.get(lastIndex).y, interpolationProgress) * cellSize;
                float beforeTailX = lerp(previousPositions.get(lastIndex-1).x, targetPositions.get(lastIndex-1).x, interpolationProgress) * cellSize;
                float beforeTailY = lerp(previousPositions.get(lastIndex-1).y, targetPositions.get(lastIndex-1).y, interpolationProgress) * cellSize;

                int tailDir = getDirectionFromCoords(tailX/cellSize, tailY/cellSize, beforeTailX/cellSize, beforeTailY/cellSize);
                snakeSprite.drawTail(canvas, tailX, tailY, tailDir);
            }
        }

        // Tiếp tục vẽ animation chỉ khi đang chơi và không tạm dừng
        if (isPlaying && !isPaused) {
            invalidate();
        }
    }

    private float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }

    private int getDirectionFromCoords(float fromX, float fromY, float toX, float toY) {
        if (Math.abs(toX - fromX) > Math.abs(toY - fromY)) {
            return toX > fromX ? SnakeSprite.RIGHT : SnakeSprite.LEFT;
        } else {
            return toY > fromY ? SnakeSprite.DOWN : SnakeSprite.UP;
        }
    }

    private void updateSnakePosition() {
        // Lưu vị trí hiện tại làm vị trí trước
        previousPositions.clear();
        for (Point p : snake) {
            previousPositions.add(new Point(p.x, p.y));
        }

        // Di chuyển rắn
        for(int i = snake.size()-1; i > 0; i--){
            snake.set(i, new Point(snake.get(i-1).x, snake.get(i-1).y));
        }

        // Cập nhật vị trí đầu rắn theo hướng
        Point head = snake.get(0);
        if(direction == 0){
            snake.set(0, new Point(head.x + 1, head.y));
        } else if(direction == 1){
            snake.set(0, new Point(head.x, head.y + 1));
        } else if(direction == 2){
            snake.set(0, new Point(head.x - 1, head.y));
        } else if(direction == 3){
            snake.set(0, new Point(head.x, head.y - 1));
        }

        // Cập nhật vị trí đích
        targetPositions.clear();
        for (Point p : snake) {
            targetPositions.add(new Point(p.x, p.y));
        }

        // Kiểm tra va chạm và xử lý game over
        if(snake.get(0).x < 0 || snake.get(0).x >= GRID_SIZE ||
           snake.get(0).y < 0 || snake.get(0).y >= GRID_SIZE){
            gameOver();
            return;
        }
        
        for(int i = 1; i < snake.size(); i++){
            if(snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y){
                gameOver();
                return;
            }
        }

        // Kiểm tra ăn mồi
        if (snake.get(0).x == food.x && snake.get(0).y == food.y) {
            Point last = snake.get(snake.size() - 1);
            snake.add(new Point(last.x, last.y));
            previousPositions.add(new Point(last.x, last.y));
            targetPositions.add(new Point(last.x, last.y));
            spawnFood();
        }
    }

    public void setDirection(int newDirection) {
        // Không cho phép đổi hướng ngược lại
        if (Math.abs(direction - newDirection) != 2) {
            direction = newDirection;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void restart() {
        resetGame();
    }

    public int getSnakeLength() {
        return snake.size();
    }

    public void update() {
        // Force a redraw of the view
        invalidate();
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
        invalidate(); // Yêu cầu vẽ lại để dừng animation
    }
}