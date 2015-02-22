package net.ruinnel.nfc.tasker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
	private static final String TAG = Settings.class.getSimpleName();

	// key for setting
	public static final String KEY_TASKER_GOT_IT = "tasker_got_it";

	private static Settings mInstance;
	private final SharedPreferences mPref;

	public static Settings getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new Settings(context);
		}

		return mInstance;
	}

	private Settings(Context context) {
		mPref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public boolean isTaskerGotIt() {
		return mPref.getBoolean(KEY_TASKER_GOT_IT, false);
	}

	public void setTaskerGotIt(boolean init) {
		final SharedPreferences.Editor editor = mPref.edit();
		editor.putBoolean(KEY_TASKER_GOT_IT, init);

		editor.commit();
	}
}
