package com.im4j.picturebeautify.editimage.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

import com.im4j.picturebeautify.R;
import com.im4j.picturebeautify.editimage.utils.DimensionUtils;
import com.im4j.picturebeautify.editimage.utils.MatrixUtils;
import com.im4j.picturebeautify.editimage.utils.PointUtils;

/**
 * @author alafighting 2016-02
 */
public class TextItem {

    public static class Size {
        private int mWidth;
        private int mHeight;

        public Size(int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        public int getWidth() {
            return mWidth;
        }

        public int getHeight() {
            return mHeight;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            if (obj instanceof Size) {
                Size other = (Size) obj;
                return mWidth == other.mWidth && mHeight == other.mHeight;
            }
            return false;
        }

        @Override
        public String toString() {
            return mWidth + "x" + mHeight;
        }
    }

    /**
     * 目标绘制矩形
     */
    public static class TargetRect {
        final int originalWidth;  // 原始宽
        final int originalHeight; // 原始高
        final float originalCenterX;  // 原始宽
        final float originalCenterY; // 原始高
        /*
         * 平移
         */
        float centerX;  // 中心点坐标X
        float centerY;  // 中心点坐标Y
        /*
         * 旋转
         */
        int rotate = 0; // 旋转角度
        /*
         * 缩放
         */
        float scale = 1; // 缩放比例
        boolean reversal = false; // 反转

        TargetRect(float originalCenterX, float originalCenterY, int originalWidth, int originalHeight) {
            this.centerX = this.originalCenterX = originalCenterX;
            this.centerY = this.originalCenterY = originalCenterY;
            this.originalWidth = originalWidth;
            this.originalHeight = originalHeight;
        }

        TargetRect(TargetRect source) {
            super();
            this.originalCenterX = source.originalCenterX;
            this.originalCenterY = source.originalCenterY;
            this.originalWidth = source.originalWidth;
            this.originalHeight = source.originalHeight;
            this.centerX = source.centerX;
            this.centerY = source.centerY;
            this.rotate = source.rotate;
            this.scale = source.scale;
            this.reversal = source.reversal;
        }

        public TargetRect clone() {
            return new TargetRect(this);
        }

        /**
         * 整体移动到指定位置
         *
         * @param x
         * @param y
         */
        public void moveTo(float x, float y) {
            this.centerX = x;
            this.centerY = y;
        }

        public void moveBy(float x, float y) {
            this.centerX += x;
            this.centerY += y;
        }

        public float centerX() {
            return centerX;
        }

        public float centerY() {
            return centerY;
        }

        public int originalWidth() {
            return originalWidth;
        }

        public int originalHeight() {
            return originalHeight;
        }

        public float width() {
            return originalWidth() * scale;
        }

        public float height() {
            return originalHeight() * scale;
        }

        public float left() {
            return centerX - width() / 2;
        }

        public float top() {
            return centerY - height() / 2;
        }

        public float right() {
            return centerX + width() / 2;
        }

        public float bottom() {
            return centerY + height() / 2;
        }

        /**
         * 判断点是否在区域范围内
         *
         * @param x
         * @param y
         * @return
         */
        public boolean contains(float x, float y) {
            return x >= left() && x < right() && y >= top() && y < bottom();
        }

        @Override
        public String toString() {
            return "{" + "centerX=" + centerX + ", centerY=" + centerY +
                    ", rotate=" + rotate + ", scale=" + scale + '}';
        }
    }


    private static final float MIN_SCALE = 0.2f;

    private final int BUTTON_WIDTH;
    private final int DEFAULT_TEXT_SIZE;


    /*
     * 源数据
     */
    private String text;  // 绘制文本
    public Paint textPaint;  // 画笔
    public TargetRect targetRect;  // 目标位置

    private Size iconSize;

    boolean isDrawHelpTool = false;
    private Paint helpBoxPaint = new Paint();

    private static Bitmap deleteBit;
    private static Bitmap rotateBit;

    public TextItem(Context context) {
        BUTTON_WIDTH = DimensionUtils.dip2px(context, 12);
        DEFAULT_TEXT_SIZE = DimensionUtils.dip2px(context, 32);

        helpBoxPaint.setColor(Color.BLACK);
        helpBoxPaint.setStyle(Style.STROKE);
        helpBoxPaint.setAntiAlias(true);
        helpBoxPaint.setStrokeWidth(4);

        textPaint = new Paint();
        textPaint.setTextSize(DEFAULT_TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setAlpha(120);

        // 导入工具按钮位图
        if (deleteBit == null) {
            deleteBit = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.sticker_delete);
        }// end if
        if (rotateBit == null) {
            rotateBit = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.sticker_rotate);
        }// end if

        iconSize = new Size(deleteBit.getWidth(), deleteBit.getHeight());
    }


    public void init(String text, Typeface typeface, View parentView) {
        this.text = text;
        this.textPaint.setTypeface(typeface);
        Rect srcRect = new Rect();
        this.textPaint.getTextBounds(text, 0, text.length(), srcRect);

        int halfWidth = parentView.getWidth() >> 1;
        int halfHeight = parentView.getHeight() >> 1;
        int bitWidth = Math.min(srcRect.width(), halfWidth);
        int bitHeight = bitWidth * srcRect.height() / srcRect.width();
        this.targetRect = new TargetRect(halfWidth, halfHeight, bitWidth, bitHeight);

        // TODO
        this.isDrawHelpTool = true;
    }

