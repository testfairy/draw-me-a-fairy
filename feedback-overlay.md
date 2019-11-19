# TestFairy Feedback Overlay

## Installation

1. Copy [this]() file into your project under a package named `com.testfairy`.
2. Install overlay with the following code.

```java
boolean showOverlayAfterInstall = true;

// For screenshots
TestFairyFeedbackOverlay.installOverlay(context, "APP_TOKEN", OverlayPurpose.SCREENSHOT, showOverlayAfterInstall);

// For video
TestFairyFeedbackOverlay.installOverlay(context, "APP_TOKEN", OverlayPurpose.VIDEO, showOverlayAfterInstall);

```

## API

```java
    /**
	 * Call this before everything else to install a human friendly overlay UI
	 * for sending feedbacks to TestFairy.
	 *
	 * @param context Application context or an Activity, must not be null.
	 * @param appToken Your TestFairy app token.
	 * @param showImmediately Set it to true of you want to show the overlay after installation completes.
	 */
	void installOverlay(Context context, String appToken, OverlayPurpose overlayPurpose, boolean showImmediately);

	/**
	 * Call this if you no longer need the overlay during your app's lifetime.
	 */
	void uninstallOverlay();

	/**
	 * Toggles overlay visibility with animation if installed.
	 */
	void toggle();

	/**
	 * Toggles overlay visibility if installed.
	 */
	void toggle(boolean animated);

	/**
	 * Shows overlay with animation if installed.
	 */
	void show();

	/**
	 * Shows overlay if installed.
	 * @param animated Whether to run an animation.
	 */
	void show(boolean animated);

	/**
	 * Hides overlay with animation if installed.
	 */
	void hide();

	/**
	 * Hides overlay if installed.
	 * @param animated Whether to run an animation.
	 */
	void hide(boolean animated);
```
