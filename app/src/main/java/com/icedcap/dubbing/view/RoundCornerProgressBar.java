package com.icedcap.dubbing.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icedcap.dubbing.R;

import static android.graphics.drawable.GradientDrawable.LINEAR_GRADIENT;

/**
 * Created by icedcap on 06/05/2017.
 */
public class RoundCornerProgressBar extends LinearLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    protected final static int DEFAULT_MAX_PROGRESS = 90;
    protected final static int DEFAULT_PROGRESS = 10;
    protected final static int DEFAULT_SECONDARY_PROGRESS = 0;
    protected final static int DEFAULT_PROGRESS_RADIUS = 30;
    protected final static int DEFAULT_BACKGROUND_PADDING = 0;

    private RelativeLayout layoutBackground;
    private LinearLayout layoutProgress;
    private LinearLayout layoutSecondaryProgress;
    private TextView mTextView;
    private View mViewRoot;

    private String mTitle;

    private int radius;
    private int padding;
    private int totalHeight;

    private float max;
    private float progress;
    private float secondaryProgress;

    private int colorBackground;
    private int colorProgressGradientStart;
    private int colorProgressGradientEnd;
    private int colorSecondaryProgress;

    private boolean isReverse;

    private OnProgressChangedListener progressChangedListener;

    public RoundCornerProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            previewLayout(context);
        } else {
            setup(context, attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RoundCornerProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            previewLayout(context);
        } else {
            setup(context, attrs);
        }
    }

    private void previewLayout(Context context) {
        setGravity(Gravity.CENTER);
        TextView tv = new TextView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        tv.setText(getClass().getSimpleName());
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundColor(Color.GRAY);
        addView(tv);
    }

    // Setup an attribute parameter on sub class
    protected void initStyleable(Context context, AttributeSet attrs) {

    }

    // Initial any view on sub class
    protected void initView() {
        mTextView = (TextView) findViewById(R.id.tv_title);
        mViewRoot = findViewById(R.id.layout_background);
    }

    // Draw a progress by sub class
    protected void drawProgress(LinearLayout layoutProgress, float max, float progress, float totalHeight,
                                int radius, int padding, int colorProgress, int colorProgress2,  boolean isReverse) {
        GradientDrawable backgroundDrawable = createGradientDrawable(new int[] {colorProgress, colorProgress2});
        int newRadius = radius - (padding / 2);
        backgroundDrawable.setCornerRadii(new float[]{newRadius, newRadius, newRadius, newRadius, newRadius, newRadius, newRadius, newRadius});
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            layoutProgress.setBackground(backgroundDrawable);
        } else {
            layoutProgress.setBackgroundDrawable(backgroundDrawable);
        }

        float ratio = (max + 25) / (progress + 10);
        int progressHeight = (int) ((totalHeight - mViewRoot.getPaddingTop() -
                mViewRoot.getPaddingBottom() - (padding * 2)) / ratio);
        ViewGroup.LayoutParams progressParams = layoutProgress.getLayoutParams();
        progressParams.height = progressHeight;
        layoutProgress.setLayoutParams(progressParams);

        // draw start dot
