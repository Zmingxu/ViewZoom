package com.app.viewzoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;

/**
 * Created by Maurice on 2017/6/7.
 * email:zhang_mingxu@126.com
 */

public class ZoomView extends View {
    private static final int FACTOR = 2;
    private static final int RADIUS = 150;
    private ShapeDrawable mShapeDrawable;
    private Bitmap mBitmap;
    private Bitmap mBitmapScale;
    private Matrix mMatrix;

    public ZoomView(Context context) {
        super(context);
        mShapeDrawable = new ShapeDrawable(new OvalShape());
        mMatrix = new Matrix();
    }

    public void setInitCurBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mBitmapScale = mBitmap;
        mBitmapScale = Bitmap.createScaledBitmap(mBitmapScale,
                mBitmapScale.getWidth() * FACTOR, mBitmapScale.getHeight() * FACTOR, true);
        BitmapShader bitmapShader = new BitmapShader(mBitmapScale
                , Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mShapeDrawable.getPaint().setShader(bitmapShader);
        mShapeDrawable.setBounds(0, 0, 0, 0);
        invalidate();
    }

    public void setCurShowPos(int x, int y) {
        mMatrix.setTranslate(RADIUS - x * FACTOR, RADIUS - y * FACTOR);
        mShapeDrawable.getPaint().getShader().setLocalMatrix(mMatrix);
        mShapeDrawable.setBounds(x - RADIUS / 2, y - 3 * RADIUS / 2
                , x + 3 * RADIUS / 2, y + RADIUS / 2);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, null);

        //画放大镜
        mShapeDrawable.draw(canvas);
    }

}
