/*
 * Filename	: BrightnessActivity.java
 * Function	:
 * Comment 	:
 * History	: 2015/02/22, ruinnel, Create
 *
 * Version	: 1.0
 * Author   : Copyright (c) 2015 by JC Square Inc. All Rights Reserved.
 */

package net.ruinnel.nfc.tasker.widget;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import net.ruinnel.nfc.tasker.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class BrightnessActivity extends Activity {
	private static final String TAG = BrightnessActivity.class.getSimpleName();

	public static final String EXTRA_BRIGHTNESS = "brightness";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int brightness = getIntent().getIntExtra(EXTRA_BRIGHTNESS, -2);
//		Log.v(TAG, "brightness = " + brightness);
		if (brightness == -2) this.finish();	// 종료

		Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
		Window window = getWindow();
//		Log.v(TAG, "brightness = " + (float)(brightness/100.0));
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.screenBrightness = (float)((brightness / 100.0));
		window.setAttributes(lp);

		Log.v(TAG, "setBrightness - " + (int)(255 * (brightness / 100.0)));
		if (brightness == -1) {
			Settings.System.putInt(getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
		} else {
			Settings.System.putInt(getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			Settings.System.putInt(getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS, (int)(255 * (brightness / 100.0)));
		}

		//		mListener.onProcessEnd(cmd.getType(), RESULT_OK);

		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				Log.v(TAG, "brightness changed");
				BrightnessActivity.this.finish();
				return false;
			}
		});

		// 화면 밝기 설정은 Activity가 바로 종료되어 버리면 적용되지 않으므로
		// 딜레이를 줍니다.
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(1);
			}
		}, 1000);
	}
}
