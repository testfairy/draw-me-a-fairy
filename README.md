# Draw Me A Fairy

*Draw Me A Fairy* is a sample application using TestFairy SDK.

![Screenshot](docs/draw-me-something-front.jpg)

With this sample application, we show how to use features of the platform, including:

- Initializing the SDK ([src](https://github.com/testfairy/draw-me-a-fairy/blob/master/app/src/main/java/com/testfairy/samples/drawmefairy/MyApplication.java#L58))
- Recording videos of app use
- Identifying users, for future searches ([src](https://github.com/testfairy/draw-me-a-fairy/blob/master/app/src/main/java/com/testfairy/samples/drawmefairy/MyApplication.java#L57))
- Capturing logs, and sending them to TestFairy
- Remote Logging (logs not written to logcat, but are sent to TestFairy) ([src](https://github.com/testfairy/draw-me-a-fairy/blob/master/app/src/main/java/com/testfairy/samples/drawmefairy/MyApplication.java#L61))
- Adding events after specific user interactions ([src](https://github.com/testfairy/draw-me-a-fairy/blob/master/app/src/main/java/com/testfairy/samples/drawmefairy/DrawingActivity.java#L245))
- Attaching files for future inspections ([src](https://github.com/testfairy/draw-me-a-fairy/blob/master/app/src/main/java/com/testfairy/samples/drawmefairy/DrawingPanel.java#L138))
- Hiding sensitive data ([src](https://github.com/testfairy/draw-me-a-fairy/blob/master/app/src/main/java/com/testfairy/samples/drawmefairy/MyApplication.java#L72))
- Capture network requests ([src](https://github.com/testfairy/draw-me-a-fairy/blob/master/app/src/main/java/com/testfairy/samples/drawmefairy/MyApplication.java#L88))
- Optionally build a version which can record audio samples via device microphone which you can preview inside the TestFairy dashboard.

```
NOTE:

To run this app on your own device, please clone and open the project using Android Studio.
Then open "app/build.gradle" file, and change the 'appToken' and `serverEndpoint` to your value.
See https://app.testfairy.com/settings/ for more information.
```

## How to build

*Draw Me A Fairy* comes with 2 product flavors. One flavors support audio recordings during the app is foreground. The other flavor disables audio entirely.

If you want to build the version without the audio, run the command below in project directory to create the apk.
```bash
./gradlew assembleRegularRelease
# or
./gradlew assembleRegularDebug 
```

If you want to build the version with the enabled audio, run the command below instead.
```bash
./gradlew assembleAudioRelease
# or 
./gradlew assembleAudioDebug
```

If you want to enable audio in your existing, SDK-enabled app, see [TFAudioRecord](https://github.com/testfairy-blog/TFAudioRecord) repo to find out how.
