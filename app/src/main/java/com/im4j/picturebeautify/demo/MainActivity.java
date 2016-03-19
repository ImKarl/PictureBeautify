package com.im4j.picturebeautify.demo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.im4j.picturebeautify.editimage.EditImageActivity;
import com.im4j.picturebeautify.picchooser.SelectPictureActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final int SELECT_GALLERY_IMAGE_CODE = 7;
    public static final int ACTION_REQUEST_EDITIMAGE = 9;
    private ImageView imgView;
    private Bitmap mainBitmap;
    private int imageWidth, imageHeight;//
    private String path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = (int) ((float) metrics.widthPixels / 1.5);
        imageHeight = (int) ((float) metrics.heightPixels / 1.5);

        imgView = (ImageView) findViewById(R.id.img);

        findViewById(R.id.select_ablum).setOnClickListener(new SelectClick());
        findViewById(R.id.edit_image).setOnClickListener(new EditImageClick());
    }

    /**
     * 编辑选择的图片
     *
     * @author panyi
     */
    private final class EditImageClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(path)) {
                Toast.makeText(MainActivity.this, R.string.no_choose, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent it = new Intent(MainActivity.this, EditImageActivity.class);
            it.putExtra(EditImageActivity.FILE_PATH, path);
            File outputFile = FileUtils.getEmptyFile("tietu"
                    + System.currentTimeMillis() + ".jpg");
            it.putExtra(EditImageActivity.EXTRA_OUTPUT,
                    outputFile.getAbsolutePath());
            MainActivity.this.startActivityForResult(it,
                    ACTION_REQUEST_EDITIMAGE);
        }
    }// end inner class

    private final class SelectClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            MainActivity.this.startActivityForResult(new Intent(
                            MainActivity.this, SelectPictureActivity.class),
                    SELECT_GALLERY_IMAGE_CODE);
        }
    }// end inner class

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_GALLERY_IMAGE_CODE://
                    handleSelectFromAblum(data);
                    break;
                case ACTION_REQUEST_EDITIMAGE://
                    handleEditorImage(data);
                    break;
            }
        }
    }

    private void handleEditorImage(Intent data) {
        String newFilePath = data.getStringExtra("save_file_path");
        Toast.makeText(this, "new image path: " + newFilePath, Toast.LENGTH_LONG).show();
        LoadImageTask loadTask = new LoadImageTask();
        loadTask.execute(newFilePath);
    }

    private void handleSelectFromAblum(Intent data) {
        path = data.getStringExtra("imgPath");
        LoadImageTask task = new LoadImageTask();
        task.execute(path);
    }


    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return getSampledBitmap(params[0], imageWidth, imageHeight);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (mainBitmap != null) {
                mainBitmap.recycle();
                mainBitmap = null;
                System.gc();
            }
            mainBitmap = result;
            imgView.setImageBitmap(mainBitmap);
        }
    }// end inner class

    public static Bitmap getSampledBitmap(String filePath, int reqWidth,
                                          int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
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
}//end class