    /**
     * 位置更新
     *
     * @param dx
     * @param dy
     */
    public void updatePos(final float dx, final float dy) {
        this.targetRect.moveBy(dx, dy);
    }

    /**
     * 旋转 缩放 更新
     *
     * @param dx
     * @param dy
     */
    public void updateRotateAndScale(final float dx, final float dy) {
        float centerX = targetRect.centerX();
        float centerY = targetRect.centerY();

        Point rbPoint = getRightBottomPoint();
        float right = rbPoint.x;
        float bottom = rbPoint.y;

        float n_x = right + dx;
        float n_y = bottom + dy;

        float xa = right - centerX;
        float ya = bottom - centerY;

        float xb = n_x - centerX;
        float yb = n_y - centerY;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        float scale = curLen / srcLen;// 计算缩放比

        float newCcale = targetRect.scale * scale;
        if (newCcale < MIN_SCALE) {// 最小缩放值检测
            return;
        }

        // 缩放
        targetRect.scale = targetRect.scale * scale;

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1)
            return;
        float angle = (float) Math.toDegrees(Math.acos(cos));

        // 拉普拉斯定理
        float calMatrix = xa * yb - xb * ya;// 行列式计算 确定转动方向

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        // 旋转
        this.targetRect.rotate += angle;
    }

    public void draw(Canvas canvas) {
        // 绘制文本
        drawText(canvas, targetRect);

        if (this.isDrawHelpTool) {// 绘制辅助工具线
            canvas.save();
            canvas.rotate(targetRect.rotate, targetRect.centerX(), targetRect.centerY());
            canvas.drawRoundRect(generateTipsRect(), 10, 10, helpBoxPaint);
            // 绘制工具按钮
            canvas.drawBitmap(deleteBit, new Rect(0, 0, iconSize.getWidth(), iconSize.getHeight()), generateDeleteRect(), null);
            canvas.drawBitmap(rotateBit, new Rect(0, 0, iconSize.getWidth(), iconSize.getHeight()), generateRotateRect(), null);
            canvas.restore();
        }
    }

    private void drawText(Canvas canvas, TargetRect targetRect) {
        textPaint.setTextSize(DEFAULT_TEXT_SIZE * targetRect.scale);

        // 获取参数
        float rotate = targetRect.rotate;
        float centerX = targetRect.centerX();
        float centerY = targetRect.centerY();

        // 绘制文本
        canvas.save();
        canvas.rotate(rotate, centerX, centerY);

        canvas.drawText(text, centerX, centerY + targetRect.height() / 2, textPaint);

        canvas.restore();
    }

    public void drawText(Canvas canvas, Matrix matrix) {
        float scale = MatrixUtils.getValue(matrix, Matrix.MSCALE_X);
        float moveX = MatrixUtils.getValue(matrix, Matrix.MTRANS_X);
        float moveY = MatrixUtils.getValue(matrix, Matrix.MTRANS_Y);

        // 获取参数
        TargetRect targetRect = this.targetRect.clone();

        targetRect.centerX = (targetRect.centerX() - moveX) / scale;
        targetRect.centerY = (targetRect.centerY() - moveY) / scale;
        targetRect.scale = targetRect.scale / scale;

        drawText(canvas, targetRect);
    }

    public Point getRightBottomPoint() {
        float centerX = targetRect.centerX();
        float centerY = targetRect.centerY();

        Rect rect = generateTextRect();
        float right = centerX + rect.width()/2;
        float bottom = centerY + rect.height()/2;

        // 旋转坐标，得到真实坐标
        Point point = new Point((int)right, (int)bottom);
        point = PointUtils.rotatePoint(point, new Point((int)centerX, (int)centerY), targetRect.rotate);
        return point;
    }

    public Rect generateTextRect() {
        textPaint.setTextSize(DEFAULT_TEXT_SIZE * targetRect.scale);

        Rect srcRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), srcRect);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        int offset = (int) Math.abs(fontMetrics.leading * 0.3);
        srcRect.set(srcRect.left - offset, srcRect.top - offset, srcRect.right + offset, srcRect.bottom + offset);
        return srcRect;
    }

    /**
     * 辅助提示框
     *
     * @return
     */
    public RectF generateTipsRect() {
        Rect rect = generateTextRect();
        float left = targetRect.centerX - rect.width()/2;
        float top = targetRect.centerY - rect.height()/2;
        float right = targetRect.centerX + rect.width()/2;
        float bottom = targetRect.centerY + rect.height()/2;
        return new RectF(left, top, right, bottom);
    }

    public RectF generateDeleteRect() {
        Rect rect = generateTextRect();
        float left = targetRect.centerX - rect.width()/2;
        float top = targetRect.centerY - rect.height()/2;
        return new RectF(left - BUTTON_WIDTH, top - BUTTON_WIDTH, left + BUTTON_WIDTH, top + BUTTON_WIDTH);
    }

    public RectF generateRotateRect() {
        Rect rect = generateTextRect();
        float right = targetRect.centerX + rect.width()/2;
        float bottom = targetRect.centerY + rect.height()/2;
        return new RectF(right - BUTTON_WIDTH, bottom - BUTTON_WIDTH, right + BUTTON_WIDTH, bottom + BUTTON_WIDTH);
    }

}
