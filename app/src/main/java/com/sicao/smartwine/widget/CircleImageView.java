package com.sicao.smartwine.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sicao.smartwine.R;

public class CircleImageView extends ImageView implements ProgImageView {

	private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

	private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
	private static final int COLORDRAWABLE_DIMENSION = 1;

	private static final int DEFAULT_BORDER_WIDTH = 0;
	private static final int DEFAULT_BORDER_COLOR = Color.BLACK;

	private final RectF mDrawableRect = new RectF();
	private final RectF mBorderRect = new RectF();

	private final Matrix mShaderMatrix = new Matrix();
	private final Paint mBitmapPaint = new Paint();
	private final Paint mBorderPaint = new Paint();

	private int mBorderColor = DEFAULT_BORDER_COLOR;
	private int mBorderWidth = DEFAULT_BORDER_WIDTH;

	private Bitmap mBitmap;
	private BitmapShader mBitmapShader;
	private int mBitmapWidth;
	private int mBitmapHeight;

	private Paint mRemainedPaint;
	private Paint mLoadPaint;
	private Paint mTextPaint;
	private float mProgress;

	private float mDrawableRadius;
	private float mBorderRadius;

	private boolean mReady;
	private boolean mSetupPending;
	// 绘制文字、
	private String mContent = "";

	public CircleImageView(Context context) {
		super(context);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		super.setScaleType(SCALE_TYPE);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CircleImageView, defStyle, 0);
		mBorderWidth = a.getDimensionPixelSize(
				R.styleable.CircleImageView_border_width, DEFAULT_BORDER_WIDTH);
		mBorderColor = a.getColor(R.styleable.CircleImageView_border_color,
				DEFAULT_BORDER_COLOR);
		a.recycle();
		mReady = true;
		if (mSetupPending) {
			setup();
			mSetupPending = false;
		}
		init();
	}

	public void setmContent(String mContent) {
		this.mContent = mContent;
	}

	private void init() {
		mProgress = 1;
		mRemainedPaint = new Paint();
		mRemainedPaint.setColor(Color.BLACK);
		mRemainedPaint.setAlpha(150);
		mRemainedPaint.setAntiAlias(true);

		mLoadPaint = new Paint();
		mLoadPaint.setColor(Color.BLACK);
		mLoadPaint.setAlpha(0);
		mLoadPaint.setAntiAlias(true);

		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(this.getContext().getResources()
				.getColor(R.color.baseColor));
		mTextPaint.setTextSize(sp2px(this.getContext(), 12));
	}

	public void setProgress(float progress) {
		if (progress > 1) {
			mProgress = 1;
		} else {
			mProgress = progress;
		}
		this.invalidate();
	}

	@Override
	public ScaleType getScaleType() {
		return SCALE_TYPE;
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		if (scaleType != SCALE_TYPE) {
			throw new IllegalArgumentException(String.format(
					"ScaleType %s not supported.", scaleType));
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		if (getDrawable() == null) {
			return;
		}
		RectF f = new RectF(0, 0, getWidth(), getHeight());
		float r = getHeight() / 2;
		float startAngle;
		float sweepAngle;
		float endAngle;
		float h = r - 2 * r * mProgress;
		startAngle = (float) ((float) Math.asin(h / r) * 180 / Math.PI);
		endAngle = 180 - startAngle;
		sweepAngle = endAngle - startAngle;
		canvas.drawArc(f, startAngle, sweepAngle, false, mLoadPaint);
		canvas.drawArc(f, endAngle, 360 - sweepAngle, false, mRemainedPaint);

		if ((int) (mProgress * 100) != 100) {
			String percentText = (int) (mProgress * 100) + "%";
			float tW = mTextPaint.measureText(percentText);
			float tH = sp2px(this.getContext(), 12);
			canvas.drawText(percentText, (int) ((getWidth() - tW) / 2),
					(int) ((getHeight() - tH) / 2), mTextPaint);

		}
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius,
				mBitmapPaint);
		if (mBorderWidth != 0) {
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius,
					mBorderPaint);
		}
		// 绘制文字
		if (!"".equals(mContent)) {
			float tW = mTextPaint.measureText(mContent);
			float tH = sp2px(this.getContext(), 12);
			canvas.drawText(mContent, (int) ((getWidth() - tW) / 2),
					(int) ((getHeight()- tH) / 2+20), mTextPaint);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		setup();
	}

	public int getBorderColor() {
		return mBorderColor;
	}

	public void setBorderColor(int borderColor) {
		if (borderColor == mBorderColor) {
			return;
		}

		mBorderColor = borderColor;
		mBorderPaint.setColor(mBorderColor);
		invalidate();
	}

	public int getBorderWidth() {
		return mBorderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		if (borderWidth == mBorderWidth) {
			return;
		}

		mBorderWidth = borderWidth;
		setup();
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		mBitmap = bm;
		setup();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		mBitmap = getBitmapFromDrawable(drawable);
		setup();
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		mBitmap = getBitmapFromDrawable(getDrawable());
		setup();
	}

	private Bitmap getBitmapFromDrawable(Drawable drawable) {
		if (drawable == null) {
			return null;
		}

		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		try {
			Bitmap bitmap;

			if (drawable instanceof ColorDrawable) {
				bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
						COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
			} else {
				bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(), BITMAP_CONFIG);
			}

			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
			return bitmap;
		} catch (OutOfMemoryError e) {
			return null;
		}
	}

	private void setup() {
		if (!mReady) {
			mSetupPending = true;
			return;
		}

		if (mBitmap == null) {
			return;
		}

		mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
				Shader.TileMode.CLAMP);

		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setShader(mBitmapShader);

		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setColor(mBorderColor);
		mBorderPaint.setStrokeWidth(mBorderWidth);

		mBitmapHeight = mBitmap.getHeight();
		mBitmapWidth = mBitmap.getWidth();

		mBorderRect.set(0, 0, getWidth(), getHeight());
		mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2,
				(mBorderRect.width() - mBorderWidth) / 2);

		mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width()
				- mBorderWidth, mBorderRect.height() - mBorderWidth);
		mDrawableRadius = Math.min(mDrawableRect.height() / 2,
				mDrawableRect.width() / 2);

		updateShaderMatrix();
		invalidate();
	}

	private void updateShaderMatrix() {
		float scale;
		float dx = 0;
		float dy = 0;

		mShaderMatrix.set(null);

		if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width()
				* mBitmapHeight) {
			scale = mDrawableRect.height() / (float) mBitmapHeight;
			dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
		} else {
			scale = mDrawableRect.width() / (float) mBitmapWidth;
			dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
		}

		mShaderMatrix.setScale(scale, scale);
		mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth,
				(int) (dy + 0.5f) + mBorderWidth);

		mBitmapShader.setLocalMatrix(mShaderMatrix);
	}

	private int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

}
