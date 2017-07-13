package com.testfairy.samples.drawmefairy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import colorpicker.ColorPickerDialog;
import colorpicker.ColorPickerDialog.OnColorSelectedListener;
import draw.me.fairy.R;
import utils.ActivityTime;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import static android.graphics.Bitmap.CompressFormat;
import static android.graphics.Bitmap.Config;

public class DrawingActivity extends Activity {

	public static final String EXTRA_PICTURE_PATH = "picturePath";
	private static ArrayList<Bitmap> bitmapHistory = new ArrayList<Bitmap>();
	private final String TAG = getClass().getSimpleName();
	private final int MENU_PRINT = 3;
	private ActivityTime activityTime;
	//top panel Buttons
	private DrawingPanel drawingPanel;
	private ImageView colorPickerButton;
	//use for color picker image
	private Bitmap bitmapColor;
	private OnColorSelectedListener onColorSelectedListener = new OnColorSelectedListener() {
		@Override
		public void onColorSelected(int color) {
			drawingPanel.setBrushColor(color);
			drawingPanel.setEraseMode(false);
			//change the button color
			bitmapColor.eraseColor(color);
			colorPickerButton.setImageBitmap(bitmapColor);

			Log.v(TAG, "Picked up color: 0x" + Integer.toHexString(color));

			int red = (color >> 16) & 0xff;
			int green = (color >> 8) & 0xff;
			int blue = color & 0xff;
			if (red >= 0xa0 && green < 0x30 && blue < 0x30) {
				Log.v("testfairy-checkpoint", "Red color selected");
			} else if (red < 0x30 && green >= 0xa0 && blue < 0x30) {
				Log.v("testfairy-checkpoint", "Green color selected");
			} else if (red < 0x30 && green < 0x30 && blue >= 0xa0) {
				Log.v("testfairy-checkpoint", "Blue color selected");
			}
		}
	};
	private View.OnClickListener onColorPickerClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ColorPickerDialog colorPickerDialog = new ColorPickerDialog(DrawingActivity.this, Color.WHITE, onColorSelectedListener);
			colorPickerDialog.show();
		}
	};
	private View.OnClickListener onPainterPickerClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			Dialog painterBrushDialog = new BrushSizeDialog(DrawingActivity.this, BrushSizeDialog.PAINT_BRUSH);
			painterBrushDialog.show();
		}
	};
	private View.OnClickListener onEraserPickerClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			Dialog eraserBrushDialog = new BrushSizeDialog(DrawingActivity.this, BrushSizeDialog.ERASER_BRUSH);
			eraserBrushDialog.show();
		}
	};
	private View.OnClickListener onSaveClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			saveToFile(false);
		}
	};
	private View.OnClickListener onShareClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			boolean isImageSaved = saveToFile(true);
			if (isImageSaved) {
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("image/jpeg");
				shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/Pictures/testFairyDemoToShare.jpg"));
				startActivity(Intent.createChooser(shareIntent, "Share Image"));
				Log.v("testfairy-checkpoint", "Image shared");
			} else {
				Toast.makeText(DrawingActivity.this, "Share failed, please try again", Toast.LENGTH_LONG).show();
			}
		}
	};
	private DialogInterface.OnClickListener onExitWithoutSaveClick = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			DrawingActivity.super.onBackPressed();
		}
	};
	private DialogInterface.OnClickListener onExitEndSaveClick = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			saveToFile(false);
			DrawingActivity.super.onBackPressed();
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate " + TAG);

		// hide title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.drawing_layout);

		String picturePath = getIntent().getStringExtra(EXTRA_PICTURE_PATH);

		if (picturePath != null) {

			int drawableResId = getResources().getIdentifier(getPackageName() + ":drawable/" + picturePath, null, null);


			ImageView imageView = (ImageView) findViewById(R.id.img_from_gallery);
			//get screen size
			DisplayMetrics display = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(display);
			int screenWidth = display.widthPixels;
			int screenHeight = display.heightPixels;
			//get image bitmap and scale it to screen size
//			Bitmap fullSizeBitmap = BitmapFactory.decodeFile(picturePath);
			Bitmap fullSizeBitmap = BitmapFactory.decodeResource(this.getResources(), drawableResId);
			Bitmap scaledBitmap = ThumbnailUtils.extractThumbnail(fullSizeBitmap, screenWidth, screenHeight);
			imageView.setImageBitmap(scaledBitmap);

			bitmapHistory.add(scaledBitmap);
			Log.v(TAG, "Total images edited: " + bitmapHistory.size());

			if (bitmapHistory.size() >= 5) {
				Log.d(TAG, "User has been editing 5 or more images already, here are the sizes:");
				for (int i = 1; i <= bitmapHistory.size(); i++) {
					Log.d(TAG, "Image: " + i + ", Width: " + bitmapHistory.get(i).getWidth() + ", Height: " + bitmapHistory.get(i).getHeight());
				}
			}
		}

		drawingPanel = (DrawingPanel) findViewById(R.id.drawing_panel);

		colorPickerButton = (ImageButton) findViewById(R.id.color_picker);
		View painterPickerButton = findViewById(R.id.painter_picker);
		View erasePickerButton = findViewById(R.id.erase_picker);
		View saveButton = findViewById(R.id.save);
		View shareButton = findViewById(R.id.share);

		colorPickerButton.setOnClickListener(onColorPickerClick);
		painterPickerButton.setOnClickListener(onPainterPickerClick);
		erasePickerButton.setOnClickListener(onEraserPickerClick);
		saveButton.setOnClickListener(onSaveClick);
		shareButton.setOnClickListener(onShareClick);

		bitmapColor = Bitmap.createBitmap(50, 50, Config.ARGB_8888);
		bitmapColor.eraseColor(Color.WHITE);
		colorPickerButton.setImageBitmap(bitmapColor);

	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
		    .setTitle("Exit and Save")
		    .setMessage("Do you want to save the image?")
		    .setPositiveButton("Save and exit", onExitEndSaveClick)
		    .setNegativeButton("Exit", onExitWithoutSaveClick)
		    .create().show();

	}

	/**
	 * extract bitmap from the given layout
	 *
	 * @param layoutId the id of the layout
	 * @return bitmap of the given layout
	 */
	private Bitmap getBitmapFromLayout(int layoutId) {
		View view = findViewById(layoutId);
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		return view.getDrawingCache();
	}

	/**
	 * save a layout into the public pictures directory
	 * and scan the file to insert the image to gallery.
	 *
	 * @param isTempFile
	 * @return true if the save is succeed, otherwise return false.
	 */
	private boolean saveToFile(boolean isTempFile) {

		Bitmap bitmapToSave = getBitmapFromLayout(R.id.drawing_container);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmapToSave.compress(CompressFormat.JPEG, 75, bytes);

		String fileName;
		if (isTempFile) {
			fileName = "paintMeSomething_share.jpg";
		} else {
			Random r = new Random();
			int rand = r.nextInt(10000 - 1) + 1;
			fileName = "paintMeSomething-" + rand + ".jpg";
		}

		File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File imageFile = new File(pictureFolder, fileName);
		try {
			imageFile.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(imageFile);
			outputStream.write(bytes.toByteArray());
			if (!isTempFile) {
				Toast.makeText(DrawingActivity.this, "Saved", Toast.LENGTH_LONG).show();
				Log.v("testfairy-checkpoint", "Image saved");
			}

			Log.d(TAG, "The image was saved on " + imageFile.getPath());
			//scan file after saving, to add him to gallery
			MediaScannerConnection.scanFile(DrawingActivity.this, new String[]{imageFile.getAbsolutePath()}, null, null);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, MENU_PRINT, 1, "Print");

		return super.onCreateOptionsMenu(menu);


	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		Log.v("testfairy-checkpoint", "Drawing Menu Opened");
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent;
		switch (item.getItemId()) {
			case MENU_PRINT:
				intent = new Intent(DrawingActivity.this, PrintActivity.class);
				startActivity(intent);
				break;

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		activityTime = new ActivityTime("Drawing");
		super.onStart();
	}

	@Override
	protected void onPause() {
		activityTime.cancel();
		super.onPause();
	}

	private class BrushSizeDialog extends Dialog {

		public final static int PAINT_BRUSH = 0;
		public final static int ERASER_BRUSH = 1;

		private final int currentBrushMode;

		private Button smallBrushButton;
		private Button mediumBrushButton;
		private Button largeBrushButton;
		private View.OnClickListener onBrushSelectorClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				BrushSizeDialog.this.dismiss();
				int brushSize = Integer.parseInt((String) v.getTag());
				drawingPanel.setBrushSize(brushSize);
				if (currentBrushMode == PAINT_BRUSH) {
					drawingPanel.setEraseMode(false);
				} else {
					drawingPanel.setEraseMode(true);
				}
			}
		};

		/**
		 * initialize the brush size buttons
		 *
		 * @param context
		 * @param brushMode determine if paint mode or erase mode
		 */
		public BrushSizeDialog(Context context, int brushMode) {
			super(context);
			currentBrushMode = brushMode;
			setTitle("Brush size:");
			setContentView(R.layout.brush_chooser);
			smallBrushButton = (Button) findViewById(R.id.small_brush);
			mediumBrushButton = (Button) findViewById(R.id.medium_brush);
			largeBrushButton = (Button) findViewById(R.id.large_brush);

			smallBrushButton.setOnClickListener(onBrushSelectorClick);
			mediumBrushButton.setOnClickListener(onBrushSelectorClick);
			largeBrushButton.setOnClickListener(onBrushSelectorClick);
		}
	}
}