//        if (mProgressDot != null) {
//            final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams()
//            mProgressDot.setLayoutParams();
//        }
    }

    // Draw all view on sub class
    protected void onViewDraw() {
        mTextView.setText(mTitle);
        mTextView.setTextColor(0xffbdbdbd);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setup(Context context, AttributeSet attrs) {
        setupStyleable(context, attrs);

        removeAllViews();
        // Setup layout for sub class
        LayoutInflater.from(context).inflate(R.layout.layout_round_corner_progress_bar, this);
        // Initial default view
        layoutBackground = (RelativeLayout) findViewById(R.id.layout_progress_holder);
        layoutProgress = (LinearLayout) findViewById(R.id.layout_progress);
        layoutSecondaryProgress = (LinearLayout) findViewById(R.id.layout_secondary_progress);

        initView();
    }

    // Retrieve initial parameter from view attribute
    public void setupStyleable(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerProgress);

        radius = (int) typedArray.getDimension(R.styleable.RoundCornerProgress_rcRadius, dp2px(DEFAULT_PROGRESS_RADIUS));
        padding = (int) typedArray.getDimension(R.styleable.RoundCornerProgress_rcBackgroundPadding, dp2px(DEFAULT_BACKGROUND_PADDING));

        isReverse = typedArray.getBoolean(R.styleable.RoundCornerProgress_rcReverse, false);

        max = typedArray.getFloat(R.styleable.RoundCornerProgress_rcMax, DEFAULT_MAX_PROGRESS);
        progress = typedArray.getFloat(R.styleable.RoundCornerProgress_rcProgress, DEFAULT_PROGRESS);
        secondaryProgress = typedArray.getFloat(R.styleable.RoundCornerProgress_rcSecondaryProgress, DEFAULT_SECONDARY_PROGRESS);

        int colorBackgroundDefault = context.getResources().getColor(R.color.round_corner_progress_bar_background_default);
        colorBackground = typedArray.getColor(R.styleable.RoundCornerProgress_rcBackgroundColor, colorBackgroundDefault);
        int colorProgressStartDefault = context.getResources().getColor(R.color.round_corner_progress_bar_progress_start_default);
        int colorProgressEndDefault = context.getResources().getColor(R.color.round_corner_progress_bar_progress_end_default);
        colorProgressGradientStart = typedArray.getColor(R.styleable.RoundCornerProgress_rcProgressColorStart, colorProgressStartDefault);
        colorProgressGradientEnd = typedArray.getColor(R.styleable.RoundCornerProgress_rcProgressColorEnd, colorProgressEndDefault);
        int colorSecondaryProgressDefault = context.getResources().getColor(R.color.round_corner_progress_bar_secondary_progress_default);
        colorSecondaryProgress = typedArray.getColor(R.styleable.RoundCornerProgress_rcSecondaryProgressColor, colorSecondaryProgressDefault);
        typedArray.recycle();

        initStyleable(context, attrs);
    }

    // Progress bar always refresh when view size has changed
    @Override
    protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        if (!isInEditMode()) {
//            totalWidth = newWidth;
            totalHeight = newHeight;
            drawAll();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawPrimaryProgress();
                    drawSecondaryProgress();
                }
            }, 5);
        }
    }

    // Redraw all view
    protected void drawAll() {
        drawBackgroundProgress();
        drawPadding();
        drawProgressReverse();
        drawPrimaryProgress();
        drawSecondaryProgress();
        onViewDraw();
    }

    // Draw progress background
    @SuppressWarnings("deprecation")
    private void drawBackgroundProgress() {
        GradientDrawable backgroundDrawable = createGradientDrawable(new int[]{colorBackground, colorBackground});
        int newRadius = radius - (padding / 2);
        backgroundDrawable.setCornerRadii(new float[]{newRadius, newRadius, newRadius, newRadius, newRadius, newRadius, newRadius, newRadius});
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            layoutBackground.setBackground(backgroundDrawable);
        } else {
            layoutBackground.setBackgroundDrawable(backgroundDrawable);
        }
    }

    // Create an empty color rectangle gradient drawable
    protected GradientDrawable createGradientDrawable(@ColorInt int[] color) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            gradientDrawable.setColors(color);
        } else {
            gradientDrawable.setColor(color[0]);
        }
        gradientDrawable.setGradientType(LINEAR_GRADIENT);
        return gradientDrawable;
    }

    private void drawPrimaryProgress() {
        drawProgress(layoutProgress, max, progress, totalHeight, radius,
                padding, colorProgressGradientStart, colorProgressGradientEnd, isReverse);
    }

    private void drawSecondaryProgress() {
        drawProgress(layoutSecondaryProgress, max, secondaryProgress, totalHeight, radius,
                padding, colorSecondaryProgress, colorSecondaryProgress, isReverse);
    }

    private void drawProgressReverse() {
        setupReverse(layoutProgress);
        setupReverse(layoutSecondaryProgress);
    }

    // Change progress position by depending on isReverse flag
    private void setupReverse(LinearLayout layoutProgress) {
        RelativeLayout.LayoutParams progressParams = (RelativeLayout.LayoutParams) layoutProgress.getLayoutParams();
        removeLayoutParamsRule(progressParams);
        if (isReverse) {
            progressParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            // For support with RTL on API 17 or more
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                progressParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        } else {
            progressParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            // For support with RTL on API 17 or more
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                progressParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        }
        layoutProgress.setLayoutParams(progressParams);
    }

    private void drawPadding() {
        layoutBackground.setPadding(padding, padding, padding, padding);
    }

    // Remove all of relative align rule
    private void removeLayoutParamsRule(RelativeLayout.LayoutParams layoutParams) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
        } else {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
        }
    }

    @SuppressLint("NewApi")
    protected float dp2px(float dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean isReverse) {
        this.isReverse = isReverse;
        drawProgressReverse();
        drawPrimaryProgress();
        drawSecondaryProgress();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        if (radius >= 0)
            this.radius = radius;
        drawBackgroundProgress();
        drawPrimaryProgress();
        drawSecondaryProgress();
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        if (padding >= 0)
            this.padding = padding;
        drawPadding();
        drawPrimaryProgress();
        drawSecondaryProgress();
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        if (max >= 0)
            this.max = max;
        if (this.progress > max)
            this.progress = max;
        drawPrimaryProgress();
        drawSecondaryProgress();
    }

    public float getLayoutHeight() {
        return totalHeight;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        if (progress < 0)
            this.progress = 0;
        else if (progress > max)
            this.progress = max;
        else
            this.progress = progress;
        drawPrimaryProgress();
        if (progressChangedListener != null)
            progressChangedListener.onProgressChanged(getId(), this.progress, true, false);
    }

    public float getSecondaryProgressWidth() {
        if (layoutSecondaryProgress != null)
            return layoutSecondaryProgress.getWidth();
        return 0;
    }

    public float getSecondaryProgressHeight() {
        if (layoutSecondaryProgress != null)
            return layoutSecondaryProgress.getHeight();
        return 0;
    }

    public float getSecondaryProgress() {
        return secondaryProgress;
    }

    public void setSecondaryProgress(float secondaryProgress) {
        if (secondaryProgress < 0)
            this.secondaryProgress = 0;
        else if (secondaryProgress > max)
            this.secondaryProgress = max;
        else
            this.secondaryProgress = secondaryProgress;
        drawSecondaryProgress();
        if (progressChangedListener != null)
            progressChangedListener.onProgressChanged(getId(), this.secondaryProgress, false, true);
    }

    public int getProgressBackgroundColor() {
        return colorBackground;
    }

    public void setProgressBackgroundColor(int colorBackground) {
        this.colorBackground = colorBackground;
        drawBackgroundProgress();
    }

    public int[] getProgressColor() {
        return new int[]{colorProgressGradientStart, colorProgressGradientEnd};
    }

    public void setProgressColor(int colorProgress, int colorProgress2) {
        this.colorProgressGradientStart = colorProgress;
        this.colorProgressGradientEnd = colorProgress2;
        drawPrimaryProgress();
    }

    public int getSecondaryProgressColor() {
        return colorSecondaryProgress;
    }

    public void setSecondaryProgressColor(int colorSecondaryProgress) {
        this.colorSecondaryProgress = colorSecondaryProgress;
        drawSecondaryProgress();
    }

    public void setOnProgressChangedListener(OnProgressChangedListener listener) {
        progressChangedListener = listener;
    }

    public LinearLayout getLayoutProgress() {
        return layoutProgress;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        drawAll();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.radius = this.radius;
        ss.padding = this.padding;

        ss.colorBackground = this.colorBackground;
        ss.colorProgress = this.colorProgressGradientStart;
        ss.colorSecondaryProgress = this.colorSecondaryProgress;

        ss.max = this.max;
        ss.progress = this.progress;
        ss.secondaryProgress = this.secondaryProgress;

        ss.isReverse = this.isReverse;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.radius = ss.radius;
        this.padding = ss.padding;

        this.colorBackground = ss.colorBackground;
        this.colorProgressGradientStart = ss.colorProgress;
        this.colorProgressGradientEnd = ss.colorProgress2;
        this.colorSecondaryProgress = ss.colorSecondaryProgress;

        this.max = ss.max;
        this.progress = ss.progress;
        this.secondaryProgress = ss.secondaryProgress;

        this.isReverse = ss.isReverse;
    }

    @Override
    public void onGlobalLayout() {

    }

    private static class SavedState extends BaseSavedState {
        float max;
        float progress;
        float secondaryProgress;

        int radius;
        int padding;

        int colorBackground;
        int colorProgress;
        int colorProgress2;
        int colorSecondaryProgress;

        boolean isReverse;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.max = in.readFloat();
            this.progress = in.readFloat();
            this.secondaryProgress = in.readFloat();

            this.radius = in.readInt();
            this.padding = in.readInt();

            this.colorBackground = in.readInt();
            this.colorProgress = in.readInt();
            this.colorSecondaryProgress = in.readInt();

            this.isReverse = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(this.max);
            out.writeFloat(this.progress);
            out.writeFloat(this.secondaryProgress);

            out.writeInt(this.radius);
            out.writeInt(this.padding);

            out.writeInt(this.colorBackground);
            out.writeInt(this.colorProgress);
            out.writeInt(this.colorSecondaryProgress);

            out.writeByte((byte) (this.isReverse ? 1 : 0));
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public interface OnProgressChangedListener {
        public void onProgressChanged(int viewId, float progress, boolean isPrimaryProgress, boolean isSecondaryProgress);
    }
}
