package net.ruinnel.nfc.tasker;

import android.app.Application;
import android.util.Log;
import net.ruinnel.nfc.tasker.bean.Category;
import net.ruinnel.nfc.tasker.func.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NfcTasker extends Application {
	private static final String TAG = NfcTasker.class.getSimpleName();

	public List<Category> mCategories;
	public static String PATH_ICON;
	public static String PATH_CACHE;

	@Override
	public void onCreate() {
		super.onCreate();
		initCategories();
		PATH_CACHE = getExternalCacheDir().getAbsolutePath();
		PATH_ICON = getExternalFilesDir(null).getAbsolutePath();
		File fileDir = new File(getExternalFilesDir(null).getAbsolutePath());
		Log.i(TAG, "fileDir = " + fileDir.getAbsolutePath() + ", " + fileDir.exists());
	}

	private void initCategories() {
		mCategories = new ArrayList<Category>();

		// tasker category
		Category tasker = new Category(getString(R.string.cat_tasker), -1, Tasker.class.getName());
		mCategories.add(tasker);

		// wifi category
		Category wifi = new Category(getString(R.string.cat_network), R.drawable.ic_network_wifi_black_24dp, "");
		wifi.sub = new ArrayList<Category>();
		wifi.sub.add(new Category(getString(R.string.cat_sub_wifi_toggle), R.drawable.ic_cached_black_24dp, WifiToggle.class.getName()));
		wifi.sub.add(new Category(getString(R.string.cat_sub_wifi_connect), R.drawable.ic_network_wifi_black_24dp, WifiConnect.class.getName()));
		mCategories.add(wifi);

		// bluetooth category
		Category bluetooth = new Category(getString(R.string.cat_bluetooth), R.drawable.ic_bluetooth_black_24dp, "");
		bluetooth.sub = new ArrayList<Category>();
		bluetooth.sub.add(new Category(getString(R.string.cat_sub_bluetooth_toggle), R.drawable.ic_cached_black_24dp, BluetoothToggle.class.getName()));
		bluetooth.sub.add(new Category(getString(R.string.cat_sub_bluetooth_discoverable), R.drawable.ic_bluetooth_searching_black_24dp, BluetoothDiscoverable.class.getName()));
		mCategories.add(bluetooth);

		// disp&sound category
		Category display = new Category(getString(R.string.cat_disp_n_sound), R.drawable.ic_settings_display_black_24dp, "");
		display.sub = new ArrayList<Category>();
		display.sub.add(new Category(getString(R.string.cat_sub_disp_rotate_toggle), R.drawable.ic_cached_black_24dp, ScreenRotateToggle.class.getName()));
		display.sub.add(new Category(getString(R.string.cat_sub_disp_brightness), R.drawable.ic_brightness_medium_black_24dp, Brightness.class.getName()));
		display.sub.add(new Category(getString(R.string.cat_sub_disp_volume), R.drawable.ic_volume_up_black_24dp, Volume.class.getName()));
		mCategories.add(display);

		// communicate category
		Category commute = new Category(getString(R.string.cat_communicate), R.drawable.ic_call_black_24dp, "");
		commute.sub = new ArrayList<Category>();
		commute.sub.add(new Category(getString(R.string.cat_sub_communicate_call), R.drawable.ic_call_black_24dp, Call.class.getName()));
		commute.sub.add(new Category(getString(R.string.cat_sub_communicate_sms), R.drawable.ic_textsms_black_24dp, Sms.class.getName()));
		commute.sub.add(new Category(getString(R.string.cat_sub_communicate_email), R.drawable.ic_email_black_24dp, Email.class.getName()));
		mCategories.add(commute);

		// apps category
		Category apps = new Category(getString(R.string.cat_apps), R.drawable.ic_apps_black_24dp, "");
		apps.sub = new ArrayList<Category>();
		apps.sub.add(new Category(getString(R.string.cat_sub_apps_launch), R.drawable.ic_apps_black_24dp, AppLaunch.class.getName()));
		apps.sub.add(new Category(getString(R.string.cat_sub_apps_playmusic), R.drawable.ic_queue_music_black_24dp, PlayMusic.class.getName()));
		mCategories.add(apps);
	}
}
