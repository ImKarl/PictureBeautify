/*
 * Copyright (C) 2012 Lightbox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.im4j.picturebeautify.editimage.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * BitmapUtils
 * 
 * @author panyi
 */
public class BitmapUtils {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "BitmapUtils";

	public static final long MAX_SZIE = 1024 * 512;// 500KB

	public static Bitmap loadImageByPath(final String imagePath, int reqWidth,
			int reqHeight) {
		File file = new File(imagePath);
		if (file.length() < MAX_SZIE) {
			return getSampledBitmap(imagePath, reqWidth, reqHeight);
		} else {// 压缩图片
			return getImageCompress(imagePath);
		}
	}

	public static Bitmap getSampledBitmap(String filePath, int reqWidth,
			int reqHeight) {
		Options options = new Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(filePath, options);

		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = (int) Math.floor(((float) height / reqHeight) + 0.5f); // Math.round((float)height
																		// /
																		// (float)reqHeight);
			} else {
				inSampleSize = (int) Math.floor(((float) width / reqWidth) + 0.5f); // Math.round((float)width
																	// /
																	// (float)reqWidth);
			}
		}
		// System.out.println("inSampleSize--->"+inSampleSize);

		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}

	// 按大小缩放
	public static Bitmap getImageCompress(final String srcPath) {
		Options newOpts = new Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}
	// 图片质量压缩
	private static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;

		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
//			System.out.println("options--->" + options + "    "
//					+ (baos.toByteArray().length / 1024));
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * Resize a bitmap object to fit the passed width and height
	 *
	 * @param input
	 *           The bitmap to be resized
	 * @param destWidth
	 *           Desired maximum width of the result bitmap
	 * @param destHeight
	 *           Desired maximum height of the result bitmap
	 * @return A new resized bitmap
	 * @throws OutOfMemoryError
	 *            if the operation exceeds the available vm memory
	 */
	public static Bitmap resizeBitmap( final Bitmap input, int destWidth, int destHeight, int rotation ) throws OutOfMemoryError {

		int dstWidth = destWidth;
		int dstHeight = destHeight;
		final int srcWidth = input.getWidth();
		final int srcHeight = input.getHeight();

		if ( rotation == 90 || rotation == 270 ) {
			dstWidth = destHeight;
			dstHeight = destWidth;
		}

		boolean needsResize = false;
		float p;
		if ( ( srcWidth > dstWidth ) || ( srcHeight > dstHeight ) ) {
			needsResize = true;
			if ( ( srcWidth > srcHeight ) && ( srcWidth > dstWidth ) ) {
				p = (float) dstWidth / (float) srcWidth;
				dstHeight = (int) ( srcHeight * p );
			} else {
				p = (float) dstHeight / (float) srcHeight;
				dstWidth = (int) ( srcWidth * p );
			}
		} else {
			dstWidth = srcWidth;
			dstHeight = srcHeight;
		}

		if ( needsResize || rotation != 0 ) {
			Bitmap output;

			if ( rotation == 0 ) {
				output = Bitmap.createScaledBitmap( input, dstWidth, dstHeight, true );
			} else {
				Matrix matrix = new Matrix();
				matrix.postScale( (float) dstWidth / srcWidth, (float) dstHeight / srcHeight );
				matrix.postRotate( rotation );
				output = Bitmap.createBitmap( input, 0, 0, srcWidth, srcHeight, matrix, true );
			}
			return output;
		} else
			return input;
	}


}
