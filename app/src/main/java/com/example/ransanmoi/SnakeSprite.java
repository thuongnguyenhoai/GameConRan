package com.example.ransanmoi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

public class SnakeSprite {
    private Bitmap headImage;
    private Bitmap bodyImage;
    private Bitmap tailImage;
    private Bitmap[] rotatedHeads;
    private Bitmap[] rotatedBodies;
    private Bitmap[] rotatedTails;
    private Context context;
    private int partSize;
    private Paint paint;

    // Hằng số cho các hướng
    public static final int RIGHT = 0;  // Phải
    public static final int DOWN = 1;   // Xuống
    public static final int LEFT = 2;   // Trái
    public static final int UP = 3;     // Lên

    public SnakeSprite(Context context, int partSize) {
        this.context = context;
        this.partSize = partSize;
        
        // Cấu hình Paint để làm nổi bật rắn
        this.paint = new Paint();
        paint.setAntiAlias(true);        // Làm mịn các cạnh
        paint.setFilterBitmap(true);     // Làm mịn bitmap khi scale
        paint.setAlpha(255);             // Độ trong suốt tối đa

        // Tạo ColorMatrix để tăng độ sáng
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
            1.2f, 0, 0, 0, 20f,  // Red
            0, 1.2f, 0, 0, 20f,  // Green
            0, 0, 1.2f, 0, 20f,  // Blue
            0, 0, 0, 1, 0        // Alpha
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        
        initializeSprites();
    }

    private void initializeSprites() {
        // Load ảnh gốc
        headImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.dau_ran_rung);
        bodyImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.than_ran_rung);
        tailImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.duoi_ran_rung);

        // Scale ảnh với kích thước lớn hơn một chút
        int scaledSize = (int)(partSize * 1.2f); // Tăng kích thước lên để rắn to hơn
        headImage = Bitmap.createScaledBitmap(headImage, scaledSize, scaledSize, true);
        bodyImage = Bitmap.createScaledBitmap(bodyImage, scaledSize, scaledSize, true);
        tailImage = Bitmap.createScaledBitmap(tailImage, scaledSize, scaledSize, true);

        rotatedHeads = new Bitmap[4];
        rotatedBodies = new Bitmap[4];
        rotatedTails = new Bitmap[4];

        for (int i = 0; i < 4; i++) {
            rotatedHeads[i] = rotateImage(headImage, i * 90);
            rotatedBodies[i] = rotateImage(bodyImage, i * 90);
            rotatedTails[i] = rotateImage(tailImage, i * 90);
        }
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void drawHead(Canvas canvas, float x, float y, int direction) {
        float offset = partSize * 0.1f; // Tăng offset để rắn to hơn
        canvas.drawBitmap(rotatedHeads[direction], x - offset, y - offset, paint);
    }

    public void drawBody(Canvas canvas, float x, float y, int fromDir, int toDir) {
        float offset = partSize * 0.1f;
        
        if (fromDir != toDir) {
            // Vẽ phần thân chính ở khúc rẽ
            int rotationIndex = getCornerRotation(fromDir, toDir);
            canvas.drawBitmap(rotatedBodies[rotationIndex], x - offset, y - offset, paint);

            // Chỉ thêm một phần thân phụ ở vị trí phù hợp
            float extraX = x;
            float extraY = y;
            float shiftAmount = partSize * 0.25f; // Tăng khoảng cách dịch chuyển

            // Xác định vị trí phần thân phụ dựa vào hướng rẽ
            if ((fromDir == RIGHT && toDir == DOWN) || (fromDir == DOWN && toDir == RIGHT)) {
                extraX -= shiftAmount;
                extraY -= shiftAmount;
            } else if ((fromDir == RIGHT && toDir == UP) || (fromDir == UP && toDir == RIGHT)) {
                extraX -= shiftAmount;
                extraY += shiftAmount;
            } else if ((fromDir == LEFT && toDir == DOWN) || (fromDir == DOWN && toDir == LEFT)) {
                extraX += shiftAmount;
                extraY -= shiftAmount;
            } else if ((fromDir == LEFT && toDir == UP) || (fromDir == UP && toDir == LEFT)) {
                extraX += shiftAmount;
                extraY += shiftAmount;
            }

            // Vẽ phần thân phụ với hướng phù hợp
            int extraRotation = (fromDir == RIGHT || fromDir == LEFT) ? 0 : 1;
            canvas.drawBitmap(rotatedBodies[extraRotation], extraX - offset, extraY - offset, paint);
        } else {
            // Nếu đoạn thân thẳng
            canvas.drawBitmap(rotatedBodies[fromDir % 2], x - offset, y - offset, paint);
        }
    }

    public void drawTail(Canvas canvas, float x, float y, int direction) {
        float offset = partSize * 0.1f;
        canvas.drawBitmap(rotatedTails[direction], x - offset, y - offset, paint);
    }

    private int getCornerRotation(int fromDir, int toDir) {
        if (fromDir == RIGHT && toDir == DOWN) return 0;
        if (fromDir == DOWN && toDir == LEFT) return 1;
        if (fromDir == LEFT && toDir == UP) return 2;
        if (fromDir == UP && toDir == RIGHT) return 3;
        
        if (fromDir == DOWN && toDir == RIGHT) return 3;
        if (fromDir == LEFT && toDir == DOWN) return 0;
        if (fromDir == UP && toDir == LEFT) return 1;
        if (fromDir == RIGHT && toDir == UP) return 2;
        
        return fromDir % 2;
    }

    public void recycle() {
        headImage.recycle();
        bodyImage.recycle();
        tailImage.recycle();
        
        for (int i = 0; i < 4; i++) {
            rotatedHeads[i].recycle();
            rotatedBodies[i].recycle();
            rotatedTails[i].recycle();
        }
    }
} 