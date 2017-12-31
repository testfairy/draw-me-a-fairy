package com.testfairy.samples.drawmefairy;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.testfairy.TestFairy;

import java.io.File;
import java.io.FileOutputStream;

public class DrawingPanel extends View implements View.OnTouchListener {

	private String TAG = getClass().getSimpleName();

	private int[] strokeWidth = {10, 15, 40};

	/// drawing path
	private Path drawPath;

	/// drawing and canvas paint
	private Paint drawPaint, canvasPaint;

	/// number of strokes in path
	private int strokeCount = 0;

	/// did we send path already?
	private boolean pathSent = false;

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
			TestFairy.addEvent("Selected large brush");
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

	private void sendDrawPath() {

		try {
			File file = new File(getContext().getExternalFilesDir(null), "DrawPath.png");
			FileOutputStream fos = new FileOutputStream(file);

			Bitmap pathBitmap = Bitmap.createBitmap(drawCanvas.getWidth(), drawCanvas.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas pathCanvas = new Canvas(pathBitmap);
			Paint pathPaint = new Paint();
			pathPaint.setARGB(255, 128, 128, 128); // gray
			pathCanvas.drawRect(0, 0, pathBitmap.getWidth(), pathBitmap.getHeight(), pathPaint);
			pathCanvas.drawPath(drawPath, drawPaint);
			pathBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.close();

			TestFairy.attachFile(file);

			// now, also attach binary
			File file2 = new File(getContext().getExternalFilesDir(null), "DrawPathInfo.txt");
			FileOutputStream fos2 = new FileOutputStream(file2);
			fos2.write(drawPath.toString().getBytes());
			fos2.close();

			TestFairy.attachFile(file2);

			// we are only interested in first complex path
			pathSent = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onMotionEventUp() {

		if (!pathSent && strokeCount > 16) {
			// this is a complexO path, let's write it to disk
			Log.v(TAG, "Found a complex path, sending path to TestFairy");
			sendDrawPath();
		}

		drawCanvas.drawPath(drawPath, drawPaint);

		// reset path
		strokeCount = 0;
		drawPath.reset();
	}

	public void onMotionMove(float touchX, float touchY) {

		strokeCount++;
		drawPath.lineTo(touchX, touchY);
		drawCanvas.drawPath(drawPath, drawPaint);
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
				this.onMotionMove(touchX, touchY);
				break;
			case MotionEvent.ACTION_UP:
				this.onMotionEventUp();
				break;
			default:
				return false;
		}
		invalidate();
		return true;
	}
}