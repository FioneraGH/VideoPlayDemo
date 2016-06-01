package com.fionera.videoplayerdemo.circularprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.fionera.videoplayerdemo.R;


/**
 * Created by castorflex on 11/10/13.
 */
public class CircularProgressBar
        extends ProgressBar {

    public CircularProgressBar(Context context) {
        this(context, null);
    }

    public CircularProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (isInEditMode()) {
            setIndeterminateDrawable(new CircularProgressDrawable.Builder(context, true).build());
            return;
        }

        TypedArray a = context
                .obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, defStyle, 0);


        final int color = a.getColor(R.styleable.CircularProgressBar_cpb_color,
                                     ContextCompat.getColor(context, R.color.blue2));
        final float strokeWidth = a
                .getDimension(R.styleable.CircularProgressBar_cpb_stroke_width, 0);
        final float sweepSpeed = a.getFloat(R.styleable.CircularProgressBar_cpb_sweep_speed, 0f);
        final float rotationSpeed = a
                .getFloat(R.styleable.CircularProgressBar_cpb_rotation_speed, 0f);
        final int minSweepAngle = a
                .getInteger(R.styleable.CircularProgressBar_cpb_min_sweep_angle, 0);
        final int maxSweepAngle = a
                .getInteger(R.styleable.CircularProgressBar_cpb_max_sweep_angle, 0);
        a.recycle();

        Drawable indeterminateDrawable;
        CircularProgressDrawable.Builder builder = new CircularProgressDrawable.Builder(context)
                .sweepSpeed(sweepSpeed).rotationSpeed(rotationSpeed).strokeWidth(strokeWidth)
                .minSweepAngle(minSweepAngle).maxSweepAngle(maxSweepAngle);

        builder.color(color);

        indeterminateDrawable = builder.build();
        setIndeterminateDrawable(indeterminateDrawable);
    }

    private CircularProgressDrawable checkIndeterminateDrawable() {
        Drawable ret = getIndeterminateDrawable();
        if (ret == null || !(ret instanceof CircularProgressDrawable)) {
            throw new RuntimeException("The drawable is not a CircularProgressDrawable");
        }
        return (CircularProgressDrawable) ret;
    }

    public void progressiveStop() {
        checkIndeterminateDrawable().progressiveStop();
    }

    public void progressiveStop(CircularProgressDrawable.OnEndListener listener) {
        checkIndeterminateDrawable().progressiveStop(listener);
    }
}
