package net.ruinnel.nfc.tasker.func;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import net.ruinnel.nfc.tasker.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PlayMusic extends Function {
	private static final String TAG = PlayMusic.class.getSimpleName();

	private final String[] params;
	public PlayMusic(Context context, String[] params) {
		super(context);
		this.params = params;
	}

	@Override
	public List<Param> getParamTypes() {
		List<Param> params = new ArrayList<Param>();
		return params;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public void run() {
		Log.v(TAG, "PlayMusic");
		try {
			Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
			buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
			context.sendOrderedBroadcast(buttonDown, null);
		} catch (Exception e) {
			Log.w(TAG, "keydown fail");
		}

		try {
			Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
			buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
			context.sendOrderedBroadcast(buttonUp, null);
		} catch (Exception e) {
			Log.w(TAG, "keyup fail");
		}
	}
}
