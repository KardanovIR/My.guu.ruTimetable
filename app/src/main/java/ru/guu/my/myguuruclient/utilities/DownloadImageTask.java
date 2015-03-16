package ru.guu.my.myguuruclient.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by Инал on 16.03.2015.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView mBmImage;
    private float mMaxWidth;
    private float mMaxHeight;


    public DownloadImageTask (ImageView bmImage, float maxHeight, float maxWidth) {
        this.mMaxWidth = maxWidth;
        this.mMaxHeight = maxHeight;
        this.mBmImage = bmImage;
    }

    public DownloadImageTask (ImageView bmImage) {
        this.mMaxHeight = 150;
        this.mMaxWidth = 300;
        this.mBmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        final int imgMaxHeight = (int) mMaxHeight;
        final int imgMaxWidth = (int) mMaxWidth;
        float ratioBitmap = (float) result.getWidth() / (float) result.getHeight();
        float ratioMax = (float) imgMaxWidth / (float) imgMaxHeight;
        int finalWidth = imgMaxWidth;
        int finalHeight = imgMaxHeight;
        if (ratioMax > 1) {
            finalWidth = (int) ((float)imgMaxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float)imgMaxWidth / ratioBitmap);
        }
        Bitmap output = Bitmap.createBitmap(result.getWidth(),
                result.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, result.getWidth(),
                result.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(result.getWidth() / 2,
                result.getHeight() / 2, result.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(result, rect, rect, paint);
        output = Bitmap.createScaledBitmap(output, finalWidth, finalHeight, true);
        mBmImage.setImageBitmap(output);
    }
}
