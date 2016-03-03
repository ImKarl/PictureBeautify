package com.im4j.picturebeautify.editimage.utils;

import android.graphics.Matrix;
import android.util.Log;

/**
 * @author alafighting 2016-03
 */
public class MatrixUtils {

    public static float getValue(Matrix matrix, int whichValue) {
        float[] mMatrixValues = new float[9];
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }


    public static void print(Matrix matrix) {
        float scale = getValue(matrix, Matrix.MSCALE_X);

        float moveX = getValue(matrix, Matrix.MTRANS_X);
        float moveY = getValue(matrix, Matrix.MTRANS_Y);

        Log.e("[MatrixUtils]", "matrix: { moveX: " + moveX + ", moveY: " + moveY + ", scale: " + scale +" }");
    }


}
