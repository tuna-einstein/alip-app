package com.usp.widget.alips;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by umasankar on 10/25/14.
 */
public class CustomTextView extends TextView {
    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onDraw(Canvas canvas){
        final ColorStateList csl = getTextColors();
        final int color = csl.getDefaultColor();
        final int paddingBottom = getPaddingBottom();
        final int paddingTop = getPaddingTop();
        final int viewWidth = getWidth();
        final int viewHeight = getHeight();
        final TextPaint paint = getPaint();
        paint.setColor(color);
        final float bottom = viewWidth * 9.0f / 11.0f;


        float width = (float)getWidth();

        float height = (float)getHeight();

        float radius;



        if (width > height) {
            radius = height/2;
        } else{
            radius = width/2;
        }

        setTextSize(0.03f*width);
        CharSequence msg = getText();
        radius = radius - getTextSize()/2 - 0.04f*width;
        Path path = new Path();
        path.addArc(
                new RectF(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius),
                270,
                270);
        canvas.drawTextOnPath(msg.toString(), path, 0, 0, paint);
    }
}
