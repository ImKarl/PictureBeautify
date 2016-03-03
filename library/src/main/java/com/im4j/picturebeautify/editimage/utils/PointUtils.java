package com.im4j.picturebeautify.editimage.utils;

import android.graphics.Point;
import android.graphics.PointF;

/**
 * @author alafighting 2016-03
 */
public class PointUtils {

    /**
     * 绕指定点旋转
     *
     * @param point
     * @param centerPoint
     * @param rotate
     * @return
     */
    public static PointF rotatePoint(PointF point, PointF centerPoint, float rotate) {
        float x = point.x;
        float y = point.y;
        float sinA = (float) Math.sin(Math.toRadians(rotate));
        float cosA = (float) Math.cos(Math.toRadians(rotate));
        float newX = centerPoint.x + (x - centerPoint.x) * cosA - (y - centerPoint.y) * sinA;
        float newY = centerPoint.y + (y - centerPoint.y) * cosA + (x - centerPoint.x) * sinA;
        return new PointF(newX, newY);
    }

    /**
     * 绕指定点旋转
     *
     * @param point
     * @param centerPoint
     * @param rotate
     * @return
     */
    public static Point rotatePoint(Point point, Point centerPoint, float rotate) {
        float x = point.x;
        float y = point.y;
        float sinA = (float) Math.sin(Math.toRadians(rotate));
        float cosA = (float) Math.cos(Math.toRadians(rotate));
        float newX = centerPoint.x + (x - centerPoint.x) * cosA - (y - centerPoint.y) * sinA;
        float newY = centerPoint.y + (y - centerPoint.y) * cosA + (x - centerPoint.x) * sinA;
        return new Point((int)newX, (int)newY);
    }

}
