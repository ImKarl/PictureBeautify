package com.im4j.picturebeautify.editimage.utils;

import android.graphics.Bitmap;

/**
 * 图片处理类
 */
public class PhotoUtils {

    public static final String[] FILTERS = {"原色", "轻柔", "黑白", "经典", "绚丽",
            "复古", "优雅", "胶片", "回忆", "优格", "流年", "光绚"};

    public static Bitmap filterPhoto(Bitmap bitmap, int position) {
        if (bitmap == null) {
            return null;
        }
        // TODO 实现图片过滤处理
        switch (position) {
            case 0: // Original
                break;
            case 1: // Instafix
//                bitmap = Instafix(bitmap);
                break;
            case 2: // Ansel
                break;
            case 3: // Testino
                break;
            case 4: // XPro
                break;
            case 5: // Retro
                break;
            case 6: // Black & White
                break;
            case 7: // Sepia
                break;
            case 8: // Cyano
                break;
            case 9: // Georgia
                break;
            case 10: // Sahara
                break;
            case 11: // HDR
                break;
        }
        return bitmap;
    }

}
