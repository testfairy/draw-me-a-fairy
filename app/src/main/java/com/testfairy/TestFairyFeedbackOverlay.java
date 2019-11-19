/*
 * Copyright (C) 2019 TestFairy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Icons are available under Apache License, Version 2.0 (the "License")
 * and provided by material.io project.
 */

package com.testfairy;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.testfairy.activities.ProvideFeedbackActivity;

public class TestFairyFeedbackOverlay {

	public enum OverlayPurpose {
		SCREENSHOT, VIDEO
	}

	/***************** Public Interface *****************/

	/**
	 * Call this before everything else to install a human friendly overlay UI
	 * for sending feedbacks to TestFairy.
	 *
	 * @param context Application context or an Activity, must not be null.
	 * @param appToken Your TestFairy app token.
	 * @param showImmediately Set it to true of you want to show the overlay after installation completes.
	 */
	static public void installOverlay(Context context, String appToken, OverlayPurpose overlayPurpose, boolean showImmediately) {
		if (context == null ) throw new AssertionError("Context cannot be null.");
		if (appToken == null) throw new AssertionError("TestFairy app token cannot be null.");

		final Application a = (Application) context.getApplicationContext();
		a.registerActivityLifecycleCallbacks(lifecycle);

		visible = showImmediately;
		visibleBeforePause = showImmediately;
		token = appToken;
		purpose = overlayPurpose;

		uninstall = new Runnable() {
			@Override
			public void run() {
				uninstall = null;
				a.unregisterActivityLifecycleCallbacks(lifecycle);

				if (instance != null) {
					synchronized (instance) {
						visibleBeforePause = false;
						instance.removeOverlay(true);
						instance = null;
						beginCalled = false;
					}
				}
			}
		};

		if (context instanceof Activity) {
			final Activity activity = (Activity) context;
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					onResume(activity);
				}
			});
		}
	}

	/**
	 * Call this if you no longer need the overlay during your app's lifetime.
	 */
	static public void uninstallOverlay() {
		if (uninstall != null) {
			uninstall.run();
		}
	}

	/**
	 * Toggles overlay visibility with animation if installed.
	 */
	static public void toggle() {
		toggle(true);
	}

	/**
	 * Toggles overlay visibility if installed.
	 */
	static public void toggle(boolean animated) {
		if (instance != null && instance.container != null) {
			if (visible) {
				hide(animated);
			} else {
				show(animated);
			}
		}
	}

	/**
	 * Shows overlay with animation if installed.
	 */
	static public void show() {
		show(true);
	}

	/**
	 * Shows overlay if installed.
	 * @param animated Whether to run an animation.
	 */
	static public void show(boolean animated) {
		if (instance != null && instance.container != null) {
			final View container = instance.container;

			if (animated) {
				float widthHeight = dpToPx(instance.container.getContext(), ICON_SIZE_IN_DP);
				TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, widthHeight * 5, 0);
				translateAnimation.setDuration(500);
				translateAnimation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						container.setVisibility(View.VISIBLE);
					}

					@Override
					public void onAnimationEnd(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});

				container.startAnimation(translateAnimation);
			} else {
				container.setVisibility(View.VISIBLE);
			}

			visible = true;
		} else {
			visible = false;
			visibleBeforePause = true;
		}
	}

	/**
	 * Hides overlay with animation if installed.
	 */
	static public void hide() {
		hide(true);
	}

	/**
	 * Hides overlay if installed.
	 * @param animated Whether to run an animation.
	 */
	static public void hide(boolean animated) {
		if (instance != null && instance.container != null) {
			final View container = instance.container;

			if (animated) {
				float widthHeight = dpToPx(instance.container.getContext(), ICON_SIZE_IN_DP);
				TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, widthHeight * 5);
				translateAnimation.setDuration(500);

				translateAnimation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						container.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});
				container.startAnimation(translateAnimation);
			} else {
				container.setVisibility(View.GONE);
			}
		}

		visible = false;
		visibleBeforePause = false;
	}

	/***************** SDK Constants *****************/

	static private String TAG = "TFFeedbackOverlay";

	/***************** Overlay State *****************/

	private LinearLayout container;

	/***************** Singleton State *****************/

	static private TestFairyFeedbackOverlay instance;
	static private String token;
	static private OverlayPurpose purpose = null;
	static private boolean visible = true;
	static private boolean visibleBeforePause = false;
	static private boolean beginCalled = false;
	static private Runnable uninstall = null;

	/***************** Lifecycle *****************/

	static private final Application.ActivityLifecycleCallbacks lifecycle = new Application.ActivityLifecycleCallbacks() {
		@Override
		public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		}

		@Override
		public void onActivityStarted(Activity activity) {

		}

		@Override
		public void onActivityResumed(Activity activity) {
			onResume(activity);
		}

		@Override
		public void onActivityPaused(Activity activity) {
			onPause(activity);
		}

		@Override
		public void onActivityStopped(Activity activity) {
		}

		@Override
		public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

		}

		@Override
		public void onActivityDestroyed(Activity activity) {

		}
	};

	private TestFairyFeedbackOverlay() { }

	private synchronized void attachOverlay(FrameLayout root) {
		Log.d(TAG, "Attaching overlay");

		final Context context = root.getContext();
		container = new LinearLayout(context);
		int padding = (int) dpToPx(context, OVERLAY_PADDING);

		int containerWidth = (int) dpToPx(context, ICON_SIZE_IN_DP) + padding * 2;
		final FrameLayout.LayoutParams containerLayoutParams = new FrameLayout.LayoutParams(
				containerWidth,
				FrameLayout.LayoutParams.WRAP_CONTENT,
				Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM
		);
		containerLayoutParams.leftMargin = 0;
		containerLayoutParams.rightMargin = 0;

		for (int i = 0; i < 2; i++) {
			int imageWidthHeight = (int) dpToPx(context, ICON_SIZE_IN_DP);
			final RoundedImageView roundedImageView = new RoundedImageView(context);

			LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
					imageWidthHeight,
					imageWidthHeight
			);
			imageLayoutParams.setMargins(0,0,0, padding);

			roundedImageView.setBorderRadius(imageWidthHeight / 2);
			roundedImageView.setPadding(padding * 2, padding * 2, padding * 2, padding * 2);
			roundedImageView.setLayoutParams(imageLayoutParams);

			if (i == 0 && purpose == OverlayPurpose.VIDEO) {
				final View containerDuringSetup = container;

				if (!beginCalled) {
					TestFairy.begin(context, token);
					beginCalled = true;
				}

				Toast.makeText(context, RECORD_MESSAGE, Toast.LENGTH_SHORT).show();
				TestFairy.addEvent(VIDEO_OVERLAY_EVENT);

				roundedImageView.setEnableBackground(false);
				roundedImageView.setImageDrawable(new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(RECORDING_ICON, 0, RECORDING_ICON.length)));
				roundedImageView.setOnClickListener(null);
				roundedImageView.post(new ShowHideAnimationRunnable(containerDuringSetup, roundedImageView));

				container.addView(roundedImageView);
			} else if (i == 1 && purpose == OverlayPurpose.SCREENSHOT) {
				roundedImageView.setImageDrawable(new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(SCREENSHOT_ICON, 0, SCREENSHOT_ICON.length)));
				roundedImageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						TestFairy.showFeedbackForm();
					}
				});

				container.addView(roundedImageView);
			}
		}

		container.setPadding(padding, 0, padding, padding);
		container.setLayoutParams(containerLayoutParams);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
		container.setDividerPadding(0);
		container.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
		root.addView(container);

		if (visible) {
			show(false);
		} else {
			hide(false);
		}
	}

	private synchronized void removeOverlay(boolean postToContainer) {
		Log.d(TAG, "Removing overlay");

		if (container != null) {
			Runnable remove = new Runnable() {
				@Override
				public void run() {
					container.removeAllViews();
					ViewParent parent = container.getParent();
					if (parent instanceof ViewGroup) {
						ViewGroup castedParent = (ViewGroup) parent;
						castedParent.removeView(container);
					}

					container = null;
				}
			};

			if (postToContainer) {
				container.post(remove);
			} else {
				remove.run();
			}
		}

		visible = false;
	}

	static private void onResume(Activity activity) {
		Log.d(TAG, "TestFairyFeedbackOverlay is attaching to " + activity.getClass().getSimpleName());

		if (instance != null && !(activity instanceof ProvideFeedbackActivity)) {
			synchronized (instance) {
				final View root = activity.getWindow().getDecorView().findViewById(android.R.id.content);
				if (root != null && root instanceof FrameLayout) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							instance.removeOverlay(false);
							visible = visibleBeforePause;
							instance.attachOverlay((FrameLayout) root);
						}
					});
				} else {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							instance.removeOverlay(false);
						}
					});
					Log.e(TAG, "There is no view to attach TestFairy feedback overlay");
				}
			}
		} else if (!(activity instanceof ProvideFeedbackActivity)) {
			instance = new TestFairyFeedbackOverlay();
			final View root = activity.getWindow().getDecorView().findViewById(android.R.id.content);

			if (root != null && root instanceof FrameLayout) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						visible = visibleBeforePause;
						instance.attachOverlay((FrameLayout) root);
					}
				});
			} else {
				Log.e(TAG, "There is no view to attach TestFairy feedback overlay");
			}
		}
	}

	static private void onPause(Activity activity) {
		Log.d(TAG, "TestFairyFeedbackOverlay is detaching from " + activity.getClass().getSimpleName());

		if (instance != null) {
			synchronized (instance) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						visibleBeforePause = visible;
						instance.removeOverlay(false);
						instance = null;
					}
				});
			}
		}
	}

	static private float dpToPx(Context context, float dp) {
		Resources r = context.getResources();
		return TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				dp,
				r.getDisplayMetrics()
		);
	}

	/**
	 * shows a bitmap as if it had rounded corners. based on :
	 * http://rahulswackyworld.blogspot.co.il/2013/04/android-drawables-with-rounded_7.html
	 * easy alternative from support library: RoundedBitmapDrawableFactory.create( ...) ;
	 */
	static private class RoundedCornersDrawable extends BitmapDrawable {
		private final BitmapShader bitmapShader;
		private final Paint p;
		private final RectF rect;
		private final float borderRadius;
		private final boolean enableBackground;

		public RoundedCornersDrawable(final Resources resources, final Bitmap bitmap, final float borderRadius, boolean enableBackground) {
			super(resources, bitmap);

			this.bitmapShader = new BitmapShader(getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

			Bitmap b = getBitmap();

			this.p = getPaint();
			this.p.setAntiAlias(true);
			this.p.setShader(bitmapShader);

			int w = b.getWidth(), h = b.getHeight();
			this.rect = new RectF(0, 0, w, h);

			this.borderRadius = borderRadius < 0 ? 0.15f * Math.min(w, h) : borderRadius;
			this.enableBackground = enableBackground;
		}

		@Override
		public void draw(final Canvas canvas) {
			if (enableBackground) {
				Paint paint = new Paint();
				paint.setColor(ICON_BG_COLOR);
				canvas.drawRoundRect(rect, borderRadius, borderRadius, paint);
			}

			canvas.drawRoundRect(rect, borderRadius, borderRadius, p);
		}
	}

	static private class RoundedImageView extends ImageView {
		private boolean isDirty = true;
		private int borderRadius = 0;
		private boolean enableBackground = true;

		public RoundedImageView(Context context) {
			super(context);
		}

		public RoundedImageView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
			super(context, attrs, defStyleAttr);
		}

		public void setBorderRadius(int borderRadius) {
			this.borderRadius = borderRadius;
			isDirty = true;
		}

		public void setEnableBackground(boolean enableBackground) {
			this.enableBackground = enableBackground;
			isDirty = true;
		}

		@Override
		protected void onDraw(final Canvas canvas) {
			if (isDirty) {
				isDirty = false;
				drawContent();
				return;
			}

			super.onDraw(canvas);
		}

		/**
		 * draws the view's content to a bitmap. code based on :
		 * http://nadavfima.com/android-snippet-inflate-a-layout-draw-to-a-bitmap/
		 */
		static private Bitmap drawToBitmap(final View viewToDrawFrom, final int width, final int height) {
			// Create a new bitmap and a new canvas using that bitmap
			final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			final Canvas canvas = new Canvas(bmp);
			viewToDrawFrom.setDrawingCacheEnabled(true);

			// Supply measurements
			viewToDrawFrom.measure(MeasureSpec.makeMeasureSpec(canvas.getWidth(), MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(canvas.getHeight(), MeasureSpec.EXACTLY));

			// Apply the measures so the layout would resize before drawing.
			viewToDrawFrom.layout(0, 0, viewToDrawFrom.getMeasuredWidth(), viewToDrawFrom.getMeasuredHeight());

			// and now the bmp object will actually contain the requested layout
			canvas.drawBitmap(viewToDrawFrom.getDrawingCache(), 0, 0, new Paint());
			viewToDrawFrom.setDrawingCacheEnabled(false);
			return bmp;
		}

		private void drawContent() {
			if (getMeasuredWidth() <= 0 || getMeasuredHeight() <= 0)
				return;
			final Bitmap bitmap = drawToBitmap(this, getMeasuredWidth(), getMeasuredHeight());
			final RoundedCornersDrawable drawable = new RoundedCornersDrawable(getResources(), bitmap, borderRadius, enableBackground);
			setImageDrawable(drawable);
		}
	}

	static private class ShowHideAnimationRunnable implements Runnable {
		private final View view;
		private final View container;
		private boolean showing = false;

		public ShowHideAnimationRunnable(View container, View view) {
			this.view = view;
			this.container = container;
		}

		@Override
		public void run() {
			if (
					view != null &&
					container != null &&
					TestFairyFeedbackOverlay.instance != null &&
					container == TestFairyFeedbackOverlay.instance.container
			) {
				if (showing) {
					view.setVisibility(View.INVISIBLE);
				} else {
					view.setVisibility(View.VISIBLE);
				}

				view.postDelayed(this, RECORD_ANIMATION_FREQUENCY);
				showing = !showing;
			}
		}
	}

	/***************** UI Setup *****************/

	static private final byte[] SCREENSHOT_ICON = Base64.decode("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", 0);
	static private final byte[] RECORDING_ICON = Base64.decode("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", 0);
	static private final int ICON_SIZE_IN_DP = 70;
	static private final int OVERLAY_PADDING = 5;
	static private final int ICON_BG_COLOR = Color.BLACK;
	static private final long RECORD_ANIMATION_FREQUENCY = 1000;
	static private final String RECORD_MESSAGE = "Now recording...";
	static private final String VIDEO_OVERLAY_EVENT = "User requested video";
}
