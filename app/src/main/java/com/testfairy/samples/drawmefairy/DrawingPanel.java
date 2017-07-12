package com.testfairy.samples.drawmefairy;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawingPanel extends View implements View.OnTouchListener {

	private String TAG = getClass().getSimpleName();

	private int[] strokeWidth = {10, 15, 40};

	/// drawing path
	private Path drawPath;

	/// drawing and canvas paint
	private Paint drawPaint, canvasPaint;

	/// canvas
	private Canvas drawCanvas;

	/// canvas bitmap
	private Bitmap canvasBitmap;

	public DrawingPanel(Context context) {
		this(context, null, 0);
	}

	public DrawingPanel(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DrawingPanel(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		drawPath = new Path();
		drawPaint = new Paint();
		drawPaint.setAntiAlias(true);
		drawPaint.setDither(true);
		drawPaint.setColor(Color.WHITE);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		drawPaint.setStrokeWidth(strokeWidth[0]);
		canvasPaint = new Paint(Paint.DITHER_FLAG);

		setFocusable(true);
		setFocusableInTouchMode(true);

		this.setOnTouchListener(this);
	}

	/**
	 * set new brush color
	 *
	 * @param color
	 */
	public void setBrushColor(int color) {
		drawPaint.setColor(color);
	}

	/**
	 * set the brush mode, paint/erase
	 *
	 * @param isEraseMode
	 */
	public void setEraseMode(boolean isEraseMode) {
		if (isEraseMode) {
			drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		} else {
			drawPaint.setXfermode(null);
		}
	}

	/**
	 * set the brush size, if (0 > brushSizeIndex > 2) nothing will happened
	 *
	 * @param brushSizeIndex 0,1,2
	 */
	public void setBrushSize(int brushSizeIndex) {
		if (brushSizeIndex < 0 || brushSizeIndex > strokeWidth.length) {
			Log.e(TAG, "illegal brush size Index");
			return;
		}
		if (brushSizeIndex == 2) {
			Log.v("testfairy-checkpoint", "large brush user");
		}

		Log.i(TAG, "set brush size to " + strokeWidth[brushSizeIndex]);
		drawPaint.setStrokeWidth(strokeWidth[brushSizeIndex]);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		Log.d(TAG, "onSizeChanged");
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		Log.d(TAG, "on Draw");
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {

		float touchX = event.getX();
		float touchY = event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				drawPath.moveTo(touchX, touchY);
				break;
			case MotionEvent.ACTION_MOVE:
				drawPath.lineTo(touchX, touchY);
				drawCanvas.drawPath(drawPath, drawPaint);
				break;
			case MotionEvent.ACTION_UP:
				drawCanvas.drawPath(drawPath, drawPaint);
				drawPath.reset();
				break;
			default:
				return false;
		}
		invalidate();
		return true;
	}
}