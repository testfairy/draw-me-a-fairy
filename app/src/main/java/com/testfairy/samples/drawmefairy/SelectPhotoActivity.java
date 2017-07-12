package com.testfairy.samples.drawmefairy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import draw.me.fairy.R;

import java.lang.reflect.Field;

/**
 * Created by gilt on 12/17/14.
 */
public class SelectPhotoActivity extends Activity {

	static int selectedPhotosCount = 0;
	private final String TAG = getClass().getSimpleName();
	LinearLayout thumbnails;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_photo);
		Log.d(TAG, "onCreate " + TAG);

		thumbnails = (LinearLayout) findViewById(R.id.thumbnails);

		loadThumbnails();

	}

	private void loadThumbnails() {
		Field[] fields = R.drawable.class.getFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				int thumbnailId = fields[i].getInt(null);
				final String thumbnailName = fields[i].getName();

				if (thumbnailName.contains("_s")) {


					Bitmap image = BitmapFactory.decodeResource(this.getResources(), thumbnailId);
					ImageView imageView = new ImageView(this);
					imageView.setPadding(5, 5, 5, 5);
					imageView.setImageBitmap(image);

					String photoName = thumbnailName.replace("_s", "");
					imageView.setOnClickListener(new OnThumbnailClicked(photoName));
					thumbnails.addView(imageView);

				}
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private class OnThumbnailClicked implements View.OnClickListener {

		private final String photoName;

		OnThumbnailClicked(String photoName) {
			this.photoName = photoName;
		}

		@Override
		public void onClick(View v) {
			selectedPhotosCount++;
			if (selectedPhotosCount == 3 || selectedPhotosCount == 4 || selectedPhotosCount == 7 || selectedPhotosCount == 10) {
				Log.v("testfairy-checkpoint", selectedPhotosCount + " photos viewed");
			}
//			Toast.makeText(SelectPhotoActivity.this, photoName, Toast.LENGTH_LONG).show();
			Intent intent = new Intent(SelectPhotoActivity.this, DrawingActivity.class);
			intent.putExtra(DrawingActivity.EXTRA_PICTURE_PATH, photoName);
			startActivity(intent);
		}
	}
}