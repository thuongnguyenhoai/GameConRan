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

    private static final int GRID_SIZE = 20; // Kích thước lưới game
    private float cellSize; // Kích thước mỗi ô
    private ArrayList<Point> snake; // Danh sách các điểm của rắn
    private Point food; // Vị trí mồi
    private int direction = 0; // 0: phải, 1: xuống, 2: trái, 3: lên
    private boolean isPlaying = false;
    private Paint snakePaint, foodPaint, gridPaint;
    private Random random;
    private GameOverCallback gameOverCallback;

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
        random = new Random();
        
        // Khởi tạo Paint cho rắn
        snakePaint = new Paint();
        snakePaint.setColor(Color.GREEN);
        snakePaint.setStyle(Paint.Style.FILL);

        // Khởi tạo Paint cho mồi
        foodPaint = new Paint();
        foodPaint.setColor(Color.RED);
        foodPaint.setStyle(Paint.Style.FILL);

        // Khởi tạo Paint cho lưới
        gridPaint = new Paint();
        gridPaint.setColor(Color.DKGRAY);
        gridPaint.setStyle(Paint.Style.STROKE);

        resetGame();
    }

    private void resetGame() {
        // Khởi tạo rắn ở giữa màn hình
        snake.clear();
        snake.add(new Point(GRID_SIZE/2, GRID_SIZE/2));
        snake.add(new Point(GRID_SIZE/2 - 1, GRID_SIZE/2));
        snake.add(new Point(GRID_SIZE/2 - 2, GRID_SIZE/2));
        
        // Tạo mồi ở vị trí ngẫu nhiên
        spawnFood();
        
        direction = 0;
        isPlaying = true;
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Vẽ lưới
        for (int i = 0; i <= GRID_SIZE; i++) {
            canvas.drawLine(i * cellSize, 0, i * cellSize, GRID_SIZE * cellSize, gridPaint);
            canvas.drawLine(0, i * cellSize, GRID_SIZE * cellSize, i * cellSize, gridPaint);
        }

        // Vẽ mồi
        canvas.drawCircle(
            (food.x + 0.5f) * cellSize,
            (food.y + 0.5f) * cellSize,
            cellSize * 0.4f,
            foodPaint
        );

        // Vẽ rắn
        for (Point p : snake) {
            canvas.drawRect(
                p.x * cellSize,
                p.y * cellSize,
                (p.x + 1) * cellSize,
                (p.y + 1) * cellSize,
                snakePaint
            );
        }
    }

    public void setDirection(int newDirection) {
        // Không cho phép đổi hướng ngược lại
        if (Math.abs(direction - newDirection) != 2) {
            direction = newDirection;
        }
    }

    public void update() {
        if (!isPlaying) return;

        // Lấy vị trí đầu rắn
        Point head = snake.get(0);
        Point newHead = new Point(head.x, head.y);

        // Di chuyển theo hướng hiện tại
        switch (direction) {
            case 0: // Phải
                newHead.x++;
                break;
            case 1: // Xuống
                newHead.y++;
                break;
            case 2: // Trái
                newHead.x--;
                break;
            case 3: // Lên
                newHead.y--;
                break;
        }

        // Kiểm tra va chạm với tường
        if (newHead.x < 0 || newHead.x >= GRID_SIZE || 
            newHead.y < 0 || newHead.y >= GRID_SIZE) {
            gameOver();
            return;
        }

        // Kiểm tra va chạm với thân rắn
        if (snake.contains(newHead)) {
            gameOver();
            return;
        }

        // Thêm đầu mới
        snake.add(0, newHead);

        // Kiểm tra ăn mồi
        if (newHead.x == food.x && newHead.y == food.y) {
            spawnFood();
        } else {
            // Nếu không ăn mồi, xóa đuôi
            snake.remove(snake.size() - 1);
        }

        invalidate();
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
} 